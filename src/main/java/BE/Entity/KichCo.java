package com.eyewear.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kich_co")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KichCo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_kich_co", nullable = false, length = 100)
    private String tenKichCo;

    @Column(name = "trang_thai")
    private Integer trangThai = 1;
}
