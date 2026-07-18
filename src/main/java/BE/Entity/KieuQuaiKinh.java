package com.eyewear.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kieu_quai_kinh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KieuQuaiKinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kieu_quai", nullable = false, length = 255)
    private String kieuQuai;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}
