package QuanLySanPham.dao;

import QuanLySanPham.Entity.GongKinh;

import java.util.List;

/**
 * Interface DAO cho entity GongKinh
 */
public interface GongKinhDao extends GenericDao<GongKinh, Integer> {
    
    /**
     * Tìm gọng kính theo tên
     */
    GongKinh findByTen(String ten);
    List<GongKinh> searchByKeyword(String keyword);

}
