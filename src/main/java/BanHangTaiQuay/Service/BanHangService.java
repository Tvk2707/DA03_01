package BanHangTaiQuay.Service;

import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.KhachHang;

import java.math.BigDecimal;
import java.util.List;

public interface BanHangService {
    HoaDon taoHoaDonMoi(int idNhanVien, int idCa);
    void themSanPhamVaoGio(int idHoaDon, int idSanPhamChiTiet, int soLuong);
    void xoaSanPhamKhoiGio(int idHoaDon, int idChiTiet);
    void capNhatSoLuong(int idChiTiet, int soLuongMoi);
    KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen);
    void ganKhachHang(int idHoaDon, int idKhachHang);
    void apDungVoucher(int idHoaDon, String maVoucher);
    void xacNhanThanhToan(int idHoaDon, String maPttt, BigDecimal soTienKhachDua);
    void huyHoaDon(int idHoaDon, String lyDo);
    List<HoaDon> layDanhSachHoaDonCho(int idNhanVien);
}
