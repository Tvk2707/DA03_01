package QuanLySanPham.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_hoa_don")
public class LichSuHoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "hanh_dong")
    private String hanhDong;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ghi_chu")
    private String ghiChu;

    public LichSuHoaDon() {}

    public LichSuHoaDon(Integer id, HoaDon hoaDon, String hanhDong, LocalDateTime ngayTao, String ghiChu) {
        this.id = id;
        this.hoaDon = hoaDon;
        this.hanhDong = hanhDong;
        this.ngayTao = ngayTao;
        this.ghiChu = ghiChu;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) { this.hoaDon = hoaDon; }
    public String getHanhDong() { return hanhDong; }
    public void setHanhDong(String hanhDong) { this.hanhDong = hanhDong; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}