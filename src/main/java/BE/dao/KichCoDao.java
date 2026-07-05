package BE.dao;

import BE.Entity.KichCo;

/**
 * Interface DAO cho entity KichCo
 */
public interface KichCoDao extends GenericDao<KichCo, Integer> {
    
    /**
     * Tìm kích cỡ theo tên
     */
    KichCo findByTen(String ten);
}
