package main.QuanLySanPham.BE.dao.impl;

import main.QuanLySanPham.BE.Entity.ThuongHieu;
import main.QuanLySanPham.BE.dao.ThuongHieuDao;
import main.QuanLySanPham.BE.Utils.EntityManagerUtlis;
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
}
