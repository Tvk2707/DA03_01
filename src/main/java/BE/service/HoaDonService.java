package BE.service;

import BE.Model.HoaDonView;
import BE.dao.HoaDonDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class HoaDonService {
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();

    public List<HoaDonView> getAllHoaDon() throws SQLException {
        return hoaDonDAO.findAll();
    }

    public BigDecimal tinhTongDoanhThu(List<HoaDonView> hoaDonList) {
        BigDecimal total = BigDecimal.ZERO;
        for (HoaDonView hoaDon : hoaDonList) {
            if (hoaDon.getTongTien() != null && hoaDon.getTrangThai() == 4) {
                total = total.add(hoaDon.getTongTien());
            }
        }
        return total;
    }

    public int demDangXuLy(List<HoaDonView> hoaDonList) {
        int count = 0;
        for (HoaDonView hoaDon : hoaDonList) {
            if (hoaDon.getTrangThai() > 0 && hoaDon.getTrangThai() < 4) {
                count++;
            }
        }
        return count;
    }

    public int demSanPham(List<HoaDonView> hoaDonList) {
        int count = 0;
        for (HoaDonView hoaDon : hoaDonList) {
            count += hoaDon.getSoLuongSanPham();
        }
        return count;
    }
}
