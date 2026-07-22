package BanHangTaiQuay.Test;

import BanHangTaiQuay.Service.BanHangService;
import BanHangTaiQuay.Service.BanHangServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;

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
    void khongChoPhepThemSoLuongKhongDuong() {
        assertThrows(IllegalArgumentException.class, () -> service.themSanPhamVaoGio(1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> service.themSanPhamVaoGio(1, 1, -1));
    }

    @Test
    void khongChoPhepCapNhatSoLuongKhongDuong() {
        assertThrows(IllegalArgumentException.class, () -> service.capNhatSoLuong(1, 0));
        assertThrows(IllegalArgumentException.class, () -> service.capNhatSoLuong(1, -1));
    }

    @Test
    void thanhToanChuyenKhoanBatBuocCoMaGiaoDich() {
        assertThrows(IllegalArgumentException.class, () -> service.xacNhanThanhToan(
                1, "PTTT002", new BigDecimal("100000"), "", "Thanh toán QR"));
    }

    @Test
    void thanhToanKhongChoPhepSoTienKhongDuong() {
        assertThrows(IllegalArgumentException.class, () -> service.xacNhanThanhToan(
                1, "PTTT002", BigDecimal.ZERO, "FT123", null));
    }
}
