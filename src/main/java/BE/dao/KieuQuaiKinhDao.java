package BE.dao;

import BE.Entity.KieuQuaiKinh;

/**
 * Interface DAO cho entity KieuQuaiKinh
 */
public interface KieuQuaiKinhDao extends GenericDao<KieuQuaiKinh, Integer> {
    
    /**
     * Tìm kiểu quai kính theo tên
     */
    KieuQuaiKinh findByTen(String ten);
}
