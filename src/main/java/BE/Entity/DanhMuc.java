package com.eyewear.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "danh_muc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_danh_muc", unique = true, nullable = false, length = 50)
    private String maDanhMuc;

    @Column(name = "ten_danh_muc", nullable = false, length = 250)
    private String tenDanhMuc;

    @Column(name = "trang_thai")
    private Integer trangThai = 1;
}
