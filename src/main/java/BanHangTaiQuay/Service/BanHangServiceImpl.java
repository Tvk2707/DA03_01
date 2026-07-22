package BanHangTaiQuay.Service;

import BanHangTaiQuay.Dao.BanHangDAO;
import BanHangTaiQuay.Dao.BanHangDAOImpl;
import QuanLyKhachHang.repository.KhachHangRepository;
import QuanLySanPham.Entity.*;
import QuanLySanPham.Utils.EntityManagerUtlis;
import QuanLySanPham.dao.HinhThucThanhToanDao;
import QuanLySanPham.dao.PhieuGiamGiaDao;
import QuanLySanPham.dao.SanPhamChiTietDao;
import QuanLySanPham.dao.impl.HinhThucThanhToanDaoImpl;
import QuanLySanPham.dao.impl.PhieuGiamGiaDaoImpl;
import QuanLySanPham.dao.impl.SanPhamChiTietDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BanHangServiceImpl implements BanHangService {

    private final BanHangDAO banHangDAO = new BanHangDAOImpl();
    private final SanPhamChiTietDao sanPhamChiTietDao = new SanPhamChiTietDaoImpl();
    private final KhachHangRepository khachHangRepository = new KhachHangRepository();
    private final PhieuGiamGiaDao phieuGiamGiaDao = new PhieuGiamGiaDaoImpl();
    private final HinhThucThanhToanDao hinhThucThanhToanDao = new HinhThucThanhToanDaoImpl();

    @Override
    public HoaDon taoHoaDonMoi(int idNhanVien, int idCa) {
        if (banHangDAO.demHoaDonCho(idNhanVien) >= 10) {
            throw new IllegalStateException("Đã đạt giới hạn 10 hóa đơn chờ. Vui lòng xử lý bớt trước khi tạo mới.");
        }

        HoaDon hd = new HoaDon();

        // 👇 THÊM DÒNG NÀY ĐỂ FIX LỖI SQL (Tạo mã hóa đơn tự động bằng thời gian thực)
        hd.setMaHoaDon("HD" + System.currentTimeMillis());

        NhanVien nv = new NhanVien();
        nv.setId(idNhanVien);
        hd.setNhanVien(nv);

       // CaLamViec ca = new CaLamViec();
       // ca.setId(idCa);
        //hd.setCa(ca);

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

            // 2. Lấy SanPhamChiTiet CÓ KHÓA để đảm bảo tính toàn vẹn dữ liệu tồn kho
            SanPhamChiTiet spct = sanPhamChiTietDao.findByIdForUpdate(idSanPhamChiTiet);

            if (spct == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại.");
            }

            // 3. Nếu sản phẩm ngừng bán
            if (spct.getTrangThai() != 1) { // Giả sử 1 là "Đang bán"
                throw new IllegalStateException("Sản phẩm đã ngừng kinh doanh");
            }

            // 4. Nếu soLuong > tồn kho hiện tại
            if (soLuong > spct.getSoLuongTon()) {
                throw new IllegalStateException("Không đủ tồn kho, còn lại: " + spct.getSoLuongTon());
            }

            HoaDon hd = banHangDAO.findHoaDonById(idHoaDon);
            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }

            // 5. Kiểm tra sản phẩm đã có trong giỏ chưa
            ChiTietHoaDon chiTiet = banHangDAO.findByHoaDonVaSpct(idHoaDon, idSanPhamChiTiet);

            if (chiTiet != null) {
                // Cộng dồn số lượng
                int soLuongMoi = chiTiet.getSoLuong() + soLuong;
                chiTiet.setSoLuong(soLuongMoi);
                chiTiet.setTongTien(chiTiet.getDonGia().multiply(new BigDecimal(soLuongMoi)));
                banHangDAO.updateChiTietHoaDon(chiTiet);
            } else {
                // Tạo dòng mới
                chiTiet = new ChiTietHoaDon();
                chiTiet.setHoaDon(hd);
                chiTiet.setSanPhamChiTiet(spct);
                chiTiet.setSoLuong(soLuong);
                chiTiet.setDonGia(spct.getGiaBan()); // Giả sử giá bán lấy từ SPCT
                chiTiet.setTongTien(chiTiet.getDonGia().multiply(new BigDecimal(soLuong)));
                banHangDAO.insertChiTietHoaDon(chiTiet);
            }

            // 6. Trừ tồn kho ngay lập tức.
            // Ghi chú: Trừ kho ngay khi thêm vào giỏ để giữ chỗ cho khách,
            // tránh tình trạng khách đã đặt hàng nhưng tới lúc thanh toán lại hết hàng.
            // Việc này yêu cầu phải có xử lý hoàn kho cẩn thận khi hủy đơn hoặc xóa sản phẩm.
            spct.setSoLuongTon(spct.getSoLuongTon() - soLuong);
            sanPhamChiTietDao.update(spct);

            // 7. Cập nhật tổng tiền hóa đơn
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

            ChiTietHoaDon chiTiet = banHangDAO.findChiTietHoaDonById(idChiTiet);
            if (chiTiet == null || chiTiet.getHoaDon().getId() != idHoaDon) {
                throw new IllegalArgumentException("Chi tiết hóa đơn không hợp lệ.");
            }

            SanPhamChiTiet spct = sanPhamChiTietDao.findByIdForUpdate(chiTiet.getSanPhamChiTiet().getId());
            if (spct == null) {
                // This case should ideally not happen if data is consistent
                throw new IllegalStateException("Sản phẩm liên quan không tồn tại.");
            }

            // Hoàn lại tồn kho
            spct.setSoLuongTon(spct.getSoLuongTon() + chiTiet.getSoLuong());
            sanPhamChiTietDao.update(spct);

            // Xóa dòng chi tiết
            banHangDAO.deleteChiTietHoaDon(chiTiet);

            // Cập nhật lại tổng tiền
            HoaDon hd = banHangDAO.findHoaDonById(idHoaDon);
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
            throw new IllegalArgumentException("Số lượng mới phải lớn hơn 0.");
        }

        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            ChiTietHoaDon chiTiet = banHangDAO.findChiTietHoaDonById(idChiTiet);
            if (chiTiet == null) {
                throw new IllegalArgumentException("Chi tiết hóa đơn không tồn tại.");
            }

            int soLuongCu = chiTiet.getSoLuong();
            int chenhLech = soLuongMoi - soLuongCu;

            if (chenhLech != 0) {
                SanPhamChiTiet spct = sanPhamChiTietDao.findByIdForUpdate(chiTiet.getSanPhamChiTiet().getId());
                if (spct == null) {
                    throw new IllegalStateException("Sản phẩm liên quan không tồn tại.");
                }

                if (chenhLech > 0) { // Tăng số lượng
                    if (chenhLech > spct.getSoLuongTon()) {
                        throw new IllegalStateException("Không đủ tồn kho, chỉ còn lại: " + spct.getSoLuongTon());
                    }
                    spct.setSoLuongTon(spct.getSoLuongTon() - chenhLech);
                } else { // Giảm số lượng
                    spct.setSoLuongTon(spct.getSoLuongTon() - chenhLech); // -chenhLech là số dương
                }
                sanPhamChiTietDao.update(spct);

                chiTiet.setSoLuong(soLuongMoi);
                chiTiet.setTongTien(chiTiet.getDonGia().multiply(new BigDecimal(soLuongMoi)));
                banHangDAO.updateChiTietHoaDon(chiTiet);

                capNhatTongTien(chiTiet.getHoaDon());
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

    @Override
    public KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen) {
        KhachHang kh = khachHangRepository.findBySoDienThoai(soDienThoai);
        if (kh == null) {
            kh = new KhachHang();
            kh.setSoDienThoai(soDienThoai);
            kh.setHoTen((hoTen == null || hoTen.trim().isEmpty()) ? "Khách lẻ" : hoTen);
            kh.setTrangThai(1); // Active
            khachHangRepository.add(kh);
        }
        return kh;
    }

    @Override
    public void ganKhachHang(int idHoaDon, int idKhachHang) {
        HoaDon hd = banHangDAO.findHoaDonById(idHoaDon);
        if (hd == null) {
            throw new IllegalArgumentException("Hóa đơn không tồn tại.");
        }
        KhachHang kh = new KhachHang();
        kh.setId(idKhachHang);
        hd.setKhachHang(kh);
        banHangDAO.updateHoaDon(hd);
        ghiLichSu(hd, "GAN_KHACH_HANG", "Gán khách hàng vào đơn");
    }

    @Override
    public void apDungVoucher(int idHoaDon, String maVoucher) {
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

    @Override
    public void xacNhanThanhToan(int idHoaDon, String maPttt, BigDecimal soTienKhachDua) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            HoaDon hd = banHangDAO.findHoaDonById(idHoaDon);
            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }

            HinhThucThanhToan pttt = hinhThucThanhToanDao.findByMa(maPttt);
            if (pttt == null) {
                throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
            }

            // Ghi nhận thanh toán
            ThanhToanHoaDon tt = new ThanhToanHoaDon();
            tt.setHoaDon(hd);
            tt.setHinhThucThanhToan(pttt);
            tt.setSoTien(soTienKhachDua);
            tt.setThoiGian(LocalDateTime.now());
            tt.setTrangThai(1); // Thành công
            banHangDAO.insertThanhToan(tt);

            // Cập nhật trạng thái hóa đơn
            hd.setTrangThai(1); // Hoàn thành
            hd.setNgayThanhToan(LocalDateTime.now());
            banHangDAO.updateHoaDon(hd);

            // Cập nhật doanh thu ca
            CaLamViec ca = hd.getCa();
            if (ca != null) {
                ca.setTongDoanhThu(ca.getTongDoanhThu().add(hd.getTongTienThanhToan()));
                // Không cần gọi DAO update ca vì nó đang trong cùng transaction
            }

            // Ghi lịch sử
            ghiLichSu(hd, "THANH_TOAN", "Xác nhận thanh toán");

            // Ghi chú: Tồn kho đã được trừ khi thêm sản phẩm vào giỏ,
            // nên không cần trừ lại ở bước này.

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
            HoaDon hd = em.find(HoaDon.class, idHoaDon);

            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }

            // Lúc này EntityManager 'em' vẫn đang mở, vòng lặp sẽ tự động query mà không bị lỗi Lazy
            for (ChiTietHoaDon ct : hd.getChiTietHoaDons()) {
                // (Giữ nguyên logic cộng lại tồn kho của bạn)
                SanPhamChiTiet spct = sanPhamChiTietDao.findByIdForUpdate(ct.getSanPhamChiTiet().getId());
                if (spct != null) {
                    spct.setSoLuongTon(spct.getSoLuongTon() + ct.getSoLuong());
                    sanPhamChiTietDao.update(spct);
                }
            }

            // Cập nhật trạng thái hóa đơn thành Đã Hủy
            // Tùy vào tên thuộc tính trong Entity của bạn, có thể là setTrangThai(0) hoặc setTrangThai("Đã hủy")
            hd.setTrangThai(5);
            hd.setLyDoHuy(lyDo);

            em.merge(hd); // Cập nhật hóa đơn vào database

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

    private void ghiLichSu(HoaDon hd, String hanhDong, String ghiChu) {
        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setHoaDon(hd);
        ls.setHanhDong(hanhDong);
        ls.setGhiChu(ghiChu);
        ls.setNgayTao(LocalDateTime.now());
        banHangDAO.insertLichSu(ls);
    }

    private void capNhatTongTien(HoaDon hd) {
        // Cần refresh lại list chi tiết để lấy dữ liệu mới nhất sau khi xóa/sửa
        banHangDAO.findHoaDonById(hd.getId()); // This is a trick to refresh the entity
        List<ChiTietHoaDon> chiTietList = hd.getChiTietHoaDons();
        BigDecimal tongTien = BigDecimal.ZERO;
        if (chiTietList != null) {
            for (ChiTietHoaDon ct : chiTietList) {
                tongTien = tongTien.add(ct.getTongTien());
            }
        }
        hd.setTongTienThanhToan(tongTien);
        banHangDAO.updateHoaDon(hd);
    }
}
