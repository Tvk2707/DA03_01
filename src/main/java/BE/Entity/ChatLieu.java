package BE.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_lieu")
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ten_chat_lieu")
    private String tenChatLieu;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "chatLieu")
    private List<SanPham> sanPhams;

    public ChatLieu() {}

    public ChatLieu(Integer id, String tenChatLieu, Integer trangThai, List<SanPham> sanPhams) {
        this.id = id;
        this.tenChatLieu = tenChatLieu;
        this.trangThai = trangThai;
        this.sanPhams = sanPhams;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTenChatLieu() { return tenChatLieu; }
    public void setTenChatLieu(String tenChatLieu) { this.tenChatLieu = tenChatLieu; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<SanPham> getSanPhams() { return sanPhams; }
    public void setSanPhams(List<SanPham> sanPhams) { this.sanPhams = sanPhams; }
}