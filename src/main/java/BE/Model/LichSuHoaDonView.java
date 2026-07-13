package BE.Model;

import java.time.LocalDateTime;

// Chua thong tin 1 dong lich su xu ly hoa don.
public class LichSuHoaDonView {
    private String hanhDong;
    private LocalDateTime ngayTao;
    private String ghiChu;

    public String getHanhDong() {
        return hanhDong;
    }

    public void setHanhDong(String hanhDong) {
        this.hanhDong = hanhDong;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
