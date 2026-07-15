package main.QuanLySanPham.BE.dao;

import main.QuanLySanPham.BE.Entity.KieuQuaiKinh;

/**
 * Interface DAO cho entity KieuQuaiKinh
 */
public interface KieuQuaiKinhDao extends GenericDao<KieuQuaiKinh, Integer> {
    
    /**
     * Tìm kiểu quai kính theo tên
     */
    KieuQuaiKinh findByTen(String ten);

}
