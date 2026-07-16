package QuanLySanPham.dao;

import QuanLySanPham.Entity.ChatLieu;

import java.util.List;

/**
 * Interface DAO cho entity ChatLieu
 */
public interface ChatLieuDao extends GenericDao<ChatLieu, Integer> {
    
    /**
     * Tìm chất liệu theo tên
     */
    ChatLieu findByTen(String ten);
    List<ChatLieu> searchByKeyword(String keyword);

}
