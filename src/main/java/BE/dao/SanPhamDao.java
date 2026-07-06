package BE.dao;

import BE.Entity.SanPham;
import java.util.List;

/**
 * Interface DAO cho entity SanPham
 */
public interface SanPhamDao extends GenericDao<SanPham, Integer> {
    
    /**
     * Tìm sản phẩm theo tên
     */
    SanPham findByTenSanPham(String ten);
    
    /**
     * Lấy danh sách sản phẩm theo danh mục
     */
    List<SanPham> findByDanhMuc(Integer danhMucId);
    
    /**
     * Lấy danh sách sản phẩm theo thương hiệu
     */
    List<SanPham> findByThuongHieu(Integer thuongHieuId);
    
    /**
     * Tìm kiếm sản phẩm theo tên, danh mục, thương hiệu, và khoảng giá
     * Nếu giá trị nào là null, bỏ qua điều kiện đó
     */
    List<SanPham> search(String tenSanPham, Integer danhMucId, Integer thuongHieuId,
                         Double giaMin, Double giaMax);

}
