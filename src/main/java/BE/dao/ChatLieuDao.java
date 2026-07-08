package BE.dao;

import BE.Entity.ChatLieu;
import BE.Entity.DanhMuc;

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
