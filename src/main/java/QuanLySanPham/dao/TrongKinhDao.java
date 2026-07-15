package QuanLySanPham.dao;

import QuanLySanPham.Entity.TrongKinh;

import java.util.List;

/**
 * Interface DAO cho entity TrongKinh
 */
public interface TrongKinhDao extends GenericDao<TrongKinh, Integer> {
    
    /**
     * Tìm trong kính theo tên
     */
    TrongKinh findByTen(String ten);
    List<TrongKinh> searchByKeyword(String keyword);

}
