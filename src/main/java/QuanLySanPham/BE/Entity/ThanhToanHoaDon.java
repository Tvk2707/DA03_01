package BE.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan_hoa_don")
public class ThanhToanHoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pttt")
    private HinhThucThanhToan hinhThucThanhToan;

    @Column(name = "ma_giao_dich")
    private String maGiaoDich;

    @Column(name = "so_tien")
    private BigDecimal soTien;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "ghi_chu")
    private String ghiChu;

    public ThanhToanHoaDon() {}

    public ThanhToanHoaDon(Integer id, HoaDon hoaDon, HinhThucThanhToan hinhThucThanhToan, String maGiaoDich, BigDecimal soTien, LocalDateTime thoiGian, Integer trangThai, String ghiChu) {
        this.id = id;
        this.hoaDon = hoaDon;
        this.hinhThucThanhToan = hinhThucThanhToan;
        this.maGiaoDich = maGiaoDich;
        this.soTien = soTien;
        this.thoiGian = thoiGian;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) { this.hoaDon = hoaDon; }
    public HinhThucThanhToan getHinhThucThanhToan() { return hinhThucThanhToan; }
    public void setHinhThucThanhToan(HinhThucThanhToan hinhThucThanhToan) { this.hinhThucThanhToan = hinhThucThanhToan; }
    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }
    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }
    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}