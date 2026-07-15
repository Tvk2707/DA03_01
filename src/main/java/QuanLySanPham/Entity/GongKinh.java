package QuanLySanPham.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "gong_kinh")
public class GongKinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer  id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hinh_dang_gong")
    private HinhDangGong hinhDangGong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kieu_quai_kinh")
    private KieuQuaiKinh kieuQuaiKinh;

    @Column(name = "trang_thai")
    private Integer trangThai = 1;

    @OneToMany(mappedBy = "gongKinh")
    private List<SanPham> sanPhams;

    public GongKinh() {
    }

    public Integer  getId() {
        return id;
    }

    public void setId(Integer  id) {
        this.id = id;
    }

    public HinhDangGong getHinhDangGong() {
        return hinhDangGong;
    }

    public void setHinhDangGong(HinhDangGong hinhDangGong) {
        this.hinhDangGong = hinhDangGong;
    }

    public KieuQuaiKinh getKieuQuaiKinh() {
        return kieuQuaiKinh;
    }

    public void setKieuQuaiKinh(KieuQuaiKinh kieuQuaiKinh) {
        this.kieuQuaiKinh = kieuQuaiKinh;
    }

    public Integer getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Integer trangThai) {
        this.trangThai = trangThai;
    }

    public List<SanPham> getSanPhams() {
        return sanPhams;
    }

    public void setSanPhams(List<SanPham> sanPhams) {
        this.sanPhams = sanPhams;
    }
}
