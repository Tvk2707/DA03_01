package BanHangTaiQuay.Test;

import BanHangTaiQuay.Service.VoucherReservationPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VoucherReservationPolicyTest {

    @Test
    void voucherConMotLuotChiChoMotHoaDonGiu() {
        assertTrue(VoucherReservationPolicy.canReserve(1, 0));
        assertFalse(VoucherReservationPolicy.canReserve(1, 1));
    }

    @Test
    void traLuotChoPhepHoaDonKhacGiuVoucher() {
        assertFalse(VoucherReservationPolicy.canReserve(1, 1));
        assertTrue(VoucherReservationPolicy.canReserve(1, 0));
    }

    @Test
    void hoaDonHoanTatDuocTinhTruocHoaDonCho() {
        assertEquals(1, VoucherReservationPolicy.waitingSlots(2, 1));
        assertEquals(0, VoucherReservationPolicy.waitingSlots(1, 1));
    }

    @Test
    void hoaDonApTruocDuocGiuVaHoaDonApSauBiLoai() {
        assertTrue(VoucherReservationPolicy.mayKeepReservation(0, 1));
        assertFalse(VoucherReservationPolicy.mayKeepReservation(1, 1));
    }
}
