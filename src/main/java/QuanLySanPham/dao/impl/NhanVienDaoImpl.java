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
    public List<NhanVien> search(String tuKhoa) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql;
            TypedQuery<NhanVien> q;
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                jpql = "SELECT n FROM NhanVien n WHERE n.trangThai = 1 AND (" +
                       "LOWER(n.hoTen) LIKE :kw OR " +
                       "LOWER(n.maNhanVien) LIKE :kw OR " +
                       "LOWER(n.email) LIKE :kw) " +
                       "ORDER BY n.id DESC";
                q = em.createQuery(jpql, NhanVien.class);
                q.setParameter("kw", "%" + tuKhoa.trim().toLowerCase() + "%");
            } else {
                jpql = "SELECT n FROM NhanVien n WHERE n.trangThai = 1 ORDER BY n.id DESC";
                q = em.createQuery(jpql, NhanVien.class);
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

    @Override
    public NhanVien findByEmail(String email) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            TypedQuery<NhanVien> q = em.createQuery("SELECT n FROM NhanVien n WHERE n.email = :email", NhanVien.class);
            q.setParameter("email", email);
            List<NhanVien> list = q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }
    /**
     * Override findWithPaging để chỉ lấy nhân viên đang hoạt động (trangThai = 1)
     * Nhân viên đã xóa mềm (trangThai = 0) sẽ không hiện trong danh sách
     */
    @Override
    public List<NhanVien> findWithPaging(int pageNumber, int pageSize) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            TypedQuery<NhanVien> q = em.createQuery(
                "SELECT n FROM NhanVien n WHERE n.trangThai = 1 ORDER BY n.id DESC", NhanVien.class);
            q.setFirstResult((pageNumber - 1) * pageSize);
            q.setMaxResults(pageSize);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public String findMaxMaNhanVien() {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            // SQL Server: sắp xếp theo phần số thay vì theo chuỗi để MNV10000 > MNV9999.
            // Không lọc trạng thái để mã của nhân viên đã vô hiệu hóa cũng không bị tái sử dụng.
            List<?> result = em.createNativeQuery(
                    "SELECT TOP 1 ma_nhan_vien " +
                    "FROM nhan_vien " +
                    "WHERE ma_nhan_vien LIKE 'MNV%' " +
                    "AND TRY_CONVERT(INT, SUBSTRING(ma_nhan_vien, 4, LEN(ma_nhan_vien) - 3)) IS NOT NULL " +
                    "ORDER BY TRY_CONVERT(INT, SUBSTRING(ma_nhan_vien, 4, LEN(ma_nhan_vien) - 3)) DESC")
                .getResultList();
            return result.isEmpty() ? null : String.valueOf(result.get(0));
        } finally {
            em.close();
        }
    }
}

