package BE.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "trong_kinh")
public class TrongKinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "loai_trong")
    private String loaiTrong;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "trongKinh")
    private List<SanPham> sanPhams;

    public TrongKinh() {}

    public TrongKinh(Integer id, String loaiTrong, Integer trangThai, List<SanPham> sanPhams) {
        this.id = id;
        this.loaiTrong = loaiTrong;
        this.trangThai = trangThai;
        this.sanPhams = sanPhams;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getLoaiTrong() { return loaiTrong; }
    public void setLoaiTrong(String loaiTrong) { this.loaiTrong = loaiTrong; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<SanPham> getSanPhams() { return sanPhams; }
    public void setSanPhams(List<SanPham> sanPhams) { this.sanPhams = sanPhams; }
}