package BE.dao.impl;

import BE.Entity.DanhMuc;
import BE.dao.DanhMucDao;
import BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho DanhMuc
 */
public class DanhMucDaoImpl extends GenericDaoImpl<DanhMuc, Integer> implements DanhMucDao {
    
    public DanhMucDaoImpl() {
        super(DanhMuc.class);
    }
    
    /**
     * Tìm danh mục theo tên
     */
    @Override
    public DanhMuc findByTen(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT d FROM DanhMuc d WHERE d.tenDanhMuc = :ten";
            TypedQuery<DanhMuc> query = em.createQuery(jpql, DanhMuc.class);
            query.setParameter("ten", ten);
            List<DanhMuc> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm danh mục theo tên", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Tìm kiếm danh mục theo keyword (tìm trong mã hoặc tên)
     */
    @Override
    public List<DanhMuc> searchByKeyword(String keyword) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT d FROM DanhMuc d WHERE " +
                    "LOWER(d.maDanhMuc) LIKE LOWER(:keyword) OR " +
                    "LOWER(d.tenDanhMuc) LIKE LOWER(:keyword) " +
                    "ORDER BY d.id DESC";
            TypedQuery<DanhMuc> query = em.createQuery(jpql, DanhMuc.class);
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
