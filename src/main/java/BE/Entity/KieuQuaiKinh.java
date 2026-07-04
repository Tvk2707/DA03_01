package BE.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kieu_quai_kinh")
public class KieuQuaiKinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "quai_thang")
    private Integer quaiThang;

    @Column(name = "quai_gap")
    private Integer quaiGap;

    @Column(name = "quai_loxo")
    private Integer quaiLoxo;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "kieuQuaiKinh")
    private List<GongKinh> gongKinhs;

    public KieuQuaiKinh() {}

    public KieuQuaiKinh(Integer id, Integer quaiThang, Integer quaiGap, Integer quaiLoxo, Integer trangThai, List<GongKinh> gongKinhs) {
        this.id = id;
        this.quaiThang = quaiThang;
        this.quaiGap = quaiGap;
        this.quaiLoxo = quaiLoxo;
        this.trangThai = trangThai;
        this.gongKinhs = gongKinhs;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getQuaiThang() { return quaiThang; }
    public void setQuaiThang(Integer quaiThang) { this.quaiThang = quaiThang; }
    public Integer getQuaiGap() { return quaiGap; }
    public void setQuaiGap(Integer quaiGap) { this.quaiGap = quaiGap; }
    public Integer getQuaiLoxo() { return quaiLoxo; }
    public void setQuaiLoxo(Integer quaiLoxo) { this.quaiLoxo = quaiLoxo; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<GongKinh> getGongKinhs() { return gongKinhs; }
    public void setGongKinhs(List<GongKinh> gongKinhs) { this.gongKinhs = gongKinhs; }
}