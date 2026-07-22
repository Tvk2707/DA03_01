package BanHangTaiQuay.Service;

import QuanLySanPham.Entity.KhachHang;

public interface KhachHangService {
    KhachHang traCuuKhachHang(String soDienThoai);

    KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen);

    KhachHang timKhachHangTheoId(int idKhachHang);
}
