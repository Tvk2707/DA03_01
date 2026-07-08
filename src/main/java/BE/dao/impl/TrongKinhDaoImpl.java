package BE.dao.impl;

import BE.Entity.DanhMuc;
import BE.Entity.TrongKinh;
import BE.dao.TrongKinhDao;
import BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho TrongKinh
 */
public class TrongKinhDaoImpl extends GenericDaoImpl<TrongKinh, Integer> implements TrongKinhDao {
    
    public TrongKinhDaoImpl() {
        super(TrongKinh.class);
    }
    
    /**
     * Tìm trong kính theo tên
     */
    @Override
    public TrongKinh findByTen(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT t FROM TrongKinh t WHERE t.loaiTrong = :ten";
            TypedQuery<TrongKinh> query = em.createQuery(jpql, TrongKinh.class);
            query.setParameter("ten", ten);
            List<TrongKinh> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm trong kính theo tên", e);
        } finally {
            em.close();
        }
    }
    @Override
    public List<TrongKinh> searchByKeyword(String keyword) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT t FROM TrongKinh t WHERE " +
                    "LOWER(t.loaiTrong) LIKE LOWER(:keyword) " +
                    "ORDER BY t.id DESC";
            TypedQuery<TrongKinh> query = em.createQuery(jpql, TrongKinh.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm trong kính", e);
        } finally {
            em.close();
        }
    }
}
