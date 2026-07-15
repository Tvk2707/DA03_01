package main.QuanLySanPham.BE.dao;

import main.QuanLySanPham.BE.Entity.HinhDangGong;

/**
 * Interface DAO cho entity HinhDangGong
 */
public interface HinhDangGongDao extends GenericDao<HinhDangGong, Integer> {
    
    /**
     * Tìm hình dáng gọng theo tên
     */
    HinhDangGong findByTen(String ten);

}
