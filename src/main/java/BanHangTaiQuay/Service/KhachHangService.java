package BanHangTaiQuay.Service;

import QuanLySanPham.Entity.KhachHang;

import java.util.List;

public interface KhachHangService {
    KhachHang traCuuKhachHang(String soDienThoai);

    List<KhachHang> timKhachHangTheoTuKhoa(String tuKhoa);

    KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen);

    KhachHang timKhachHangTheoId(int idKhachHang);
}
