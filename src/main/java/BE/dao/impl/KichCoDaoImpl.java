package BE.dao.impl;

import BE.Entity.KichCo;
import BE.dao.KichCoDao;
import BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho KichCo
 */
public class KichCoDaoImpl extends GenericDaoImpl<KichCo, Integer> implements KichCoDao {
    
    public KichCoDaoImpl() {
        super(KichCo.class);
    }
    
    /**
     * Tìm kích cỡ theo tên
     */
    @Override
    public KichCo findByTen(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT k FROM KichCo k WHERE k.tenKichCo = :ten";
            TypedQuery<KichCo> query = em.createQuery(jpql, KichCo.class);
            query.setParameter("ten", ten);
            List<KichCo> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kích cỡ theo tên", e);
        } finally {
            em.close();
        }
    }
}
