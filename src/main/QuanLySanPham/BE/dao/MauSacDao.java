package BE.dao;

import BE.Entity.MauSac;

import java.util.List;

/**
 * Interface DAO cho entity MauSac
 */
public interface MauSacDao extends GenericDao<MauSac, Integer> {
    
    /**
     * Tìm màu sắc theo tên
     */
    MauSac findByTen(String ten);
    List<MauSac> searchByKeyword(String keyword);

}
