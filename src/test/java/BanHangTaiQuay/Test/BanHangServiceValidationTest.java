package BanHangTaiQuay.Test;

import BanHangTaiQuay.Service.BanHangService;
import BanHangTaiQuay.Service.BanHangServiceImpl;
import BanHangTaiQuay.Dao.BanHangDAO;
import QuanLySanPham.Entity.ChiTietHoaDon;
import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.LichSuHoaDon;
import QuanLySanPham.Entity.ThanhToanHoaDon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.util.List;

class BanHangServiceValidationTest {

    private BanHangService service;

    @BeforeEach
    void setUp() {
        service = new BanHangServiceImpl();
    }

    @Test
    void khongChoPhepTaoHoaDonKhiCaKhongHopLe() {
        assertThrows(IllegalArgumentException.class, () -> service.taoHoaDonMoi(1, 0));
    }

    @Test
    void khongChoPhepTaoHoaDonKhiChuaDangNhap() {
        assertThrows(IllegalArgumentException.class, () -> service.taoHoaDonMoi(null, null));
    }

    @Test
    void khongChoPhepTaoQuaMuoiHoaDonCho() {
        BanHangDAO daoCoMuoiHoaDonCho = new FakeBanHangDAO(10);
        BanHangService serviceCoGioiHan = new BanHangServiceImpl(daoCoMuoiHoaDonCho);

        assertThrows(IllegalStateException.class, () -> serviceCoGioiHan.taoHoaDonMoi(1, 1));
    }

    @Test
    void khongChoPhepThemSoLuongKhongDuong() {
        assertThrows(IllegalArgumentException.class, () -> service.themSanPhamVaoGio(1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> service.themSanPhamVaoGio(1, 1, -1));
    }

    @Test
    void khongChoPhepThemSanPhamVoiIdKhongHopLe() {
        assertThrows(IllegalArgumentException.class, () -> service.themSanPhamVaoGio(0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> service.themSanPhamVaoGio(1, 0, 1));
    }

    @Test
    void khongChoPhepXoaSanPhamVoiIdKhongHopLe() {
        assertThrows(IllegalArgumentException.class, () -> service.xoaSanPhamKhoiGio(0, 1));
        assertThrows(IllegalArgumentException.class, () -> service.xoaSanPhamKhoiGio(1, 0));
    }

    @Test
    void khongChoPhepCapNhatSoLuongKhongDuong() {
        assertThrows(IllegalArgumentException.class, () -> service.capNhatSoLuong(1, 0));
        assertThrows(IllegalArgumentException.class, () -> service.capNhatSoLuong(1, -1));
    }

    @Test
    void khongChoPhepCapNhatVoiIdChiTietKhongHopLe() {
        assertThrows(IllegalArgumentException.class, () -> service.capNhatSoLuong(0, 1));
    }

    @Test
    void khongChoPhepGoVoucherVoiHoaDonKhongHopLe() {
        assertThrows(IllegalArgumentException.class, () -> service.goVoucher(0));
    }

    private static final class FakeBanHangDAO implements BanHangDAO {
        private final long soHoaDonCho;

        private FakeBanHangDAO(long soHoaDonCho) {
            this.soHoaDonCho = soHoaDonCho;
        }

        @Override
        public HoaDon insertHoaDon(HoaDon hd) { return hd; }

        @Override
        public void updateHoaDon(HoaDon hd) { }

        @Override
        public HoaDon findHoaDonById(int id) { return null; }

        @Override
        public long demHoaDonCho(int idNhanVien) { return soHoaDonCho; }

        @Override
        public List<HoaDon> layDanhSachHoaDonCho(int idNhanVien) { return List.of(); }

        @Override
        public Integer timCaDangMo(int idNhanVien) { return null; }

        @Override
        public ChiTietHoaDon insertChiTietHoaDon(ChiTietHoaDon ct) { return ct; }

        @Override
        public void updateChiTietHoaDon(ChiTietHoaDon ct) { }

        @Override
        public void deleteChiTietHoaDon(ChiTietHoaDon ct) { }

        @Override
        public ChiTietHoaDon findByHoaDonVaSpct(int idHoaDon, int idSanPhamChiTiet) { return null; }

        @Override
        public ChiTietHoaDon findChiTietHoaDonById(int id) { return null; }

        @Override
        public ThanhToanHoaDon insertThanhToan(ThanhToanHoaDon tt) { return tt; }

        @Override
        public LichSuHoaDon insertLichSu(LichSuHoaDon ls) { return ls; }
    }
}
