package BE.dao;

import BE.Entity.KieuDang;

/**
 * Interface DAO cho entity KieuDang
 */
public interface KieuDangDao extends GenericDao<KieuDang, Integer> {
    
    /**
     * Tìm kiểu dáng theo tên
     */
    KieuDang findByTen(String ten);
}
