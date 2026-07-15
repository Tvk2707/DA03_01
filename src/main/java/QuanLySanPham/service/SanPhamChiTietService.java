package QuanLySanPham.service;

import QuanLySanPham.Entity.SanPhamChiTiet;
import java.util.List;

/**
 * Interface SanPhamChiTietService - quản lý biến thể sản phẩm
 */
public interface SanPhamChiTietService {
    List<SanPhamChiTiet> timKiem(Integer sanPhamId, String ma, Integer mauSacId, Integer kichCoId, Integer trangThai);
    /**
     * Thêm biến thể sản phẩm mới
     */
    SanPhamChiTiet themBienThe(SanPhamChiTiet sanPhamChiTiet);
    
    /**
     * Cập nhật biến thể sản phẩm
     */
    SanPhamChiTiet capNhatBienThe(SanPhamChiTiet sanPhamChiTiet);
    
    /**
     * Xóa biến thể sản phẩm theo ID
     */
    void xoaBienThe(Integer id);
    
    /**
     * Lấy danh sách biến thể theo ID sản phẩm
     */
    List<SanPhamChiTiet> layTheoSanPham(Integer sanPhamId);
    SanPhamChiTiet timBienTheTheoSanPhamId(Integer sanPhamId);


    /**
     * Cập nhật tồn kho
     * Tính tồn kho mới = tồn kho hiện tại + soLuongThayDoi (có thể âm để trừ)
     * Không cho phép tồn kho âm
     */
    void capNhatTonKho(Integer sanPhamChiTietId, Integer soLuongThayDoi);
}
