package QuanLyNhanVien.service;

import QuanLyNhanVien.service.impl.NhanVienServiceImpl;
import QuanLySanPham.Entity.NhanVien;
import QuanLySanPham.Utils.PasswordUtil;
import QuanLySanPham.Utils.ValidationException;
import QuanLySanPham.dao.NhanVienDao;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NhanVienServiceImplTest {

    @Test
    void themNhanVienGanQuyenMacDinhVaBamMatKhau() {
        FakeNhanVienDao dao = new FakeNhanVienDao();
        NhanVienService service = new NhanVienServiceImpl(dao);
        NhanVien nhanVien = taoNhanVien("NV0001", "Nhân viên mới", "MatKhau123");
        nhanVien.setVaiTro(null);

        NhanVien saved = service.themNhanVien(nhanVien);

        assertEquals(NhanVien.VAI_TRO_NHAN_VIEN, saved.getVaiTro());
        assertNotEquals("MatKhau123", saved.getMatKhau());
        assertTrue(PasswordUtil.matches("MatKhau123", saved.getMatKhau()));
    }

    @Test
    void themNhanVienBatBuocSoDienThoaiEmailVaDiaChi() {
        NhanVienService service = new NhanVienServiceImpl(new FakeNhanVienDao());
        NhanVien nhanVien = taoNhanVien("NV0006", "Nhân viên thiếu liên hệ", "MatKhau123");
        nhanVien.setSoDienThoai(" ");
        nhanVien.setEmail(null);
        nhanVien.setDiaChi("");

        ValidationException error = assertThrows(ValidationException.class,
                () -> service.themNhanVien(nhanVien));

        assertEquals("Vui lòng nhập số điện thoại.", error.getErrors().get("soDienThoai"));
        assertEquals("Vui lòng nhập email.", error.getErrors().get("email"));
        assertEquals("Vui lòng chọn địa chỉ.", error.getErrors().get("diaChi"));
    }

    @Test
    void themNhanVienKiemTraDinhDangSoDienThoaiVaEmail() {
        NhanVienService service = new NhanVienServiceImpl(new FakeNhanVienDao());
        NhanVien nhanVien = taoNhanVien("NV0007", "Nhân viên sai liên hệ", "MatKhau123");
        nhanVien.setSoDienThoai("09123");
        nhanVien.setEmail("email-sai");

        ValidationException error = assertThrows(ValidationException.class,
                () -> service.themNhanVien(nhanVien));

        assertEquals("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.",
                error.getErrors().get("soDienThoai"));
        assertEquals("Email không đúng định dạng.", error.getErrors().get("email"));
    }

    @Test
    void themNhanVienKhongChoTrungSoDienThoaiVaEmail() {
        FakeNhanVienDao dao = new FakeNhanVienDao();
        NhanVien existing = taoNhanVien("NV0008", "Nhân viên đã có", "MatKhau123");
        existing.setEmail("DaCo@Example.com");
        dao.save(existing);
        NhanVienService service = new NhanVienServiceImpl(dao);
        NhanVien nhanVien = taoNhanVien("NV0009", "Nhân viên bị trùng", "MatKhau123");
        nhanVien.setEmail("daco@example.com");

        ValidationException error = assertThrows(ValidationException.class,
                () -> service.themNhanVien(nhanVien));

        assertEquals("Số điện thoại đã được sử dụng.", error.getErrors().get("soDienThoai"));
        assertEquals("Email đã được sử dụng.", error.getErrors().get("email"));
    }

    @Test
    void dangNhapHoTroMatKhauCuVaTuDongChuyenSangDangBam() throws Exception {
        FakeNhanVienDao dao = new FakeNhanVienDao();
        NhanVien legacy = taoNhanVien("NV0002", "Tài khoản cũ", "legacyPass");
        legacy.setEmail("legacy@example.com");
        legacy.setVaiTro(null);
        dao.save(legacy);
        NhanVienService service = new NhanVienServiceImpl(dao);

        NhanVien loggedIn = service.dangNhap(" legacy@example.com ", "legacyPass");

        assertTrue(PasswordUtil.matches("legacyPass", loggedIn.getMatKhau()));
        assertEquals(NhanVien.VAI_TRO_NHAN_VIEN, loggedIn.getVaiTro());
    }

    @Test
    void capNhatHoSoKhongLamMatQuyenVaMatKhau() {
        FakeNhanVienDao dao = new FakeNhanVienDao();
        NhanVien current = taoNhanVien("NV0003", "Quản lý cũ", PasswordUtil.hash("secret"));
        current.setVaiTro(NhanVien.VAI_TRO_QUAN_LY);
        dao.save(current);
        NhanVienService service = new NhanVienServiceImpl(dao);

        NhanVien form = new NhanVien();
        form.setId(current.getId());
        form.setHoTen("Quản lý đã sửa");
        form.setChucVu("Quản lý cửa hàng");
        form.setTrangThai(1);

        NhanVien updated = service.capNhatNhanVien(form);

        assertEquals("Quản lý đã sửa", updated.getHoTen());
        assertEquals(NhanVien.VAI_TRO_QUAN_LY, updated.getVaiTro());
        assertTrue(PasswordUtil.matches("secret", updated.getMatKhau()));
    }

    @Test
    void khongChoHaQuyenQuanLyCuoiCung() {
        FakeNhanVienDao dao = new FakeNhanVienDao();
        NhanVien manager = taoNhanVien("NV0004", "Quản lý duy nhất", PasswordUtil.hash("secret"));
        manager.setVaiTro(NhanVien.VAI_TRO_QUAN_LY);
        dao.save(manager);
        NhanVienService service = new NhanVienServiceImpl(dao);

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> service.capNhatVaiTro(manager.getId(), NhanVien.VAI_TRO_NHAN_VIEN, manager.getId()));

        assertTrue(error.getMessage().contains("tự hạ quyền"));
    }

    @Test
    void khongChoVoHieuHoaQuanLyCuoiCung() {
        FakeNhanVienDao dao = new FakeNhanVienDao();
        NhanVien manager = taoNhanVien("NV0005", "Quản lý cuối cùng", PasswordUtil.hash("secret"));
        manager.setVaiTro(NhanVien.VAI_TRO_QUAN_LY);
        dao.save(manager);
        NhanVienService service = new NhanVienServiceImpl(dao);

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> service.xoaNhanVien(manager.getId()));

        assertTrue(error.getMessage().contains("quản lý cuối cùng"));
        assertEquals(1, manager.getTrangThai());
    }

    private static NhanVien taoNhanVien(String ma, String hoTen, String matKhau) {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(ma);
        nhanVien.setHoTen(hoTen);
        nhanVien.setMatKhau(matKhau);
        nhanVien.setSoDienThoai("0900000000");
        nhanVien.setEmail("nhanvien@example.com");
        nhanVien.setDiaChi("TP. Hà Nội");
        nhanVien.setTrangThai(1);
        return nhanVien;
    }

    private static class FakeNhanVienDao implements NhanVienDao {
        private final Map<Integer, NhanVien> data = new LinkedHashMap<>();
        private int sequence = 1;

        @Override
        public NhanVien save(NhanVien entity) {
            if (entity.getId() == null) entity.setId(sequence++);
            data.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public NhanVien update(NhanVien entity) {
            data.put(entity.getId(), entity);
            return entity;
        }

        @Override public void delete(NhanVien entity) { data.remove(entity.getId()); }
        @Override public void deleteById(Integer id) { data.remove(id); }
        @Override public NhanVien findById(Integer id) { return data.get(id); }
        @Override public List<NhanVien> findAll() { return new ArrayList<>(data.values()); }
        @Override public List<NhanVien> findWithPaging(int pageNumber, int pageSize) { return findAll(); }
        @Override public long count() { return data.size(); }

        @Override
        public NhanVien findByMaNhanVien(String maNhanVien) {
            return data.values().stream()
                    .filter(item -> maNhanVien.equals(item.getMaNhanVien()))
                    .findFirst().orElse(null);
        }

        @Override public List<NhanVien> search(String tuKhoa) { return findAll(); }

        @Override
        public void softDelete(Integer id) {
            NhanVien item = data.get(id);
            if (item != null) item.setTrangThai(0);
        }

        @Override
        public NhanVien findByEmail(String email) {
            return data.values().stream()
                    .filter(item -> item.getEmail() != null && email.trim().equalsIgnoreCase(item.getEmail().trim()))
                    .findFirst().orElse(null);
        }

        @Override
        public NhanVien findBySoDienThoai(String soDienThoai) {
            return data.values().stream()
                    .filter(item -> item.getSoDienThoai() != null
                            && soDienThoai.trim().equals(item.getSoDienThoai().trim()))
                    .findFirst().orElse(null);
        }

        @Override
        public String findMaxMaNhanVien() {
            return data.values().stream().map(NhanVien::getMaNhanVien)
                    .filter(value -> value != null && value.startsWith("MNV"))
                    .max(String::compareTo).orElse(null);
        }
    }
}
