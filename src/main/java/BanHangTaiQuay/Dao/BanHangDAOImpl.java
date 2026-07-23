package BanHangTaiQuay.Dao;

import QuanLySanPham.Entity.ChiTietHoaDon;
import QuanLySanPham.Entity.CaLamViec;
import QuanLySanPham.Entity.HinhThucThanhToan;
import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.KhachHang;
import QuanLySanPham.Entity.LichSuHoaDon;
import QuanLySanPham.Entity.NhanVien;
import QuanLySanPham.Entity.PhieuGiamGia;
import QuanLySanPham.Entity.SanPhamChiTiet;
import QuanLySanPham.Entity.ThanhToanHoaDon;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class BanHangDAOImpl implements BanHangDAO {

    @Override
    public HoaDon insertHoaDon(HoaDon hd) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            em.getTransaction().begin();

            ganReferenceHoaDon(em, hd);

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
            ganReferenceHoaDon(em, hd);
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
            String jpql = "SELECT count(h) FROM HoaDon h WHERE h.trangThai IN (0, 1) AND h.nhanVien.id = :idNhanVien";
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
            String jpql = "SELECT h FROM HoaDon h WHERE h.trangThai IN (0, 1) AND h.nhanVien.id = :idNhanVien";
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
            ganReferenceChiTiet(em, ct);
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
            ganReferenceChiTiet(em, ct);
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
            if (ct == null || ct.getId() == null) {
                throw new IllegalArgumentException("Chi tiết hóa đơn không hợp lệ.");
            }
            ChiTietHoaDon managed = em.find(ChiTietHoaDon.class, ct.getId());
            if (managed != null) {
                em.remove(managed);
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
    public ChiTietHoaDon findByHoaDonVaSpct(int idHoaDon, int idSanPhamChiTiet) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT c FROM ChiTietHoaDon c WHERE c.hoaDon.id = :idHoaDon AND c.sanPhamChiTiet.id = :idSanPhamChiTiet";
            TypedQuery<ChiTietHoaDon> query = em.createQuery(jpql, ChiTietHoaDon.class);
            query.setParameter("idHoaDon", idHoaDon);
            query.setParameter("idSanPhamChiTiet", idSanPhamChiTiet);
            query.setMaxResults(1);
            List<ChiTietHoaDon> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
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
            if (tt.getHoaDon() != null && tt.getHoaDon().getId() != null) {
                tt.setHoaDon(em.getReference(HoaDon.class, tt.getHoaDon().getId()));
            }
            if (tt.getHinhThucThanhToan() != null && tt.getHinhThucThanhToan().getId() != null) {
                tt.setHinhThucThanhToan(em.getReference(HinhThucThanhToan.class,
                        tt.getHinhThucThanhToan().getId()));
            }
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
            if (ls.getHoaDon() != null && ls.getHoaDon().getId() != null) {
                ls.setHoaDon(em.getReference(HoaDon.class, ls.getHoaDon().getId()));
            }
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

    private void ganReferenceHoaDon(EntityManager em, HoaDon hd) {
        if (hd.getCa() != null && hd.getCa().getId() != null) {
            hd.setCa(em.getReference(CaLamViec.class, hd.getCa().getId()));
        }
        if (hd.getNhanVien() != null && hd.getNhanVien().getId() != null) {
            hd.setNhanVien(em.getReference(NhanVien.class, hd.getNhanVien().getId()));
        }
        if (hd.getKhachHang() != null && hd.getKhachHang().getId() != null) {
            hd.setKhachHang(em.getReference(KhachHang.class, hd.getKhachHang().getId()));
        }
        if (hd.getPhieuGiamGia() != null && hd.getPhieuGiamGia().getId() != null) {
            hd.setPhieuGiamGia(em.getReference(PhieuGiamGia.class, hd.getPhieuGiamGia().getId()));
        }
    }

    private void ganReferenceChiTiet(EntityManager em, ChiTietHoaDon ct) {
        if (ct.getHoaDon() != null && ct.getHoaDon().getId() != null) {
            ct.setHoaDon(em.getReference(HoaDon.class, ct.getHoaDon().getId()));
        }
        if (ct.getSanPhamChiTiet() != null && ct.getSanPhamChiTiet().getId() != null) {
            ct.setSanPhamChiTiet(em.getReference(SanPhamChiTiet.class, ct.getSanPhamChiTiet().getId()));
        }
    }
}
