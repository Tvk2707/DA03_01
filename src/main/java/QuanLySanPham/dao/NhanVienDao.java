package QuanLySanPham.dao;

import QuanLySanPham.Entity.NhanVien;
import java.util.List;

public interface NhanVienDao extends GenericDao<NhanVien, Integer> {
    NhanVien findByMaNhanVien(String maNhanVien);
    List<NhanVien> search(String tuKhoa);
    void softDelete(Integer id);
    NhanVien findByEmail(String email);
    String findMaxMaNhanVien();
}

