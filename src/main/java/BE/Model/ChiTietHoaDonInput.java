package BE.Model;

// San pham va so luong nguoi dung chon khi tao hoa don.
public class ChiTietHoaDonInput {
    private final int idSanPhamChiTiet;
    private final int soLuong;

    public ChiTietHoaDonInput(int idSanPhamChiTiet, int soLuong) {
        this.idSanPhamChiTiet = idSanPhamChiTiet;
        this.soLuong = soLuong;
    }

    public int getIdSanPhamChiTiet() {
        return idSanPhamChiTiet;
    }

    public int getSoLuong() {
        return soLuong;
    }
}

