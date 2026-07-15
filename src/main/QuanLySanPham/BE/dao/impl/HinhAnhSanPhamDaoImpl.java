package BE.dao.impl;

import BE.Entity.HinhAnhSanPham;
import BE.dao.HinhAnhSanPhamDao;
import BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho HinhAnhSanPham
 */
public class HinhAnhSanPhamDaoImpl extends GenericDaoImpl<HinhAnhSanPham, Integer> implements HinhAnhSanPhamDao {
    
    public HinhAnhSanPhamDaoImpl() {
        super(HinhAnhSanPham.class);
    }
    
    /**
     * Lấy danh sách hình ảnh sản phẩm theo ID sản phẩm
     */
    @Override
    public List<HinhAnhSanPham> findBySanPhamId(Integer sanPhamId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT h FROM HinhAnhSanPham h WHERE h.sanPham.id = :sanPhamId";
            TypedQuery<HinhAnhSanPham> query = em.createQuery(jpql, HinhAnhSanPham.class);
            query.setParameter("sanPhamId", sanPhamId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy hình ảnh sản phẩm theo ID", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Xóa tất cả hình ảnh của một sản phẩm
     */
    @Override
    public void deleteBySanPhamId(Integer sanPhamId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            
            String jpql = "DELETE FROM HinhAnhSanPham h WHERE h.sanPham.id = :sanPhamId";
            em.createQuery(jpql)
              .setParameter("sanPhamId", sanPhamId)
              .executeUpdate();
            
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa hình ảnh sản phẩm", e);
        } finally {
            em.close();
        }
    }
}
