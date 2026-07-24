package QuanLyHoaDon.Model;

import java.math.BigDecimal;

public class ThongKeVoucher {
    private String maVoucher;
    private int soDon;
    private BigDecimal tongGiam = BigDecimal.ZERO;

    public String getMaVoucher() {
        return maVoucher;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher;
    }

    public int getSoDon() {
        return soDon;
    }

    public void setSoDon(int soDon) {
        this.soDon = soDon;
    }

    public BigDecimal getTongGiam() {
        return tongGiam;
    }

    public void setTongGiam(BigDecimal tongGiam) {
        this.tongGiam = tongGiam == null ? BigDecimal.ZERO : tongGiam;
    }
}
