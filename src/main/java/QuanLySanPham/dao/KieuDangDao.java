package main.QuanLySanPham.BE.dao;

import main.QuanLySanPham.BE.Entity.KieuDang;

import java.util.List;

/**
 * Interface DAO cho entity KieuDang
 */
public interface KieuDangDao extends GenericDao<KieuDang, Integer> {
    
    /**
     * Tìm kiểu dáng theo tên
     */
    KieuDang findByTen(String ten);
    List<KieuDang> searchByKeyword(String keyword);

}
