package BE.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "khach_hang_phieu_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHangPhieuGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", nullable = false)
    private BE.Entity.KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia", nullable = false)
    private BE.Entity.PhieuGiamGia phieuGiamGia;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;

    @Column(name = "ngay_su_dung")
    private LocalDateTime ngaySuDung;
}

