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
    private Integer id;

    @Column(name = "quai_thang")
    private Integer quaiThang = 0;

    @Column(name = "quai_gap")
    private Integer quaiGap = 0;

    @Column(name = "quai_loxo")
    private Integer quaiLoxo = 0;

    @Column(name = "trang_thai")
    private Integer trangThai = 1;
}
