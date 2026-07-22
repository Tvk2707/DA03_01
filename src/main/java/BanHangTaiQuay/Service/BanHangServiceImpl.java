package BanHangTaiQuay.Service;

import BanHangTaiQuay.Dao.BanHangDAO;
import BanHangTaiQuay.Dao.BanHangDAOImpl;
import QuanLySanPham.Entity.*;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BanHangServiceImpl implements BanHangService {

    private final BanHangDAO banHangDAO = new BanHangDAOImpl();
    private final KhachHangService khachHangService = new KhachHangServiceImpl();
    private final VoucherService voucherService = new VoucherServiceImpl();

    @Override
    public HoaDon taoHoaDonMoi(int idNhanVien, int idCa) {
        if (idNhanVien <= 0) {
            throw new IllegalArgumentException("Nhân viên không hợp lệ.");
        }
        if (idCa <= 0) {
            throw new IllegalArgumentException("Ca làm việc không hợp lệ.");
        }

        if (banHangDAO.demHoaDonCho(idNhanVien) >= 10) {
            throw new IllegalStateException("Đã đạt giới hạn 10 hóa đơn chờ. Vui lòng xử lý bớt trước khi tạo mới.");
        }

        HoaDon hd = new HoaDon();

        // 👇 THÊM DÒNG NÀY ĐỂ FIX LỖI SQL (Tạo mã hóa đơn tự động bằng thời gian thực)
        hd.setMaHoaDon("HD" + System.currentTimeMillis());

        NhanVien nv = new NhanVien();
        nv.setId(idNhanVien);
        hd.setNhanVien(nv);

        // Gắn hóa đơn vào ca đang được lưu trong session, không tạo ca mới.
        CaLamViec ca = new CaLamViec();
        ca.setId(idCa);
        hd.setCa(ca);

        hd.setNgayTao(LocalDateTime.now());
        hd.setTongTienThanhToan(BigDecimal.ZERO);
        hd.setTrangThai(0); // Trạng thái "Chờ xác nhận"

        HoaDon newHoaDon = banHangDAO.insertHoaDon(hd);
        ghiLichSu(newHoaDon, "TAO_DON", "Tạo hóa đơn mới");
        return newHoaDon;
    }
    @Override
    public void themSanPhamVaoGio(int idHoaDon, int idSanPhamChiTiet, int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            SanPhamChiTiet spct = em.find(SanPhamChiTiet.class, idSanPhamChiTiet, LockModeType.PESSIMISTIC_WRITE);

            if (spct == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại.");
            }

            if (spct.getTrangThai() != null && spct.getTrangThai() != 1) {
                throw new IllegalStateException("Sản phẩm đã ngừng kinh doanh.");
            }

            int tonKho = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
            if (soLuong > tonKho) {
                throw new IllegalStateException("Không đủ tồn kho, còn lại: " + tonKho);
            }

            HoaDon hd = findHoaDonWithDetails(em, idHoaDon);
            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            if (hd.getTrangThai() == null || hd.getTrangThai() != 0) {
                throw new IllegalStateException("Chỉ được thêm sản phẩm vào hóa đơn đang chờ thanh toán.");
            }

            ChiTietHoaDon chiTiet = hd.getChiTietHoaDons().stream()
                    .filter(ct -> ct.getSanPhamChiTiet() != null
                            && Integer.valueOf(idSanPhamChiTiet).equals(ct.getSanPhamChiTiet().getId()))
                    .findFirst()
                    .orElse(null);

            if (chiTiet != null) {
                int soLuongMoi = (chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong()) + soLuong;
                chiTiet.setSoLuong(soLuongMoi);
                chiTiet.setTongTien(chiTiet.getDonGia().multiply(new BigDecimal(soLuongMoi)));
            } else {
                chiTiet = new ChiTietHoaDon();
                chiTiet.setHoaDon(hd);
                chiTiet.setSanPhamChiTiet(spct);
                chiTiet.setSoLuong(soLuong);
                if (spct.getGiaBan() == null) {
                    throw new IllegalStateException("Sản phẩm chưa có giá bán.");
                }
                chiTiet.setDonGia(spct.getGiaBan());
                chiTiet.setGiaBanRa(spct.getGiaBan());
                chiTiet.setTongTien(chiTiet.getDonGia().multiply(new BigDecimal(soLuong)));
                hd.getChiTietHoaDons().add(chiTiet);
                em.persist(chiTiet);
            }

            spct.setSoLuongTon(tonKho - soLuong);
            capNhatTongTien(hd);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e; // Re-throw the exception to be handled by the controller
        } finally {
            em.close();
        }
    }

    @Override
    public void xoaSanPhamKhoiGio(int idHoaDon, int idChiTiet) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            HoaDon hd = findHoaDonWithDetails(em, idHoaDon);
            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            if (hd.getTrangThai() == null || hd.getTrangThai() != 0) {
                throw new IllegalStateException("Chỉ được xóa sản phẩm khỏi hóa đơn đang chờ thanh toán.");
            }
            ChiTietHoaDon chiTiet = hd.getChiTietHoaDons().stream()
                    .filter(ct -> Integer.valueOf(idChiTiet).equals(ct.getId()))
                    .findFirst()
                    .orElse(null);
            if (chiTiet == null) {
                throw new IllegalArgumentException("Chi tiết hóa đơn không hợp lệ.");
            }

            SanPhamChiTiet spct = em.find(
                    SanPhamChiTiet.class,
                    chiTiet.getSanPhamChiTiet().getId(),
                    LockModeType.PESSIMISTIC_WRITE
            );
            if (spct == null) {
                throw new IllegalStateException("Sản phẩm liên quan không tồn tại.");
            }

            int tonKho = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
            spct.setSoLuongTon(tonKho + (chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong()));
            hd.getChiTietHoaDons().remove(chiTiet);
            em.remove(chiTiet);
            capNhatTongTien(hd);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void capNhatSoLuong(int idChiTiet, int soLuongMoi) {
        if (soLuongMoi <= 0) {
            throw new IllegalArgumentException("Số lượng mới không được âm.");
        }

        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            ChiTietHoaDon chiTietCu = em.find(ChiTietHoaDon.class, idChiTiet);
            if (chiTietCu == null || chiTietCu.getHoaDon() == null) {
                throw new IllegalArgumentException("Chi tiết hóa đơn không tồn tại.");
            }
            HoaDon hd = findHoaDonWithDetails(em, chiTietCu.getHoaDon().getId());
            ChiTietHoaDon chiTiet = hd.getChiTietHoaDons().stream()
                    .filter(ct -> Integer.valueOf(idChiTiet).equals(ct.getId()))
                    .findFirst()
                    .orElse(null);
            if (chiTiet == null) {
                throw new IllegalArgumentException("Chi tiết hóa đơn không tồn tại.");
            }

            if (hd.getTrangThai() == null || hd.getTrangThai() != 0) {
                throw new IllegalStateException("Chỉ được sửa hóa đơn đang chờ thanh toán.");
            }

            int soLuongCu = chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong();
            int chenhLech = soLuongMoi - soLuongCu;

            if (chenhLech != 0) {
                SanPhamChiTiet spct = em.find(
                        SanPhamChiTiet.class,
                        chiTiet.getSanPhamChiTiet().getId(),
                        LockModeType.PESSIMISTIC_WRITE
                );
                if (spct == null) {
                    throw new IllegalStateException("Sản phẩm liên quan không tồn tại.");
                }

                if (chenhLech > 0) { // Tăng số lượng
                    int tonKho = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
                    if (chenhLech > tonKho) {
                        throw new IllegalStateException("Không đủ tồn kho, chỉ còn lại: " + tonKho);
                    }
                    spct.setSoLuongTon(tonKho - chenhLech);
                } else { // Giảm số lượng
                    int tonKho = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
                    spct.setSoLuongTon(tonKho - chenhLech);
                }

                chiTiet.setSoLuong(soLuongMoi);
                chiTiet.setTongTien(chiTiet.getDonGia().multiply(new BigDecimal(soLuongMoi)));
                capNhatTongTien(hd);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private void xoaChiTietTrongGiaoDich(EntityManager em, HoaDon hd, ChiTietHoaDon chiTiet) {
        SanPhamChiTiet spct = em.find(
                SanPhamChiTiet.class,
                chiTiet.getSanPhamChiTiet().getId(),
                LockModeType.PESSIMISTIC_WRITE
        );
        if (spct == null) {
            throw new IllegalStateException("Sản phẩm liên quan không tồn tại.");
        }
        int tonKho = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
        spct.setSoLuongTon(tonKho + (chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong()));
        hd.getChiTietHoaDons().remove(chiTiet);
        em.remove(chiTiet);
        capNhatTongTien(hd);
    }

    @Override
    public KhachHang traCuuKhachHang(String soDienThoai) {
        return khachHangService.traCuuKhachHang(soDienThoai);
    }

    @Override
    public KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen) {
        return khachHangService.traCuuHoacTaoKhachHang(soDienThoai, hoTen);
        /*
        KhachHang kh = khachHangRepository.findBySoDienThoai(soDienThoai);
        if (kh == null) {
            kh = new KhachHang();
            kh.setSoDienThoai(soDienThoai);
            kh.setHoTen((hoTen == null || hoTen.trim().isEmpty()) ? "Khách lẻ" : hoTen);
            kh.setTrangThai(1); // Active
            khachHangRepository.add(kh);
        }
        return kh;
        */
    }

    @Override
    public void ganKhachHang(int idHoaDon, int idKhachHang) {
        if (idHoaDon <= 0 || idKhachHang <= 0) {
            throw new IllegalArgumentException("ID hóa đơn và ID khách hàng phải lớn hơn 0.");
        }

        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            HoaDon hd = em.find(HoaDon.class, idHoaDon, LockModeType.PESSIMISTIC_WRITE);
            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            if (hd.getTrangThai() == null || hd.getTrangThai() != 0) {
                throw new IllegalStateException("Chỉ được gắn khách hàng cho hóa đơn đang chờ thanh toán.");
            }

            KhachHang kh = em.find(KhachHang.class, idKhachHang);
            if (kh == null) {
                throw new IllegalArgumentException("Khách hàng không tồn tại.");
            }
            hd.setKhachHang(kh);
            ghiLichSu(em, hd, "GAN_KHACH_HANG", "Gán khách hàng vào đơn");
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
    public void apDungVoucher(int idHoaDon, String maVoucher) {
        voucherService.apDungVoucher(idHoaDon, maVoucher);
        /*
        HoaDon hd = banHangDAO.findHoaDonById(idHoaDon);
        if (hd == null) {
            throw new IllegalArgumentException("Hóa đơn không tồn tại.");
        }

        PhieuGiamGia pgg = phieuGiamGiaDao.findByMaVoucher(maVoucher);
        if (pgg == null) {
            throw new IllegalArgumentException("Mã voucher không hợp lệ.");
        }
        if (pgg.getTrangThai() != 1) { // Giả sử 1 là "Hoạt động"
            throw new IllegalStateException("Voucher không thể sử dụng.");
        }
        if (pgg.getNgayBatDau() != null && pgg.getNgayKetThuc().isBefore(LocalDate.now().atStartOfDay())) {
            throw new IllegalStateException("Voucher đã hết hạn.");
        }
        if (hd.getTongTienThanhToan().compareTo(pgg.getDonToiThieu()) < 0) {
            throw new IllegalStateException("Hóa đơn không đủ điều kiện áp dụng voucher.");
        }

        hd.setPhieuGiamGia(pgg);

        // Áp dụng giảm giá cho từng chi tiết hóa đơn
        for (ChiTietHoaDon ct : hd.getChiTietHoaDons()) {
            BigDecimal giaGoc = ct.getDonGia();
            BigDecimal giaSauGiam = giaGoc;

            if (pgg.getLoaiGiamGia().trim().equals("Phần trăm")) { // Giảm theo %
                BigDecimal phanTramGiam = pgg.getGiaTriGiam().divide(new BigDecimal(100));
                BigDecimal giamToiDa = pgg.getGiamToiDa();
                BigDecimal soTienGiam = giaGoc.multiply(phanTramGiam);
                if (giamToiDa != null && soTienGiam.compareTo(giamToiDa) > 0) {
                    soTienGiam = giamToiDa;
                }
                giaSauGiam = giaGoc.subtract(soTienGiam);
            } else if (pgg.getLoaiGiamGia().trim().equals("Tiền mặt")) { // Giảm theo số tiền
                giaSauGiam = giaGoc.subtract(pgg.getGiaTriGiam());
            }

            ct.setGiaBanRa(giaSauGiam);
            ct.setTongTien(giaSauGiam.multiply(new BigDecimal(ct.getSoLuong())));
            banHangDAO.updateChiTietHoaDon(ct);
        }

        capNhatTongTien(hd);
        ghiLichSu(hd, "AP_VOUCHER", "Áp dụng voucher: " + maVoucher);
    }

        */
    }

    @Override
    public void xacNhanThanhToan(int idHoaDon, String maPttt, BigDecimal soTienKhachDua) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            HoaDon hd = em.find(HoaDon.class, idHoaDon, LockModeType.PESSIMISTIC_WRITE);
            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }

            if (hd.getTrangThai() != null && hd.getTrangThai() == 3) {
                throw new IllegalStateException("Hóa đơn đã được thanh toán.");
            }
            if (hd.getTrangThai() != null && hd.getTrangThai() == 5) {
                throw new IllegalStateException("Không thể thanh toán hóa đơn đã hủy.");
            }
            if (hd.getChiTietHoaDons() == null || hd.getChiTietHoaDons().isEmpty()) {
                throw new IllegalStateException("Hóa đơn chưa có sản phẩm.");
            }
            if (soTienKhachDua == null || soTienKhachDua.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Số tiền khách đưa phải lớn hơn 0.");
            }

            BigDecimal tongTien = hd.getTongTienThanhToan() == null
                    ? BigDecimal.ZERO
                    : hd.getTongTienThanhToan();
            if (soTienKhachDua.compareTo(tongTien) < 0) {
                throw new IllegalArgumentException("Số tiền khách đưa chưa đủ.");
            }

            HinhThucThanhToan pttt = em.createQuery(
                            "SELECT p FROM HinhThucThanhToan p WHERE p.maPttt = :maPttt",
                            HinhThucThanhToan.class)
                    .setParameter("maPttt", maPttt)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (pttt == null) {
                throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
            }
            if (pttt.getTrangThai() != null && pttt.getTrangThai() != 1) {
                throw new IllegalStateException("Phương thức thanh toán đang tạm khóa.");
            }

            // Ghi nhận thanh toán
            ThanhToanHoaDon tt = new ThanhToanHoaDon();
            tt.setHoaDon(hd);
            tt.setHinhThucThanhToan(pttt);
            tt.setSoTien(soTienKhachDua);
            tt.setThoiGian(LocalDateTime.now());
            tt.setTrangThai(1);
            em.persist(tt);

            // Cập nhật trạng thái hóa đơn
            hd.setTrangThai(3);
            hd.setNgayThanhToan(LocalDateTime.now());

            // Cập nhật doanh thu ca
            CaLamViec ca = hd.getCa();
            if (ca != null) {
                BigDecimal doanhThuCu = ca.getTongDoanhThu() == null
                        ? BigDecimal.ZERO
                        : ca.getTongDoanhThu();
                ca.setTongDoanhThu(doanhThuCu.add(tongTien));
            }

            ghiLichSu(em, hd, "THANH_TOAN", "Xác nhận thanh toán");

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void huyHoaDon(int idHoaDon, String lyDo) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // SỬA Ở ĐÂY: Dùng trực tiếp 'em' đang mở của hàm này để tìm HoaDon
            HoaDon hd = em.find(HoaDon.class, idHoaDon, LockModeType.PESSIMISTIC_WRITE);

            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }

            if (hd.getTrangThai() != null && hd.getTrangThai() == 5) {
                throw new IllegalStateException("Hóa đơn đã được hủy.");
            }
            if (hd.getTrangThai() != null && hd.getTrangThai() == 3) {
                throw new IllegalStateException("Không thể hủy hóa đơn đã thanh toán.");
            }

            // Lúc này EntityManager 'em' vẫn đang mở, vòng lặp sẽ tự động query mà không bị lỗi Lazy
            for (ChiTietHoaDon ct : hd.getChiTietHoaDons()) {
                SanPhamChiTiet spct = em.find(
                        SanPhamChiTiet.class,
                        ct.getSanPhamChiTiet().getId(),
                        LockModeType.PESSIMISTIC_WRITE
                );
                if (spct != null) {
                    int tonKhoHienTai = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
                    int soLuong = ct.getSoLuong() == null ? 0 : ct.getSoLuong();
                    spct.setSoLuongTon(tonKhoHienTai + soLuong);
                }
            }

            hd.setTrangThai(5);
            hd.setLyDoHuy(lyDo);
            ghiLichSu(em, hd, "HUY_DON", "Hủy hóa đơn: " + lyDo);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e; // Ném lỗi ra ngoài để Controller bắt được và trả về JSON {success: false}
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<HoaDon> layDanhSachHoaDonCho(int idNhanVien) {
        return banHangDAO.layDanhSachHoaDonCho(idNhanVien);
    }

    @Override
    public HoaDon layHoaDonTheoId(int idHoaDon) {
        if (idHoaDon <= 0) {
            throw new IllegalArgumentException("ID hóa đơn không hợp lệ.");
        }
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            return findHoaDonWithDetails(em, idHoaDon);
        } finally {
            em.close();
        }
    }

    private void ghiLichSu(HoaDon hd, String hanhDong, String ghiChu) {
        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setHoaDon(hd);
        ls.setHanhDong(hanhDong);
        ls.setGhiChu(ghiChu);
        ls.setNgayTao(LocalDateTime.now());
        banHangDAO.insertLichSu(ls);
    }

    private void ghiLichSu(EntityManager em, HoaDon hd, String hanhDong, String ghiChu) {
        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setHoaDon(hd);
        ls.setHanhDong(hanhDong);
        ls.setGhiChu(ghiChu);
        ls.setNgayTao(LocalDateTime.now());
        em.persist(ls);
    }

    private void capNhatTongTien(HoaDon hd) {
        List<ChiTietHoaDon> chiTietList = hd.getChiTietHoaDons();
        BigDecimal tongTien = BigDecimal.ZERO;
        if (chiTietList != null) {
            for (ChiTietHoaDon ct : chiTietList) {
                if (ct.getTongTien() != null) {
                    tongTien = tongTien.add(ct.getTongTien());
                }
            }
        }
        hd.setTongTienThanhToan(tongTien);
    }

    private HoaDon findHoaDonWithDetails(EntityManager em, int idHoaDon) {
        return em.createQuery(
                        "SELECT DISTINCT h FROM HoaDon h "
                                + "LEFT JOIN FETCH h.chiTietHoaDons ct "
                                + "LEFT JOIN FETCH ct.sanPhamChiTiet "
                                + "LEFT JOIN FETCH ct.sanPhamChiTiet.sanPham "
                                + "LEFT JOIN FETCH ct.sanPhamChiTiet.mauSac "
                                + "LEFT JOIN FETCH ct.sanPhamChiTiet.kichCo "
                                + "LEFT JOIN FETCH h.khachHang "
                                + "WHERE h.id = :idHoaDon",
                        HoaDon.class)
                .setParameter("idHoaDon", idHoaDon)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
