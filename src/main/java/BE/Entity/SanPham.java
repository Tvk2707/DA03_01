package BE.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "san_pham")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc")
    private DanhMuc danhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thuong_hieu")
    private ThuongHieu thuongHieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_lieu")
    private ChatLieu chatLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kieu_dang")
    private KieuDang kieuDang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gong_kinh")
    private GongKinh gongKinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_trong_kinh")
    private TrongKinh trongKinh;

    @Column(name = "ma_san_pham")
    private String maSanPham;

    @Column(name = "ten_san_pham")
    private String tenSanPham;

    @Column(name = "mo_ta_chi_tiet")
    private String moTaChiTiet;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_sua")
    private LocalDateTime ngaySua;

    @Column(name = "trang_thai")
    private Integer trangThai;

    // --- THÊM THUỘC TÍNH XÓA MỀM ---
    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted = false;
    // --------------------------------

    @OneToMany(mappedBy = "sanPham")
    private List<SanPhamChiTiet> sanPhamChiTiets;

    @OneToMany(mappedBy = "sanPham")
    private List<HinhAnhSanPham> hinhAnhSanPhams;

    public SanPham() {}

    public SanPham(Integer id, DanhMuc danhMuc, ThuongHieu thuongHieu, ChatLieu chatLieu, KieuDang kieuDang, GongKinh gongKinh, TrongKinh trongKinh, String maSanPham, String tenSanPham, String moTaChiTiet, LocalDateTime ngayTao, LocalDateTime ngaySua, Integer trangThai, List<SanPhamChiTiet> sanPhamChiTiets, List<HinhAnhSanPham> hinhAnhSanPhams) {
        this.id = id;
        this.danhMuc = danhMuc;
        this.thuongHieu = thuongHieu;
        this.chatLieu = chatLieu;
        this.kieuDang = kieuDang;
        this.gongKinh = gongKinh;
        this.trongKinh = trongKinh;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.moTaChiTiet = moTaChiTiet;
        this.ngayTao = ngayTao;
        this.ngaySua = ngaySua;
        this.trangThai = trangThai;
        this.sanPhamChiTiets = sanPhamChiTiets;
        this.hinhAnhSanPhams = hinhAnhSanPhams;
        // isDeleted mặc định là false, không cần truyền vào constructor này để tránh vỡ code cũ
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public DanhMuc getDanhMuc() { return danhMuc; }
    public void setDanhMuc(DanhMuc danhMuc) { this.danhMuc = danhMuc; }
    public ThuongHieu getThuongHieu() { return thuongHieu; }
    public void setThuongHieu(ThuongHieu thuongHieu) { this.thuongHieu = thuongHieu; }
    public ChatLieu getChatLieu() { return chatLieu; }
    public void setChatLieu(ChatLieu chatLieu) { this.chatLieu = chatLieu; }
    public KieuDang getKieuDang() { return kieuDang; }
    public void setKieuDang(KieuDang kieuDang) { this.kieuDang = kieuDang; }
    public GongKinh getGongKinh() { return gongKinh; }
    public void setGongKinh(GongKinh gongKinh) { this.gongKinh = gongKinh; }
    public TrongKinh getTrongKinh() { return trongKinh; }
    public void setTrongKinh(TrongKinh trongKinh) { this.trongKinh = trongKinh; }
    public String getMaSanPham() { return maSanPham; }
    public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }
    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
    public String getMoTaChiTiet() { return moTaChiTiet; }
    public void setMoTaChiTiet(String moTaChiTiet) { this.moTaChiTiet = moTaChiTiet; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public LocalDateTime getNgaySua() { return ngaySua; }
    public void setNgaySua(LocalDateTime ngaySua) { this.ngaySua = ngaySua; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<SanPhamChiTiet> getSanPhamChiTiets() { return sanPhamChiTiets; }
    public void setSanPhamChiTiets(List<SanPhamChiTiet> sanPhamChiTiets) { this.sanPhamChiTiets = sanPhamChiTiets; }
    public List<HinhAnhSanPham> getHinhAnhSanPhams() { return hinhAnhSanPhams; }
    public void setHinhAnhSanPhams(List<HinhAnhSanPham> hinhAnhSanPhams) { this.hinhAnhSanPhams = hinhAnhSanPhams; }

    // --- GETTER / SETTER CHO XÓA MỀM ---
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    // -----------------------------------
}