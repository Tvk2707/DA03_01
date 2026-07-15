package main.QuanLySanPham.BE.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "hinh_dang_gong")
public class HinhDangGong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "hinh_dang")
    private String hinhDang;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "hinhDangGong")
    private List<GongKinh> gongKinhs;

    public HinhDangGong() {}

    public HinhDangGong(Integer id, String hinhDang, Integer trangThai, List<GongKinh> gongKinhs) {
        this.id = id;
        this.hinhDang = hinhDang;
        this.trangThai = trangThai;
        this.gongKinhs = gongKinhs;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getHinhDang() { return hinhDang; }
    public void setHinhDang(String hinhDang) { this.hinhDang = hinhDang; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<GongKinh> getGongKinhs() { return gongKinhs; }
    public void setGongKinhs(List<GongKinh> gongKinhs) { this.gongKinhs = gongKinhs; }
}