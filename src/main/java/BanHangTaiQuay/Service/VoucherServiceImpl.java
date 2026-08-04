package BanHangTaiQuay.Service;

import BanHangTaiQuay.Model.VoucherRevalidationResult;
import QuanLySanPham.Entity.ChiTietHoaDon;
import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.KhachHangPhieuGiamGia;
import QuanLySanPham.Entity.LichSuHoaDon;
import QuanLySanPham.Entity.PhieuGiamGia;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class VoucherServiceImpl implements VoucherService {

    @Override
    public List<PhieuGiamGia> timKiemVoucher(int idHoaDon, String tuKhoa) {
        if (idHoaDon <= 0) {
            throw new IllegalArgumentException("ID hóa đơn không hợp lệ.");
        }

        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            HoaDon hoaDon = em.find(HoaDon.class, idHoaDon);
            if (hoaDon == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            String keyword = tuKhoa == null ? null : tuKhoa.trim().toLowerCase(Locale.ROOT);
            LocalDateTime hienTai = LocalDateTime.now();

            StringBuilder jpql = new StringBuilder(
                    "SELECT p FROM PhieuGiamGia p "
                    + "WHERE p.trangThai = 1 "
                    + "AND (p.ngayBatDau IS NULL OR p.ngayBatDau <= :hienTai) "
                    + "AND (p.ngayKetThuc IS NULL OR p.ngayKetThuc >= :hienTai)");
            if (keyword != null && !keyword.isEmpty()) {
                jpql.append(" AND (LOWER(p.maVoucher) LIKE :keyword OR LOWER(p.tenVoucher) LIKE :keyword)");
            }
            jpql.append(" ORDER BY p.id DESC");

            var q = em.createQuery(jpql.toString(), PhieuGiamGia.class)
                    .setParameter("hienTai", hienTai)
                    .setMaxResults(50);
            if (keyword != null && !keyword.isEmpty()) {
                q.setParameter("keyword", "%" + keyword + "%");
            }
            List<PhieuGiamGia> ungVien = q.getResultList();

            BigDecimal tongTienHang = tinhTongTienHang(hoaDon);
            List<PhieuGiamGia> ketQua = new ArrayList<>();
            Integer idVoucherDangAp = hoaDon.getPhieuGiamGia() == null
                    ? null
                    : hoaDon.getPhieuGiamGia().getId();
            for (PhieuGiamGia voucher : ungVien) {
                boolean laVoucherDangAp = idVoucherDangAp != null
                        && idVoucherDangAp.equals(voucher.getId());
                if (laVoucherDangAp || laVoucherPhuHop(em, voucher, hoaDon, tongTienHang)) {
                    ketQua.add(voucher);
                }
                if (ketQua.size() >= 8 && !laVoucherDangAp) {
                    break;
                }
            }
            if (idVoucherDangAp != null && ketQua.stream().noneMatch(v -> idVoucherDangAp.equals(v.getId()))) {
                ketQua.add(hoaDon.getPhieuGiamGia());
            }
            return ketQua;
        } finally {
            em.close();
        }
    }

    @Override
    public void apDungVoucher(int idHoaDon, String maVoucher) {
        if (idHoaDon <= 0) {
            throw new IllegalArgumentException("ID hóa đơn không hợp lệ.");
        }
        if (maVoucher == null || maVoucher.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã voucher không được để trống.");
        }

        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            HoaDon hoaDon = em.find(HoaDon.class, idHoaDon, LockModeType.PESSIMISTIC_WRITE);
            if (hoaDon == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            if (hoaDon.getTrangThai() == null
                    || (hoaDon.getTrangThai() != 0 && hoaDon.getTrangThai() != 1)) {
                throw new IllegalStateException("Chỉ được áp dụng voucher cho hóa đơn đang chờ thanh toán.");
            }

            List<ChiTietHoaDon> chiTietHoaDons = hoaDon.getChiTietHoaDons();
            if (chiTietHoaDons == null || chiTietHoaDons.isEmpty()) {
                throw new IllegalStateException("Hóa đơn chưa có sản phẩm.");
            }

            String maVoucherChuanHoa = maVoucher.trim().toUpperCase(Locale.ROOT);
            PhieuGiamGia voucher = em.createQuery(
                            "SELECT p FROM PhieuGiamGia p WHERE UPPER(p.maVoucher) = :maVoucher",
                            PhieuGiamGia.class)
                    .setParameter("maVoucher", maVoucherChuanHoa)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (voucher == null) {
                throw new IllegalArgumentException("Mã voucher không hợp lệ.");
            }
            if (hoaDon.getPhieuGiamGia() != null) {
                throw new IllegalStateException("Hóa đơn đã được áp dụng voucher.");
            }
            dongBoSoLuongDaDung(em, voucher);
            kiemTraVoucher(voucher, hoaDon);
            kiemTraPhanQuyenVoucher(em, voucher, hoaDon);

            hoaDon.setPhieuGiamGia(voucher);
            for (ChiTietHoaDon chiTiet : chiTietHoaDons) {
                int soLuong = chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong();
                if (chiTiet.getDonGia() == null) {
                    throw new IllegalStateException("Chi tiết hóa đơn chưa có đơn giá.");
                }
                chiTiet.setGiaBanRa(chiTiet.getDonGia());
                chiTiet.setTongTien(chiTiet.getDonGia().multiply(BigDecimal.valueOf(soLuong)));
            }

            danhDauVoucherCaNhanDaDung(em, voucher, hoaDon);
            dongBoSoLuongDaDung(em, voucher);
            capNhatTongTien(hoaDon);
            ghiLichSu(em, hoaDon, "AP_VOUCHER", "Áp dụng voucher: " + maVoucherChuanHoa);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void goVoucher(int idHoaDon) {
        if (idHoaDon <= 0) {
            throw new IllegalArgumentException("Hóa đơn không hợp lệ.");
        }

        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            HoaDon hoaDon = em.find(HoaDon.class, idHoaDon, LockModeType.PESSIMISTIC_WRITE);
            if (hoaDon == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            if (hoaDon.getTrangThai() == null
                    || (hoaDon.getTrangThai() != 0 && hoaDon.getTrangThai() != 1)) {
                throw new IllegalStateException("Chỉ được gỡ voucher khỏi hóa đơn đang chờ thanh toán.");
            }
            if (hoaDon.getPhieuGiamGia() == null) {
                throw new IllegalStateException("Hóa đơn chưa áp dụng voucher.");
            }

            String maVoucher = hoaDon.getPhieuGiamGia().getMaVoucher();
            hoanVoucherKhiHuy(em, hoaDon);
            ghiLichSu(em, hoaDon, "GO_VOUCHER", "Gỡ voucher: " + maVoucher);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void kiemTraVoucherKhiThanhToan(EntityManager em, HoaDon hoaDon) {
        if (em == null || hoaDon == null || hoaDon.getPhieuGiamGia() == null) {
            return;
        }
        PhieuGiamGia voucher = em.find(
                PhieuGiamGia.class,
                hoaDon.getPhieuGiamGia().getId(),
                LockModeType.PESSIMISTIC_WRITE
        );
        if (voucher == null) {
            throw new IllegalStateException("Voucher của hóa đơn không còn tồn tại.");
        }
        kiemTraVoucher(voucher, hoaDon, true);
        kiemTraPhanQuyenVoucherDaApDung(em, voucher, hoaDon);
    }

    /**
     * Kiểm tra lại voucher trong transaction đang xử lý hóa đơn.
     * Nếu voucher không còn hợp lệ thì gỡ voucher và trả hóa đơn về giá gốc;
     * nếu giá trị giảm thay đổi thì đồng bộ lại snapshot các dòng và tổng tiền.
     */
    @Override
    public VoucherRevalidationResult revalidateVoucher(EntityManager em, HoaDon hoaDon) {
        if (em == null || hoaDon == null || hoaDon.getPhieuGiamGia() == null) {
            return VoucherRevalidationResult.unchanged(null, BigDecimal.ZERO);
        }

        String maVoucher = hoaDon.getPhieuGiamGia().getMaVoucher();
        BigDecimal tongTienHang = tinhTongTienHang(hoaDon);
        BigDecimal tongTienCu = hoaDon.getTongTienThanhToan() == null
                ? tongTienHang
                : hoaDon.getTongTienThanhToan();
        BigDecimal tienGiamCu = tongTienHang.subtract(tongTienCu).max(BigDecimal.ZERO);

        PhieuGiamGia voucher = hoaDon.getPhieuGiamGia().getId() == null
                ? null
                : em.find(PhieuGiamGia.class, hoaDon.getPhieuGiamGia().getId(), LockModeType.PESSIMISTIC_WRITE);
        if (voucher == null) {
            hoanVoucherKhiHuy(em, hoaDon);
            ghiLichSu(em, hoaDon, "VOUCHER_HET_HAN",
                    "Voucher " + maVoucher + " không còn tồn tại, đã gỡ khỏi hóa đơn.");
            return new VoucherRevalidationResult(
                    true, false, "VOUCHER_HET_HAN",
                    "Mã giảm giá đã hết hạn, hệ thống đã gỡ voucher",
                    maVoucher, tienGiamCu, BigDecimal.ZERO);
        }

        if (loaiBoHoaDonGiuVuotLuot(em, voucher, hoaDon.getId())) {
            return new VoucherRevalidationResult(
                    true, false, "VOUCHER_HET_LUOT",
                    "Mã giảm giá đã hết lượt do hóa đơn khác áp dụng trước",
                    maVoucher, tienGiamCu, BigDecimal.ZERO);
        }

        try {
            kiemTraVoucher(voucher, hoaDon, true);
            kiemTraPhanQuyenVoucherDaApDung(em, voucher, hoaDon);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            boolean hetHan = voucher.getNgayKetThuc() != null
                    && LocalDateTime.now().isAfter(voucher.getNgayKetThuc());
            String hanhDong = hetHan ? "VOUCHER_HET_HAN" : "VOUCHER_KHONG_DU_DK";
            String thongBao = hetHan
                    ? "Mã giảm giá đã hết hạn, hệ thống đã gỡ voucher"
                    : "Mã giảm giá không còn đủ điều kiện, hệ thống đã gỡ voucher: " + ex.getMessage();

            hoanVoucherKhiHuy(em, hoaDon);
            ghiLichSu(em, hoaDon, hanhDong,
                    "Voucher " + maVoucher + " bị gỡ khi kiểm tra lại. Lý do: " + ex.getMessage());
            return new VoucherRevalidationResult(
                    true, false, hanhDong, thongBao, maVoucher, tienGiamCu, BigDecimal.ZERO);
        }

        BigDecimal tienGiamMoi = tinhTienGiam(tongTienHang, voucher);
        capNhatChiTietVeGiaGoc(hoaDon);
        capNhatTongTien(hoaDon);

        if (tienGiamCu.compareTo(tienGiamMoi) != 0) {
            String thongBao = "Giá trị ưu đãi của mã " + maVoucher
                    + " đã thay đổi, hệ thống đã cập nhật lại tổng tiền";
            ghiLichSu(em, hoaDon, "VOUCHER_CAP_NHAT_GIA_TRI",
                    "Voucher " + maVoucher + " thay đổi giá trị giảm từ "
                            + tienGiamCu.toPlainString() + "đ thành "
                            + tienGiamMoi.toPlainString() + "đ.");
            return new VoucherRevalidationResult(
                    false, true, "VOUCHER_CAP_NHAT_GIA_TRI", thongBao,
                    maVoucher, tienGiamCu, tienGiamMoi);
        }

        return VoucherRevalidationResult.unchanged(maVoucher, tienGiamMoi);
    }

    @Override
    public void hoanVoucherKhiHuy(EntityManager em, HoaDon hoaDon) {
        if (em == null || hoaDon == null || hoaDon.getPhieuGiamGia() == null) {
            return;
        }

        Integer idVoucher = hoaDon.getPhieuGiamGia().getId();
        Integer idKhachHang = hoaDon.getKhachHang() == null ? null : hoaDon.getKhachHang().getId();
        PhieuGiamGia voucher = idVoucher == null ? null : em.find(
                PhieuGiamGia.class, idVoucher, LockModeType.PESSIMISTIC_WRITE);

        for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDons()) {
            BigDecimal donGiaGoc = chiTiet.getDonGia();
            int soLuong = chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong();
            if (donGiaGoc != null) {
                chiTiet.setGiaBanRa(donGiaGoc);
                chiTiet.setTongTien(donGiaGoc.multiply(BigDecimal.valueOf(soLuong)));
            }
        }
        hoaDon.setPhieuGiamGia(null);
        capNhatTongTien(hoaDon);
        em.flush();

        if (idKhachHang != null && voucher != null
                && !conHoaDonKhacGiuVoucherCuaKhach(em, idKhachHang, voucher.getId())) {
            KhachHangPhieuGiamGia lienKet = em.createQuery(
                            "SELECT k FROM KhachHangPhieuGiamGia k "
                                    + "WHERE k.khachHang.id = :idKhachHang "
                                    + "AND k.phieuGiamGia.id = :idVoucher "
                                    + "AND k.ngaySuDung IS NOT NULL",
                            KhachHangPhieuGiamGia.class)
                    .setParameter("idKhachHang", idKhachHang)
                    .setParameter("idVoucher", voucher.getId())
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (lienKet != null) {
                lienKet.setNgaySuDung(null);
                lienKet.setTrangThai(1);
            }
        }
        if (voucher != null) {
            dongBoSoLuongDaDung(em, voucher);
        }
    }

    private void kiemTraVoucher(PhieuGiamGia voucher, HoaDon hoaDon) {
        kiemTraVoucher(voucher, hoaDon, false);
    }

    private void kiemTraVoucher(PhieuGiamGia voucher, HoaDon hoaDon, boolean daGiuLuot) {
        if (voucher == null) {
            throw new IllegalArgumentException("Mã voucher không hợp lệ.");
        }
        if (voucher.getTrangThai() == null || voucher.getTrangThai() != 1) {
            throw new IllegalStateException("Voucher không thể sử dụng.");
        }

        LocalDateTime hienTai = LocalDateTime.now();
        if (voucher.getNgayBatDau() != null && hienTai.isBefore(voucher.getNgayBatDau())) {
            throw new IllegalStateException("Voucher chưa bắt đầu áp dụng.");
        }
        if (voucher.getNgayKetThuc() != null && hienTai.isAfter(voucher.getNgayKetThuc())) {
            throw new IllegalStateException("Mã giảm giá đã hết hạn sử dụng");
        }

        int soLuong = voucher.getSoLuong() == null ? 0 : voucher.getSoLuong();
        int soLuongDaDung = voucher.getSoLuongDaDung() == null ? 0 : voucher.getSoLuongDaDung();
        if (soLuong <= 0 || (!daGiuLuot && soLuongDaDung >= soLuong)) {
            throw new IllegalStateException("Voucher đã hết lượt sử dụng.");
        }

        BigDecimal tongTien = tinhTongTienHang(hoaDon);
        BigDecimal donToiThieu = voucher.getDonToiThieu() == null
                ? BigDecimal.ZERO
                : voucher.getDonToiThieu();
        if (tongTien.compareTo(donToiThieu) < 0) {
            throw new IllegalStateException("Hóa đơn không đủ điều kiện áp dụng voucher.");
        }
        if (voucher.getLoaiGiamGia() == null || voucher.getGiaTriGiam() == null) {
            throw new IllegalStateException("Voucher chưa có thông tin giảm giá hợp lệ.");
        }
    }

    private boolean laVoucherPhuHop(EntityManager em, PhieuGiamGia voucher,
                                    HoaDon hoaDon, BigDecimal tongTienHang) {
        int soLuong = voucher.getSoLuong() == null ? 0 : voucher.getSoLuong();
        long soLuotDangGiu = demHoaDonDangGiuVoucher(em, voucher.getId());
        if (soLuong <= 0 || !VoucherReservationPolicy.canReserve(soLuong, soLuotDangGiu)
                || voucher.getLoaiGiamGia() == null || voucher.getGiaTriGiam() == null) {
            return false;
        }

        BigDecimal donToiThieu = voucher.getDonToiThieu() == null
                ? BigDecimal.ZERO : voucher.getDonToiThieu();
        if (tongTienHang.compareTo(donToiThieu) < 0) {
            return false;
        }

        if (voucher.getLoaiPhieu() == null || voucher.getLoaiPhieu() != 1) {
            return true;
        }
        if (hoaDon.getKhachHang() == null || hoaDon.getKhachHang().getId() == null) {
            return false;
        }
        Long soLienKet = em.createQuery(
                        "SELECT COUNT(k) FROM KhachHangPhieuGiamGia k "
                                + "WHERE k.khachHang.id = :idKhachHang "
                                + "AND k.phieuGiamGia.id = :idVoucher "
                                + "AND k.ngaySuDung IS NULL "
                                + "AND (k.trangThai IS NULL OR k.trangThai = 1)",
                        Long.class)
                .setParameter("idKhachHang", hoaDon.getKhachHang().getId())
                .setParameter("idVoucher", voucher.getId())
                .getSingleResult();
        return soLienKet != null && soLienKet > 0;
    }

    private void kiemTraPhanQuyenVoucher(EntityManager em, PhieuGiamGia voucher, HoaDon hoaDon) {
        if (voucher.getLoaiPhieu() == null || voucher.getLoaiPhieu() != 1) {
            return;
        }
        if (hoaDon.getKhachHang() == null || hoaDon.getKhachHang().getId() == null) {
            throw new IllegalStateException("Voucher cá nhân cần gắn khách hàng trước.");
        }

        KhachHangPhieuGiamGia lienKet = em.createQuery(
                        "SELECT k FROM KhachHangPhieuGiamGia k "
                                + "WHERE k.khachHang.id = :idKhachHang "
                                + "AND k.phieuGiamGia.id = :idVoucher "
                                + "AND k.ngaySuDung IS NULL "
                                + "AND (k.trangThai IS NULL OR k.trangThai = 1)",
                        KhachHangPhieuGiamGia.class)
                .setParameter("idKhachHang", hoaDon.getKhachHang().getId())
                .setParameter("idVoucher", voucher.getId())
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
        if (lienKet == null) {
            throw new IllegalStateException("Voucher cá nhân không thuộc về khách hàng hoặc đã được sử dụng.");
        }
    }

    private void danhDauVoucherCaNhanDaDung(EntityManager em, PhieuGiamGia voucher, HoaDon hoaDon) {
        if (voucher.getLoaiPhieu() == null || voucher.getLoaiPhieu() != 1 || hoaDon.getKhachHang() == null) {
            return;
        }
        KhachHangPhieuGiamGia lienKet = em.createQuery(
                        "SELECT k FROM KhachHangPhieuGiamGia k "
                                + "WHERE k.khachHang.id = :idKhachHang "
                                + "AND k.phieuGiamGia.id = :idVoucher "
                                + "AND k.ngaySuDung IS NULL",
                        KhachHangPhieuGiamGia.class)
                .setParameter("idKhachHang", hoaDon.getKhachHang().getId())
                .setParameter("idVoucher", voucher.getId())
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
        if (lienKet != null) {
            lienKet.setNgaySuDung(LocalDateTime.now());
            lienKet.setTrangThai(0);
        }
    }

    private BigDecimal tinhTienGiam(BigDecimal tongTienHang, PhieuGiamGia voucher) {
        String loaiGiamGia = Normalizer.normalize(voucher.getLoaiGiamGia(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase(Locale.ROOT);
        BigDecimal tienGiam;

        if (loaiGiamGia.contains("%") || loaiGiamGia.contains("phan tram") || loaiGiamGia.contains("percent")) {
            BigDecimal phanTramGiam = voucher.getGiaTriGiam().divide(BigDecimal.valueOf(100));
            tienGiam = tongTienHang.multiply(phanTramGiam);
            if (voucher.getGiamToiDa() != null && tienGiam.compareTo(voucher.getGiamToiDa()) > 0) {
                tienGiam = voucher.getGiamToiDa();
            }
        } else if (loaiGiamGia.contains("tien") || loaiGiamGia.contains("amount")) {
            tienGiam = voucher.getGiaTriGiam();
        } else {
            throw new IllegalStateException("Loại giảm giá của voucher không hợp lệ.");
        }

        return tienGiam.max(BigDecimal.ZERO).min(tongTienHang);
    }

    private BigDecimal tinhTongTienHang(HoaDon hoaDon) {
        BigDecimal tongTienHang = BigDecimal.ZERO;
        if (hoaDon.getChiTietHoaDons() == null) {
            return tongTienHang;
        }
        for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDons()) {
            if (chiTiet.getDonGia() != null && chiTiet.getSoLuong() != null) {
                tongTienHang = tongTienHang.add(
                        chiTiet.getDonGia().multiply(BigDecimal.valueOf(chiTiet.getSoLuong())));
            }
        }
        return tongTienHang;
    }

    private void capNhatTongTien(HoaDon hoaDon) {
        BigDecimal tongTienHang = BigDecimal.ZERO;
        for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDons()) {
            if (chiTiet.getTongTien() != null) {
                tongTienHang = tongTienHang.add(chiTiet.getTongTien());
            }
        }
        BigDecimal tienGiam = hoaDon.getPhieuGiamGia() == null
                ? BigDecimal.ZERO
                : tinhTienGiam(tongTienHang, hoaDon.getPhieuGiamGia());
        hoaDon.setTongTienThanhToan(tongTienHang.subtract(tienGiam));
    }

    private void capNhatChiTietVeGiaGoc(HoaDon hoaDon) {
        if (hoaDon.getChiTietHoaDons() == null) {
            return;
        }
        for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDons()) {
            BigDecimal donGiaGoc = chiTiet.getDonGia();
            int soLuong = chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong();
            if (donGiaGoc != null) {
                chiTiet.setGiaBanRa(donGiaGoc);
                chiTiet.setTongTien(donGiaGoc.multiply(BigDecimal.valueOf(soLuong)));
            }
        }
    }

    private void kiemTraPhanQuyenVoucherDaApDung(
            EntityManager em, PhieuGiamGia voucher, HoaDon hoaDon) {
        if (voucher.getLoaiPhieu() == null || voucher.getLoaiPhieu() != 1) {
            return;
        }
        if (hoaDon.getKhachHang() == null || hoaDon.getKhachHang().getId() == null) {
            throw new IllegalStateException("Voucher cá nhân cần gắn đúng khách hàng.");
        }
        Long soLienKet = em.createQuery(
                        "SELECT COUNT(k) FROM KhachHangPhieuGiamGia k "
                                + "WHERE k.khachHang.id = :idKhachHang "
                                + "AND k.phieuGiamGia.id = :idVoucher "
                                + "AND k.ngaySuDung IS NOT NULL",
                        Long.class)
                .setParameter("idKhachHang", hoaDon.getKhachHang().getId())
                .setParameter("idVoucher", voucher.getId())
                .getSingleResult();
        if (soLienKet == null || soLienKet == 0) {
            throw new IllegalStateException("Voucher cá nhân không thuộc về khách hàng của hóa đơn.");
        }
    }

    /**
     * Kiểm tra hóa đơn hiện tại có nằm trong nhóm giữ voucher sớm nhất hay không.
     * Chỉ gỡ trên chính hóa đơn đang revalidate để tránh khóa chéo nhiều hóa đơn POS.
     */
    private boolean loaiBoHoaDonGiuVuotLuot(
            EntityManager em, PhieuGiamGia voucher, Integer idHoaDonDangKiemTra) {
        List<HoaDon> hoaDonDangGiu = em.createQuery(
                        "SELECT h FROM HoaDon h "
                                + "WHERE h.phieuGiamGia.id = :idVoucher "
                                + "AND (h.trangThai IS NULL OR h.trangThai <> 5)",
                        HoaDon.class)
                .setParameter("idVoucher", voucher.getId())
                .getResultList();

        long soHoaDonDaHoanTat = hoaDonDangGiu.stream()
                .filter(hoaDon -> !laHoaDonCho(hoaDon))
                .count();
        int soChoConLai = VoucherReservationPolicy.waitingSlots(
                voucher.getSoLuong(), soHoaDonDaHoanTat);

        List<VoucherReservationOrder> hoaDonCho = hoaDonDangGiu.stream()
                .filter(this::laHoaDonCho)
                .map(hoaDon -> layThuTuGiuVoucher(em, hoaDon))
                .sorted(Comparator
                        .comparing(VoucherReservationOrder::appliedAt)
                        .thenComparing(VoucherReservationOrder::historyId)
                        .thenComparing(order -> order.hoaDon().getId()))
                .toList();

        int viTriHoaDonDangKiemTra = -1;
        for (int index = 0; index < hoaDonCho.size(); index++) {
            if (idHoaDonDangKiemTra != null
                    && idHoaDonDangKiemTra.equals(hoaDonCho.get(index).hoaDon().getId())) {
                viTriHoaDonDangKiemTra = index;
                break;
            }
        }
        if (VoucherReservationPolicy.mayKeepReservation(
                viTriHoaDonDangKiemTra, soChoConLai)) {
            dongBoSoLuongDaDung(em, voucher);
            return false;
        }
        if (viTriHoaDonDangKiemTra < 0) {
            dongBoSoLuongDaDung(em, voucher);
            return false;
        }

        HoaDon hoaDonBiGo = hoaDonCho.get(viTriHoaDonDangKiemTra).hoaDon();
        String maVoucher = voucher.getMaVoucher();
        hoanVoucherKhiHuy(em, hoaDonBiGo);
        ghiLichSu(em, hoaDonBiGo, "VOUCHER_HET_LUOT",
                "Tự động gỡ voucher " + maVoucher
                        + " vì số lượt đã được hóa đơn áp trước giữ hết.");
        dongBoSoLuongDaDung(em, voucher);
        return true;
    }

    private VoucherReservationOrder layThuTuGiuVoucher(EntityManager em, HoaDon hoaDon) {
        LichSuHoaDon lichSuAp = em.createQuery(
                        "SELECT l FROM LichSuHoaDon l "
                                + "WHERE l.hoaDon.id = :idHoaDon AND l.hanhDong = :hanhDong "
                                + "ORDER BY l.ngayTao DESC, l.id DESC",
                        LichSuHoaDon.class)
                .setParameter("idHoaDon", hoaDon.getId())
                .setParameter("hanhDong", "AP_VOUCHER")
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
        LocalDateTime thoiDiemAp = lichSuAp == null || lichSuAp.getNgayTao() == null
                ? (hoaDon.getNgayTao() == null ? LocalDateTime.MIN : hoaDon.getNgayTao())
                : lichSuAp.getNgayTao();
        int idLichSu = lichSuAp == null || lichSuAp.getId() == null ? 0 : lichSuAp.getId();
        return new VoucherReservationOrder(hoaDon, thoiDiemAp, idLichSu);
    }

    private boolean laHoaDonCho(HoaDon hoaDon) {
        return hoaDon.getTrangThai() == null
                || hoaDon.getTrangThai() == 0
                || hoaDon.getTrangThai() == 1;
    }

    private long demHoaDonDangGiuVoucher(EntityManager em, Integer idVoucher) {
        if (idVoucher == null) {
            return 0L;
        }
        Long soHoaDon = em.createQuery(
                        "SELECT COUNT(h) FROM HoaDon h "
                                + "WHERE h.phieuGiamGia.id = :idVoucher "
                                + "AND (h.trangThai IS NULL OR h.trangThai <> 5)",
                        Long.class)
                .setParameter("idVoucher", idVoucher)
                .getSingleResult();
        return soHoaDon == null ? 0L : soHoaDon;
    }

    private void dongBoSoLuongDaDung(EntityManager em, PhieuGiamGia voucher) {
        em.flush();
        long soLuotDangGiu = demHoaDonDangGiuVoucher(em, voucher.getId());
        voucher.setSoLuongDaDung(soLuotDangGiu > Integer.MAX_VALUE
                ? Integer.MAX_VALUE
                : (int) soLuotDangGiu);
    }

    private boolean conHoaDonKhacGiuVoucherCuaKhach(
            EntityManager em, Integer idKhachHang, Integer idVoucher) {
        Long soHoaDon = em.createQuery(
                        "SELECT COUNT(h) FROM HoaDon h "
                                + "WHERE h.khachHang.id = :idKhachHang "
                                + "AND h.phieuGiamGia.id = :idVoucher "
                                + "AND (h.trangThai IS NULL OR h.trangThai <> 5)",
                        Long.class)
                .setParameter("idKhachHang", idKhachHang)
                .setParameter("idVoucher", idVoucher)
                .getSingleResult();
        return soHoaDon != null && soHoaDon > 0;
    }

    private record VoucherReservationOrder(
            HoaDon hoaDon, LocalDateTime appliedAt, int historyId) {
    }

    private void ghiLichSu(EntityManager em, HoaDon hoaDon, String hanhDong, String ghiChu) {
        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(hoaDon);
        lichSu.setHanhDong(hanhDong);
        lichSu.setGhiChu(ghiChu);
        lichSu.setNgayTao(LocalDateTime.now());
        em.persist(lichSu);
    }
}
