package BanHangTaiQuay.Model;

import java.math.BigDecimal;

public class ThanhToanRequest {
    private int idHoaDon;
    private String maPttt;
    private BigDecimal soTienKhachDua;
    private String maGiaoDich;
    private String ghiChu;

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

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
