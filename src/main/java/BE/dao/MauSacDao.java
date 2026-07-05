package BE.dao;

import BE.Entity.MauSac;

/**
 * Interface DAO cho entity MauSac
 */
public interface MauSacDao extends GenericDao<MauSac, Integer> {
    
    /**
     * Tìm màu sắc theo tên
     */
    MauSac findByTen(String ten);
}
