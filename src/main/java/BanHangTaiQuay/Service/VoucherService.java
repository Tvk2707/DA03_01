package BanHangTaiQuay.Service;

import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.PhieuGiamGia;
import jakarta.persistence.EntityManager;

import java.util.List;

public interface VoucherService {
    List<PhieuGiamGia> timKiemVoucher(int idHoaDon, String tuKhoa);

    void apDungVoucher(int idHoaDon, String maVoucher);

    void goVoucher(int idHoaDon);

    void kiemTraVoucherKhiThanhToan(EntityManager em, HoaDon hoaDon);

    void hoanVoucherKhiHuy(EntityManager em, HoaDon hoaDon);
}
