package QuanLySanPham.dao.impl;

import QuanLySanPham.Entity.HinhAnhSanPham;
import QuanLySanPham.Entity.SanPhamChiTiet;
import QuanLySanPham.dao.SanPhamChiTietDao;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            List<SanPhamChiTiet> results = query.getResultList();
            ganAnhHienThi(em, results);
            return results;
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
        return timKiemNoiBo(sanPhamId, ma, mauSacId, kichCoId, trangThai, null);
    }

    @Override
    public List<SanPhamChiTiet> timKiemTheoDanhMuc(String ma, Integer danhMucId, Integer trangThai) {
        return timKiemNoiBo(null, ma, null, null, trangThai, danhMucId);
    }

    private List<SanPhamChiTiet> timKiemNoiBo(Integer sanPhamId, String ma, Integer mauSacId,
                                               Integer kichCoId, Integer trangThai, Integer danhMucId) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT s FROM SanPhamChiTiet s " +
                            "LEFT JOIN FETCH s.sanPham sp " +
                            "LEFT JOIN FETCH sp.thuongHieu th " +
                            "LEFT JOIN FETCH s.mauSac " +
                            "LEFT JOIN FETCH s.kichCo " +
                            "WHERE s.isDeleted = false " +
                            "AND (sp.isDeleted = false OR sp.isDeleted IS NULL)"
            );

            if (sanPhamId != null) {
                jpql.append(" AND s.sanPham.id = :sanPhamId");
            }
            if (ma != null && !ma.trim().isEmpty()) {
                jpql.append(" AND (LOWER(s.ma) LIKE :ma"
                        + " OR LOWER(sp.maSanPham) LIKE :ma"
                        + " OR LOWER(sp.tenSanPham) LIKE :ma"
                        + " OR LOWER(th.maThuongHieu) LIKE :ma"
                        + " OR LOWER(th.tenThuongHieu) LIKE :ma)");
            }
            if (mauSacId != null) {
                jpql.append(" AND s.mauSac.id = :mauSacId");
            }
            if (kichCoId != null) {
                jpql.append(" AND s.kichCo.id = :kichCoId");
            }
            if (trangThai != null) {
                jpql.append(" AND s.trangThai = :trangThai AND sp.trangThai = :trangThai");
                if (trangThai == 1) {
                    jpql.append(" AND s.soLuongTon > 0");
                }
            }
            if (danhMucId != null) {
                jpql.append(" AND sp.danhMuc.id = :danhMucId");
            }

            TypedQuery<SanPhamChiTiet> query = em.createQuery(jpql.toString(), SanPhamChiTiet.class);

            if (sanPhamId != null) query.setParameter("sanPhamId", sanPhamId);
            if (ma != null && !ma.trim().isEmpty()) query.setParameter("ma", "%" + ma.trim().toLowerCase() + "%");
            if (mauSacId != null) query.setParameter("mauSacId", mauSacId);
            if (kichCoId != null) query.setParameter("kichCoId", kichCoId);
            if (trangThai != null) query.setParameter("trangThai", trangThai);
            if (danhMucId != null) query.setParameter("danhMucId", danhMucId);

            List<SanPhamChiTiet> results = query.getResultList();
            ganAnhHienThi(em, results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm chi tiết sản phẩm", e);
        } finally {
            em.close();
        }
    }

    private void ganAnhHienThi(EntityManager em, List<SanPhamChiTiet> sanPhamChiTiets) {
        if (sanPhamChiTiets == null || sanPhamChiTiets.isEmpty()) {
            return;
        }

        Set<Integer> sanPhamIdsCanLayAnh = new HashSet<>();
        for (SanPhamChiTiet spct : sanPhamChiTiets) {
            if (spct == null) {
                continue;
            }
            String anhBienThe = chuanHoaAnh(spct.getHinhAnh(), "File_Anh/images");
            if (anhBienThe != null) {
                spct.setHinhAnhHienThi(anhBienThe);
                continue;
            }
            if (spct.getSanPham() != null && spct.getSanPham().getId() != null) {
                sanPhamIdsCanLayAnh.add(spct.getSanPham().getId());
            }
        }

        if (sanPhamIdsCanLayAnh.isEmpty()) {
            return;
        }

        List<HinhAnhSanPham> hinhAnhs = em.createQuery(
                        "SELECT ha FROM HinhAnhSanPham ha "
                                + "WHERE ha.sanPham.id IN :sanPhamIds "
                                + "ORDER BY ha.sanPham.id, ha.isAnhChinh DESC, ha.id ASC",
                        HinhAnhSanPham.class)
                .setParameter("sanPhamIds", sanPhamIdsCanLayAnh)
                .getResultList();
        Map<Integer, String> anhTheoSanPham = new LinkedHashMap<>();
        for (HinhAnhSanPham hinhAnh : hinhAnhs) {
            if (hinhAnh.getSanPham() == null || hinhAnh.getSanPham().getId() == null) {
                continue;
            }
            String urlAnh = chuanHoaAnh(hinhAnh.getUrlAnh(), "FE/Admin/hinh_anh_san_pham");
            if (urlAnh != null) {
                anhTheoSanPham.putIfAbsent(hinhAnh.getSanPham().getId(), urlAnh);
            }
        }

        for (SanPhamChiTiet spct : sanPhamChiTiets) {
            if (spct.getHinhAnhHienThi() == null
                    && spct.getSanPham() != null
                    && spct.getSanPham().getId() != null) {
                spct.setHinhAnhHienThi(anhTheoSanPham.get(spct.getSanPham().getId()));
            }
        }
    }

    private String chuanHoaAnh(String hinhAnh, String thuMucMacDinh) {
        if (hinhAnh == null) {
            return null;
        }
        String trimmed = hinhAnh.trim().replace("\\", "/");
        if (trimmed.isEmpty() || "null".equalsIgnoreCase(trimmed)) {
            return null;
        }
        int fileAnhIndex = trimmed.lastIndexOf("File_Anh/images/");
        if (fileAnhIndex >= 0) {
            return trimmed.substring(fileAnhIndex);
        }
        int hinhAnhIndex = trimmed.lastIndexOf("hinh_anh_san_pham/");
        if (hinhAnhIndex >= 0) {
            return "FE/Admin/" + trimmed.substring(hinhAnhIndex);
        }
        return thuMucMacDinh + "/" + trimmed;
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
