package main.QuanLySanPham.BE.dao.impl;

import main.QuanLySanPham.BE.Entity.HinhDangGong;
import main.QuanLySanPham.BE.dao.HinhDangGongDao;
import main.QuanLySanPham.BE.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Lớp DAO cài đặt cho HinhDangGong
 */
public class HinhDangGongDaoImpl extends GenericDaoImpl<HinhDangGong, Integer> implements HinhDangGongDao {
    
    public HinhDangGongDaoImpl() {
        super(HinhDangGong.class);
    }
    
    /**
     * Tìm hình dáng gọng theo tên
     */
    @Override
    public HinhDangGong findByTen(String ten) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            String jpql = "SELECT h FROM HinhDangGong h WHERE h.hinhDang = :ten";
            TypedQuery<HinhDangGong> query = em.createQuery(jpql, HinhDangGong.class);
            query.setParameter("ten", ten);
            List<HinhDangGong> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm hình dáng gọng theo tên", e);
        } finally {
            em.close();
        }
    }
}
