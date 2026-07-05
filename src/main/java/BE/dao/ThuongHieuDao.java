package BE.dao;

import BE.Entity.ThuongHieu;

/**
 * Interface DAO cho entity ThuongHieu
 */
public interface ThuongHieuDao extends GenericDao<ThuongHieu, Integer> {
    
    /**
     * Tìm thương hiệu theo tên
     */
    ThuongHieu findByTen(String ten);
}
