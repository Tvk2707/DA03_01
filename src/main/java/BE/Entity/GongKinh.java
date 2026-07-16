package BE.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gong_kinh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GongKinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hinh_dang_gong")
    private BE.Entity.HinhDangGong hinhDangGong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kieu_quai_kinh")
    private BE.Entity.KieuQuaiKinh kieuQuaiKinh;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}

