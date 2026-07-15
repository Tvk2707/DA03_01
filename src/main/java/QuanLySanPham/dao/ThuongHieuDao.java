package main.QuanLySanPham.BE.dao;

import main.QuanLySanPham.BE.Entity.ThuongHieu;

import java.util.List;

/**
 * Interface DAO cho entity ThuongHieu
 */
public interface ThuongHieuDao extends GenericDao<ThuongHieu, Integer> {
    
    /**
     * Tìm thương hiệu theo tên
     */
    ThuongHieu findByTen(String ten);
    List<ThuongHieu> searchByKeyword(String keyword);

}
