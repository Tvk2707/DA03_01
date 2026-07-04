package com.eyewear.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "nhan_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_nhan_vien", unique = true, nullable = false, length = 50)
    private String maNhanVien;

    @Column(name = "ho_ten", nullable = false, length = 250)
    private String hoTen;

    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "so_dien_thoai", length = 15)
    private String soDienThoai;

    @Column(name = "mat_khau", nullable = false, length = 250)
    private String matKhau;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh")
    private Integer gioiTinh;

    @Column(name = "dia_chi", columnDefinition = "NVARCHAR(MAX)")
    private String diaChi;

    @Column(name = "chuc_vu", length = 100)
    private String chucVu;

    @Column(name = "anh_dai_dien", length = 250)
    private String anhDaiDien;

    @Column(name = "trang_thai")
    private Integer trangThai = 1;
}
