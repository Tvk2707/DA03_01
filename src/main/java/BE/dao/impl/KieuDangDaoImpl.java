package BE.dao.impl;

import BE.Entity.KieuDang;
import BE.dao.KieuDangDao;
import BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho KieuDang
 */
public class KieuDangDaoImpl extends GenericDaoImpl<KieuDang, Integer> implements KieuDangDao {
    
    public KieuDangDaoImpl() {
        super(KieuDang.class);
    }
    
    /**
     * Tìm kiểu dáng theo tên
     */
    @Override
    public KieuDang findByTen(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT k FROM KieuDang k WHERE k.tenKieuDang = :ten";
            TypedQuery<KieuDang> query = em.createQuery(jpql, KieuDang.class);
            query.setParameter("ten", ten);
            List<KieuDang> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiểu dáng theo tên", e);
        } finally {
            em.close();
        }
    }
}
