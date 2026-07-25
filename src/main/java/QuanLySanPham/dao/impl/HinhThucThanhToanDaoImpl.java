package QuanLySanPham.dao.impl;

import QuanLySanPham.Entity.HinhThucThanhToan;
import QuanLySanPham.dao.HinhThucThanhToanDao;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class HinhThucThanhToanDaoImpl extends GenericDaoImpl<HinhThucThanhToan, Integer> implements HinhThucThanhToanDao {

    public HinhThucThanhToanDaoImpl() {
        super(HinhThucThanhToan.class);
    }

    @Override
    public HinhThucThanhToan findByMa(String ma) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            TypedQuery<HinhThucThanhToan> query = em.createQuery("SELECT h FROM HinhThucThanhToan h WHERE h.maPttt = :ma", HinhThucThanhToan.class);
            query.setParameter("ma", ma);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
