package QuanLySanPham.dao;

import QuanLySanPham.Entity.KichCo;

import java.util.List;

/**
 * Interface DAO cho entity KichCo
 */
public interface KichCoDao extends GenericDao<KichCo, Integer> {
    
    /**
     * Tìm kích cỡ theo tên
     */
    KichCo findByTen(String ten);
    List<KichCo> searchByKeyword(String keyword);

}
