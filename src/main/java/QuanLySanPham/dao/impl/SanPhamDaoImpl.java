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
                    "ORDER BY s.id desc";
            TypedQuery<SanPham> query = em.createQuery(jpql, SanPham.class);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);

            List<SanPham> list = query.getResultList();

            // Gọi hàm tính toán số lượng và giá của biến thể
            this.setThongTinBienThe(list);

            return list;
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
            // 1. Khởi tạo câu JPQL chính: Cần JOIN sang SanPhamChiTiet (đặt alias là ct) để tính khoảng giá
            StringBuilder jpql = new StringBuilder(
                    "SELECT s FROM SanPham s " +
                            "LEFT JOIN FETCH s.danhMuc " +
                            "LEFT JOIN FETCH s.thuongHieu " +
                            "LEFT JOIN FETCH s.chatLieu " +
                            "LEFT JOIN FETCH s.kieuDang " +
                            "LEFT JOIN FETCH s.gongKinh gk " +
                            "LEFT JOIN FETCH gk.hinhDangGong " +
                            "LEFT JOIN FETCH gk.kieuQuaiKinh " +
                            "LEFT JOIN FETCH s.trongKinh " +
                            "WHERE s.isDeleted = false"
            );

            // 2. Thêm các điều kiện lọc cơ bản
            if (tenSanPham != null && !tenSanPham.trim().isEmpty()) {
                jpql.append(" AND LOWER(s.tenSanPham) LIKE LOWER(:tenSanPham)");
            }
            if (danhMucId != null) {
                jpql.append(" AND s.danhMuc.id = :danhMucId");
            }
            if (thuongHieuId != null) {
                jpql.append(" AND s.thuongHieu.id = :thuongHieuId");
            }

            // 3. Sử dụng SUBQUERY với HAVING để lọc chính xác khoảng giá dựa trên bảng con SanPhamChiTiet
            if (giaMin != null || giaMax != null) {
                jpql.append(" AND s.id IN ( " +
                        "    SELECT ct.sanPham.id FROM SanPhamChiTiet ct " +
                        "    WHERE ct.isDeleted = false " +
                        "    GROUP BY ct.sanPham.id " +
                        "    HAVING 1=1 ");
                if (giaMin != null) {
                    jpql.append("    AND MIN(ct.giaBan) >= :giaMin ");
                }
                if (giaMax != null) {
                    jpql.append("    AND MIN(ct.giaBan) <= :giaMax ");
                }
                jpql.append(")");
            }

            TypedQuery<SanPham> query = em.createQuery(jpql.toString(), SanPham.class);

            // 4. Truyền tham số (Set Parameter) vào câu Query
            if (tenSanPham != null && !tenSanPham.trim().isEmpty()) {
                query.setParameter("tenSanPham", "%" + tenSanPham + "%");
            }
            if (danhMucId != null) {
                query.setParameter("danhMucId", danhMucId);
            }
            if (thuongHieuId != null) {
                query.setParameter("thuongHieuId", thuongHieuId);
            }

            // Truyền tham số khoảng giá kiểu BigDecimal (vì cột giaBan trong DB của bạn dùng BigDecimal)
            if (giaMin != null) {
                query.setParameter("giaMin", java.math.BigDecimal.valueOf(giaMin));
            }
            if (giaMax != null) {
                query.setParameter("giaMax", java.math.BigDecimal.valueOf(giaMax));
            }

            List<SanPham> list = query.getResultList();

            // 5. Gọi hàm tính toán số lượng và hiển thị chuỗi giá MIN, MAX như cũ
            this.setThongTinBienThe(list);

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm sản phẩm", e);
        } finally {
            em.close();
        }
    }

    // tính toán sô luong giá min , max bên bảng sản phẩm biến thể
    private void setThongTinBienThe(List<SanPham> list) {
        if (list == null || list.isEmpty()) return;

        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            for (SanPham sp : list) {
                String jpql = "SELECT COALESCE(SUM(ct.soLuongTon), 0), " +
                        "MIN(ct.giaBan), " +
                        "MAX(ct.giaBan) " +
                        "FROM SanPhamChiTiet ct " +
                        "WHERE ct.sanPham.id = :sanPhamId AND ct.isDeleted = false";

                Object[] result = (Object[]) em.createQuery(jpql)
                        .setParameter("sanPhamId", sp.getId())
                        .getSingleResult();

                // 1. Gán tổng số lượng tồn (Xử lý kiểu Long trả về từ SUM trong JPQL)
                if (result[0] != null) {
                    sp.setTongSoLuong(((Long) result[0]).intValue());
                } else {
                    sp.setTongSoLuong(0);
                }

                // 2. Gán giá nhỏ nhất và lớn nhất (Xử lý kiểu BigDecimal từ Entity)
                java.math.BigDecimal giaMinBd = (java.math.BigDecimal) result[1];
                java.math.BigDecimal giaMaxBd = (java.math.BigDecimal) result[2];

                sp.setGiaMin(giaMinBd != null ? giaMinBd.doubleValue() : 0.0);
                sp.setGiaMax(giaMaxBd != null ? giaMaxBd.doubleValue() : 0.0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}