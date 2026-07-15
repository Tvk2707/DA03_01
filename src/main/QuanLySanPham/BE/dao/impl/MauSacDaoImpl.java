package BE.dao.impl;

import BE.Entity.MauSac;
import BE.dao.MauSacDao;
import BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho MauSac
 */
public class MauSacDaoImpl extends GenericDaoImpl<MauSac, Integer> implements MauSacDao {
    
    public MauSacDaoImpl() {
        super(MauSac.class);
    }
    
    /**
     * Tìm màu sắc theo tên
     */
    @Override
    public MauSac findByTen(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT m FROM MauSac m WHERE m.tenMau = :ten";
            TypedQuery<MauSac> query = em.createQuery(jpql, MauSac.class);
            query.setParameter("ten", ten);
            List<MauSac> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm màu sắc theo tên", e);
        } finally {
            em.close();
        }
    }
    @Override
    public List<MauSac> searchByKeyword(String keyword) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT m FROM MauSac m WHERE " +
                    "LOWER(m.tenMau) LIKE LOWER(:keyword) " +
                    "ORDER BY m.id DESC";
            TypedQuery<MauSac> query = em.createQuery(jpql, MauSac.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm màu sắc", e);
        } finally {
            em.close();
        }
    }
}
