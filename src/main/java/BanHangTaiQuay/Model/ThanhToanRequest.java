package BanHangTaiQuay.Model;

import java.math.BigDecimal;

public class ThanhToanRequest {
    private int idHoaDon;
    private String maPttt;
    private BigDecimal soTienKhachDua;

    public int getIdHoaDon() {
        return idHoaDon;
    }

    public void setIdHoaDon(int idHoaDon) {
        this.idHoaDon = idHoaDon;
    }

    public String getMaPttt() {
        return maPttt;
    }

    public void setMaPttt(String maPttt) {
        this.maPttt = maPttt;
    }

    public BigDecimal getSoTienKhachDua() {
        return soTienKhachDua;
    }

    public void setSoTienKhachDua(BigDecimal soTienKhachDua) {
        this.soTienKhachDua = soTienKhachDua;
    }
}
