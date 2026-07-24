package BanHangTaiQuay.Service;

import QuanLySanPham.Entity.KhachHang;

import java.time.LocalDate;
import java.util.List;

public interface KhachHangService {
    KhachHang traCuuKhachHang(String soDienThoai);

    List<KhachHang> timKhachHangTheoTuKhoa(String tuKhoa);

    KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen);

    KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen,
                                      String email, LocalDate ngaySinh,
                                      Integer gioiTinh, String matKhau);

    KhachHang timKhachHangTheoId(int idKhachHang);
}
