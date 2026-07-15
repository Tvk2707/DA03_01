package QuanLySanPham.service;

import QuanLySanPham.Entity.SanPhamChiTiet;
import java.util.List;

/**
 * Interface SanPhamChiTietService - quản lý biến thể sản phẩm
 */
public interface SanPhamChiTietService {
    List<SanPhamChiTiet> timKiem(Integer sanPhamId, String ma, Integer mauSacId, Integer kichCoId, Integer trangThai);

    /**
     * Thêm danh sách biến thể sản phẩm mới
     */
    List<SanPhamChiTiet> themBienThe(List<SanPhamChiTiet> danhSach);

    /**
     * Cập nhật danh sách biến thể sản phẩm
     */
    List<SanPhamChiTiet> capNhatDanhSachBienThe(List<SanPhamChiTiet> danhSach);

    /**
     * Xóa biến thể sản phẩm theo ID
     */
    void xoaBienThe(Integer id);

    /**
     * Lấy danh sách biến thể theo ID sản phẩm
     */
    List<SanPhamChiTiet> timBienTheTheoSanPhamId(Integer sanPhamId);

    /**
     * Cập nhật tồn kho
     * Tính tồn kho mới = tồn kho hiện tại + soLuongThayDoi (có thể âm để trừ)
     * Không cho phép tồn kho âm
     */
    void capNhatTonKho(Integer sanPhamChiTietId, Integer soLuongThayDoi);
}