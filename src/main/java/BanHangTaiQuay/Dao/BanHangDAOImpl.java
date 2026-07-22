package BanHangTaiQuay.Dao;

import QuanLySanPham.Entity.ChiTietHoaDon;
import QuanLySanPham.Entity.CaLamViec;
import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.LichSuHoaDon;
import QuanLySanPham.Entity.ThanhToanHoaDon;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class BanHangDAOImpl implements BanHangDAO {

    @Override
    public HoaDon insertHoaDon(HoaDon hd) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();

            // Đổi object CaLamViec chỉ có ID thành reference đang được quản lý bởi EntityManager.
            if (hd.getCa() != null && hd.getCa().getId() != null) {
                CaLamViec ca = em.getReference(CaLamViec.class, hd.getCa().getId());
                hd.setCa(ca);
            }

            em.persist(hd);
            em.getTransaction().commit();
            return hd;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public void updateHoaDon(HoaDon hd) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(hd);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public HoaDon findHoaDonById(int id) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            return em.find(HoaDon.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public long demHoaDonCho(int idNhanVien) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT count(h) FROM HoaDon h WHERE h.trangThai = 0 AND h.nhanVien.id = :idNhanVien";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("idNhanVien", idNhanVien);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public List<HoaDon> layDanhSachHoaDonCho(int idNhanVien) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT h FROM HoaDon h WHERE h.trangThai = 0 AND h.nhanVien.id = :idNhanVien";
            TypedQuery<HoaDon> query = em.createQuery(jpql, HoaDon.class);
            query.setParameter("idNhanVien", idNhanVien);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Integer timCaDangMo(int idNhanVien) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT c.id FROM CaLamViec c "
                    + "WHERE c.nhanVien.id = :idNhanVien "
                    + "AND c.thoiGianKetThuc IS NULL "
                    + "ORDER BY c.thoiGianBatDau DESC";
            TypedQuery<Integer> query = em.createQuery(jpql, Integer.class);
            query.setParameter("idNhanVien", idNhanVien);
            query.setMaxResults(1);
            List<Integer> caDangMo = query.getResultList();
            return caDangMo.isEmpty() ? null : caDangMo.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public ChiTietHoaDon insertChiTietHoaDon(ChiTietHoaDon ct) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ct);
            em.getTransaction().commit();
            return ct;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public void updateChiTietHoaDon(ChiTietHoaDon ct) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(ct);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteChiTietHoaDon(ChiTietHoaDon ct) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.contains(ct) ? ct : em.merge(ct));
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public ChiTietHoaDon findByHoaDonVaSpct(int idHoaDon, int idSanPhamChiTiet) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT c FROM ChiTietHoaDon c WHERE c.hoaDon.id = :idHoaDon AND c.sanPhamChiTiet.id = :idSanPhamChiTiet";
            TypedQuery<ChiTietHoaDon> query = em.createQuery(jpql, ChiTietHoaDon.class);
            query.setParameter("idHoaDon", idHoaDon);
            query.setParameter("idSanPhamChiTiet", idSanPhamChiTiet);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public ChiTietHoaDon findChiTietHoaDonById(int id) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            return em.find(ChiTietHoaDon.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public ThanhToanHoaDon insertThanhToan(ThanhToanHoaDon tt) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(tt);
            em.getTransaction().commit();
            return tt;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public LichSuHoaDon insertLichSu(LichSuHoaDon ls) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ls);
            em.getTransaction().commit();
            return ls;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
}
