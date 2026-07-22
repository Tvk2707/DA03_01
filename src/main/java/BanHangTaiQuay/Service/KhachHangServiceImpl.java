package BanHangTaiQuay.Service;

import QuanLyKhachHang.repository.KhachHangRepository;
import QuanLySanPham.Entity.KhachHang;

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
