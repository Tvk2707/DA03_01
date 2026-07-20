package QuanLyNhanVien.service.impl;

import QuanLySanPham.Entity.NhanVien;
import QuanLySanPham.dao.NhanVienDao;
import QuanLySanPham.dao.impl.NhanVienDaoImpl;
import QuanLyNhanVien.service.NhanVienService;

import java.util.List;

public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienDao nhanVienDao = new NhanVienDaoImpl();

    @Override
    public NhanVien themNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) throw new RuntimeException("Nhân viên không được để trống");
        if (nhanVien.getHoTen() == null || nhanVien.getHoTen().trim().isEmpty()) {
            throw new RuntimeException("Họ tên không được để trống");
        }
        if (nhanVien.getMaNhanVien() != null) {
            NhanVien existing = nhanVienDao.findByMaNhanVien(nhanVien.getMaNhanVien());
            if (existing != null) throw new RuntimeException("Mã nhân viên đã tồn tại");
        }
        return nhanVienDao.save(nhanVien);
    }

    @Override
    public NhanVien capNhatNhanVien(NhanVien nhanVien) {
        if (nhanVien == null || nhanVien.getId() == null) throw new RuntimeException("ID nhân viên không hợp lệ");
        if (nhanVien.getHoTen() == null || nhanVien.getHoTen().trim().isEmpty()) throw new RuntimeException("Họ tên không được để trống");
        return nhanVienDao.update(nhanVien);
    }

    @Override
    public void xoaNhanVien(Integer id) {
        if (id == null) throw new RuntimeException("ID không được để trống");
        nhanVienDao.softDelete(id);
    }

    @Override
    public NhanVien timTheoId(Integer id) {
        return nhanVienDao.findById(id);
    }

    @Override
    public List<NhanVien> layTatCa() {
        return nhanVienDao.findAll();
    }

    @Override
    public List<NhanVien> layCoPhanTrang(int pageNumber, int pageSize) {
        return nhanVienDao.findWithPaging(pageNumber, pageSize);
    }

    @Override
    public List<NhanVien> timKiem(String hoTen, String email) {
        return nhanVienDao.search(hoTen, email);
    }
    @Override
    public NhanVien dangNhap(String taiKhoan, String matKhau) throws Exception {
        if (taiKhoan == null || taiKhoan.trim().isEmpty() || matKhau == null || matKhau.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập đầy đủ tài khoản và mật khẩu");
        }

        NhanVien nv = nhanVienDao.findByEmail(taiKhoan);
        if (nv == null) {
            nv = nhanVienDao.findByMaNhanVien(taiKhoan);
        }
        if (nv == null) {
            throw new Exception("Tài khoản không tồn tại");
        }
        if (nv.getTrangThai() != null && nv.getTrangThai() == 0) {
            throw new Exception("Tài khoản đã bị vô hiệu hóa");
        }
        if (!matKhau.equals(nv.getMatKhau().trim())) {
            throw new Exception("Mật khẩu không đúng");
        }
        return nv;
    }
}


