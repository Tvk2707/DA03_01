package main.QuanLySanPham.BE.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    @Column(name = "ma_hoa_don")
    private String maHoaDon;

    @Column(name = "ten_nguoi_nhan")
    private String tenNguoiNhan;

    @Column(name = "sdt_nguoi_nhan")
    private String sdtNguoiNhan;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "tong_tien_thanh_toan")
    private BigDecimal tongTienThanhToan;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "ngay_nhan_ban_dau")
    private LocalDateTime ngayNhanBanDau;

    @Column(name = "ngay_nhan")
    private LocalDateTime ngayNhan;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "ly_do_huy")
    private String lyDoHuy;

    @OneToMany(mappedBy = "hoaDon")
    private List<ChiTietHoaDon> chiTietHoaDons;

    @OneToMany(mappedBy = "hoaDon")
    private List<ThanhToanHoaDon> thanhToanHoaDons;

    @OneToMany(mappedBy = "hoaDon")
    private List<LichSuHoaDon> lichSuHoaDons;

    @OneToMany(mappedBy = "hoaDon")
    private List<LichSuThanhToan> lichSuThanhToans;

    public HoaDon() {}

    public HoaDon(Integer id, KhachHang khachHang, NhanVien nhanVien, PhieuGiamGia phieuGiamGia, String maHoaDon, String tenNguoiNhan, String sdtNguoiNhan, LocalDateTime ngayTao, LocalDateTime ngayThanhToan, BigDecimal tongTienThanhToan, Integer trangThai, LocalDateTime ngayNhanBanDau, LocalDateTime ngayNhan, String ghiChu, String lyDoHuy, List<ChiTietHoaDon> chiTietHoaDons, List<ThanhToanHoaDon> thanhToanHoaDons, List<LichSuHoaDon> lichSuHoaDons, List<LichSuThanhToan> lichSuThanhToans) {
        this.id = id;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
        this.phieuGiamGia = phieuGiamGia;
        this.maHoaDon = maHoaDon;
        this.tenNguoiNhan = tenNguoiNhan;
        this.sdtNguoiNhan = sdtNguoiNhan;
        this.ngayTao = ngayTao;
        this.ngayThanhToan = ngayThanhToan;
        this.tongTienThanhToan = tongTienThanhToan;
        this.trangThai = trangThai;
        this.ngayNhanBanDau = ngayNhanBanDau;
        this.ngayNhan = ngayNhan;
        this.ghiChu = ghiChu;
        this.lyDoHuy = lyDoHuy;
        this.chiTietHoaDons = chiTietHoaDons;
        this.thanhToanHoaDons = thanhToanHoaDons;
        this.lichSuHoaDons = lichSuHoaDons;
        this.lichSuThanhToans = lichSuThanhToans;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
    public PhieuGiamGia getPhieuGiamGia() { return phieuGiamGia; }
    public void setPhieuGiamGia(PhieuGiamGia phieuGiamGia) { this.phieuGiamGia = phieuGiamGia; }
    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    public String getTenNguoiNhan() { return tenNguoiNhan; }
    public void setTenNguoiNhan(String tenNguoiNhan) { this.tenNguoiNhan = tenNguoiNhan; }
    public String getSdtNguoiNhan() { return sdtNguoiNhan; }
    public void setSdtNguoiNhan(String sdtNguoiNhan) { this.sdtNguoiNhan = sdtNguoiNhan; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public LocalDateTime getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(LocalDateTime ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }
    public BigDecimal getTongTienThanhToan() { return tongTienThanhToan; }
    public void setTongTienThanhToan(BigDecimal tongTienThanhToan) { this.tongTienThanhToan = tongTienThanhToan; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public LocalDateTime getNgayNhanBanDau() { return ngayNhanBanDau; }
    public void setNgayNhanBanDau(LocalDateTime ngayNhanBanDau) { this.ngayNhanBanDau = ngayNhanBanDau; }
    public LocalDateTime getNgayNhan() { return ngayNhan; }
    public void setNgayNhan(LocalDateTime ngayNhan) { this.ngayNhan = ngayNhan; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public String getLyDoHuy() { return lyDoHuy; }
    public void setLyDoHuy(String lyDoHuy) { this.lyDoHuy = lyDoHuy; }
    public List<ChiTietHoaDon> getChiTietHoaDons() { return chiTietHoaDons; }
    public void setChiTietHoaDons(List<ChiTietHoaDon> chiTietHoaDons) { this.chiTietHoaDons = chiTietHoaDons; }
    public List<ThanhToanHoaDon> getThanhToanHoaDons() { return thanhToanHoaDons; }
    public void setThanhToanHoaDons(List<ThanhToanHoaDon> thanhToanHoaDons) { this.thanhToanHoaDons = thanhToanHoaDons; }
    public List<LichSuHoaDon> getLichSuHoaDons() { return lichSuHoaDons; }
    public void setLichSuHoaDons(List<LichSuHoaDon> lichSuHoaDons) { this.lichSuHoaDons = lichSuHoaDons; }
    public List<LichSuThanhToan> getLichSuThanhToans() { return lichSuThanhToans; }
    public void setLichSuThanhToans(List<LichSuThanhToan> lichSuThanhToans) { this.lichSuThanhToans = lichSuThanhToans; }
}