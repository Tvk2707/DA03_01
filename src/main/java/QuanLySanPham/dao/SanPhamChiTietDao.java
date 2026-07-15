package main.QuanLySanPham.BE.dao;

import main.QuanLySanPham.BE.Entity.SanPhamChiTiet;
import java.util.List;

public interface SanPhamChiTietDao extends GenericDao<SanPhamChiTiet, Integer> {

    List<SanPhamChiTiet> timKiem(Integer sanPhamId, String ma, Integer mauSacId, Integer kichCoId, Integer trangThai);

    List<SanPhamChiTiet> findBySanPhamId(Integer sanPhamId);

    SanPhamChiTiet findByMauSacVaKichCo(Integer sanPhamId, Integer mauSacId, Integer kichCoId);

    void updateTonKho(Integer sanPhamChiTietId, Integer tonKhoMoi);

    // --- THÊM 2 HÀM XÓA MỀM ---

    /**
     * Xóa mềm 1 chi tiết sản phẩm cụ thể
     */
    void softDelete(Integer id);

    /**
     * Xóa mềm TẤT CẢ chi tiết của một sản phẩm cha
     * Dùng khi xóa mềm sản phẩm cha để đồng bộ dữ liệu con
     */
    void softDeleteBySanPhamId(Integer sanPhamId);
}