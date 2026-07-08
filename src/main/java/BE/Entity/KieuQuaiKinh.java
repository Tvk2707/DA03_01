package BE.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "kieu_quai_kinh")
public class KieuQuaiKinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kieu_quai", nullable = false, length = 255)
    private String kieuQuai;

    @Column(name = "trang_thai")
    private Integer trangThai = 1;

    @OneToMany(mappedBy = "kieuQuaiKinh")
    private List<GongKinh> gongKinhs;

    public KieuQuaiKinh() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKieuQuai() {
        return kieuQuai;
    }

    public void setKieuQuai(String kieuQuai) {
        this.kieuQuai = kieuQuai;
    }

    public Integer getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Integer trangThai) {
        this.trangThai = trangThai;
    }

    public List<GongKinh> getGongKinhs() {
        return gongKinhs;
    }

    public void setGongKinhs(List<GongKinh> gongKinhs) {
        this.gongKinhs = gongKinhs;
    }
}
