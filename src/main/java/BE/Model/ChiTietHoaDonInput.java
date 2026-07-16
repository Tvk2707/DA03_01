package BE.Model;

<<<<<<< HEAD
// Một sản phẩm và số lượng người dùng chọn khi tạo hóa đơn.
=======
// Sản phẩm và số lượng người dùng chọn khi tạo hóa đơn.
>>>>>>> 5a398f6 (delete button)
public class ChiTietHoaDonInput {
    private final int idSanPhamChiTiet;
    private final int soLuong;

    public ChiTietHoaDonInput(int idSanPhamChiTiet, int soLuong) {
        this.idSanPhamChiTiet = idSanPhamChiTiet;
        this.soLuong = soLuong;
    }

    public int getIdSanPhamChiTiet() { return idSanPhamChiTiet; }
    public int getSoLuong() { return soLuong; }
}
