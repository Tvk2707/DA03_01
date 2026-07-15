package main.QuanLySanPham.BE.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hinh_anh_san_pham")
public class HinhAnhSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham")
    private SanPham sanPham;

    @Column(name = "url_anh")
    private String urlAnh;

    @Column(name = "is_anh_chinh")
    private Integer isAnhChinh;

    public HinhAnhSanPham() {}

    public HinhAnhSanPham(Integer id, SanPham sanPham, String urlAnh, Integer isAnhChinh) {
        this.id = id;
        this.sanPham = sanPham;
        this.urlAnh = urlAnh;
        this.isAnhChinh = isAnhChinh;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }
    public String getUrlAnh() { return urlAnh; }
    public void setUrlAnh(String urlAnh) { this.urlAnh = urlAnh; }
    public Integer getIsAnhChinh() { return isAnhChinh; }
    public void setIsAnhChinh(Integer isAnhChinh) { this.isAnhChinh = isAnhChinh; }
}