package BE.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Chứa dữ liệu từ bảng lich_su_thanh_toan để hiển thị ở trang chi tiết hóa đơn.
public class LichSuThanhToanView {
    private BigDecimal soTien;
    private String phuongThucThanhToan;
    private Integer trangThaiThanhToan;
    private LocalDateTime ngayThanhToan;
    private String ghiChu;

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public Integer getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(Integer trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> HOA_DON
