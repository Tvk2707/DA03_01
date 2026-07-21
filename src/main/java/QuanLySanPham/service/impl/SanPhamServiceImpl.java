package QuanLySanPham.service.impl;

import QuanLySanPham.Entity.SanPham;
import QuanLySanPham.Entity.SanPhamChiTiet;
import QuanLySanPham.Utils.EntityManagerUtlis;
import QuanLySanPham.Utils.ValidationException;
import QuanLySanPham.Utils.ValidationUtils;
import QuanLySanPham.dao.SanPhamChiTietDao;
import QuanLySanPham.dao.HinhAnhSanPhamDao;
import QuanLySanPham.dao.impl.SanPhamDaoImpl;
import QuanLySanPham.dao.impl.SanPhamChiTietDaoImpl;
import QuanLySanPham.dao.impl.HinhAnhSanPhamDaoImpl;
import QuanLySanPham.service.SanPhamService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamDaoImpl sanPhamDao = new SanPhamDaoImpl();
    private final SanPhamChiTietDao sanPhamChiTietDao = new SanPhamChiTietDaoImpl();
    private final HinhAnhSanPhamDao hinhAnhSanPhamDao = new HinhAnhSanPhamDaoImpl();

    private void validateAndThrow(Map<String, String> errors) {
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }

    private void validateSanPham(SanPham sanPham, Map<String, String> errors) {
        ValidationUtils.validateMa(errors, "maSanPham", sanPham.getMaSanPham());
        ValidationUtils.validateTenSanPham(errors, "tenSanPham", sanPham.getTenSanPham());

        if (!errors.containsKey("tenSanPham")) {
            String normalizedTen = ValidationUtils.normalizeString(sanPham.getTenSanPham());
            SanPham existing = sanPhamDao.findByTenSanPham(normalizedTen);
            if (existing != null && (sanPham.getId() == null || !existing.getId().equals(sanPham.getId()))) {
                errors.put("tenSanPham", "Tên sản phẩm đã tồn tại.");
            }
            sanPham.setTenSanPham(normalizedTen);
        }
        if (!errors.containsKey("maSanPham")) {
            sanPham.setMaSanPham(sanPham.getMaSanPham().trim());
        }

        ValidationUtils.checkNull(errors, "danhMuc", sanPham.getDanhMuc(), "Vui lòng chọn danh mục.");
        ValidationUtils.checkNull(errors, "thuongHieu", sanPham.getThuongHieu(), "Vui lòng chọn thương hiệu.");
        ValidationUtils.checkNull(errors, "chatLieu", sanPham.getChatLieu(), "Vui lòng chọn chất liệu.");
        ValidationUtils.checkNull(errors, "kieuDang", sanPham.getKieuDang(), "Vui lòng chọn kiểu dáng.");
        ValidationUtils.checkNull(errors, "trongKinh", sanPham.getTrongKinh(), "Vui lòng chọn tròng kính.");
    }

    @Override
    public SanPham themSanPham(SanPham sanPham) {
        Map<String, String> errors = new HashMap<>();
        validateSanPham(sanPham, errors);
        validateAndThrow(errors);
        return sanPhamDao.save(sanPham);
    }

    @Override
    public SanPham capNhatSanPham(SanPham sanPham) {
        Map<String, String> errors = new HashMap<>();
        if (sanPham == null || sanPham.getId() == null) {
            errors.put("id", "ID sản phẩm không hợp lệ.");
            throw new ValidationException("Validation failed", errors);
        }
        validateSanPham(sanPham, errors);
        validateAndThrow(errors);
        return sanPhamDao.update(sanPham);
    }

    @Override
    public SanPham themSanPhamVaBienThe(SanPham sanPham, List<SanPhamChiTiet> danhSachBienThe) {
        Map<String, String> errors = new HashMap<>();
        validateSanPham(sanPham, errors);
        // Thêm các validate cho biến thể nếu cần
        if (danhSachBienThe == null || danhSachBienThe.isEmpty()) {
            errors.put("variants", "Sản phẩm phải có ít nhất một biến thể.");
        } else {
            Set<String> uniqueCombinations = new HashSet<>();
            for (int i = 0; i < danhSachBienThe.size(); i++) {
                SanPhamChiTiet spct = danhSachBienThe.get(i);
                if (spct.getMauSac() == null || spct.getKichCo() == null) {
                    errors.put("variant_" + i, "Màu sắc hoặc Kích cỡ của biến thể không hợp lệ.");
                }
                String combination = spct.getMauSac().getId() + "-" + spct.getKichCo().getId();
                if (!uniqueCombinations.add(combination)) {
                    errors.put("variant_" + i, "Các biến thể không được trùng lặp (cùng màu sắc và kích cỡ).");
                }
            }
        }
        validateAndThrow(errors);

        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(sanPham);
            for (SanPhamChiTiet spct : danhSachBienThe) {
                spct.setSanPham(sanPham);
                em.persist(spct);
            }
            transaction.commit();
            return sanPham;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Lỗi khi thêm sản phẩm và biến thể: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // Các phương thức khác giữ nguyên...
    @Override
    public void xoaSanPham(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống");
        }
        sanPhamDao.softDelete(id);
    }

    @Override
    public SanPham timTheoId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống");
        }
        return sanPhamDao.findById(id);
    }

    @Override
    public List<SanPham> layTatCa() {
        return sanPhamDao.findAll();
    }

    @Override
    public List<SanPham> layCoPhanTrang(int pageNumber, int pageSize) {
        if (pageNumber < 1 || pageSize < 1) {
            throw new IllegalArgumentException("Số trang và kích thước trang phải >= 1");
        }
        return sanPhamDao.findWithPaging(pageNumber, pageSize);
    }

    @Override
    public List<SanPham> timKiem(String tenSanPham, Integer danhMucId, Integer thuongHieuId, Double giaTu, Double giaDen) {
        return sanPhamDao.search(tenSanPham, danhMucId, thuongHieuId, giaTu, giaDen);
    }

    @Override
    public void softdelete(Integer id) {
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
            throw new RuntimeException("Lỗi khi xóa mềm sản phẩm: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
