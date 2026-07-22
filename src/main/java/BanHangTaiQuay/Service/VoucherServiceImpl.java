package BanHangTaiQuay.Service;

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
import java.util.List;
import java.util.Locale;

public class VoucherServiceImpl implements VoucherService {

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

            HoaDon hoaDon = em.find(HoaDon.class, idHoaDon);
            if (hoaDon == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            if (hoaDon.getTrangThai() == null || hoaDon.getTrangThai() != 0) {
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
            kiemTraVoucher(voucher, hoaDon);
            kiemTraPhanQuyenVoucher(em, voucher, hoaDon);

            hoaDon.setPhieuGiamGia(voucher);
            for (ChiTietHoaDon chiTiet : chiTietHoaDons) {
                BigDecimal donGiaGoc = chiTiet.getDonGia();
                BigDecimal giaSauGiam = tinhGiaSauGiam(donGiaGoc, voucher);
                int soLuong = chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong();

                chiTiet.setGiaBanRa(giaSauGiam);
                chiTiet.setTongTien(giaSauGiam.multiply(BigDecimal.valueOf(soLuong)));
            }

            voucher.setSoLuongDaDung((voucher.getSoLuongDaDung() == null ? 0 : voucher.getSoLuongDaDung()) + 1);
            danhDauVoucherCaNhanDaDung(em, voucher, hoaDon);
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

    private void kiemTraVoucher(PhieuGiamGia voucher, HoaDon hoaDon) {
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
            throw new IllegalStateException("Voucher đã hết hạn.");
        }

        int soLuong = voucher.getSoLuong() == null ? 0 : voucher.getSoLuong();
        int soLuongDaDung = voucher.getSoLuongDaDung() == null ? 0 : voucher.getSoLuongDaDung();
        if (soLuong <= 0 || soLuongDaDung >= soLuong) {
            throw new IllegalStateException("Voucher đã hết lượt sử dụng.");
        }

        BigDecimal tongTien = hoaDon.getTongTienThanhToan() == null
                ? BigDecimal.ZERO
                : hoaDon.getTongTienThanhToan();
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

    private BigDecimal tinhGiaSauGiam(BigDecimal donGiaGoc, PhieuGiamGia voucher) {
        if (donGiaGoc == null) {
            throw new IllegalStateException("Chi tiết hóa đơn chưa có đơn giá.");
        }

        String loaiGiamGia = Normalizer.normalize(voucher.getLoaiGiamGia(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase(Locale.ROOT);
        BigDecimal giaSauGiam;

        if (loaiGiamGia.contains("%") || loaiGiamGia.contains("phan tram") || loaiGiamGia.contains("percent")) {
            BigDecimal phanTramGiam = voucher.getGiaTriGiam().divide(BigDecimal.valueOf(100));
            BigDecimal soTienGiam = donGiaGoc.multiply(phanTramGiam);
            if (voucher.getGiamToiDa() != null && soTienGiam.compareTo(voucher.getGiamToiDa()) > 0) {
                soTienGiam = voucher.getGiamToiDa();
            }
            giaSauGiam = donGiaGoc.subtract(soTienGiam);
        } else if (loaiGiamGia.contains("tien") || loaiGiamGia.contains("amount")) {
            giaSauGiam = donGiaGoc.subtract(voucher.getGiaTriGiam());
        } else {
            throw new IllegalStateException("Loại giảm giá của voucher không hợp lệ.");
        }

        return giaSauGiam.max(BigDecimal.ZERO);
    }

    private void capNhatTongTien(HoaDon hoaDon) {
        BigDecimal tongTien = BigDecimal.ZERO;
        for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDons()) {
            if (chiTiet.getTongTien() != null) {
                tongTien = tongTien.add(chiTiet.getTongTien());
            }
        }
        hoaDon.setTongTienThanhToan(tongTien);
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
