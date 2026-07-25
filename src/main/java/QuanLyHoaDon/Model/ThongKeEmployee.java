package QuanLyHoaDon.Model;

import java.math.BigDecimal;

public class ThongKeEmployee {
    private String tenNhanVien;
    private int soDon;
    private BigDecimal doanhThu = BigDecimal.ZERO;

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public int getSoDon() {
        return soDon;
    }

    public void setSoDon(int soDon) {
        this.soDon = soDon;
    }

    public BigDecimal getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(BigDecimal doanhThu) {
        this.doanhThu = doanhThu == null ? BigDecimal.ZERO : doanhThu;
    }
}
