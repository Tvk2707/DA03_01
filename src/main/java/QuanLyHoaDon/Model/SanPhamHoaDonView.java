package QuanLyHoaDon.Model;

import java.math.BigDecimal;

// Dữ liệu sản phẩm chi tiết được lấy từ database để chọn khi thêm hóa đơn.
public class SanPhamHoaDonView {
    private Integer id;
    private String ma;
    private String tenSanPham;
    private BigDecimal giaBan;
    private Integer soLuongTon;
    private String hinhAnh;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }
    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
    public BigDecimal getGiaBan() { return giaBan; }
    public void setGiaBan(BigDecimal giaBan) { this.giaBan = giaBan; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
}
