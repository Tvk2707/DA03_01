package QuanLyNhanVien.service.impl;

import QuanLySanPham.Entity.NhanVien;
import QuanLySanPham.dao.NhanVienDao;
import QuanLySanPham.dao.impl.NhanVienDaoImpl;
import QuanLySanPham.Utils.PasswordUtil;
import QuanLySanPham.Utils.ValidationException;
import QuanLyNhanVien.service.NhanVienService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class NhanVienServiceImpl implements NhanVienService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final NhanVienDao nhanVienDao;

    public NhanVienServiceImpl() {
        this(new NhanVienDaoImpl());
    }

    public NhanVienServiceImpl(NhanVienDao nhanVienDao) {
        this.nhanVienDao = nhanVienDao;
    }

    @Override
    public NhanVien themNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) throw new RuntimeException("Nhân viên không được để trống");
        Map<String, String> errors = new LinkedHashMap<>();
        String soDienThoai = nhanVien.getSoDienThoai() == null ? "" : nhanVien.getSoDienThoai().trim();
        String email = nhanVien.getEmail() == null ? "" : nhanVien.getEmail().trim();
        String diaChi = nhanVien.getDiaChi() == null ? "" : nhanVien.getDiaChi().trim();
        if (nhanVien.getHoTen() == null || nhanVien.getHoTen().trim().isEmpty()) {
            errors.put("hoTen", "Vui lòng nhập họ và tên.");
        }
        if (soDienThoai.isEmpty()) {
            errors.put("soDienThoai", "Vui lòng nhập số điện thoại.");
        } else if (!PHONE_PATTERN.matcher(soDienThoai).matches()) {
            errors.put("soDienThoai", "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.");
        } else if (nhanVienDao.findBySoDienThoai(soDienThoai) != null) {
            errors.put("soDienThoai", "Số điện thoại đã được sử dụng.");
        }
        if (email.isEmpty()) {
            errors.put("email", "Vui lòng nhập email.");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.put("email", "Email không đúng định dạng.");
        } else if (nhanVienDao.findByEmail(email) != null) {
            errors.put("email", "Email đã được sử dụng.");
        }
        if (diaChi.isEmpty()) {
            errors.put("diaChi", "Vui lòng chọn địa chỉ.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Thông tin nhân viên chưa hợp lệ.", errors);
        }
        nhanVien.setHoTen(nhanVien.getHoTen().trim());
        nhanVien.setSoDienThoai(soDienThoai);
        nhanVien.setEmail(email);
        nhanVien.setDiaChi(diaChi);
        if (nhanVien.getMaNhanVien() != null) {
            NhanVien existing = nhanVienDao.findByMaNhanVien(nhanVien.getMaNhanVien());
            if (existing != null) throw new RuntimeException("Mã nhân viên đã tồn tại");
        }
        if (nhanVien.getMatKhau() == null || nhanVien.getMatKhau().isBlank()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }
        if (!PasswordUtil.isHashed(nhanVien.getMatKhau())) {
            nhanVien.setMatKhau(PasswordUtil.hash(nhanVien.getMatKhau()));
        }
        if (nhanVien.getVaiTro() == null) {
            nhanVien.setVaiTro(NhanVien.VAI_TRO_NHAN_VIEN);
        }
        if (nhanVien.getTrangThai() == null) {
            nhanVien.setTrangThai(1);
        }
        return nhanVienDao.save(nhanVien);
    }

    @Override
    public NhanVien capNhatNhanVien(NhanVien nhanVien) {
        if (nhanVien == null || nhanVien.getId() == null) throw new RuntimeException("ID nhân viên không hợp lệ");
        if (nhanVien.getHoTen() == null || nhanVien.getHoTen().trim().isEmpty()) throw new RuntimeException("Họ tên không được để trống");

        NhanVien hienTai = nhanVienDao.findById(nhanVien.getId());
        if (hienTai == null) throw new RuntimeException("Không tìm thấy nhân viên");

        // Chỉ cập nhật thông tin hồ sơ. Quyền và mật khẩu được quản lý bằng luồng riêng.
        hienTai.setHoTen(nhanVien.getHoTen().trim());
        hienTai.setEmail(nhanVien.getEmail());
        hienTai.setSoDienThoai(nhanVien.getSoDienThoai());
        hienTai.setNgaySinh(nhanVien.getNgaySinh());
        hienTai.setGioiTinh(nhanVien.getGioiTinh());
        hienTai.setDiaChi(nhanVien.getDiaChi());
        hienTai.setChucVu(nhanVien.getChucVu());
        hienTai.setAnhDaiDien(nhanVien.getAnhDaiDien());
        if (hienTai.isQuanLy() && hienTai.getTrangThai() != null && hienTai.getTrangThai() == 1
                && nhanVien.getTrangThai() != null && nhanVien.getTrangThai() == 0
                && demQuanLyDangHoatDong() <= 1) {
            throw new RuntimeException("Không thể vô hiệu hóa tài khoản quản lý cuối cùng");
        }
        hienTai.setTrangThai(nhanVien.getTrangThai());
        if (hienTai.getVaiTro() == null) {
            hienTai.setVaiTro(NhanVien.VAI_TRO_NHAN_VIEN);
        }
        return nhanVienDao.update(hienTai);
    }

    @Override
    public void xoaNhanVien(Integer id) {
        if (id == null) throw new RuntimeException("ID không được để trống");
        NhanVien nhanVien = nhanVienDao.findById(id);
        if (nhanVien == null) throw new RuntimeException("Không tìm thấy nhân viên");
        if (nhanVien.isQuanLy() && demQuanLyDangHoatDong() <= 1) {
            throw new RuntimeException("Không thể vô hiệu hóa tài khoản quản lý cuối cùng");
        }
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
    public List<NhanVien> timKiem(String tuKhoa) {
        return nhanVienDao.search(tuKhoa);
    }
    @Override
    public NhanVien dangNhap(String taiKhoan, String matKhau) throws Exception {
        if (taiKhoan == null || taiKhoan.trim().isEmpty() || matKhau == null || matKhau.isBlank()) {
            throw new Exception("Vui lòng nhập đầy đủ tài khoản và mật khẩu");
        }

        String taiKhoanChuanHoa = taiKhoan.trim();
        NhanVien nv = nhanVienDao.findByEmail(taiKhoanChuanHoa);
        if (nv == null) {
            nv = nhanVienDao.findByMaNhanVien(taiKhoanChuanHoa);
        }
        if (nv == null) {
            throw new Exception("Tài khoản không tồn tại");
        }
        if (nv.getTrangThai() != null && nv.getTrangThai() == 0) {
            throw new Exception("Tài khoản đã bị vô hiệu hóa");
        }
        String matKhauDaLuu = nv.getMatKhau() == null ? "" : nv.getMatKhau().trim();
        boolean dungMatKhauBam = PasswordUtil.isHashed(matKhauDaLuu)
                && PasswordUtil.matches(matKhau, matKhauDaLuu);
        boolean dungMatKhauCu = !PasswordUtil.isHashed(matKhauDaLuu)
                && matKhau.equals(matKhauDaLuu);
        if (!dungMatKhauBam && !dungMatKhauCu) {
            throw new Exception("Mật khẩu không đúng");
        }

        // Tự chuyển tài khoản cũ sang mật khẩu băm và chuẩn hóa quyền khi đăng nhập thành công.
        if (dungMatKhauCu || nv.getVaiTro() == null) {
            if (dungMatKhauCu) {
                nv.setMatKhau(PasswordUtil.hash(matKhau));
            }
            if (nv.getVaiTro() == null) {
                nv.setVaiTro(NhanVien.VAI_TRO_NHAN_VIEN);
            }
            nv = nhanVienDao.update(nv);
        }
        return nv;
    }

    @Override
    public NhanVien capNhatVaiTro(Integer nhanVienId, Integer vaiTroMoi, Integer nguoiThucHienId) {
        if (nhanVienId == null || nguoiThucHienId == null) {
            throw new RuntimeException("Thông tin phân quyền không hợp lệ");
        }
        if (vaiTroMoi == null || (vaiTroMoi != NhanVien.VAI_TRO_NHAN_VIEN
                && vaiTroMoi != NhanVien.VAI_TRO_QUAN_LY)) {
            throw new RuntimeException("Vai trò không hợp lệ");
        }

        NhanVien nguoiThucHien = nhanVienDao.findById(nguoiThucHienId);
        if (nguoiThucHien == null || !nguoiThucHien.isQuanLy()) {
            throw new RuntimeException("Chỉ quản lý mới được thay đổi quyền");
        }

        NhanVien nhanVien = nhanVienDao.findById(nhanVienId);
        if (nhanVien == null) {
            throw new RuntimeException("Không tìm thấy nhân viên");
        }
        if (nhanVienId.equals(nguoiThucHienId) && vaiTroMoi == NhanVien.VAI_TRO_NHAN_VIEN) {
            throw new RuntimeException("Bạn không thể tự hạ quyền của chính mình");
        }
        if (nhanVien.isQuanLy() && vaiTroMoi == NhanVien.VAI_TRO_NHAN_VIEN) {
            long soQuanLyDangHoatDong = demQuanLyDangHoatDong();
            if (soQuanLyDangHoatDong <= 1) {
                throw new RuntimeException("Hệ thống phải còn ít nhất một tài khoản quản lý");
            }
        }

        nhanVien.setVaiTro(vaiTroMoi);
        return nhanVienDao.update(nhanVien);
    }

    private long demQuanLyDangHoatDong() {
        return nhanVienDao.findAll().stream()
                .filter(NhanVien::isQuanLy)
                .filter(item -> item.getTrangThai() == null || item.getTrangThai() == 1)
                .count();
    }
}


