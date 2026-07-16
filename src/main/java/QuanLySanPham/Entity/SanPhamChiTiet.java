package QuanLySanPham.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "san_pham_chi_tiet")
public class SanPhamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham")
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_sac")
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_co")
    private KichCo kichCo;

    @Column(name = "ma")
    private String ma;

    @Column(name = "gia_nhap")
    private BigDecimal giaNhap;

    @Column(name = "gia_ban")
    private BigDecimal giaBan;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon;

    @Column(name = "trong_luong")
    private Integer trongLuong;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "trang_thai")
    private Integer trangThai;

    // --- THÊM THUỘC TÍNH XÓA MỀM ---
    @Column(name = "isDeleted")
    private Boolean isDeleted = false;
    // --------------------------------

    @OneToMany(mappedBy = "sanPhamChiTiet")
    private List<ChiTietHoaDon> chiTietHoaDons;

    public SanPhamChiTiet() {}

    // Constructor all-args giữ nguyên, không thêm isDeleted để tránh vỡ code cũ
    public SanPhamChiTiet(Integer id, SanPham sanPham, MauSac mauSac, KichCo kichCo, String ma, BigDecimal giaNhap, BigDecimal giaBan, Integer soLuongTon, Integer trongLuong, String hinhAnh, Integer trangThai, List<ChiTietHoaDon> chiTietHoaDons) {
        this.id = id;
        this.sanPham = sanPham;
        this.mauSac = mauSac;
        this.kichCo = kichCo;
        this.ma = ma;
        this.giaNhap = giaNhap;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.trongLuong = trongLuong;
        this.hinhAnh = hinhAnh;
        this.trangThai = trangThai;
        this.chiTietHoaDons = chiTietHoaDons;
    }

    // Getter/Setter giữ nguyên
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }
    public MauSac getMauSac() { return mauSac; }
    public void setMauSac(MauSac mauSac) { this.mauSac = mauSac; }
    public KichCo getKichCo() { return kichCo; }
    public void setKichCo(KichCo kichCo) { this.kichCo = kichCo; }
    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }
    public BigDecimal getGiaNhap() { return giaNhap; }
    public void setGiaNhap(BigDecimal giaNhap) { this.giaNhap = giaNhap; }
    public BigDecimal getGiaBan() { return giaBan; }
    public void setGiaBan(BigDecimal giaBan) { this.giaBan = giaBan; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }
    public Integer getTrongLuong() { return trongLuong; }
    public void setTrongLuong(Integer trongLuong) { this.trongLuong = trongLuong; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<ChiTietHoaDon> getChiTietHoaDons() { return chiTietHoaDons; }
    public void setChiTietHoaDons(List<ChiTietHoaDon> chiTietHoaDons) { this.chiTietHoaDons = chiTietHoaDons; }

    // --- GETTER / SETTER CHO XÓA MỀM ---
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    // -----------------------------------
}