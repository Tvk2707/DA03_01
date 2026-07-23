package QuanLySanPham.service.impl;

import QuanLySanPham.Entity.SanPhamChiTiet;
import QuanLySanPham.dao.SanPhamChiTietDao;
import QuanLySanPham.dao.impl.SanPhamChiTietDaoImpl;
import QuanLySanPham.service.SanPhamChiTietService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SanPhamChiTietService implementation - quản lý biến thể sản phẩm
 */
public class SanPhamChiTietServiceImpl implements SanPhamChiTietService {

    private final SanPhamChiTietDao sanPhamChiTietDao = new SanPhamChiTietDaoImpl();

    @Override
    public SanPhamChiTiet timTheoId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID biến thể sản phẩm không hợp lệ.");
        }
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietDao.findById(id);
        if (sanPhamChiTiet != null && Boolean.TRUE.equals(sanPhamChiTiet.getIsDeleted())) {
            return null;
        }
        return sanPhamChiTiet;
    }

    @Override
    public List<SanPhamChiTiet> timTheoIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Danh sách biến thể sản phẩm không được để trống.");
        }
        if (ids.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new IllegalArgumentException("ID biến thể sản phẩm không hợp lệ.");
        }
        return sanPhamChiTietDao.findByIds(ids);
    }

    @Override
    public List<SanPhamChiTiet> themBienThe(List<SanPhamChiTiet> danhSach) {
        if (danhSach == null || danhSach.isEmpty()) {
            throw new RuntimeException("Danh sách biến thể không được để trống.");
        }

        // 1. Validate từng phần tử
        for (SanPhamChiTiet spct : danhSach) {
            validateSanPhamChiTiet(spct, false);
        }

        // 2. Kiểm tra trùng lặp trong chính danh sách gửi lên
        Set<String> uniqueCombinations = new HashSet<>();
        for (SanPhamChiTiet spct : danhSach) {
            String combination = spct.getMauSac().getId() + "-" + spct.getKichCo().getId();
            if (!uniqueCombinations.add(combination)) {
                throw new RuntimeException("Danh sách chứa các biến thể trùng lặp (cùng màu sắc và kích cỡ).");
            }
        }

        // 3. Kiểm tra trùng lặp với DB
        for (SanPhamChiTiet spct : danhSach) {
            SanPhamChiTiet existing = sanPhamChiTietDao.findByMauSacVaKichCo(
                    spct.getSanPham().getId(),
                    spct.getMauSac().getId(),
                    spct.getKichCo().getId()
            );
            if (existing != null) {
                throw new RuntimeException("Biến thể sản phẩm (màu sắc + kích cỡ) đã tồn tại trong CSDL.");
            }
            // Khởi tạo tồn kho mặc định nếu null
            if (spct.getSoLuongTon() == null) {
                spct.setSoLuongTon(0);
            }
        }

        // 4. Gọi DAO để lưu hàng loạt
        return sanPhamChiTietDao.saveAll(danhSach);
    }

    @Override
    public List<SanPhamChiTiet> capNhatDanhSachBienThe(List<SanPhamChiTiet> danhSach) {
        if (danhSach == null || danhSach.isEmpty()) {
            return danhSach;
        }

        // 1. Validate từng phần tử
        for (SanPhamChiTiet spct : danhSach) {
            validateSanPhamChiTiet(spct, true); // isUpdate = true
        }

        // 2. Kiểm tra trùng lặp trong chính danh sách gửi lên
        Set<String> uniqueCombinations = new HashSet<>();
        for (SanPhamChiTiet spct : danhSach) {
            String combination = spct.getMauSac().getId() + "-" + spct.getKichCo().getId();
            if (!uniqueCombinations.add(combination)) {
                throw new RuntimeException("Danh sách chứa các biến thể trùng lặp (cùng màu sắc và kích cỡ).");
            }
        }

        // 3. Kiểm tra trùng lặp với DB
        for (SanPhamChiTiet spct : danhSach) {
            SanPhamChiTiet existing = sanPhamChiTietDao.findByMauSacVaKichCo(
                    spct.getSanPham().getId(),
                    spct.getMauSac().getId(),
                    spct.getKichCo().getId()
            );
            // Nếu tìm thấy một biến thể khác có cùng tổ hợp màu/size
            if (existing != null && !existing.getId().equals(spct.getId())) {
                throw new RuntimeException("Tổ hợp Màu sắc + Kích cỡ đã được sử dụng bởi một biến thể khác.");
            }
        }

        // 4. Gọi DAO để cập nhật hàng loạt
        return sanPhamChiTietDao.updateAll(danhSach);
    }

    @Override
    public void xoaBienThe(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID biến thể sản phẩm không được để trống");
        }
        sanPhamChiTietDao.softDelete(id);
    }

    @Override
    public List<SanPhamChiTiet> timBienTheTheoSanPhamId(Integer sanPhamId) {
        if (sanPhamId == null) {
            throw new RuntimeException("ID sản phẩm không được để trống");
        }
        return sanPhamChiTietDao.findBySanPhamId(sanPhamId);
    }

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

    @Override
    public List<SanPhamChiTiet> timKiem(Integer sanPhamId, String ma, Integer mauSacId,
                                        Integer kichCoId, Integer trangThai) {
        return sanPhamChiTietDao.timKiem(sanPhamId, ma, mauSacId, kichCoId, trangThai);
    }

    @Override
    public List<SanPhamChiTiet> timKiemTheoDanhMuc(String ma, Integer danhMucId, Integer trangThai) {
        return sanPhamChiTietDao.timKiemTheoDanhMuc(ma, danhMucId, trangThai);
    }

    private void validateSanPhamChiTiet(SanPhamChiTiet sanPhamChiTiet, boolean isUpdate) {
        if (sanPhamChiTiet == null) {
            throw new RuntimeException("Biến thể sản phẩm không được để trống");
        }

        if (isUpdate && sanPhamChiTiet.getId() == null) {
            throw new RuntimeException("ID biến thể sản phẩm không được để trống khi cập nhật");
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
