package BE.dao;

import BE.Entity.ChatLieu;

/**
 * Interface DAO cho entity ChatLieu
 */
public interface ChatLieuDao extends GenericDao<ChatLieu, Integer> {
    
    /**
     * Tìm chất liệu theo tên
     */
    ChatLieu findByTen(String ten);
}
