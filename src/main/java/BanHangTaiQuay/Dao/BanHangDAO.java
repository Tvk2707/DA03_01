package BanHangTaiQuay.Dao;

import QuanLySanPham.Entity.ChiTietHoaDon;
import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.LichSuHoaDon;
import QuanLySanPham.Entity.ThanhToanHoaDon;

import java.util.List;

public interface BanHangDAO {
    HoaDon insertHoaDon(HoaDon hd);
    void updateHoaDon(HoaDon hd);
    HoaDon findHoaDonById(int id);
    long demHoaDonCho(int idNhanVien);
    List<HoaDon> layDanhSachHoaDonCho(int idNhanVien);
    Integer timCaDangMo(int idNhanVien);

    ChiTietHoaDon insertChiTietHoaDon(ChiTietHoaDon ct);
    void updateChiTietHoaDon(ChiTietHoaDon ct);
    void deleteChiTietHoaDon(ChiTietHoaDon ct);
    ChiTietHoaDon findByHoaDonVaSpct(int idHoaDon, int idSanPhamChiTiet);
    ChiTietHoaDon findChiTietHoaDonById(int id);

    ThanhToanHoaDon insertThanhToan(ThanhToanHoaDon tt);
    LichSuHoaDon insertLichSu(LichSuHoaDon ls);
}
