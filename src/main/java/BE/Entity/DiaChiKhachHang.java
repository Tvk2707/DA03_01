package BE.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dia_chi_khach_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaChiKhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", nullable = false)
    private BE.Entity.KhachHang khachHang;

    @Column(name = "ten_nguoi_nhan", nullable = false, length = 250)
    private String tenNguoiNhan;

    @Column(name = "sdt_nguoi_nhan", nullable = false, length = 15)
    private String sdtNguoiNhan;

    @Column(name = "tinh_thanh", nullable = false, length = 150)
    private String tinhThanh;

    @Column(name = "quan_huyen", nullable = false, length = 150)
    private String quanHuyen;

    @Column(name = "phuong_xa", nullable = false, length = 150)
    private String phuongXa;

    @Column(name = "dia_chi_cu_the", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String diaChiCuThe;

    @Column(name = "loai_dia_chi")
    private Integer loaiDiaChi;

    @Column(name = "is_mac_dinh")
    @Builder.Default
    private Integer isMacDinh = 0;
}

