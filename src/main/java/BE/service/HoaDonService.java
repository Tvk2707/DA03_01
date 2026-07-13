package BE.service;

import BE.Model.ChiTietHoaDonView;
import BE.Model.HoaDonView;
import BE.Model.LichSuHoaDonView;
import BE.Model.ThanhToanHoaDonView;
import BE.dao.HoaDonDAO;

import java.sql.SQLException;
import java.util.List;

public class HoaDonService {
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();

    public List<HoaDonView> getAllHoaDon() throws SQLException {
        return hoaDonDAO.findAll();
    }

    public HoaDonView getHoaDonById(int id) throws SQLException {
        return hoaDonDAO.findById(id);
    }

    public List<ChiTietHoaDonView> getChiTietHoaDon(int hoaDonId) throws SQLException {
        return hoaDonDAO.findDetailsByHoaDonId(hoaDonId);
    }

    public List<ThanhToanHoaDonView> getThanhToanHoaDon(int hoaDonId) throws SQLException {
        return hoaDonDAO.findPaymentsByHoaDonId(hoaDonId);
    }

    public List<LichSuHoaDonView> getLichSuHoaDon(int hoaDonId) throws SQLException {
        return hoaDonDAO.findHistoryByHoaDonId(hoaDonId);
    }

    public void saveHoaDon(HoaDonView hoaDon) throws SQLException {
        if (hoaDon.getId() == null) {
            hoaDonDAO.insert(hoaDon);
        } else {
            hoaDonDAO.update(hoaDon);
        }
    }

    public void huyHoaDon(int id) throws SQLException {
        hoaDonDAO.delete(id);
    }

    public void updateTrangThai(int id, int trangThai, String ghiChu) throws SQLException {
        hoaDonDAO.updateStatus(id, trangThai, ghiChu);
    }
}
