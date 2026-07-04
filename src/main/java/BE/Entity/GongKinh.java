package BE.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gong_kinh")
public class GongKinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hinh_dang_gong")
    private HinhDangGong hinhDangGong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kieu_quai_kinh")
    private KieuQuaiKinh kieuQuaiKinh;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "gongKinh")
    private List<SanPham> sanPhams;

    public GongKinh() {}

    public GongKinh(Integer id, HinhDangGong hinhDangGong, KieuQuaiKinh kieuQuaiKinh, Integer trangThai, List<SanPham> sanPhams) {
        this.id = id;
        this.hinhDangGong = hinhDangGong;
        this.kieuQuaiKinh = kieuQuaiKinh;
        this.trangThai = trangThai;
        this.sanPhams = sanPhams;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public HinhDangGong getHinhDangGong() { return hinhDangGong; }
    public void setHinhDangGong(HinhDangGong hinhDangGong) { this.hinhDangGong = hinhDangGong; }
    public KieuQuaiKinh getKieuQuaiKinh() { return kieuQuaiKinh; }
    public void setKieuQuaiKinh(KieuQuaiKinh kieuQuaiKinh) { this.kieuQuaiKinh = kieuQuaiKinh; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<SanPham> getSanPhams() { return sanPhams; }
    public void setSanPhams(List<SanPham> sanPhams) { this.sanPhams = sanPhams; }
}