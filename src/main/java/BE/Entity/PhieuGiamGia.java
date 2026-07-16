package BE.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "phieu_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_voucher", unique = true, nullable = false, length = 50)
    private String maVoucher;

    @Column(name = "ten_voucher", nullable = false, length = 250)
    private String tenVoucher;

    @Column(name = "loai_giam_gia", length = 50)
    private String loaiGiamGia; // "Pháº§n trÄƒm" hoáº·c "Tiá»n máº·t"

    @Column(name = "gia_tri_giam", precision = 15, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "giam_toi_da", precision = 15, scale = 2)
    private BigDecimal giamToiDa;

    @Column(name = "don_toi_thieu", precision = 15, scale = 2)
    private BigDecimal donToiThieu;

    @Column(name = "so_luong", nullable = false)
    @Builder.Default
    private Integer soLuong = 0;

    @Column(name = "so_luong_da_dung")
    @Builder.Default
    private Integer soLuongDaDung = 0;

    @Column(name = "loai_phieu")
    private Integer loaiPhieu;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}

