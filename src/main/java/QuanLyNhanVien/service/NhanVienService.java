package QuanLyNhanVien.service;

import QuanLySanPham.Entity.NhanVien;
import java.util.List;

public interface NhanVienService {
    NhanVien themNhanVien(NhanVien nhanVien);
    NhanVien capNhatNhanVien(NhanVien nhanVien);
    void xoaNhanVien(Integer id);
    NhanVien timTheoId(Integer id);
    List<NhanVien> layTatCa();
    List<NhanVien> layCoPhanTrang(int pageNumber, int pageSize);
    List<NhanVien> timKiem(String hoTen, String email);
}

