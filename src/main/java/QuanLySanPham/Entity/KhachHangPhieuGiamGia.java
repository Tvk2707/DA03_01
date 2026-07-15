package main.QuanLySanPham.BE.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "khach_hang_phieu_giam_gia")
public class KhachHangPhieuGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "ngay_su_dung")
    private LocalDateTime ngaySuDung;

    public KhachHangPhieuGiamGia() {}

    public KhachHangPhieuGiamGia(Integer id, KhachHang khachHang, PhieuGiamGia phieuGiamGia, Integer trangThai, LocalDateTime ngaySuDung) {
        this.id = id;
        this.khachHang = khachHang;
        this.phieuGiamGia = phieuGiamGia;
        this.trangThai = trangThai;
        this.ngaySuDung = ngaySuDung;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public PhieuGiamGia getPhieuGiamGia() { return phieuGiamGia; }
    public void setPhieuGiamGia(PhieuGiamGia phieuGiamGia) { this.phieuGiamGia = phieuGiamGia; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public LocalDateTime getNgaySuDung() { return ngaySuDung; }
    public void setNgaySuDung(LocalDateTime ngaySuDung) { this.ngaySuDung = ngaySuDung; }
}