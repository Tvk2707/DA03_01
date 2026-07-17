package QuanLySanPham.service.impl;

import QuanLySanPham.Entity.SanPham;
import QuanLySanPham.Entity.SanPhamChiTiet;
import QuanLySanPham.Utils.EntityManagerUtlis;
import QuanLySanPham.dao.SanPhamChiTietDao;
import QuanLySanPham.dao.HinhAnhSanPhamDao;
import QuanLySanPham.dao.impl.SanPhamDaoImpl;
import QuanLySanPham.dao.impl.SanPhamChiTietDaoImpl;
import QuanLySanPham.dao.impl.HinhAnhSanPhamDaoImpl;
import QuanLySanPham.service.SanPhamService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SanPhamService implementation - quản lý sản phẩm
 */
public class SanPhamServiceImpl implements SanPhamService {
    
    private final SanPhamDaoImpl sanPhamDao = new SanPhamDaoImpl();
    private final SanPhamChiTietDao sanPhamChiTietDao = new SanPhamChiTietDaoImpl();
    private final HinhAnhSanPhamDao hinhAnhSanPhamDao = new HinhAnhSanPhamDaoImpl();
    
    /**
     * Thêm sản phẩm mới
     */
    @Override
    public SanPham themSanPham(SanPham sanPham) {
        if (sanPham == null) {
            throw new RuntimeException("Sản phẩm không được để trống");
        }
        if (sanPham.getTenSanPham() == null || sanPham.getTenSanPham().trim().isEmpty()) {
            throw new RuntimeException("Tên sản phẩm không được để trống");
        }
        
        SanPham existing = sanPhamDao.findByTenSanPham(sanPham.getTenSanPham().trim());
        if (existing != null) {
            throw new RuntimeException("Sản phẩm với tên này đã tồn tại");
        }
        
        return sanPhamDao.save(sanPham);
    }
    
    /**
     * Cập nhật sản phẩm
     */
    @Override
    public SanPham capNhatSanPham(SanPham sanPham) {
        if (sanPham == null || sanPham.getId() == null) {
            throw new RuntimeException("ID sản phẩm không được để trống");
        }
        if (sanPham.getTenSanPham() == null || sanPham.getTenSanPham().trim().isEmpty()) {
            throw new RuntimeException("Tên sản phẩm không được để trống");
        }
        
        SanPham existing = sanPhamDao.findByTenSanPham(sanPham.getTenSanPham().trim());
        if (existing != null && !existing.getId().equals(sanPham.getId())) {
            throw new RuntimeException("Sản phẩm với tên này đã tồn tại");
        }
        
        return sanPhamDao.update(sanPham);
    }
    
    /**
     * Xóa sản phẩm theo ID
     * Chặn xóa nếu có biến thể hoặc hình ảnh
     */
    @Override

    public void xoaSanPham(Integer id) {
     if (id == null) {
     throw new RuntimeException("ID sản phẩm không được để trống");
     }

          // Gọi xuống DAO để thực hiện xóa mềm (cập nhật isDeleted = true)
     sanPhamDao.softDelete(id);
    }
    
    /**
     * Lấy sản phẩm theo ID
     */
    @Override
    public SanPham timTheoId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID sản phẩm không được để trống");
        }
        return sanPhamDao.findById(id);
    }
    
    /**
     * Lấy tất cả sản phẩm
     */
    @Override
    public List<SanPham> layTatCa() {
        return sanPhamDao.findAll();
    }
    
    /**
     * Lấy sản phẩm có phân trang
     */
    @Override
    public List<SanPham> layCoPhanTrang(int pageNumber, int pageSize) {
        if (pageNumber < 1 || pageSize < 1) {
            throw new RuntimeException("Số trang và kích thước trang phải >= 1");
        }
        return sanPhamDao.findWithPaging(pageNumber, pageSize);
    }
    
    /**
     * Tìm kiếm sản phẩm theo tên, danh mục, thương hiệu
     * Các tham số có thể null - nếu null thì bỏ qua điều kiện đó
     */
    @Override
    public List<SanPham> timKiem(String tenSanPham, Integer danhMucId, Integer thuongHieuId, Double giaTu, Double giaDen) {
        return sanPhamDao.search(tenSanPham, danhMucId, thuongHieuId, giaTu, giaDen); // Thay thế sanPhamDAO bằng tên biến của bạn
    }
    /**
     * Override lại hàm deleteById của GenericDao để thực hiện XÓA MỀM
     */

    public void softdelete(Integer id) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            SanPham sanPham = em.find(SanPham.class, id);
            if (sanPham != null) {
                sanPham.setIsDeleted(true); // Đánh dấu đã xóa thay vì gọi em.remove()
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

    @Override
    public SanPham themSanPhamVaBienThe(SanPham sanPham, List<SanPhamChiTiet> danhSachBienThe) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            // 1. Validate và lưu SanPham (sản phẩm cha)
            if (sanPham.getTenSanPham() == null || sanPham.getTenSanPham().trim().isEmpty()) {
                throw new RuntimeException("Tên sản phẩm không được để trống");
            }
            em.persist(sanPham);

            // 2. Validate và lưu danh sách SanPhamChiTiet (biến thể)
            if (danhSachBienThe != null && !danhSachBienThe.isEmpty()) {
                Set<String> uniqueCombinations = new HashSet<>();
                for (SanPhamChiTiet spct : danhSachBienThe) {
                    // Gán sản phẩm cha vừa được lưu vào từng biến thể
                    spct.setSanPham(sanPham);

                    // Validate
                    if (spct.getMauSac() == null || spct.getKichCo() == null) {
                         throw new RuntimeException("Màu sắc hoặc Kích cỡ không hợp lệ.");
                    }
                    String combination = spct.getMauSac().getId() + "-" + spct.getKichCo().getId();
                    if (!uniqueCombinations.add(combination)) {
                        throw new RuntimeException("Danh sách chứa các biến thể trùng lặp (cùng màu sắc và kích cỡ).");
                    }
                    
                    em.persist(spct);
                }
            }

            // 3. Nếu mọi thứ thành công, commit transaction
            transaction.commit();
            return sanPham;

        } catch (Exception e) {
            // 4. Nếu có bất kỳ lỗi nào, rollback toàn bộ transaction
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            // Ném lại exception để Servlet có thể bắt và thông báo lỗi
            throw new RuntimeException("Lỗi khi thêm sản phẩm và biến thể: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}