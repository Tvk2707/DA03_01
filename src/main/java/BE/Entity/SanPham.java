package BE.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "san_pham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc")
    private BE.Entity.DanhMuc danhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thuong_hieu")
    private BE.Entity.ThuongHieu thuongHieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_lieu")
    private BE.Entity.ChatLieu chatLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kieu_dang")
    private BE.Entity.KieuDang kieuDang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gong_kinh")
    private BE.Entity.GongKinh gongKinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_trong_kinh")
    private BE.Entity.TrongKinh trongKinh;

    @Column(name = "ma_san_pham", unique = true, nullable = false, length = 50)
    private String maSanPham;

    @Column(name = "ten_san_pham", nullable = false, length = 250)
    private String tenSanPham;

    @Column(name = "mo_ta_chi_tiet", columnDefinition = "NVARCHAR(MAX)")
    private String moTaChiTiet;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_sua")
    private LocalDateTime ngaySua;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}

