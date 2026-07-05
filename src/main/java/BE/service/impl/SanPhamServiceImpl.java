package BE.service.impl;

import BE.Entity.SanPham;
import BE.Entity.SanPhamChiTiet;
import BE.Entity.HinhAnhSanPham;
import BE.dao.SanPhamDao;
import BE.dao.SanPhamChiTietDao;
import BE.dao.HinhAnhSanPhamDao;
import BE.dao.impl.SanPhamDaoImpl;
import BE.dao.impl.SanPhamChiTietDaoImpl;
import BE.dao.impl.HinhAnhSanPhamDaoImpl;
import BE.service.SanPhamService;
import java.util.List;

/**
 * SanPhamService implementation - quản lý sản phẩm
 */
public class SanPhamServiceImpl implements SanPhamService {
    
    private final SanPhamDao sanPhamDao = new SanPhamDaoImpl();
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
        
        // Kiểm tra xem sản phẩm có biến thể hay không
        List<SanPhamChiTiet> chiTiets = sanPhamChiTietDao.findBySanPhamId(id);
        if (chiTiets != null && !chiTiets.isEmpty()) {
            throw new RuntimeException("Sản phẩm đang có biến thể, không thể xóa");
        }
        
        // Kiểm tra xem sản phẩm có hình ảnh hay không
        List<HinhAnhSanPham> hinhAnhs = hinhAnhSanPhamDao.findBySanPhamId(id);
        if (hinhAnhs != null && !hinhAnhs.isEmpty()) {
            throw new RuntimeException("Sản phẩm đang có hình ảnh, không thể xóa");
        }
        
        sanPhamDao.deleteById(id);
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
    public List<SanPham> timKiem(String tenSanPham, Integer danhMucId, Integer thuongHieuId) {
        return sanPhamDao.search(tenSanPham, danhMucId, thuongHieuId, null, null);
    }
}
