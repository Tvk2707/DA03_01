package main.QuanLySanPham.BE.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_hoa_don")
public class ChiTietHoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham_chi_tiet")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "don_gia")
    private BigDecimal donGia;

    @Column(name = "gia_ban_ra")
    private BigDecimal giaBanRa;

    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    @Column(name = "trang_thai")
    private Integer trangThai;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(Integer id, HoaDon hoaDon, SanPhamChiTiet sanPhamChiTiet, Integer soLuong, BigDecimal donGia, BigDecimal giaBanRa, BigDecimal tongTien, Integer trangThai) {
        this.id = id;
        this.hoaDon = hoaDon;
        this.sanPhamChiTiet = sanPhamChiTiet;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.giaBanRa = giaBanRa;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) { this.hoaDon = hoaDon; }
    public SanPhamChiTiet getSanPhamChiTiet() { return sanPhamChiTiet; }
    public void setSanPhamChiTiet(SanPhamChiTiet sanPhamChiTiet) { this.sanPhamChiTiet = sanPhamChiTiet; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
    public BigDecimal getGiaBanRa() { return giaBanRa; }
    public void setGiaBanRa(BigDecimal giaBanRa) { this.giaBanRa = giaBanRa; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
}