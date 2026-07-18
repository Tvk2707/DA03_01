package BE.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dia_chi_khach_hang")
public class DiaChiKhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @Column(name = "ten_nguoi_nhan")
    private String tenNguoiNhan;

    @Column(name = "sdt_nguoi_nhan")
    private String sdtNguoiNhan;

    @Column(name = "tinh_thanh")
    private String tinhThanh;

    @Column(name = "quan_huyen")
    private String quanHuyen;

    @Column(name = "phuong_xa")
    private String phuongXa;

    @Column(name = "dia_chi_cu_the")
    private String diaChiCuThe;

    @Column(name = "loai_dia_chi")
    private Integer loaiDiaChi;

    @Column(name = "is_mac_dinh")
    private Integer isMacDinh;

    public DiaChiKhachHang() {}

    public DiaChiKhachHang(Integer id, KhachHang khachHang, String tenNguoiNhan, String sdtNguoiNhan, String tinhThanh, String quanHuyen, String phuongXa, String diaChiCuThe, Integer loaiDiaChi, Integer isMacDinh) {
        this.id = id;
        this.khachHang = khachHang;
        this.tenNguoiNhan = tenNguoiNhan;
        this.sdtNguoiNhan = sdtNguoiNhan;
        this.tinhThanh = tinhThanh;
        this.quanHuyen = quanHuyen;
        this.phuongXa = phuongXa;
        this.diaChiCuThe = diaChiCuThe;
        this.loaiDiaChi = loaiDiaChi;
        this.isMacDinh = isMacDinh;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public String getTenNguoiNhan() { return tenNguoiNhan; }
    public void setTenNguoiNhan(String tenNguoiNhan) { this.tenNguoiNhan = tenNguoiNhan; }
    public String getSdtNguoiNhan() { return sdtNguoiNhan; }
    public void setSdtNguoiNhan(String sdtNguoiNhan) { this.sdtNguoiNhan = sdtNguoiNhan; }
    public String getTinhThanh() { return tinhThanh; }
    public void setTinhThanh(String tinhThanh) { this.tinhThanh = tinhThanh; }
    public String getQuanHuyen() { return quanHuyen; }
    public void setQuanHuyen(String quanHuyen) { this.quanHuyen = quanHuyen; }
    public String getPhuongXa() { return phuongXa; }
    public void setPhuongXa(String phuongXa) { this.phuongXa = phuongXa; }
    public String getDiaChiCuThe() { return diaChiCuThe; }
    public void setDiaChiCuThe(String diaChiCuThe) { this.diaChiCuThe = diaChiCuThe; }
    public Integer getLoaiDiaChi() { return loaiDiaChi; }
    public void setLoaiDiaChi(Integer loaiDiaChi) { this.loaiDiaChi = loaiDiaChi; }
    public Integer getIsMacDinh() { return isMacDinh; }
    public void setIsMacDinh(Integer isMacDinh) { this.isMacDinh = isMacDinh; }
}