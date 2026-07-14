package BE.service;

import BE.Entity.SanPham;
import java.util.List;

/**
 * Interface SanPhamService - quản lý sản phẩm
 */
public interface SanPhamService {
    
    /**
     * Thêm sản phẩm mới
     */
    SanPham themSanPham(SanPham sanPham);
    
    /**
     * Cập nhật sản phẩm
     */
    SanPham capNhatSanPham(SanPham sanPham);
    
    /**
     * Xóa sản phẩm theo ID
     * Chặn xóa nếu sản phẩm có biến thể hoặc hình ảnh
     */
    void xoaSanPham(Integer id);
    
    /**
     * Lấy sản phẩm theo ID
     */
    SanPham timTheoId(Integer id);
    
    /**
     * Lấy tất cả sản phẩm
     */
    List<SanPham> layTatCa();
    
    /**
     * Lấy sản phẩm có phân trang
     */
    List<SanPham> layCoPhanTrang(int pageNumber, int pageSize);
    
    /**
     * Tìm kiếm sản phẩm theo tên, danh mục, thương hiệu
     * Các tham số có thể null - nếu null thì bỏ qua điều kiện đó
     */
    List<SanPham> timKiem(String tenSanPham, Integer danhMucId, Integer thuongHieuId);

    void softdelete(Integer id);
}
