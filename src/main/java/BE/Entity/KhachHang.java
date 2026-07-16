package BE.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "khach_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_khach_hang", unique = true, nullable = false, length = 50)
    private String maKhachHang;

    @Column(name = "ho_ten", nullable = false, length = 250)
    private String hoTen;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "so_dien_thoai", length = 15)
    private String soDienThoai;

    @Column(name = "mat_khau", length = 250)
    private String matKhau;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh")
    private Integer gioiTinh;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}

