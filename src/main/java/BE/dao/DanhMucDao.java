package BE.dao;

import BE.Entity.DanhMuc;
import java.util.List;

/**
 * Interface DAO cho entity DanhMuc
 */
public interface DanhMucDao extends GenericDao<DanhMuc, Integer> {
    
    /**
     * Tìm danh mục theo tên
     */
    DanhMuc findByTen(String ten);
}
