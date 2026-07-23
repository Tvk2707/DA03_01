package BanHangTaiQuay.Service;

import BanHangTaiQuay.Dao.BanHangDAO;
import BanHangTaiQuay.Dao.BanHangDAOImpl;
import QuanLySanPham.Entity.*;
import QuanLySanPham.service.SanPhamChiTietService;
import QuanLySanPham.service.impl.SanPhamChiTietServiceImpl;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BanHangServiceImpl implements BanHangService {

    private final BanHangDAO banHangDAO;
    private final KhachHangService khachHangService = new KhachHangServiceImpl();
    private final VoucherService voucherService = new VoucherServiceImpl();
    private final SanPhamChiTietService sanPhamChiTietService = new SanPhamChiTietServiceImpl();

    public BanHangServiceImpl() {
        this(new BanHangDAOImpl());
    }

    // Cho phép kiểm thử các quy tắc nghiệp vụ mà không cần mở kết nối database.
    public BanHangServiceImpl(BanHangDAO banHangDAO) {
        if (banHangDAO == null) {
            throw new IllegalArgumentException("DAO bán hàng không được để trống.");
        }
        this.banHangDAO = banHangDAO;
    }

    @Override
    public HoaDon taoHoaDonMoi(Integer idNhanVien, Integer idCa) {
        if (idNhanVien == null || idNhanVien <= 0) {
            throw new IllegalArgumentException("Nhân viên không hợp lệ.");
        }
        if (idCa == null || idCa <= 0) {
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
        hd.setKhachHang(null);
        hd.setTrangThai(0); // Trạng thái "Chờ xác nhận"

        HoaDon newHoaDon = banHangDAO.insertHoaDon(hd);
        ghiLichSu(newHoaDon, "TAO_DON", "Tạo hóa đơn mới");
        return newHoaDon;
    }
    @Override
    public void themSanPhamVaoGio(int idHoaDon, int idSanPhamChiTiet, int soLuong) {
        validatePositiveId(idHoaDon, "ID hóa đơn");
        validatePositiveId(idSanPhamChiTiet, "ID sản phẩm chi tiết");
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        // Lấy sản phẩm qua module QuanLySanPham trước khi cập nhật giỏ hàng.
        SanPhamChiTiet sanPhamTuModule = sanPhamChiTietService.timTheoId(idSanPhamChiTiet);
        if (sanPhamTuModule == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại.");
        }
        if (sanPhamTuModule.getTrangThai() != null && sanPhamTuModule.getTrangThai() != 1) {
            throw new IllegalStateException("Sản phẩm đã ngừng kinh doanh.");
        }

        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            SanPhamChiTiet spct = em.find(SanPhamChiTiet.class, idSanPhamChiTiet, LockModeType.PESSIMISTIC_WRITE);

            if (spct == null || Boolean.TRUE.equals(spct.getIsDeleted())) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại.");
            }

            if (spct.getTrangThai() != null && spct.getTrangThai() != 1) {
                throw new IllegalStateException("Sản phẩm đã ngừng kinh doanh.");
            }
            if (spct.getSanPham() == null
                    || Boolean.TRUE.equals(spct.getSanPham().getIsDeleted())
                    || (spct.getSanPham().getTrangThai() != null && spct.getSanPham().getTrangThai() != 1)) {
                throw new IllegalStateException("Sản phẩm đã ngừng kinh doanh.");
            }

            HoaDon hd = findHoaDonWithDetails(em, idHoaDon, true);
            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            if (!laHoaDonDangChoThanhToan(hd)) {
                throw new IllegalStateException("Chỉ được thêm sản phẩm vào hóa đơn đang chờ thanh toán.");
            }

            ChiTietHoaDon chiTiet = hd.getChiTietHoaDons().stream()
                    .filter(ct -> ct.getSanPhamChiTiet() != null
                            && Integer.valueOf(idSanPhamChiTiet).equals(ct.getSanPhamChiTiet().getId()))
                    .findFirst()
                    .orElse(null);

            int tonKho = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
            int soLuongTrongGio = chiTiet == null || chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong();
            if (soLuongTrongGio + soLuong > tonKho) {
                throw new IllegalStateException("Không đủ tồn kho, còn lại: " + tonKho);
            }

            if (chiTiet != null) {
                int soLuongMoi = soLuongTrongGio + soLuong;
                if (chiTiet.getDonGia() == null) {
                    if (spct.getGiaBan() == null) {
                        throw new IllegalStateException("Sản phẩm chưa có giá bán.");
                    }
                    chiTiet.setDonGia(spct.getGiaBan());
                    chiTiet.setGiaBanRa(spct.getGiaBan());
                }
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
        validatePositiveId(idHoaDon, "ID hóa đơn");
        validatePositiveId(idChiTiet, "ID chi tiết hóa đơn");
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            HoaDon hd = findHoaDonWithDetails(em, idHoaDon, true);
            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            if (!laHoaDonDangChoThanhToan(hd)) {
                throw new IllegalStateException("Chỉ được xóa sản phẩm khỏi hóa đơn đang chờ thanh toán.");
            }
            ChiTietHoaDon chiTiet = hd.getChiTietHoaDons().stream()
                    .filter(ct -> Integer.valueOf(idChiTiet).equals(ct.getId()))
                    .findFirst()
                    .orElse(null);
            if (chiTiet == null) {
                throw new IllegalArgumentException("Chi tiết hóa đơn không hợp lệ.");
            }

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
        validatePositiveId(idChiTiet, "ID chi tiết hóa đơn");
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
            HoaDon hd = findHoaDonWithDetails(em, chiTietCu.getHoaDon().getId(), true);
            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            ChiTietHoaDon chiTiet = hd.getChiTietHoaDons().stream()
                    .filter(ct -> Integer.valueOf(idChiTiet).equals(ct.getId()))
                    .findFirst()
                    .orElse(null);
            if (chiTiet == null) {
                throw new IllegalArgumentException("Chi tiết hóa đơn không tồn tại.");
            }

            if (!laHoaDonDangChoThanhToan(hd)) {
                throw new IllegalStateException("Chỉ được sửa hóa đơn đang chờ thanh toán.");
            }

            int soLuongCu = chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong();
            if (soLuongMoi != soLuongCu) {
                if (chiTiet.getSanPhamChiTiet() == null || chiTiet.getSanPhamChiTiet().getId() == null) {
                    throw new IllegalStateException("Chi tiết hóa đơn chưa liên kết sản phẩm.");
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
                if (soLuongMoi > tonKho) {
                    throw new IllegalStateException("Không đủ tồn kho, chỉ còn lại: " + tonKho);
                }

                chiTiet.setSoLuong(soLuongMoi);
                if (chiTiet.getDonGia() == null) {
                    throw new IllegalStateException("Chi tiết hóa đơn chưa có đơn giá.");
                }
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

    @Override
    public KhachHang traCuuKhachHang(String soDienThoai) {
        return khachHangService.traCuuKhachHang(soDienThoai);
    }

    @Override
    public List<KhachHang> timKiemKhachHang(String tuKhoa) {
        return khachHangService.timKiemKhachHang(tuKhoa);
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
    public KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen, String email,
                                            LocalDate ngaySinh, Integer gioiTinh) {
        return khachHangService.traCuuHoacTaoKhachHang(soDienThoai, hoTen, email, ngaySinh, gioiTinh);
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
            if (!laHoaDonDangChoThanhToan(hd)) {
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
    public void chonKhachLe(int idHoaDon) {
        if (idHoaDon <= 0) {
            throw new IllegalArgumentException("ID hóa đơn không hợp lệ.");
        }

        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            HoaDon hd = em.find(HoaDon.class, idHoaDon, LockModeType.PESSIMISTIC_WRITE);
            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            if (!laHoaDonDangChoThanhToan(hd)) {
                throw new IllegalStateException("Chỉ được chọn khách lẻ cho hóa đơn đang chờ thanh toán.");
            }

            hd.setKhachHang(null);
            ghiLichSu(em, hd, "CHON_KHACH_LE", "Chuyển hóa đơn sang khách lẻ");
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
    public void goVoucher(int idHoaDon) {
        voucherService.goVoucher(idHoaDon);
    }

    @Override
    public void xacNhanThanhToan(int idHoaDon, String maPttt, BigDecimal soTienKhachDua) {
        xacNhanThanhToan(idHoaDon, maPttt, soTienKhachDua, null, null);
    }

    @Override
    public void xacNhanThanhToan(int idHoaDon, String maPttt, BigDecimal soTienKhachDua,
                                 String maGiaoDich, String ghiChu) {
        if (idHoaDon <= 0) {
            throw new IllegalArgumentException("ID hóa đơn không hợp lệ.");
        }
        if (maPttt == null || maPttt.trim().isEmpty()) {
            throw new IllegalArgumentException("Phương thức thanh toán không được để trống.");
        }
        String maPtttChuanHoa = "TM".equalsIgnoreCase(maPttt.trim())
                ? "PTTT001"
                : maPttt.trim().toUpperCase();

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
            voucherService.kiemTraVoucherKhiThanhToan(em, hd);
            // Luôn tính lại từ toàn bộ chi tiết để không thanh toán theo tổng tiền cũ.
            capNhatTongTien(hd);

            for (ChiTietHoaDon chiTiet : hd.getChiTietHoaDons()) {
                if (chiTiet.getSoLuong() == null || chiTiet.getSoLuong() <= 0) {
                    throw new IllegalStateException("Số lượng sản phẩm trong hóa đơn không hợp lệ.");
                }
                if (chiTiet.getSanPhamChiTiet() == null || chiTiet.getSanPhamChiTiet().getId() == null) {
                    throw new IllegalStateException("Chi tiết hóa đơn chưa liên kết sản phẩm.");
                }
                SanPhamChiTiet spct = em.find(
                        SanPhamChiTiet.class,
                        chiTiet.getSanPhamChiTiet().getId(),
                        LockModeType.PESSIMISTIC_WRITE
                );
                if (spct == null
                        || Boolean.TRUE.equals(spct.getIsDeleted())
                        || spct.getTrangThai() == null
                        || spct.getTrangThai() != 1
                        || spct.getSanPham() == null
                        || Boolean.TRUE.equals(spct.getSanPham().getIsDeleted())
                        || (spct.getSanPham().getTrangThai() != null && spct.getSanPham().getTrangThai() != 1)) {
                    throw new IllegalStateException("Hóa đơn có sản phẩm đã ngừng kinh doanh.");
                }
                int tonKho = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
                if (chiTiet.getSoLuong() > tonKho) {
                    throw new IllegalStateException("Không đủ tồn kho cho sản phẩm " + spct.getMa()
                            + ", chỉ còn: " + tonKho);
                }
                spct.setSoLuongTon(tonKho - chiTiet.getSoLuong());
            }

            BigDecimal tongTien = hd.getTongTienThanhToan() == null
                    ? BigDecimal.ZERO
                    : hd.getTongTienThanhToan();
            String maGiaoDichChuanHoa = normalizeText(maGiaoDich);
            if (maGiaoDichChuanHoa != null) {
                Long soLanTrungMa = em.createQuery(
                                "SELECT COUNT(t) FROM ThanhToanHoaDon t WHERE t.maGiaoDich = :maGiaoDich",
                                Long.class)
                        .setParameter("maGiaoDich", maGiaoDichChuanHoa)
                        .getSingleResult();
                if (soLanTrungMa != null && soLanTrungMa > 0) {
                    throw new IllegalStateException("Mã giao dịch đã được sử dụng cho hóa đơn khác.");
                }
            }

            HinhThucThanhToan pttt = em.createQuery(
                            "SELECT p FROM HinhThucThanhToan p WHERE p.maPttt = :maPttt",
                            HinhThucThanhToan.class)
                    .setParameter("maPttt", maPtttChuanHoa)
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
            // Doanh thu/thanh toán ghi nhận đúng số tiền của hóa đơn, không cộng tiền thối.
            tt.setSoTien(tongTien);
            tt.setMaGiaoDich(maGiaoDichChuanHoa);
            String ghiChuThanhToan = normalizeText(ghiChu);
            if (ghiChuThanhToan == null) {
                String tenPhuongThuc = normalizeText(pttt.getTenPttt());
                ghiChuThanhToan = (tenPhuongThuc == null ? maPtttChuanHoa : tenPhuongThuc)
                        + " tại quầy; Đã xác nhận đủ: " + tongTien;
            }
            LocalDateTime thoiGianThanhToan = LocalDateTime.now();
            tt.setGhiChu(ghiChuThanhToan);
            tt.setThoiGian(thoiGianThanhToan);
            tt.setTrangThai(1);
            em.persist(tt);

            // Đồng bộ bảng lịch sử để màn hình quản lý hóa đơn có đủ dữ liệu giao dịch POS.
            LichSuThanhToan lichSuThanhToan = new LichSuThanhToan();
            lichSuThanhToan.setHoaDon(hd);
            lichSuThanhToan.setSoTien(tongTien);
            lichSuThanhToan.setPhuongThucThanhToan(
                    normalizeText(pttt.getTenPttt()) == null
                            ? maPtttChuanHoa
                            : pttt.getTenPttt().trim());
            lichSuThanhToan.setTrangThaiThanhToan(1);
            lichSuThanhToan.setNgayThanhToan(thoiGianThanhToan);
            lichSuThanhToan.setGhiChu(ghiChuThanhToan);
            em.persist(lichSuThanhToan);

            // Cập nhật trạng thái hóa đơn
            hd.setTrangThai(3);
            hd.setNgayThanhToan(thoiGianThanhToan);

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

            voucherService.hoanVoucherKhiHuy(em, hd);

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
            HoaDon hoaDon = findHoaDonWithDetails(em, idHoaDon);
            if (hoaDon != null && laHoaDonDangChoThanhToan(hoaDon)) {
                // Đồng bộ số tiền hiển thị từ tất cả dòng trong giỏ.
                capNhatTongTien(hoaDon);
            }
            return hoaDon;
        } finally {
            em.close();
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private void validatePositiveId(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " phải lớn hơn 0.");
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
        BigDecimal tongTienHang = BigDecimal.ZERO;
        if (chiTietList != null) {
            for (ChiTietHoaDon ct : chiTietList) {
                if (ct.getTongTien() != null) {
                    tongTienHang = tongTienHang.add(ct.getTongTien());
                }
            }
        }
        if (hd.getPhieuGiamGia() == null) {
            hd.setTongTienThanhToan(tongTienHang);
            return;
        }
        hd.setTongTienThanhToan(tinhTongTienSauVoucher(tongTienHang, hd.getPhieuGiamGia()));
    }

    private BigDecimal tinhTongTienSauVoucher(BigDecimal tongTienHang, PhieuGiamGia voucher) {
        if (voucher.getLoaiGiamGia() == null || voucher.getGiaTriGiam() == null) {
            return tongTienHang;
        }
        String loai = java.text.Normalizer.normalize(
                        voucher.getLoaiGiamGia(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase(java.util.Locale.ROOT);
        BigDecimal tienGiam;
        if (loai.contains("%") || loai.contains("phan tram") || loai.contains("percent")) {
            tienGiam = tongTienHang.multiply(
                    voucher.getGiaTriGiam().divide(BigDecimal.valueOf(100)));
            if (voucher.getGiamToiDa() != null && tienGiam.compareTo(voucher.getGiamToiDa()) > 0) {
                tienGiam = voucher.getGiamToiDa();
            }
        } else if (loai.contains("tien") || loai.contains("amount")) {
            tienGiam = voucher.getGiaTriGiam();
        } else {
            return tongTienHang;
        }
        return tongTienHang.subtract(tienGiam.max(BigDecimal.ZERO).min(tongTienHang));
    }

    private HoaDon findHoaDonWithDetails(EntityManager em, int idHoaDon) {
        return findHoaDonWithDetails(em, idHoaDon, false);
    }

    private HoaDon findHoaDonWithDetails(EntityManager em, int idHoaDon, boolean lock) {
        var query = em.createQuery(
                        "SELECT DISTINCT h FROM HoaDon h "
                                + "LEFT JOIN FETCH h.chiTietHoaDons ct "
                                + "LEFT JOIN FETCH ct.sanPhamChiTiet "
                                + "LEFT JOIN FETCH ct.sanPhamChiTiet.sanPham "
                                + "LEFT JOIN FETCH ct.sanPhamChiTiet.mauSac "
                                + "LEFT JOIN FETCH ct.sanPhamChiTiet.kichCo "
                                + "LEFT JOIN FETCH h.khachHang "
                                + "LEFT JOIN FETCH h.phieuGiamGia "
                                + "WHERE h.id = :idHoaDon",
                        HoaDon.class)
                .setParameter("idHoaDon", idHoaDon);
        if (lock) {
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }
        HoaDon hoaDon = query.getResultStream()
                .findFirst()
                .orElse(null);
        ganAnhHienThiChoGioHang(em, hoaDon);
        return hoaDon;
    }

    // Dữ liệu cũ dùng 1, POS mới tạo đơn dùng 0 cho trạng thái đang chờ.
    private void ganAnhHienThiChoGioHang(EntityManager em, HoaDon hoaDon) {
        if (hoaDon == null || hoaDon.getChiTietHoaDons() == null || hoaDon.getChiTietHoaDons().isEmpty()) {
            return;
        }

        Set<Integer> sanPhamIdsCanLayAnh = new HashSet<>();
        for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDons()) {
            SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
            if (spct == null) {
                continue;
            }
            String anhBienThe = chuanHoaAnh(spct.getHinhAnh(), "File_Anh/images");
            if (anhBienThe != null) {
                spct.setHinhAnhHienThi(anhBienThe);
                continue;
            }
            if (spct.getSanPham() != null && spct.getSanPham().getId() != null) {
                sanPhamIdsCanLayAnh.add(spct.getSanPham().getId());
            }
        }

        if (sanPhamIdsCanLayAnh.isEmpty()) {
            return;
        }

        List<HinhAnhSanPham> hinhAnhs = em.createQuery(
                        "SELECT ha FROM HinhAnhSanPham ha "
                                + "WHERE ha.sanPham.id IN :sanPhamIds "
                                + "ORDER BY ha.sanPham.id, ha.isAnhChinh DESC, ha.id ASC",
                        HinhAnhSanPham.class)
                .setParameter("sanPhamIds", sanPhamIdsCanLayAnh)
                .getResultList();
        Map<Integer, String> anhTheoSanPham = new LinkedHashMap<>();
        for (HinhAnhSanPham hinhAnh : hinhAnhs) {
            if (hinhAnh.getSanPham() == null || hinhAnh.getSanPham().getId() == null) {
                continue;
            }
            String urlAnh = chuanHoaAnh(hinhAnh.getUrlAnh(), "FE/Admin/hinh_anh_san_pham");
            if (urlAnh != null) {
                anhTheoSanPham.putIfAbsent(hinhAnh.getSanPham().getId(), urlAnh);
            }
        }

        for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDons()) {
            SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
            if (spct != null
                    && spct.getHinhAnhHienThi() == null
                    && spct.getSanPham() != null
                    && spct.getSanPham().getId() != null) {
                spct.setHinhAnhHienThi(anhTheoSanPham.get(spct.getSanPham().getId()));
            }
        }
    }

    private String chuanHoaAnh(String hinhAnh, String thuMucMacDinh) {
        if (hinhAnh == null) {
            return null;
        }
        String trimmed = hinhAnh.trim().replace("\\", "/");
        if (trimmed.isEmpty() || "null".equalsIgnoreCase(trimmed)) {
            return null;
        }
        int fileAnhIndex = trimmed.lastIndexOf("File_Anh/images/");
        if (fileAnhIndex >= 0) {
            return trimmed.substring(fileAnhIndex);
        }
        int hinhAnhIndex = trimmed.lastIndexOf("hinh_anh_san_pham/");
        if (hinhAnhIndex >= 0) {
            return "FE/Admin/" + trimmed.substring(hinhAnhIndex);
        }
        return thuMucMacDinh + "/" + trimmed;
    }

    private boolean laHoaDonDangChoThanhToan(HoaDon hoaDon) {
        if (hoaDon == null || hoaDon.getTrangThai() == null) {
            return false;
        }
        return hoaDon.getTrangThai() == 0 || hoaDon.getTrangThai() == 1;
    }
}
