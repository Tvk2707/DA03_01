package BanHangTaiQuay.Service;

import QuanLyKhachHang.repository.KhachHangRepository;
import QuanLySanPham.Entity.KhachHang;

import java.time.LocalDate;
import java.util.List;

public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepository khachHangRepository = new KhachHangRepository();

    @Override
    public KhachHang traCuuKhachHang(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Tu khoa khach hang khong duoc de trong.");
        }
        var ketQua = khachHangRepository.findByKeyword(soDienThoai.trim());
        if (ketQua.size() > 1) {
            throw new IllegalArgumentException(
                    "Co nhieu khach hang phu hop. Vui long nhap chinh xac so dien thoai hoac ma khach hang.");
        }
        return ketQua.isEmpty() ? null : ketQua.get(0);
    }

    @Override
    public List<KhachHang> timKiemKhachHang(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            throw new IllegalArgumentException("Tu khoa khach hang khong duoc de trong.");
        }
        return khachHangRepository.findByKeyword(tuKhoa.trim());
    }

    @Override
    public KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen) {
        return traCuuHoacTaoKhachHang(soDienThoai, hoTen, null, null, null);
    }

    @Override
    public KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen, String email,
                                            LocalDate ngaySinh, Integer gioiTinh) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("So dien thoai khach hang khong duoc de trong.");
        }

        String soDienThoaiDaChuanHoa = soDienThoai.trim();
        KhachHang khachHang = khachHangRepository.findBySoDienThoai(soDienThoaiDaChuanHoa);
        if (khachHang != null) {
            return khachHang;
        }
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên khách hàng không được để trống.");
        }

        KhachHang khachHangMoi = new KhachHang();
        khachHangMoi.setSoDienThoai(soDienThoaiDaChuanHoa);
        khachHangMoi.setHoTen(hoTen.trim());
        khachHangMoi.setEmail(email == null || email.trim().isEmpty() ? null : email.trim());
        khachHangMoi.setNgaySinh(ngaySinh);
        khachHangMoi.setGioiTinh(gioiTinh);
        khachHangMoi.setTrangThai(1);
        khachHangRepository.add(khachHangMoi);

        if (khachHangMoi.getId() == null) {
            throw new IllegalStateException("Khong the luu khach hang vao database.");
        }
        return khachHangMoi;
    }

    @Override
    public KhachHang timKhachHangTheoId(int idKhachHang) {
        if (idKhachHang <= 0) {
            throw new IllegalArgumentException("ID khach hang khong hop le.");
        }
        return khachHangRepository.getAll().stream()
                .filter(khachHang -> Integer.valueOf(idKhachHang).equals(khachHang.getId()))
                .findFirst()
                .orElse(null);
    }
}
