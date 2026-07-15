package QuanLySanPham.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "kich_co")
public class KichCo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ten_kich_co")
    private String tenKichCo;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "kichCo")
    private List<SanPhamChiTiet> sanPhamChiTiets;

    public KichCo() {}

    public KichCo(Integer id, String tenKichCo, Integer trangThai, List<SanPhamChiTiet> sanPhamChiTiets) {
        this.id = id;
        this.tenKichCo = tenKichCo;
        this.trangThai = trangThai;
        this.sanPhamChiTiets = sanPhamChiTiets;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTenKichCo() { return tenKichCo; }
    public void setTenKichCo(String tenKichCo) { this.tenKichCo = tenKichCo; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<SanPhamChiTiet> getSanPhamChiTiets() { return sanPhamChiTiets; }
    public void setSanPhamChiTiets(List<SanPhamChiTiet> sanPhamChiTiets) { this.sanPhamChiTiets = sanPhamChiTiets; }
}