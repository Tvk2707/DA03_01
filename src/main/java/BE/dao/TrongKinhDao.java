package BE.dao;

import BE.Entity.TrongKinh;

/**
 * Interface DAO cho entity TrongKinh
 */
public interface TrongKinhDao extends GenericDao<TrongKinh, Integer> {
    
    /**
     * Tìm trong kính theo tên
     */
    TrongKinh findByTen(String ten);
}
