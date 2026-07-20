package QuanLyHoaDon.Model;

import java.math.BigDecimal;

public class ThongKeCustomer {
    private String tenKhachHang;
    private int soDon;
    private BigDecimal tongChiTieu = BigDecimal.ZERO;

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public int getSoDon() {
        return soDon;
    }

    public void setSoDon(int soDon) {
        this.soDon = soDon;
    }

    public BigDecimal getTongChiTieu() {
        return tongChiTieu;
    }

    public void setTongChiTieu(BigDecimal tongChiTieu) {
        this.tongChiTieu = tongChiTieu == null ? BigDecimal.ZERO : tongChiTieu;
    }
}
