package BanHangTaiQuay.Test;

import BanHangTaiQuay.Model.ApVoucherRequest;
import BanHangTaiQuay.Model.CapNhatSoLuongRequest;
import BanHangTaiQuay.Model.ChonKhachHangRequest;
import BanHangTaiQuay.Model.HoaDonCreateRequest;
import BanHangTaiQuay.Model.ThanhToanRequest;
import BanHangTaiQuay.Model.ThemSanPhamRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoContractTest {

    @Test
    void themSanPhamRequestLuuDungDuLieu() {
        ThemSanPhamRequest request = new ThemSanPhamRequest();
        request.setIdHoaDon(10);
        request.setIdSanPhamChiTiet(20);
        request.setSoLuong(2);

        assertEquals(10, request.getIdHoaDon());
        assertEquals(20, request.getIdSanPhamChiTiet());
        assertEquals(2, request.getSoLuong());
    }

    @Test
    void cacRequestConLaiLuuDungDuLieu() {
        CapNhatSoLuongRequest quantityRequest = new CapNhatSoLuongRequest();
        quantityRequest.setIdChiTiet(3);
        quantityRequest.setSoLuongMoi(4);

        ChonKhachHangRequest customerRequest = new ChonKhachHangRequest();
        customerRequest.setSoDienThoai("0900000000");
        customerRequest.setHoTen("Khách hàng test");

        HoaDonCreateRequest invoiceRequest = new HoaDonCreateRequest();
        invoiceRequest.setIdNhanVien(1);
        invoiceRequest.setIdCa(2);

        ApVoucherRequest voucherRequest = new ApVoucherRequest();
        voucherRequest.setIdHoaDon(10);
        voucherRequest.setMaVoucher("TEST10");

        ThanhToanRequest paymentRequest = new ThanhToanRequest();
        paymentRequest.setIdHoaDon(10);
        paymentRequest.setMaPttt("TM");
        paymentRequest.setSoTienKhachDua(new BigDecimal("100000"));
        paymentRequest.setMaGiaoDich("FT260722123456");
        paymentRequest.setGhiChu("Thanh toán bằng QR");

        assertEquals(3, quantityRequest.getIdChiTiet());
        assertEquals(4, quantityRequest.getSoLuongMoi());
        assertEquals("0900000000", customerRequest.getSoDienThoai());
        assertEquals("Khách hàng test", customerRequest.getHoTen());
        assertEquals(1, invoiceRequest.getIdNhanVien());
        assertEquals(2, invoiceRequest.getIdCa());
        assertEquals("TEST10", voucherRequest.getMaVoucher());
        assertEquals("TM", paymentRequest.getMaPttt());
        assertEquals(new BigDecimal("100000"), paymentRequest.getSoTienKhachDua());
        assertEquals("FT260722123456", paymentRequest.getMaGiaoDich());
        assertEquals("Thanh toán bằng QR", paymentRequest.getGhiChu());
    }
}
