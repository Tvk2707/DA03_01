package QuanLySanPham.dao;

import QuanLySanPham.Entity.DanhMuc;
import java.util.List;

/**
 * Interface DAO cho entity DanhMuc
 */
public interface DanhMucDao extends GenericDao<DanhMuc, Integer> {
    
    /**
     * Tìm danh mục theo tên
     */
    DanhMuc findByTen(String ten);
    
    /**
     * Tìm kiếm danh mục theo keyword (tìm trong mã hoặc tên)
     */
    List<DanhMuc> searchByKeyword(String keyword);
}
