package QuanLySanPham.dao.impl;

import QuanLySanPham.Entity.SanPham;
import QuanLySanPham.dao.SanPhamDao;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDaoImpl extends GenericDaoImpl<SanPham, Integer> implements SanPhamDao {

    public SanPhamDaoImpl() {
        super(SanPham.class);
    }

        // ❌ BỎ @Override Ở ĐÂY (vì có thể GenericDao không khai báo hàm delete)
    public void softDelete(Integer id) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            SanPham sanPham = em.find(SanPham.class, id);
            if (sanPham != null) {
                sanPham.setIsDeleted(true);
                em.merge(sanPham);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa mềm sản phẩm", e);
        } finally {
            em.close();
        }
    }

    // ❌ BỎ @Override Ở ĐÂY (vì có thể GenericDao không khai báo hàm findAll)
    public List<SanPham> findAll() {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPham s WHERE s.isDeleted = false ORDER BY s.id DESC";
            TypedQuery<SanPham> query = em.createQuery(jpql, SanPham.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy tất cả sản phẩm", e);
        } finally {
            em.close();
        }
    }

    // ❌ BỎ @Override Ở ĐÂY (vì có thể GenericDao không khai báo hàm findById)
    public SanPham findById(Integer id) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPham s WHERE s.id = :id AND s.isDeleted = false";
            TypedQuery<SanPham> query = em.createQuery(jpql, SanPham.class);
            query.setParameter("id", id);
            List<SanPham> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm sản phẩm theo id", e);
        } finally {
            em.close();
        }
    }

    // ✅ GIỮ NGUYÊN @Override vì các hàm này CÓ trong interface SanPhamDao
    @Override
    public SanPham findByTenSanPham(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPham s WHERE s.tenSanPham = :ten AND s.isDeleted = false";
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

    @Override
    public List<SanPham> findWithPaging(int pageNumber, int pageSize) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT s FROM SanPham s " +
                    "LEFT JOIN FETCH s.danhMuc " +
                    "LEFT JOIN FETCH s.thuongHieu " +
                    "LEFT JOIN FETCH s.chatLieu " +
                    "LEFT JOIN FETCH s.kieuDang " +
                    "LEFT JOIN FETCH s.gongKinh gk " +
                    "LEFT JOIN FETCH gk.hinhDangGong " +
                    "LEFT JOIN FETCH gk.kieuQuaiKinh " +
                    "LEFT JOIN FETCH s.trongKinh " +
                    "WHERE s.isDeleted = false " +
                    "ORDER BY s.id ";
            TypedQuery<SanPham> query = em.createQuery(jpql, SanPham.class);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy dữ liệu với phân trang", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<SanPham> findByDanhMuc(Integer danhMucId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPham s WHERE s.danhMuc.id = :danhMucId AND s.isDeleted = false";
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

    @Override
    public List<SanPham> findByThuongHieu(Integer thuongHieuId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPham s WHERE s.thuongHieu.id = :thuongHieuId AND s.isDeleted = false";
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

    @Override
    public List<SanPham> search(String tenSanPham, Integer danhMucId, Integer thuongHieuId,
                                Double giaMin, Double giaMax) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT s FROM SanPham s " +
                            "LEFT JOIN FETCH s.danhMuc " +
                            "LEFT JOIN FETCH s.thuongHieu " +
                            "LEFT JOIN FETCH s.chatLieu " +
                            "LEFT JOIN FETCH s.kieuDang " +
                            "LEFT JOIN FETCH s.gongKinh gk " +
                            "LEFT JOIN FETCH gk.hinhDangGong " +
                            "LEFT JOIN FETCH gk.kieuQuaiKinh " +
                            "LEFT JOIN FETCH s.trongKinh " +
                            "WHERE s.isDeleted = false");
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