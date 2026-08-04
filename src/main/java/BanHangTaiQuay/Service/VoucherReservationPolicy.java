package BanHangTaiQuay.Service;

/**
 * Quy tắc phân bổ số lượt voucher giữa hóa đơn đã hoàn tất và hóa đơn đang chờ.
 */
public final class VoucherReservationPolicy {

    private VoucherReservationPolicy() {
    }

    public static boolean canReserve(Integer totalQuantity, long currentHolders) {
        int total = totalQuantity == null ? 0 : Math.max(0, totalQuantity);
        return currentHolders < total;
    }

    public static int waitingSlots(Integer totalQuantity, long permanentHolders) {
        int total = totalQuantity == null ? 0 : Math.max(0, totalQuantity);
        long remaining = Math.max(0L, total - Math.max(0L, permanentHolders));
        return remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) remaining;
    }

    public static boolean mayKeepReservation(int reservationIndex, int waitingSlots) {
        return reservationIndex >= 0 && reservationIndex < Math.max(0, waitingSlots);
    }
}
