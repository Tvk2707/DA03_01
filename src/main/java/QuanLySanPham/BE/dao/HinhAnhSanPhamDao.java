package BE.dao;

import BE.Entity.HinhAnhSanPham;
import java.util.List;

/**
 * Interface DAO cho entity HinhAnhSanPham
 */
public interface HinhAnhSanPhamDao extends GenericDao<HinhAnhSanPham, Integer> {
    
    /**
     * Lấy danh sách hình ảnh sản phẩm theo ID sản phẩm
     */
    List<HinhAnhSanPham> findBySanPhamId(Integer sanPhamId);
    
    /**
     * Xóa tất cả hình ảnh của một sản phẩm
     */
    void deleteBySanPhamId(Integer sanPhamId);
}
