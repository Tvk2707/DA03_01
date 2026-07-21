package BE.jdbc;

import QuanLyHoaDon.Model.HoaDonView;
import QuanLyHoaDon.service.HoaDonService;

import java.util.List;

public class HoaDonDataCheck {
    public static void main(String[] args) throws Exception {
        HoaDonService service = new HoaDonService();
        List<HoaDonView> hoaDonList = service.getAllHoaDon();
        System.out.println("Hoa don count: " + hoaDonList.size());
        for (HoaDonView hoaDon : hoaDonList) {
            System.out.println(hoaDon.getMaHoaDon() + " | " + hoaDon.getTenKhachHang() + " | " + hoaDon.getTongTienThanhToan());
        }
    }
}
