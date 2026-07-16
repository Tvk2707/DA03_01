package BE.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_lieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_chat_lieu", nullable = false, length = 250)
    private String tenChatLieu;

    @Column(name = "trang_thai")
    @Builder.Default
    private Integer trangThai = 1;
}

