package main.QuanLySanPham.BE.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "hinh_thuc_thanh_toan")
public class HinhThucThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_pttt")
    private String maPttt;

    @Column(name = "ten_pttt")
    private String tenPttt;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "hinhThucThanhToan")
    private List<ThanhToanHoaDon> thanhToanHoaDons;

    public HinhThucThanhToan() {}

    public HinhThucThanhToan(Integer id, String maPttt, String tenPttt, Integer trangThai, List<ThanhToanHoaDon> thanhToanHoaDons) {
        this.id = id;
        this.maPttt = maPttt;
        this.tenPttt = tenPttt;
        this.trangThai = trangThai;
        this.thanhToanHoaDons = thanhToanHoaDons;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMaPttt() { return maPttt; }
    public void setMaPttt(String maPttt) { this.maPttt = maPttt; }
    public String getTenPttt() { return tenPttt; }
    public void setTenPttt(String tenPttt) { this.tenPttt = tenPttt; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<ThanhToanHoaDon> getThanhToanHoaDons() { return thanhToanHoaDons; }
    public void setThanhToanHoaDons(List<ThanhToanHoaDon> thanhToanHoaDons) { this.thanhToanHoaDons = thanhToanHoaDons; }
}