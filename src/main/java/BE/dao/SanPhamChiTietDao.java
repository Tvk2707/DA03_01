package BE.dao;

import BE.Entity.SanPhamChiTiet;
import java.util.List;

/**
 * Interface DAO cho entity SanPhamChiTiet
 */
public interface SanPhamChiTietDao extends GenericDao<SanPhamChiTiet, Integer> {
    
    /**
     * Lấy danh sách chi tiết sản phẩm theo ID sản phẩm
     */
    List<SanPhamChiTiet> findBySanPhamId(Integer sanPhamId);
    
    /**
     * Tìm chi tiết sản phẩm theo sản phẩm, màu sắc và kích cỡ
     */
    SanPhamChiTiet findByMauSacVaKichCo(Integer sanPhamId, Integer mauSacId, Integer kichCoId);
    
    /**
     * Cập nhật tồn kho cho chi tiết sản phẩm
     */
    void updateTonKho(Integer sanPhamChiTietId, Integer tonKhoMoi);
}
