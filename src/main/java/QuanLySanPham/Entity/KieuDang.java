package QuanLySanPham.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "kieu_dang")
public class KieuDang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ten_kieu_dang")
    private String tenKieuDang;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "kieuDang")
    private List<SanPham> sanPhams;

    public KieuDang() {}

    public KieuDang(Integer id, String tenKieuDang, Integer trangThai, List<SanPham> sanPhams) {
        this.id = id;
        this.tenKieuDang = tenKieuDang;
        this.trangThai = trangThai;
        this.sanPhams = sanPhams;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTenKieuDang() { return tenKieuDang; }
    public void setTenKieuDang(String tenKieuDang) { this.tenKieuDang = tenKieuDang; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<SanPham> getSanPhams() { return sanPhams; }
    public void setSanPhams(List<SanPham> sanPhams) { this.sanPhams = sanPhams; }
}