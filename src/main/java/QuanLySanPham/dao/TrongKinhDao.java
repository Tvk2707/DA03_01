package main.QuanLySanPham.BE.dao;

import main.QuanLySanPham.BE.Entity.TrongKinh;

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
