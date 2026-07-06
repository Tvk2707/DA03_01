package com.eyewear.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hinh_thuc_thanh_toan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HinhThucThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_pttt", unique = true, nullable = false, length = 50)
    private String maPttt;

    @Column(name = "ten_pttt", nullable = false, length = 150)
    private String tenPttt;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}
