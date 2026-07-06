package com.eyewear.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hinh_dang_gong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HinhDangGong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "hinh_dang", nullable = false, length = 250)
    private String hinhDang;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}
