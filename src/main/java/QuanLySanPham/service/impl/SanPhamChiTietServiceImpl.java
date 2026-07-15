package main.QuanLySanPham.BE.service.impl;

import main.QuanLySanPham.BE.Entity.SanPhamChiTiet;
import main.QuanLySanPham.BE.dao.SanPhamChiTietDao;
import main.QuanLySanPham.BE.dao.impl.SanPhamChiTietDaoImpl;
import main.QuanLySanPham.BE.service.SanPhamChiTietService;

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
        validateSanPhamChiTiet(sanPhamChiTiet, false);

        // Kiểm tra biến thể đã tồn tại chưa (theo sản phẩm + màu sắc + kích cỡ)
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
        validateSanPhamChiTiet(sanPhamChiTiet, true);

        // Kiểm tra có biến thể KHÁC cũng có cùng tổ hợp màu sắc + kích cỡ không
        SanPhamChiTiet existing = sanPhamChiTietDao.findByMauSacVaKichCo(
                sanPhamChiTiet.getSanPham().getId(),
                sanPhamChiTiet.getMauSac().getId(),
                sanPhamChiTiet.getKichCo().getId()
        );
        if (existing != null && !existing.getId().equals(sanPhamChiTiet.getId())) {
            throw new RuntimeException("Biến thể sản phẩm (màu sắc + kích cỡ) này đã được sử dụng bởi biến thể khác");
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
        sanPhamChiTietDao.softDelete(id);
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
     * Lấy biến thể đầu tiên theo ID sản phẩm (dùng cho form Edit)
     * Nếu không tìm thấy, trả về null
     */
    @Override
    public SanPhamChiTiet timBienTheTheoSanPhamId(Integer sanPhamId) {
        if (sanPhamId == null) {
            throw new RuntimeException("ID sản phẩm không được để trống");
        }

        List<SanPhamChiTiet> danhSach = sanPhamChiTietDao.findBySanPhamId(sanPhamId);
        if (danhSach == null || danhSach.isEmpty()) {
            return null;
        }
        // Trả về biến thể đầu tiên
        return danhSach.get(0);
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

        Integer tonKhoHienTai = sanPhamChiTiet.getSoLuongTon() != null
                ? sanPhamChiTiet.getSoLuongTon() : 0;

        Integer tonKhoMoi = tonKhoHienTai + soLuongThayDoi;

        if (tonKhoMoi < 0) {
            throw new RuntimeException("Số lượng tồn kho không được âm. " +
                    "Tồn kho hiện tại: " + tonKhoHienTai +
                    ", không thể trừ " + Math.abs(soLuongThayDoi));
        }

        sanPhamChiTietDao.updateTonKho(sanPhamChiTietId, tonKhoMoi);
    }

    /**
     * Tìm kiếm biến thể sản phẩm theo nhiều tiêu chí
     */
    @Override
    public List<SanPhamChiTiet> timKiem(Integer sanPhamId, String ma, Integer mauSacId,
                                        Integer kichCoId, Integer trangThai) {
        return sanPhamChiTietDao.timKiem(sanPhamId, ma, mauSacId, kichCoId, trangThai);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Validate dữ liệu biến thể sản phẩm
     *
     * @param sanPhamChiTiet đối tượng cần validate
     * @param isUpdate       true nếu là cập nhật (cần có ID), false nếu là thêm mới
     */
    private void validateSanPhamChiTiet(SanPhamChiTiet sanPhamChiTiet, boolean isUpdate) {
        if (sanPhamChiTiet == null) {
            throw new RuntimeException("Biến thể sản phẩm không được để trống");
        }

        // Khi update thì bắt buộc phải có ID
        if (isUpdate && sanPhamChiTiet.getId() == null) {
            throw new RuntimeException("ID biến thể sản phẩm không được để trống khi cập nhật");
        }

        // Validate các trường bắt buộc
        if (sanPhamChiTiet.getSanPham() == null || sanPhamChiTiet.getSanPham().getId() == null) {
            throw new RuntimeException("Sản phẩm không được để trống");
        }
        if (sanPhamChiTiet.getMauSac() == null || sanPhamChiTiet.getMauSac().getId() == null) {
            throw new RuntimeException("Màu sắc không được để trống");
        }
        if (sanPhamChiTiet.getKichCo() == null || sanPhamChiTiet.getKichCo().getId() == null) {
            throw new RuntimeException("Kích cỡ không được để trống");
        }

        // Validate giá trị số
        if (sanPhamChiTiet.getGiaNhap() != null && sanPhamChiTiet.getGiaNhap().signum() < 0) {
            throw new RuntimeException("Giá nhập không được âm");
        }
        if (sanPhamChiTiet.getGiaBan() != null && sanPhamChiTiet.getGiaBan().signum() < 0) {
            throw new RuntimeException("Giá bán không được âm");
        }
        if (sanPhamChiTiet.getSoLuongTon() != null && sanPhamChiTiet.getSoLuongTon() < 0) {
            throw new RuntimeException("Số lượng tồn không được âm");
        }
        if (sanPhamChiTiet.getTrongLuong() != null && sanPhamChiTiet.getTrongLuong() < 0) {
            throw new RuntimeException("Trọng lượng không được âm");
        }
    }
}