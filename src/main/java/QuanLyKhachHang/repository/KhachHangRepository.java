package QuanLyKhachHang.repository;

import QuanLySanPham.Utils.EntityManagerUtlis;
import QuanLySanPham.Entity.KhachHang;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class KhachHangRepository {

    private EntityManagerUtlis utils = new EntityManagerUtlis();

    public List<KhachHang> getAll() {
        EntityManager em = null;
        try {
            em = utils.getEntityManager();
            return em.createQuery("SELECT kh FROM KhachHang kh ORDER BY kh.id DESC", KhachHang.class)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public KhachHang findBySoDienThoai(String sdt) {
        EntityManager em = null;
        try {
            em = utils.getEntityManager();
            TypedQuery<KhachHang> query = em.createQuery("SELECT kh FROM KhachHang kh WHERE kh.soDienThoai = :sdt", KhachHang.class);
            query.setParameter("sdt", sdt);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<KhachHang> findByKeyword(String keyword) {
        EntityManager em = null;
        try {
            em = utils.getEntityManager();
            String normalized = keyword == null ? "" : keyword.trim().toLowerCase();
            if (normalized.isEmpty()) {
                return new ArrayList<>();
            }
            return em.createQuery(
                            "SELECT kh FROM KhachHang kh "
                                    + "WHERE (kh.trangThai IS NULL OR kh.trangThai = 1) "
                                    + "AND (LOWER(kh.soDienThoai) LIKE :contains "
                                    + "OR LOWER(kh.maKhachHang) LIKE :contains "
                                    + "OR LOWER(kh.hoTen) LIKE :contains) "
                                    + "ORDER BY CASE WHEN LOWER(kh.soDienThoai) = :exact "
                                    + "OR LOWER(kh.maKhachHang) = :exact THEN 0 "
                                    + "WHEN LOWER(kh.soDienThoai) LIKE :prefix "
                                    + "OR LOWER(kh.maKhachHang) LIKE :prefix THEN 1 ELSE 2 END, kh.id DESC",
                            KhachHang.class)
                    .setParameter("exact", normalized)
                    .setParameter("contains", "%" + normalized + "%")
                    .setParameter("prefix", normalized + "%")
                    .setMaxResults(8)
                    .getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void add(KhachHang khachHang) {
        if (khachHang == null) {
            throw new IllegalArgumentException("Khách hàng không được để trống.");
        }
        EntityManager em = null;
        EntityTransaction tran = null;

        try {
            em = utils.getEntityManager();
            tran = em.getTransaction();
            tran.begin();

            if (khachHang.getMaKhachHang() == null || khachHang.getMaKhachHang().trim().isEmpty()) {
                Number soThuTu = (Number) em.createNativeQuery(
                                "SELECT COALESCE(MAX(TRY_CONVERT(int, SUBSTRING(ma_khach_hang, 3, 48))), 0) + 1 "
                                        + "FROM khach_hang WITH (UPDLOCK, HOLDLOCK) "
                                        + "WHERE ma_khach_hang LIKE 'KH%'")
                        .getSingleResult();
                int maTiepTheo = soThuTu == null ? 1 : soThuTu.intValue();
                khachHang.setMaKhachHang(String.format("KH%03d", maTiepTheo));
            } else {
                khachHang.setMaKhachHang(khachHang.getMaKhachHang().trim());
            }

            em.persist(khachHang);
            em.flush();
            tran.commit();
        } catch (Exception e) {
            if (tran != null && tran.isActive()) {
                tran.rollback();
            }
            throw new IllegalStateException("Không thể lưu khách hàng vào database.", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void doiTrangThai(Integer id) {
        EntityManager em = null;
        EntityTransaction tran = null;

        try {
            em = utils.getEntityManager();
            tran = em.getTransaction();
            tran.begin();

            KhachHang kh = em.find(KhachHang.class, id);

            if (kh != null) {
                if (kh.getTrangThai() == null || kh.getTrangThai() == 0) {
                    kh.setTrangThai(1);
                } else {
                    kh.setTrangThai(0);
                }
            }

            tran.commit();
        } catch (Exception e) {
            if (tran != null && tran.isActive()) {
                tran.rollback();
            }
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
