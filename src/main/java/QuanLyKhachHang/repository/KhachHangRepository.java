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

    public List<KhachHang> timKiemTheoTenHoacSoDienThoai(String tuKhoa) {
        EntityManager em = null;
        try {
            em = utils.getEntityManager();
            return em.createQuery(
                            "SELECT kh FROM KhachHang kh "
                                    + "WHERE (kh.trangThai IS NULL OR kh.trangThai = 1) "
                                    + "AND (LOWER(kh.hoTen) LIKE LOWER(:tuKhoa) "
                                    + "OR LOWER(kh.maKhachHang) LIKE LOWER(:tuKhoa) "
                                    + "OR kh.soDienThoai LIKE :tuKhoa) "
                                    + "ORDER BY kh.hoTen ASC",
                            KhachHang.class)
                    .setParameter("tuKhoa", "%" + tuKhoa + "%")
                    .setMaxResults(10)
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

    public void add(KhachHang khachHang) {
        EntityManager em = null;
        EntityTransaction tran = null;

        try {
            em = utils.getEntityManager();
            tran = em.getTransaction();
            tran.begin();
            em.persist(khachHang);
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
