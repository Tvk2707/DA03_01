package BE.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "thuong_hieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThuongHieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_thuong_hieu", unique = true, nullable = false, length = 50)
    private String maThuongHieu;

    @Column(name = "ten_thuong_hieu", nullable = false, length = 250)
    private String tenThuongHieu;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}

