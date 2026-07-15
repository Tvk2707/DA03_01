package main.QuanLySanPham.BE.dao.impl;

import main.QuanLySanPham.BE.Entity.GongKinh;
import main.QuanLySanPham.BE.dao.GongKinhDao;
import main.QuanLySanPham.BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class GongKinhDaoImpl extends GenericDaoImpl<GongKinh, Integer> implements GongKinhDao {

    public GongKinhDaoImpl() {
        super(GongKinh.class);
    }

    @Override
    public GongKinh findByTen(String ten) {
        // GongKinh không có thuộc tính "tên" nên không áp dụng, không dùng ở đâu trong luồng hiện tại
        return null;
    }

    @Override
    public List<GongKinh> findAll() {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT g FROM GongKinh g " +
                    "JOIN FETCH g.hinhDangGong " +
                    "JOIN FETCH g.kieuQuaiKinh " +
                    "ORDER BY g.id asc ";
            TypedQuery<GongKinh> query = em.createQuery(jpql, GongKinh.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy toàn bộ gọng kính", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<GongKinh> searchByKeyword(String keyword) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT g FROM GongKinh g " +
                    "JOIN FETCH g.hinhDangGong h " +
                    "JOIN FETCH g.kieuQuaiKinh k " +
                    "WHERE LOWER(h.hinhDang) LIKE LOWER(:keyword) OR " +
                    "LOWER(k.kieuQuai) LIKE LOWER(:keyword) " +
                    "ORDER BY g.id ";
            TypedQuery<GongKinh> query = em.createQuery(jpql, GongKinh.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm gọng kính", e);
        } finally {
            em.close();
        }
    }
}