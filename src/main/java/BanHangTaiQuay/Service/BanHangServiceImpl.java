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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        // Mã hóa đơn được sinh tuần tự trong BanHangDAOImpl khi lưu vào database.

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

            int tonKho = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
            if (soLuong > tonKho) {
                throw new IllegalStateException("Không đủ tồn kho, còn lại: " + tonKho);
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

            if (chiTiet != null) {
                int soLuongMoi = (chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong()) + soLuong;
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
    public void themNhieuSanPhamVaoGio(int idHoaDon, Map<Integer, Integer> sanPhamSoLuong) {
        validatePositiveId(idHoaDon, "ID hóa đơn");
        if (sanPhamSoLuong == null || sanPhamSoLuong.isEmpty()) {
            throw new IllegalArgumentException("Danh sách sản phẩm không được để trống.");
        }

        Map<Integer, Integer> danhSachHopLe = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : sanPhamSoLuong.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("ID sản phẩm chi tiết không hợp lệ.");
            }
            validatePositiveId(entry.getKey(), "ID sản phẩm chi tiết");
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException("Số lượng sản phẩm phải lớn hơn 0.");
            }
            danhSachHopLe.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        List<SanPhamChiTiet> sanPhamTuModule = sanPhamChiTietService
                .timTheoIds(new ArrayList<>(danhSachHopLe.keySet()));
        Map<Integer, SanPhamChiTiet> sanPhamModuleTheoId = new LinkedHashMap<>();
        for (SanPhamChiTiet spct : sanPhamTuModule) {
            sanPhamModuleTheoId.put(spct.getId(), spct);
        }
        for (Integer idSanPhamChiTiet : danhSachHopLe.keySet()) {
            SanPhamChiTiet spct = sanPhamModuleTheoId.get(idSanPhamChiTiet);
            if (spct == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại.");
            }
            if (spct.getTrangThai() != null && spct.getTrangThai() != 1) {
                throw new IllegalStateException("Sản phẩm đã ngừng kinh doanh.");
            }
        }

        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Map<Integer, SanPhamChiTiet> sanPhamDaKhoa = new LinkedHashMap<>();
            for (Map.Entry<Integer, Integer> entry : danhSachHopLe.entrySet()) {
                SanPhamChiTiet spct = em.find(
                        SanPhamChiTiet.class,
                        entry.getKey(),
                        LockModeType.PESSIMISTIC_WRITE
                );
                if (spct == null || Boolean.TRUE.equals(spct.getIsDeleted())) {
                    throw new IllegalArgumentException("Sản phẩm không tồn tại.");
                }
                if (spct.getTrangThai() != null && spct.getTrangThai() != 1) {
                    throw new IllegalStateException("Sản phẩm đã ngừng kinh doanh.");
                }
                int tonKho = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
                if (entry.getValue() > tonKho) {
                    throw new IllegalStateException("Không đủ tồn kho cho sản phẩm " + entry.getKey()
                            + ", còn lại: " + tonKho);
                }
                sanPhamDaKhoa.put(entry.getKey(), spct);
            }

            HoaDon hd = findHoaDonWithDetails(em, idHoaDon, true);
            if (hd == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }
            if (!laHoaDonDangChoThanhToan(hd)) {
                throw new IllegalStateException("Chỉ được thêm sản phẩm vào hóa đơn đang chờ thanh toán.");
            }
            if (hd.getChiTietHoaDons() == null) {
                hd.setChiTietHoaDons(new java.util.ArrayList<>());
            }

            for (Map.Entry<Integer, Integer> entry : danhSachHopLe.entrySet()) {
                Integer idSanPhamChiTiet = entry.getKey();
                int soLuong = entry.getValue();
                SanPhamChiTiet spct = sanPhamDaKhoa.get(idSanPhamChiTiet);
                ChiTietHoaDon chiTiet = hd.getChiTietHoaDons().stream()
                        .filter(ct -> ct.getSanPhamChiTiet() != null
                                && idSanPhamChiTiet.equals(ct.getSanPhamChiTiet().getId()))
                        .findFirst()
                        .orElse(null);

                if (spct.getGiaBan() == null && (chiTiet == null || chiTiet.getDonGia() == null)) {
                    throw new IllegalStateException("Sản phẩm chưa có giá bán.");
                }

                if (chiTiet == null) {
                    chiTiet = new ChiTietHoaDon();
                    chiTiet.setHoaDon(hd);
                    chiTiet.setSanPhamChiTiet(spct);
                    chiTiet.setSoLuong(soLuong);
                    chiTiet.setDonGia(spct.getGiaBan());
                    chiTiet.setGiaBanRa(spct.getGiaBan());
                    chiTiet.setTongTien(chiTiet.getDonGia().multiply(BigDecimal.valueOf(soLuong)));
                    hd.getChiTietHoaDons().add(chiTiet);
                    em.persist(chiTiet);
                } else {
                    if (chiTiet.getDonGia() == null) {
                        chiTiet.setDonGia(spct.getGiaBan());
                        chiTiet.setGiaBanRa(spct.getGiaBan());
                    }
                    int soLuongMoi = (chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong()) + soLuong;
                    chiTiet.setSoLuong(soLuongMoi);
                    chiTiet.setTongTien(chiTiet.getDonGia().multiply(BigDecimal.valueOf(soLuongMoi)));
                }

                int tonKho = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
                spct.setSoLuongTon(tonKho - soLuong);
            }

            capNhatTongTien(hd);
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
            int chenhLech = soLuongMoi - soLuongCu;

            if (chenhLech != 0) {
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
    public List<KhachHang> timKhachHangTheoTuKhoa(String tuKhoa) {
        return khachHangService.timKhachHangTheoTuKhoa(tuKhoa);
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
    public KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen,
                                             String email, LocalDate ngaySinh,
                                             Integer gioiTinh, String matKhau) {
        return khachHangService.traCuuHoacTaoKhachHang(
                soDienThoai, hoTen, email, ngaySinh, gioiTinh, matKhau
        );
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
            if (kh.getTrangThai() != null && kh.getTrangThai() != 1) {
                throw new IllegalStateException("Khách hàng đã ngừng hoạt động.");
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

            boolean daGanKhachHang = hd.getKhachHang() != null;
            hd.setKhachHang(null);
            if (daGanKhachHang) {
                ghiLichSu(em, hd, "CHON_KHACH_LE", "Chuyển hóa đơn sang khách lẻ");
            }
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
        if (soTienKhachDua == null || soTienKhachDua.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0.");
        }

        String maPtttChuanHoa = "TM".equalsIgnoreCase(maPttt.trim())
                ? "PTTT001"
                : maPttt.trim().toUpperCase();
        boolean thanhToanKhongTienMat = !"PTTT001".equals(maPtttChuanHoa);
            if (thanhToanKhongTienMat && (maGiaoDich == null || maGiaoDich.trim().isEmpty())) {
                throw new IllegalArgumentException("Thanh toán chuyển khoản/QR phải có mã giao dịch.");
            }

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
            BigDecimal tongTien = hd.getTongTienThanhToan() == null
                    ? BigDecimal.ZERO
                    : hd.getTongTienThanhToan();
            if (thanhToanKhongTienMat && soTienKhachDua.compareTo(tongTien) != 0) {
                throw new IllegalArgumentException("Số tiền chuyển khoản phải đúng bằng tổng tiền hóa đơn.");
            }
            if (!thanhToanKhongTienMat && soTienKhachDua.compareTo(tongTien) < 0) {
                throw new IllegalArgumentException("Số tiền khách đưa chưa đủ.");
            }

            if (thanhToanKhongTienMat) {
                Long soLanTrungMa = em.createQuery(
                                "SELECT COUNT(t) FROM ThanhToanHoaDon t WHERE t.maGiaoDich = :maGiaoDich",
                                Long.class)
                        .setParameter("maGiaoDich", maGiaoDich.trim())
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
            tt.setMaGiaoDich(normalizeText(maGiaoDich));
            String ghiChuThanhToan = normalizeText(ghiChu);
            if (!thanhToanKhongTienMat && ghiChuThanhToan == null) {
                BigDecimal tienThoi = soTienKhachDua.subtract(tongTien);
                ghiChuThanhToan = "Tiền mặt tại quầy; Khách đưa: " + soTienKhachDua
                        + "; Tiền thối: " + tienThoi;
            }
            tt.setGhiChu(ghiChuThanhToan);
            tt.setThoiGian(LocalDateTime.now());
            tt.setTrangThai(1);
            em.persist(tt);

            // Đồng bộ lịch sử để màn hình chi tiết hóa đơn hiển thị giao dịch POS.
            LichSuThanhToan lichSuThanhToan = new LichSuThanhToan();
            lichSuThanhToan.setHoaDon(hd);
            lichSuThanhToan.setSoTien(tongTien);
            lichSuThanhToan.setPhuongThucThanhToan(
                    normalizeText(pttt.getTenPttt()) == null
                            ? maPtttChuanHoa
                            : pttt.getTenPttt().trim());
            lichSuThanhToan.setTrangThaiThanhToan(1);
            lichSuThanhToan.setNgayThanhToan(LocalDateTime.now());
            lichSuThanhToan.setGhiChu(ghiChuThanhToan);
            em.persist(lichSuThanhToan);

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
            return findHoaDonWithDetails(em, idHoaDon);
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
                                + "WHERE h.id = :idHoaDon",
                        HoaDon.class)
                .setParameter("idHoaDon", idHoaDon);
        if (lock) {
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }
        return query.getResultStream()
                .findFirst()
                .orElse(null);
    }

    // Dữ liệu cũ dùng 1, POS mới tạo đơn dùng 0 cho trạng thái đang chờ.
    private boolean laHoaDonDangChoThanhToan(HoaDon hoaDon) {
        if (hoaDon == null || hoaDon.getTrangThai() == null) {
            return false;
        }
        return hoaDon.getTrangThai() == 0 || hoaDon.getTrangThai() == 1;
    }
}
