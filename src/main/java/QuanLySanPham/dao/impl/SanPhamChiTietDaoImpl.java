package QuanLySanPham.dao.impl;

import QuanLySanPham.Entity.SanPhamChiTiet;
import QuanLySanPham.dao.SanPhamChiTietDao;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class SanPhamChiTietDaoImpl extends GenericDaoImpl<SanPhamChiTiet, Integer> implements SanPhamChiTietDao {

    public SanPhamChiTietDaoImpl() {
        super(SanPhamChiTiet.class);
    }
    
    @Override
    public SanPhamChiTiet findByIdForUpdate(Integer id) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            // Sử dụng PESSIMISTIC_WRITE để khóa dòng dữ liệu, ngăn chặn xung đột khi nhiều giao dịch cùng lúc
            return em.find(SanPhamChiTiet.class, id, LockModeType.PESSIMISTIC_WRITE);
        } finally {
            // Lưu ý: KHÔNG đóng EntityManager ở đây nếu transaction còn tiếp diễn ở service
            // Service sẽ chịu trách nhiệm đóng EntityManager sau khi commit/rollback
        }
    }

    // --- HÀM XÓA MỀM 1 BIẾN THỂ (ĐÃ SỬA ĐỂ TRÁNH CACHE) ---
    public void softDelete(Integer id) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            SanPhamChiTiet entity = em.find(SanPhamChiTiet.class, id);
            if (entity != null) {
                entity.setIsDeleted(true);
                em.merge(entity);
                em.flush(); // ✅ ÉP GHI NGAY LẬP TỨC XUỐNG DATABASE

                // ✅ QUAN TRỌNG: XÓA ENTITY KHỎI CACHE ĐỂ LẦN QUERY SAU PHẢI LẤY TỪ DB
                em.detach(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa mềm chi tiết sản phẩm", e);
        } finally {
            em.close();
        }
    }

    // --- HÀM XÓA MỀM HÀNG LOẠT THEO SẢN PHẨM CHA ---
    public void softDeleteBySanPhamId(Integer sanPhamId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            String jpql = "UPDATE SanPhamChiTiet s SET s.isDeleted = true WHERE s.sanPham.id = :sanPhamId AND s.isDeleted = false";
            em.createQuery(jpql)
                    .setParameter("sanPhamId", sanPhamId)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa mềm hàng loạt chi tiết sản phẩm", e);
        } finally {
            em.close();
        }
    }

    // --- CÁC HÀM QUERY ĐÃ THÊM ĐIỀU KIỆN isDeleted = false ---

    @Override
    public List<SanPhamChiTiet> findBySanPhamId(Integer sanPhamId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPhamChiTiet s WHERE s.sanPham.id = :sanPhamId AND s.isDeleted = false";
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

    @Override
    public SanPhamChiTiet findByMauSacVaKichCo(Integer sanPhamId, Integer mauSacId, Integer kichCoId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT s FROM SanPhamChiTiet s " +
                    "WHERE s.sanPham.id = :sanPhamId " +
                    "AND s.mauSac.id = :mauSacId " +
                    "AND s.kichCo.id = :kichCoId " +
                    "AND s.isDeleted = false";
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
    public List<SanPhamChiTiet> timKiem(Integer sanPhamId, String ma, Integer mauSacId,
                                        Integer kichCoId, Integer trangThai) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            // ✅ BẮT BUỘC: Phải LEFT JOIN FETCH s.sanPham để JSP lấy được temp.sanPham.id
            StringBuilder jpql = new StringBuilder(
                    "SELECT s FROM SanPhamChiTiet s " +
                            "LEFT JOIN FETCH s.sanPham " +          // <-- THÊM DÒNG NÀY
                            "LEFT JOIN FETCH s.mauSac " +
                            "LEFT JOIN FETCH s.kichCo " +
                            "WHERE s.isDeleted = false"
            );

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

            if (sanPhamId != null) query.setParameter("sanPhamId", sanPhamId);
            if (ma != null && !ma.trim().isEmpty()) query.setParameter("ma", "%" + ma.trim().toLowerCase() + "%");
            if (mauSacId != null) query.setParameter("mauSacId", mauSacId);
            if (kichCoId != null) query.setParameter("kichCoId", kichCoId);
            if (trangThai != null) query.setParameter("trangThai", trangThai);

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm chi tiết sản phẩm", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<SanPhamChiTiet> saveAll(List<SanPhamChiTiet> danhSach) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        List<SanPhamChiTiet> result = new ArrayList<>();
        try {
            transaction.begin();
            for (SanPhamChiTiet spct : danhSach) {
                em.persist(spct);
                result.add(spct);
            }
            em.flush();
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lưu hàng loạt chi tiết sản phẩm", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<SanPhamChiTiet> updateAll(List<SanPhamChiTiet> danhSach) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        List<SanPhamChiTiet> result = new ArrayList<>();
        try {
            transaction.begin();
            for (SanPhamChiTiet spct : danhSach) {
                SanPhamChiTiet updated = em.merge(spct);
                result.add(updated);
            }
            em.flush();
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật hàng loạt chi tiết sản phẩm", e);
        } finally {
            em.close();
        }
    }
}