package QuanLySanPham.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "thuong_hieu")
public class ThuongHieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_thuong_hieu")
    private String maThuongHieu;

    @Column(name = "ten_thuong_hieu")
    private String tenThuongHieu;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "thuongHieu")
    private List<SanPham> sanPhams;

    public ThuongHieu() {}

    public ThuongHieu(Integer id, String maThuongHieu, String tenThuongHieu, Integer trangThai, List<SanPham> sanPhams) {
        this.id = id;
        this.maThuongHieu = maThuongHieu;
        this.tenThuongHieu = tenThuongHieu;
        this.trangThai = trangThai;
        this.sanPhams = sanPhams;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMaThuongHieu() { return maThuongHieu; }
    public void setMaThuongHieu(String maThuongHieu) { this.maThuongHieu = maThuongHieu; }
    public String getTenThuongHieu() { return tenThuongHieu; }
    public void setTenThuongHieu(String tenThuongHieu) { this.tenThuongHieu = tenThuongHieu; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<SanPham> getSanPhams() { return sanPhams; }
    public void setSanPhams(List<SanPham> sanPhams) { this.sanPhams = sanPhams; }
}