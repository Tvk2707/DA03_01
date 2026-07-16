package QuanLySanPham.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "kieu_quai_kinh")
public class KieuQuaiKinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "quai_thang")
    private Integer quaiThang;

    @Column(name = "quai_gap")
    private Integer quaiGap;

    @Column(name = "quai_loxo")
    private Integer quaiLoxo;

    // Giữ thuộc tính hiển thị cũ để các màn hình cũ vẫn biên dịch.
    @Transient
    private String kieuQuai;

    @Column(name = "trang_thai")
    private Integer trangThai = 1;

    @OneToMany(mappedBy = "kieuQuaiKinh")
    private List<GongKinh> gongKinhs;

    public KieuQuaiKinh() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKieuQuai() {
        return kieuQuai;
    }

    public void setKieuQuai(String kieuQuai) {
        this.kieuQuai = kieuQuai;
    }

    public Integer getQuaiThang() { return quaiThang; }
    public void setQuaiThang(Integer quaiThang) { this.quaiThang = quaiThang; }
    public Integer getQuaiGap() { return quaiGap; }
    public void setQuaiGap(Integer quaiGap) { this.quaiGap = quaiGap; }
    public Integer getQuaiLoxo() { return quaiLoxo; }
    public void setQuaiLoxo(Integer quaiLoxo) { this.quaiLoxo = quaiLoxo; }

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
