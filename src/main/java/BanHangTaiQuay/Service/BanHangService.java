package BanHangTaiQuay.Service;

import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.KhachHang;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BanHangService {
    HoaDon taoHoaDonMoi(Integer idNhanVien, Integer idCa);
    void themSanPhamVaoGio(int idHoaDon, int idSanPhamChiTiet, int soLuong);
    void xoaSanPhamKhoiGio(int idHoaDon, int idChiTiet);
    void capNhatSoLuong(int idChiTiet, int soLuongMoi);
    KhachHang traCuuKhachHang(String soDienThoai);
    List<KhachHang> timKiemKhachHang(String tuKhoa);
    KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen);
    KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen, String email,
                                     LocalDate ngaySinh, Integer gioiTinh);
    void ganKhachHang(int idHoaDon, int idKhachHang);
    void chonKhachLe(int idHoaDon);
    void apDungVoucher(int idHoaDon, String maVoucher);
    void goVoucher(int idHoaDon);
    void xacNhanThanhToan(int idHoaDon, String maPttt, BigDecimal soTienKhachDua);
    void xacNhanThanhToan(int idHoaDon, String maPttt, BigDecimal soTienKhachDua,
                          String maGiaoDich, String ghiChu);
    void huyHoaDon(int idHoaDon, String lyDo);
    List<HoaDon> layDanhSachHoaDonCho(int idNhanVien);
    HoaDon layHoaDonTheoId(int idHoaDon);
}
