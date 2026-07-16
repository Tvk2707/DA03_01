package BE.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trong_kinh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrongKinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "loai_trong", nullable = false, length = 250)
    private String loaiTrong;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}

