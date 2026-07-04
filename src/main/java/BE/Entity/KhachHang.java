package BE.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "khach_hang")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_khach_hang")
    private String maKhachHang;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "email")
    private String email;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "mat_khau")
    private String matKhau;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh")
    private Integer gioiTinh;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "khachHang")
    private List<DiaChiKhachHang> diaChiKhachHangs;

    @OneToMany(mappedBy = "khachHang")
    private List<HoaDon> hoaDons;

    @OneToMany(mappedBy = "khachHang")
    private List<KhachHangPhieuGiamGia> khachHangPhieuGiamGias;

    public KhachHang() {}

    public KhachHang(Integer id, String maKhachHang, String hoTen, String email, String soDienThoai, String matKhau, LocalDate ngaySinh, Integer gioiTinh, Integer trangThai, List<DiaChiKhachHang> diaChiKhachHangs, List<HoaDon> hoaDons, List<KhachHangPhieuGiamGia> khachHangPhieuGiamGias) {
        this.id = id;
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.matKhau = matKhau;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.trangThai = trangThai;
        this.diaChiKhachHangs = diaChiKhachHangs;
        this.hoaDons = hoaDons;
        this.khachHangPhieuGiamGias = khachHangPhieuGiamGias;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public Integer getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(Integer gioiTinh) { this.gioiTinh = gioiTinh; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<DiaChiKhachHang> getDiaChiKhachHangs() { return diaChiKhachHangs; }
    public void setDiaChiKhachHangs(List<DiaChiKhachHang> diaChiKhachHangs) { this.diaChiKhachHangs = diaChiKhachHangs; }
    public List<HoaDon> getHoaDons() { return hoaDons; }
    public void setHoaDons(List<HoaDon> hoaDons) { this.hoaDons = hoaDons; }
    public List<KhachHangPhieuGiamGia> getKhachHangPhieuGiamGias() { return khachHangPhieuGiamGias; }
    public void setKhachHangPhieuGiamGias(List<KhachHangPhieuGiamGia> khachHangPhieuGiamGias) { this.khachHangPhieuGiamGias = khachHangPhieuGiamGias; }
}