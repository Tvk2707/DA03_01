package BE.service.impl;

import BE.Entity.SanPhamChiTiet;
import BE.dao.SanPhamChiTietDao;
import BE.dao.impl.SanPhamChiTietDaoImpl;
import BE.service.SanPhamChiTietService;
import java.util.List;

/**
 * SanPhamChiTietService implementation - quản lý biến thể sản phẩm
 */
public class SanPhamChiTietServiceImpl implements SanPhamChiTietService {
    
    private final SanPhamChiTietDao sanPhamChiTietDao = new SanPhamChiTietDaoImpl();
    
    /**
     * Thêm biến thể sản phẩm mới
     */
    @Override
    public SanPhamChiTiet themBienThe(SanPhamChiTiet sanPhamChiTiet) {
        if (sanPhamChiTiet == null) {
            throw new RuntimeException("Biến thể sản phẩm không được để trống");
        }
        if (sanPhamChiTiet.getSanPham() == null || sanPhamChiTiet.getSanPham().getId() == null) {
            throw new RuntimeException("Sản phẩm không được để trống");
        }
        if (sanPhamChiTiet.getMauSac() == null || sanPhamChiTiet.getMauSac().getId() == null) {
            throw new RuntimeException("Màu sắc không được để trống");
        }
        if (sanPhamChiTiet.getKichCo() == null || sanPhamChiTiet.getKichCo().getId() == null) {
            throw new RuntimeException("Kích cỡ không được để trống");
        }
        
        // Kiểm tra xem biến thể này đã tồn tại chưa
        SanPhamChiTiet existing = sanPhamChiTietDao.findByMauSacVaKichCo(
            sanPhamChiTiet.getSanPham().getId(),
            sanPhamChiTiet.getMauSac().getId(),
            sanPhamChiTiet.getKichCo().getId()
        );
        if (existing != null) {
            throw new RuntimeException("Biến thể sản phẩm (màu sắc + kích cỡ) này đã tồn tại");
        }
        
        // Khởi tạo tồn kho mặc định nếu null
        if (sanPhamChiTiet.getSoLuongTon() == null) {
            sanPhamChiTiet.setSoLuongTon(0);
        }
        
        return sanPhamChiTietDao.save(sanPhamChiTiet);
    }
    
    /**
     * Cập nhật biến thể sản phẩm
     */
    @Override
    public SanPhamChiTiet capNhatBienThe(SanPhamChiTiet sanPhamChiTiet) {
        if (sanPhamChiTiet == null || sanPhamChiTiet.getId() == null) {
            throw new RuntimeException("ID biến thể sản phẩm không được để trống");
        }
        if (sanPhamChiTiet.getSanPham() == null || sanPhamChiTiet.getSanPham().getId() == null) {
            throw new RuntimeException("Sản phẩm không được để trống");
        }
        if (sanPhamChiTiet.getMauSac() == null || sanPhamChiTiet.getMauSac().getId() == null) {
            throw new RuntimeException("Màu sắc không được để trống");
        }
        if (sanPhamChiTiet.getKichCo() == null || sanPhamChiTiet.getKichCo().getId() == null) {
            throw new RuntimeException("Kích cỡ không được để trống");
        }
        
        // Kiểm tra xem có biến thể khác với tổ hợp màu sắc + kích cỡ này không
        SanPhamChiTiet existing = sanPhamChiTietDao.findByMauSacVaKichCo(
            sanPhamChiTiet.getSanPham().getId(),
            sanPhamChiTiet.getMauSac().getId(),
            sanPhamChiTiet.getKichCo().getId()
        );
        if (existing != null && !existing.getId().equals(sanPhamChiTiet.getId())) {
            throw new RuntimeException("Biến thể sản phẩm (màu sắc + kích cỡ) này đã được sử dụng");
        }
        
        return sanPhamChiTietDao.update(sanPhamChiTiet);
    }
    
    /**
     * Xóa biến thể sản phẩm theo ID
     */
    @Override
    public void xoaBienThe(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID biến thể sản phẩm không được để trống");
        }
        sanPhamChiTietDao.deleteById(id);
    }
    
    /**
     * Lấy danh sách biến thể theo ID sản phẩm
     */
    @Override
    public List<SanPhamChiTiet> layTheoSanPham(Integer sanPhamId) {
        if (sanPhamId == null) {
            throw new RuntimeException("ID sản phẩm không được để trống");
        }
        return sanPhamChiTietDao.findBySanPhamId(sanPhamId);
    }
    
    /**
     * Cập nhật tồn kho
     * Tính tồn kho mới = tồn kho hiện tại + soLuongThayDoi
     * Không cho phép tồn kho âm
     */
    @Override
    public void capNhatTonKho(Integer sanPhamChiTietId, Integer soLuongThayDoi) {
        if (sanPhamChiTietId == null) {
            throw new RuntimeException("ID biến thể sản phẩm không được để trống");
        }
        if (soLuongThayDoi == null) {
            throw new RuntimeException("Số lượng thay đổi không được để trống");
        }
        
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietDao.findById(sanPhamChiTietId);
        if (sanPhamChiTiet == null) {
            throw new RuntimeException("Biến thể sản phẩm không tồn tại");
        }
        
        Integer tonKhoHienTai = sanPhamChiTiet.getSoLuongTon();
        if (tonKhoHienTai == null) {
            tonKhoHienTai = 0;
        }
        
        Integer tonKhoMoi = tonKhoHienTai + soLuongThayDoi;
        
        if (tonKhoMoi < 0) {
            throw new RuntimeException("Số lượng tồn kho không được âm. Tồn kho hiện tại: " + 
                tonKhoHienTai + ", không thể trừ " + Math.abs(soLuongThayDoi));
        }
        
        sanPhamChiTietDao.updateTonKho(sanPhamChiTietId, tonKhoMoi);
    }
}
