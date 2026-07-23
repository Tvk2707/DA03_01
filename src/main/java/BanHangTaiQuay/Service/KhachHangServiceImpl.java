package BanHangTaiQuay.Service;

import QuanLyKhachHang.repository.KhachHangRepository;
import QuanLySanPham.Entity.KhachHang;

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
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại khách hàng không được để trống.");
        }

        String soDienThoaiDaChuanHoa = soDienThoai.trim();
        KhachHang khachHang = khachHangRepository.findBySoDienThoai(soDienThoaiDaChuanHoa);
        if (khachHang != null) {
            return khachHang;
        }

        KhachHang khachHangMoi = new KhachHang();
        khachHangMoi.setMaKhachHang(sinhMaKhachHangTiepTheo());
        khachHangMoi.setSoDienThoai(soDienThoaiDaChuanHoa);
        khachHangMoi.setHoTen(hoTen == null || hoTen.trim().isEmpty()
                ? "Khách lẻ"
                : hoTen.trim());
        khachHangMoi.setTrangThai(1);
        khachHangRepository.add(khachHangMoi);

        if (khachHangMoi.getId() == null) {
            throw new IllegalStateException("Không thể lưu khách hàng vào database.");
        }
        return khachHangMoi;
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
