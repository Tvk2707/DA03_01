package BE.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mau_sac")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_mau", unique = true, nullable = false, length = 50)
    private String maMau;

    @Column(name = "ten_mau", nullable = false, length = 100)
    private String tenMau;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}

