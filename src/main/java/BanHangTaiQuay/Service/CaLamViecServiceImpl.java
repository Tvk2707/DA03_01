package BanHangTaiQuay.Service;

import BanHangTaiQuay.Dao.BanHangDAO;
import BanHangTaiQuay.Dao.BanHangDAOImpl;
import QuanLySanPham.Entity.CaLamViec;
import QuanLySanPham.Entity.NhanVien;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CaLamViecServiceImpl implements CaLamViecService {

    private final BanHangDAO banHangDAO = new BanHangDAOImpl();

    @Override
    public Integer layCaDangMo(int idNhanVien) {
        return banHangDAO.timCaDangMo(idNhanVien);
    }

    @Override
    public Integer layHoacTaoCaDangMo(int idNhanVien) {
        if (idNhanVien <= 0) {
            throw new IllegalArgumentException("Nhân viên không hợp lệ.");
        }

        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            List<CaLamViec> caDangMo = em.createQuery(
                            "SELECT c FROM CaLamViec c "
                                    + "WHERE c.nhanVien.id = :idNhanVien "
                                    + "AND c.thoiGianKetThuc IS NULL "
                                    + "ORDER BY c.thoiGianBatDau DESC",
                            CaLamViec.class)
                    .setParameter("idNhanVien", idNhanVien)
                    .setMaxResults(1)
                    .getResultList();

            if (!caDangMo.isEmpty()) {
                Integer idCa = caDangMo.get(0).getId();
                transaction.commit();
                return idCa;
            }

            NhanVien nhanVien = em.find(NhanVien.class, idNhanVien);
            if (nhanVien == null) {
                throw new IllegalArgumentException("Nhân viên không tồn tại.");
            }

            CaLamViec caMoi = new CaLamViec();
            caMoi.setNhanVien(nhanVien);
            caMoi.setThoiGianBatDau(LocalDateTime.now());
            caMoi.setTongDoanhThu(BigDecimal.ZERO);
            caMoi.setTrangThai(1);
            em.persist(caMoi);
            transaction.commit();
            return caMoi.getId();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
