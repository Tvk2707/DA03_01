package QuanLyKhachHang.repository;

import QuanLySanPham.Utils.EntityManagerUtlis;
import QuanLySanPham.Entity.DiaChiKhachHang;
import QuanLySanPham.Entity.KhachHang;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.List;

public class DiaChiKhachHangRepository {

    private EntityManagerUtlis utils = new EntityManagerUtlis();

    public List<DiaChiKhachHang> getByKhachHangId(Integer idKhachHang) {
        EntityManager em = null;
        try {
            em = utils.getEntityManager();
            return em.createQuery("SELECT dc FROM DiaChiKhachHang dc WHERE dc.khachHang.id = :idKhachHang ORDER BY dc.id DESC", DiaChiKhachHang.class)
                    .setParameter("idKhachHang", idKhachHang)
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

    public void add(Integer idKhachHang, DiaChiKhachHang diaChi) {
        EntityManager em = null;
        EntityTransaction tran = null;

        try {
            em = utils.getEntityManager();
            tran = em.getTransaction();
            tran.begin();

            KhachHang khachHang = em.find(KhachHang.class, idKhachHang);
            diaChi.setKhachHang(khachHang);
            em.persist(diaChi);

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

    public void datMacDinh(Integer idDiaChi, Integer idKhachHang) {
        EntityManager em = null;
        EntityTransaction tran = null;

        try {
            em = utils.getEntityManager();
            tran = em.getTransaction();
            tran.begin();

            List<DiaChiKhachHang> listDiaChi = em.createQuery("SELECT dc FROM DiaChiKhachHang dc WHERE dc.khachHang.id = :idKhachHang", DiaChiKhachHang.class)
                    .setParameter("idKhachHang", idKhachHang)
                    .getResultList();

            for (DiaChiKhachHang dc : listDiaChi) {
                dc.setIsMacDinh(0);
            }

            DiaChiKhachHang diaChi = em.find(DiaChiKhachHang.class, idDiaChi);
            if (diaChi != null) {
                diaChi.setIsMacDinh(1);
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
