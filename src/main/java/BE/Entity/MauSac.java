package BE.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "mau_sac")
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_mau")
    private String maMau;

    @Column(name = "ten_mau")
    private String tenMau;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "mauSac")
    private List<SanPhamChiTiet> sanPhamChiTiets;

    public MauSac() {}

    public MauSac(Integer id, String maMau, String tenMau, Integer trangThai, List<SanPhamChiTiet> sanPhamChiTiets) {
        this.id = id;
        this.maMau = maMau;
        this.tenMau = tenMau;
        this.trangThai = trangThai;
        this.sanPhamChiTiets = sanPhamChiTiets;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMaMau() { return maMau; }
    public void setMaMau(String maMau) { this.maMau = maMau; }
    public String getTenMau() { return tenMau; }
    public void setTenMau(String tenMau) { this.tenMau = tenMau; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<SanPhamChiTiet> getSanPhamChiTiets() { return sanPhamChiTiets; }
    public void setSanPhamChiTiets(List<SanPhamChiTiet> sanPhamChiTiets) { this.sanPhamChiTiets = sanPhamChiTiets; }
}