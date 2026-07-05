package BE.dao.impl;

import BE.Entity.SanPham;
import BE.dao.SanPhamDao;
import BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO cài đặt cho SanPham
 */
public class SanPhamDaoImpl extends GenericDaoImpl<SanPham, Integer> implements SanPhamDao {
    
    public SanPhamDaoImpl() {
        super(SanPham.class);
    }
    
    /**
     * Tìm sản phẩm theo tên
     */
    @Override
    public SanPham findByTenSanPham(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPham s WHERE s.tenSanPham = :ten";
            TypedQuery<SanPham> query = em.createQuery(jpql, SanPham.class);
            query.setParameter("ten", ten);
            List<SanPham> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm sản phẩm theo tên", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Lấy danh sách sản phẩm theo danh mục
     */
    @Override
    public List<SanPham> findByDanhMuc(Integer danhMucId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPham s WHERE s.danhMuc.id = :danhMucId";
            TypedQuery<SanPham> query = em.createQuery(jpql, SanPham.class);
            query.setParameter("danhMucId", danhMucId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm sản phẩm theo danh mục", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Lấy danh sách sản phẩm theo thương hiệu
     */
    @Override
    public List<SanPham> findByThuongHieu(Integer thuongHieuId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPham s WHERE s.thuongHieu.id = :thuongHieuId";
            TypedQuery<SanPham> query = em.createQuery(jpql, SanPham.class);
            query.setParameter("thuongHieuId", thuongHieuId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm sản phẩm theo thương hiệu", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Tìm kiếm sản phẩm theo tên, danh mục, thương hiệu, và khoảng giá
     * Các tham số có thể null - nếu null thì bỏ qua điều kiện đó
     */
    @Override
    public List<SanPham> search(String tenSanPham, Integer danhMucId, Integer thuongHieuId,
                                Double giaMin, Double giaMax) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT s FROM SanPham s WHERE 1=1");
            List<String> conditions = new ArrayList<>();
            
            if (tenSanPham != null && !tenSanPham.trim().isEmpty()) {
                conditions.add("LOWER(s.tenSanPham) LIKE LOWER(:tenSanPham)");
            }
            if (danhMucId != null) {
                conditions.add("s.danhMuc.id = :danhMucId");
            }
            if (thuongHieuId != null) {
                conditions.add("s.thuongHieu.id = :thuongHieuId");
            }
            
            for (String condition : conditions) {
                jpql.append(" AND ").append(condition);
            }
            
            TypedQuery<SanPham> query = em.createQuery(jpql.toString(), SanPham.class);
            
            if (tenSanPham != null && !tenSanPham.trim().isEmpty()) {
                query.setParameter("tenSanPham", "%" + tenSanPham + "%");
            }
            if (danhMucId != null) {
                query.setParameter("danhMucId", danhMucId);
            }
            if (thuongHieuId != null) {
                query.setParameter("thuongHieuId", thuongHieuId);
            }
            
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm sản phẩm", e);
        } finally {
            em.close();
        }
    }
}
