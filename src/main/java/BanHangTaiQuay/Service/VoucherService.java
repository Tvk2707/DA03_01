package BanHangTaiQuay.Service;

import QuanLySanPham.Entity.HoaDon;
import jakarta.persistence.EntityManager;

public interface VoucherService {
    void apDungVoucher(int idHoaDon, String maVoucher);

    void goVoucher(int idHoaDon);

    void kiemTraVoucherKhiThanhToan(EntityManager em, HoaDon hoaDon);

    void hoanVoucherKhiHuy(EntityManager em, HoaDon hoaDon);
}
