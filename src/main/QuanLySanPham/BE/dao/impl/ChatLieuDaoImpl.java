package BE.dao.impl;

import BE.Entity.ChatLieu;
import BE.dao.ChatLieuDao;
import BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho ChatLieu
 */
public class ChatLieuDaoImpl extends GenericDaoImpl<ChatLieu, Integer> implements ChatLieuDao {
    
    public ChatLieuDaoImpl() {
        super(ChatLieu.class);
    }
    
    /**
     * Tìm chất liệu theo tên
     */
    @Override
    public ChatLieu findByTen(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT c FROM ChatLieu c WHERE c.tenChatLieu = :ten";
            TypedQuery<ChatLieu> query = em.createQuery(jpql, ChatLieu.class);
            query.setParameter("ten", ten);
            List<ChatLieu> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm chất liệu theo tên", e);
        } finally {
            em.close();
        }

    }
        @Override
        public List<ChatLieu> searchByKeyword(String keyword) {
            EntityManager em = EntityManagerUtlis.getEntityManager();
            try {
                String jpql = "SELECT c FROM ChatLieu c WHERE " +
                        "LOWER(c.tenChatLieu) LIKE LOWER(:keyword) " +
                        "ORDER BY c.id DESC";
                TypedQuery<ChatLieu> query = em.createQuery(jpql, ChatLieu.class);
                query.setParameter("keyword", "%" + keyword + "%");
                return query.getResultList();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi tìm kiếm danh mục", e);
            } finally {
                em.close();
            }
        }
}

