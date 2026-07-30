package BanHangTaiQuay.Model;

/**
 * Dữ liệu bổ sung dùng để hiển thị thông tin hóa đơn tại màn hình bán hàng.
 */
public class HoaDonResponse {

    private String moTaGiamGia;

    public HoaDonResponse() {
    }

    public HoaDonResponse(String moTaGiamGia) {
        this.moTaGiamGia = moTaGiamGia;
    }

    public String getMoTaGiamGia() {
        return moTaGiamGia;
    }

    public void setMoTaGiamGia(String moTaGiamGia) {
        this.moTaGiamGia = moTaGiamGia;
    }
}
