package BE.Model;

import java.math.BigDecimal;

// Chứa dữ liệu 1 dòng sản phẩm trong chi tiết hóa đơn để hiển thị lên JSP.
public class ChiTietHoaDonView {
    private String tenSanPham;
    private String maSanPhamChiTiet;
    private String danhMuc;
    private String thuongHieu;
    private String chatLieu;
    private String kieuDang;
    private String hinhDangGong;
    private String kieuQuaiKinh;
    private String loaiTrong;
    private String mauSac;
    private String kichCo;
    private String hinhAnhSanPham;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal tongTien;

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getMaSanPhamChiTiet() {
        return maSanPhamChiTiet;
    }

    public void setMaSanPhamChiTiet(String maSanPhamChiTiet) {
        this.maSanPhamChiTiet = maSanPhamChiTiet;
    }

    public String getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(String danhMuc) {
        this.danhMuc = danhMuc;
    }

    public String getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(String thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public String getChatLieu() {
        return chatLieu;
    }

    public void setChatLieu(String chatLieu) {
        this.chatLieu = chatLieu;
    }

    public String getKieuDang() {
        return kieuDang;
    }

    public void setKieuDang(String kieuDang) {
        this.kieuDang = kieuDang;
    }

    public String getHinhDangGong() {
        return hinhDangGong;
    }

    public void setHinhDangGong(String hinhDangGong) {
        this.hinhDangGong = hinhDangGong;
    }

    public String getKieuQuaiKinh() {
        return kieuQuaiKinh;
    }

    public void setKieuQuaiKinh(String kieuQuaiKinh) {
        this.kieuQuaiKinh = kieuQuaiKinh;
    }

    public String getLoaiTrong() {
        return loaiTrong;
    }

    public void setLoaiTrong(String loaiTrong) {
        this.loaiTrong = loaiTrong;
    }

    public String getMauSac() {
        return mauSac;
    }

    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }

    public String getKichCo() {
        return kichCo;
    }

    public void setKichCo(String kichCo) {
        this.kichCo = kichCo;
    }

    public String getHinhAnhSanPham() {
        return hinhAnhSanPham;
    }

    public void setHinhAnhSanPham(String hinhAnhSanPham) {
        this.hinhAnhSanPham = hinhAnhSanPham;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }
}

