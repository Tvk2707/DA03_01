package QuanLySanPham.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "phieu_giam_gia")
public class PhieuGiamGia {
    private static final DateTimeFormatter DATE_TEXT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_VALUE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_voucher")
    private String maVoucher;

    @Column(name = "ten_voucher")
    private String tenVoucher;

    @Column(name = "loai_giam_gia")
    private String loaiGiamGia;

    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;

    @Column(name = "giam_toi_da")
    private BigDecimal giamToiDa;

    @Column(name = "don_toi_thieu")
    private BigDecimal donToiThieu;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "so_luong_da_dung")
    private Integer soLuongDaDung;

    @Column(name = "loai_phieu")
    private Integer loaiPhieu;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "phieuGiamGia")
    private List<HoaDon> hoaDons;

    @OneToMany(mappedBy = "phieuGiamGia")
    private List<KhachHangPhieuGiamGia> khachHangPhieuGiamGias;

    public PhieuGiamGia() {}

    public PhieuGiamGia(Integer id, String maVoucher, String tenVoucher, String loaiGiamGia, BigDecimal giaTriGiam, BigDecimal giamToiDa, BigDecimal donToiThieu, Integer soLuong, Integer soLuongDaDung, Integer loaiPhieu, LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc, LocalDateTime ngayTao, Integer trangThai, List<HoaDon> hoaDons, List<KhachHangPhieuGiamGia> khachHangPhieuGiamGias) {
        this.id = id;
        this.maVoucher = maVoucher;
        this.tenVoucher = tenVoucher;
        this.loaiGiamGia = loaiGiamGia;
        this.giaTriGiam = giaTriGiam;
        this.giamToiDa = giamToiDa;
        this.donToiThieu = donToiThieu;
        this.soLuong = soLuong;
        this.soLuongDaDung = soLuongDaDung;
        this.loaiPhieu = loaiPhieu;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.hoaDons = hoaDons;
        this.khachHangPhieuGiamGias = khachHangPhieuGiamGias;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMaVoucher() { return maVoucher; }
    public void setMaVoucher(String maVoucher) { this.maVoucher = maVoucher; }
    public String getTenVoucher() { return tenVoucher; }
    public void setTenVoucher(String tenVoucher) { this.tenVoucher = tenVoucher; }
    public String getLoaiGiamGia() { return loaiGiamGia; }
    public void setLoaiGiamGia(String loaiGiamGia) { this.loaiGiamGia = loaiGiamGia; }
    public BigDecimal getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(BigDecimal giaTriGiam) { this.giaTriGiam = giaTriGiam; }
    public BigDecimal getGiamToiDa() { return giamToiDa; }
    public void setGiamToiDa(BigDecimal giamToiDa) { this.giamToiDa = giamToiDa; }
    public BigDecimal getDonToiThieu() { return donToiThieu; }
    public void setDonToiThieu(BigDecimal donToiThieu) { this.donToiThieu = donToiThieu; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public Integer getSoLuongDaDung() { return soLuongDaDung; }
    public void setSoLuongDaDung(Integer soLuongDaDung) { this.soLuongDaDung = soLuongDaDung; }
    public Integer getLoaiPhieu() { return loaiPhieu; }
    public void setLoaiPhieu(Integer loaiPhieu) { this.loaiPhieu = loaiPhieu; }
    public LocalDateTime getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDateTime ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public LocalDateTime getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDateTime ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }
    public List<HoaDon> getHoaDons() { return hoaDons; }
    public void setHoaDons(List<HoaDon> hoaDons) { this.hoaDons = hoaDons; }
    public List<KhachHangPhieuGiamGia> getKhachHangPhieuGiamGias() { return khachHangPhieuGiamGias; }
    public void setKhachHangPhieuGiamGias(List<KhachHangPhieuGiamGia> khachHangPhieuGiamGias) { this.khachHangPhieuGiamGias = khachHangPhieuGiamGias; }

    public boolean isGiamPhanTram() {
        if (loaiGiamGia == null) {
            return true;
        }
        String value = loaiGiamGia.toLowerCase();
        return value.contains("%") || value.contains("phần") || value.contains("phan") || value.contains("percent");
    }

    public String getLoaiGiamGiaText() {
        return isGiamPhanTram() ? "Giảm phần trăm" : "Giảm tiền";
    }

    public String getLoaiGiamGiaFilterValue() {
        return isGiamPhanTram() ? "percent" : "amount";
    }

    public String getLoaiPhieuText() {
        return loaiPhieu != null && loaiPhieu == 1 ? "Cá nhân" : "Công khai";
    }

    public String getLoaiPhieuFilterValue() {
        return loaiPhieu != null && loaiPhieu == 1 ? "personal" : "public";
    }

    public String getTrangThaiHienThi() {
        LocalDateTime now = LocalDateTime.now();
        if (trangThai == null || trangThai != 1) {
            return "Ngừng áp dụng";
        }
        if (ngayBatDau != null && now.isBefore(ngayBatDau)) {
            return "Chưa bắt đầu";
        }
        if (ngayKetThuc != null && now.isAfter(ngayKetThuc)) {
            return "Kết thúc";
        }
        return "Đang áp dụng";
    }

    public String getTrangThaiCssClass() {
        String text = getTrangThaiHienThi();
        if ("Đang áp dụng".equals(text)) {
            return "status-active";
        }
        if ("Chưa bắt đầu".equals(text)) {
            return "status-upcoming";
        }
        if ("Kết thúc".equals(text)) {
            return "status-expired";
        }
        return "status-inactive";
    }

    public boolean isDangBat() {
        return trangThai != null && trangThai == 1;
    }

    public String getNgayBatDauText() {
        return ngayBatDau == null ? "" : ngayBatDau.toLocalDate().format(DATE_TEXT_FORMATTER);
    }

    public String getNgayKetThucText() {
        return ngayKetThuc == null ? "" : ngayKetThuc.toLocalDate().format(DATE_TEXT_FORMATTER);
    }

    public String getNgayBatDauValue() {
        return ngayBatDau == null ? "" : ngayBatDau.toLocalDate().format(DATE_VALUE_FORMATTER);
    }

    public String getNgayKetThucValue() {
        return ngayKetThuc == null ? "" : ngayKetThuc.toLocalDate().format(DATE_VALUE_FORMATTER);
    }
}
