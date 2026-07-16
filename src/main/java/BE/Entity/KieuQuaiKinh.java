package BE.Entity;

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
    @Builder.Default
    private Integer quaiThang = 0;

    @Column(name = "quai_gap")
    @Builder.Default
    private Integer quaiGap = 0;

    @Column(name = "quai_loxo")
    @Builder.Default
    private Integer quaiLoxo = 0;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}

