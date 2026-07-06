package BE.dao.impl;

import BE.Entity.SanPhamChiTiet;
import BE.dao.SanPhamChiTietDao;
import BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho SanPhamChiTiet
 */
public class SanPhamChiTietDaoImpl extends GenericDaoImpl<SanPhamChiTiet, Integer> implements SanPhamChiTietDao {
    
    public SanPhamChiTietDaoImpl() {
        super(SanPhamChiTiet.class);
    }
    
    /**
     * Lấy danh sách chi tiết sản phẩm theo ID sản phẩm
     */
    @Override
    public List<SanPhamChiTiet> findBySanPhamId(Integer sanPhamId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPhamChiTiet s WHERE s.sanPham.id = :sanPhamId";
            TypedQuery<SanPhamChiTiet> query = em.createQuery(jpql, SanPhamChiTiet.class);
            query.setParameter("sanPhamId", sanPhamId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy chi tiết sản phẩm theo ID", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Tìm chi tiết sản phẩm theo sản phẩm, màu sắc và kích cỡ
     */
    @Override
    public SanPhamChiTiet findByMauSacVaKichCo(Integer sanPhamId, Integer mauSacId, Integer kichCoId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPhamChiTiet s " +
                         "WHERE s.sanPham.id = :sanPhamId " +
                         "AND s.mauSac.id = :mauSacId " +
                         "AND s.kichCo.id = :kichCoId";
            TypedQuery<SanPhamChiTiet> query = em.createQuery(jpql, SanPhamChiTiet.class);
            query.setParameter("sanPhamId", sanPhamId);
            query.setParameter("mauSacId", mauSacId);
            query.setParameter("kichCoId", kichCoId);
            
            List<SanPhamChiTiet> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm chi tiết sản phẩm theo màu sắc và kích cỡ", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Cập nhật tồn kho cho chi tiết sản phẩm
     */
    @Override
    public void updateTonKho(Integer sanPhamChiTietId, Integer tonKhoMoi) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            
            SanPhamChiTiet sanPhamChiTiet = em.find(SanPhamChiTiet.class, sanPhamChiTietId);
            if (sanPhamChiTiet != null) {
                sanPhamChiTiet.setSoLuongTon(tonKhoMoi);
                em.merge(sanPhamChiTiet);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật tồn kho", e);
        } finally {
            em.close();
        }
    }
    @Override
    public List<SanPhamChiTiet> timKiem(Integer sanPhamId, String ma, Integer mauSacId, Integer kichCoId, Integer trangThai) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            // Sửa câu JPQL: Thêm LEFT JOIN FETCH để tải luôn dữ liệu màu sắc và kích cỡ
            StringBuilder jpql = new StringBuilder("SELECT s FROM SanPhamChiTiet s LEFT JOIN FETCH s.mauSac LEFT JOIN FETCH s.kichCo WHERE 1=1");

            if (sanPhamId != null) {
                jpql.append(" AND s.sanPham.id = :sanPhamId");
            }
            if (ma != null && !ma.trim().isEmpty()) {
                jpql.append(" AND LOWER(s.ma) LIKE :ma");
            }
            if (mauSacId != null) {
                jpql.append(" AND s.mauSac.id = :mauSacId");
            }
            if (kichCoId != null) {
                jpql.append(" AND s.kichCo.id = :kichCoId");
            }
            if (trangThai != null) {
                jpql.append(" AND s.trangThai = :trangThai");
            }

            TypedQuery<SanPhamChiTiet> query = em.createQuery(jpql.toString(), SanPhamChiTiet.class);

            if (sanPhamId != null) {
                query.setParameter("sanPhamId", sanPhamId);
            }
            if (ma != null && !ma.trim().isEmpty()) {
                query.setParameter("ma", "%" + ma.trim().toLowerCase() + "%");
            }
            if (mauSacId != null) {
                query.setParameter("mauSacId", mauSacId);
            }
            if (kichCoId != null) {
                query.setParameter("kichCoId", kichCoId);
            }
            if (trangThai != null) {
                query.setParameter("trangThai", trangThai);
            }

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm chi tiết sản phẩm", e);
        } finally {
            em.close();
        }
    }
}