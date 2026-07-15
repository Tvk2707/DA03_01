package QuanLySanPham.dao.impl;

import QuanLySanPham.Entity.KieuQuaiKinh;
import QuanLySanPham.dao.KieuQuaiKinhDao;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho KieuQuaiKinh
 */
public class KieuQuaiKinhDaoImpl extends GenericDaoImpl<KieuQuaiKinh, Integer> implements KieuQuaiKinhDao {
    
    public KieuQuaiKinhDaoImpl() {
        super(KieuQuaiKinh.class);
    }
    
    /**
     * Tìm kiểu quai kính theo tên
     */
    @Override
    public KieuQuaiKinh findByTen(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT k FROM KieuQuaiKinh k WHERE k.class = :ten";
            TypedQuery<KieuQuaiKinh> query = em.createQuery(jpql, KieuQuaiKinh.class);
            query.setParameter("ten", ten);
            List<KieuQuaiKinh> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiểu quai kính theo tên", e);
        } finally {
            em.close();
        }
    }
}
