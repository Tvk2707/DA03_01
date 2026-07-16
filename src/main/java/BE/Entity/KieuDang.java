package BE.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kieu_dang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KieuDang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_kieu_dang", nullable = false, length = 250)
    private String tenKieuDang;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}

