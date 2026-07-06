package com.eyewear.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private com.eyewear.entity.KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private com.eyewear.entity.NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private com.eyewear.entity.PhieuGiamGia phieuGiamGia;

    @Column(name = "ma_hoa_don", unique = true, nullable = false, length = 50)
    private String maHoaDon;

    @Column(name = "ten_nguoi_nhan", length = 250)
    private String tenNguoiNhan;

    @Column(name = "sdt_nguoi_nhan", length = 15)
    private String sdtNguoiNhan;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "tong_tien_thanh_toan", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal tongTienThanhToan = BigDecimal.ZERO;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;

    @Column(name = "ngay_nhan_ban_dau")
    private LocalDateTime ngayNhanBanDau;

    @Column(name = "ngay_nhan")
    private LocalDateTime ngayNhan;

    @Column(name = "ghi_chu", columnDefinition = "NVARCHAR(MAX)")
    private String ghiChu;

    @Column(name = "ly_do_huy", columnDefinition = "NVARCHAR(MAX)")
    private String lyDoHuy;
}
