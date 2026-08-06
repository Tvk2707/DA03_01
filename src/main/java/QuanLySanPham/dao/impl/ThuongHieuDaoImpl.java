package QuanLySanPham.dao.impl;

import QuanLySanPham.Entity.ThuongHieu;
import QuanLySanPham.dao.ThuongHieuDao;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho ThuongHieu
 */
public class ThuongHieuDaoImpl extends GenericDaoImpl<ThuongHieu, Integer> implements ThuongHieuDao {
    
    public ThuongHieuDaoImpl() {
        super(ThuongHieu.class);
    }

    /**
     * Lấy tất cả thương hiệu, sắp xếp mới nhất lên đầu
     */
    @Override
    public List<ThuongHieu> findAll() {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT t FROM ThuongHieu t ORDER BY t.id DESC";
            TypedQuery<ThuongHieu> query = em.createQuery(jpql, ThuongHieu.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy tất cả thương hiệu", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Tìm thương hiệu theo tên
     */
    @Override
    public ThuongHieu findByTen(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT t FROM ThuongHieu t WHERE t.tenThuongHieu = :ten";
            TypedQuery<ThuongHieu> query = em.createQuery(jpql, ThuongHieu.class);
            query.setParameter("ten", ten);
            List<ThuongHieu> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm thương hiệu theo tên", e);
        } finally {
            em.close();
        }
    }
    @Override
    public List<ThuongHieu> searchByKeyword(String keyword) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT t FROM ThuongHieu t WHERE " +
                    "LOWER(t.tenThuongHieu) LIKE LOWER(:keyword) " +
                    "ORDER BY t.id DESC";
            TypedQuery<ThuongHieu> query = em.createQuery(jpql, ThuongHieu.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm thương hiệu", e);
        } finally {
            em.close();
        }
    }

    @Override
    public String generateNextMaThuongHieu() {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT MAX(t.maThuongHieu) FROM ThuongHieu t WHERE t.maThuongHieu LIKE 'TH%'";
            String maxMa = (String) em.createQuery(jpql).getSingleResult();
            if (maxMa == null) {
                return "TH001";
            }
            int num = Integer.parseInt(maxMa.substring(2)) + 1;
            return String.format("TH%03d", num);
        } catch (Exception e) {
            return "TH001";
        } finally {
            em.close();
        }
    }
}
