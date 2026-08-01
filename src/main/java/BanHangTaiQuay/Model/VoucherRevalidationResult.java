package BanHangTaiQuay.Model;

import java.math.BigDecimal;

/**
 * Kết quả kiểm tra lại voucher trước khi cập nhật hóa đơn hoặc thanh toán.
 */
public class VoucherRevalidationResult {

    private boolean voucherRemoved;
    private boolean discountChanged;
    private String action;
    private String message;
    private String maVoucher;
    private BigDecimal tienGiamCu;
    private BigDecimal tienGiamMoi;

    public VoucherRevalidationResult() {
    }

    public VoucherRevalidationResult(boolean voucherRemoved, boolean discountChanged,
                                     String action, String message, String maVoucher,
                                     BigDecimal tienGiamCu, BigDecimal tienGiamMoi) {
        this.voucherRemoved = voucherRemoved;
        this.discountChanged = discountChanged;
        this.action = action;
        this.message = message;
        this.maVoucher = maVoucher;
        this.tienGiamCu = tienGiamCu;
        this.tienGiamMoi = tienGiamMoi;
    }

    public static VoucherRevalidationResult unchanged(String maVoucher, BigDecimal tienGiam) {
        return new VoucherRevalidationResult(false, false, "", "", maVoucher, tienGiam, tienGiam);
    }

    public boolean isVoucherRemoved() { return voucherRemoved; }
    public boolean isDiscountChanged() { return discountChanged; }
    public String getAction() { return action; }
    public String getMessage() { return message; }
    public String getMaVoucher() { return maVoucher; }
    public BigDecimal getTienGiamCu() { return tienGiamCu; }
    public BigDecimal getTienGiamMoi() { return tienGiamMoi; }
}
