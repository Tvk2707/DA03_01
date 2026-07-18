package BE.Model;

// Một sản phẩm và số lượng người dùng chọn khi tạo hóa đơn.
public class ChiTietHoaDonInput {
    private final int idSanPhamChiTiet;
    private final int soLuong;

    public ChiTietHoaDonInput(int idSanPhamChiTiet, int soLuong) {
        this.idSanPhamChiTiet = idSanPhamChiTiet;
        this.soLuong = soLuong;
    }

<<<<<<< HEAD
    public int getIdSanPhamChiTiet() {
        return idSanPhamChiTiet;
    }

    public int getSoLuong() {
        return soLuong;
    }
}
=======
    public int getIdSanPhamChiTiet() { return idSanPhamChiTiet; }
    public int getSoLuong() { return soLuong; }
}
>>>>>>> HOA_DON
