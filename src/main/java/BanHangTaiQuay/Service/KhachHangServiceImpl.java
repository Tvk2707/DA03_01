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
            throw new IllegalArgumentException("Số điện thoại khách hàng không được để trống.");
        }
        return khachHangRepository.findBySoDienThoai(soDienThoai.trim());
    }

    @Override
    public List<KhachHang> timKhachHangTheoTuKhoa(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().length() < 2) {
            return List.of();
        }
        return khachHangRepository.timKiemTheoTenHoacSoDienThoai(tuKhoa.trim());
    }

    @Override
    public KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen) {
        return traCuuHoacTaoKhachHang(soDienThoai, hoTen, null, null, null, null);
    }

    @Override
    public KhachHang traCuuHoacTaoKhachHang(String soDienThoai, String hoTen,
                                             String email, LocalDate ngaySinh,
                                             Integer gioiTinh, String matKhau) {
        String soDienThoaiDaChuanHoa = lamSach(soDienThoai);
        KhachHang khachHang = soDienThoaiDaChuanHoa == null
                ? null
                : khachHangRepository.findBySoDienThoai(soDienThoaiDaChuanHoa);
        if (khachHang != null) {
            return khachHang;
        }

        KhachHang khachHangMoi = new KhachHang();
        khachHangMoi.setMaKhachHang(sinhMaKhachHangTiepTheo());
        khachHangMoi.setSoDienThoai(soDienThoaiDaChuanHoa);
        khachHangMoi.setHoTen(lamSach(hoTen) == null ? "Khách lẻ" : lamSach(hoTen));
        khachHangMoi.setEmail(lamSach(email));
        khachHangMoi.setMatKhau(lamSach(matKhau));
        khachHangMoi.setNgaySinh(ngaySinh);
        khachHangMoi.setGioiTinh(gioiTinh);
        khachHangMoi.setTrangThai(1);
        khachHangRepository.add(khachHangMoi);

        if (khachHangMoi.getId() == null) {
            throw new IllegalStateException("Không thể lưu khách hàng vào database.");
        }
        return khachHangMoi;
    }

    private String lamSach(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String sinhMaKhachHangTiepTheo() {
        int soThuTuLonNhat = 0;
        for (KhachHang khachHang : khachHangRepository.getAll()) {
            String maKhachHang = khachHang.getMaKhachHang();
            if (maKhachHang == null || !maKhachHang.matches("KH\\d{3,6}")) {
                continue;
            }
            try {
                soThuTuLonNhat = Math.max(
                        soThuTuLonNhat,
                        Integer.parseInt(maKhachHang.substring(2))
                );
            } catch (NumberFormatException ignored) {
                // Bỏ qua mã cũ không đúng định dạng số.
            }
        }
        return String.format("KH%03d", soThuTuLonNhat + 1);
    }

    @Override
    public KhachHang timKhachHangTheoId(int idKhachHang) {
        if (idKhachHang <= 0) {
            throw new IllegalArgumentException("ID khách hàng không hợp lệ.");
        }
        return khachHangRepository.getAll().stream()
                .filter(khachHang -> Integer.valueOf(idKhachHang).equals(khachHang.getId()))
                .findFirst()
                .orElse(null);
    }
}
