package BE.dao;

import BE.Entity.GongKinh;

/**
 * Interface DAO cho entity GongKinh
 */
public interface GongKinhDao extends GenericDao<GongKinh, Integer> {
    
    /**
     * Tìm gọng kính theo tên
     */
    GongKinh findByTen(String ten);
}
