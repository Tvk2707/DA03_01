package BE.dao.impl;

import BE.Entity.GongKinh;
import BE.dao.GongKinhDao;
import BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho GongKinh
 */
public class GongKinhDaoImpl extends GenericDaoImpl<GongKinh, Integer> implements GongKinhDao {
    
    public GongKinhDaoImpl() {
        super(GongKinh.class);
    }
    

    @Override
    public GongKinh findByTen(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT g FROM GongKinh g WHERE g.id = :ten";
            TypedQuery<GongKinh> query = em.createQuery(jpql, GongKinh.class);
            query.setParameter("ten", ten);
            List<GongKinh> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm gọng kính theo tên", e);
        } finally {
            em.close();
        }
    }
}
