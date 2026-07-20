package QuanLySanPham.dao.impl;

import QuanLySanPham.dao.NhanVienDao;
import QuanLySanPham.Entity.NhanVien;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class NhanVienDaoImpl extends GenericDaoImpl<NhanVien, Integer> implements NhanVienDao {

    public NhanVienDaoImpl() {
        super(NhanVien.class);
    }

    @Override
    public NhanVien findByMaNhanVien(String maNhanVien) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            TypedQuery<NhanVien> q = em.createQuery("SELECT n FROM NhanVien n WHERE n.maNhanVien = :ma", NhanVien.class);
            q.setParameter("ma", maNhanVien);
            List<NhanVien> list = q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<NhanVien> search(String hoTen, String email) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT n FROM NhanVien n WHERE 1=1");
            if (hoTen != null && !hoTen.trim().isEmpty()) {
                jpql.append(" AND LOWER(n.hoTen) LIKE :hoTen");
            }
            if (email != null && !email.trim().isEmpty()) {
                jpql.append(" AND LOWER(n.email) LIKE :email");
            }
            TypedQuery<NhanVien> q = em.createQuery(jpql.toString(), NhanVien.class);
            if (hoTen != null && !hoTen.trim().isEmpty()) {
                q.setParameter("hoTen", "%" + hoTen.toLowerCase() + "%");
            }
            if (email != null && !email.trim().isEmpty()) {
                q.setParameter("email", "%" + email.toLowerCase() + "%");
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void softDelete(Integer id) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            NhanVien nv = em.find(NhanVien.class, id);
            if (nv != null) {
                nv.setTrangThai(0); // mark inactive
                em.merge(nv);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
}

