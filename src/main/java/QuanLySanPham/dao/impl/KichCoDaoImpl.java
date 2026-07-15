package QuanLySanPham.dao.impl;

import QuanLySanPham.Entity.KichCo;
import QuanLySanPham.dao.KichCoDao;
import QuanLySanPham.Utils.EntityManagerUtlis;
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
    @Override
    public List<KichCo> searchByKeyword(String keyword) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT k FROM KichCo k WHERE " +
                    "LOWER(k.tenKichCo) LIKE LOWER(:keyword) " +
                    "ORDER BY k.id DESC";
            TypedQuery<KichCo> query = em.createQuery(jpql, KichCo.class);
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
