package QuanLyHoaDon.service;

import QuanLyHoaDon.Model.ChiTietHoaDonView;
import QuanLyHoaDon.Model.HoaDonView;
import QuanLyHoaDon.Model.LichSuHoaDonView;
import QuanLyHoaDon.Model.LichSuThanhToanView;
import QuanLyHoaDon.Model.NhanVienView;
import QuanLyHoaDon.Model.ThanhToanHoaDonView;
import QuanLyHoaDon.dao.HoaDonDAO;
import QuanLyHoaDon.Model.ChiTietHoaDonView;
import QuanLyHoaDon.Model.HoaDonView;
import QuanLyHoaDon.Model.LichSuHoaDonView;
import QuanLyHoaDon.Model.LichSuThanhToanView;
import QuanLyHoaDon.Model.ThanhToanHoaDonView;
import QuanLyHoaDon.dao.HoaDonDAO;

import java.sql.SQLException;
import java.util.List;

public class HoaDonService {
    // Service là tầng trung gian: controller không gọi SQL trực tiếp mà gọi qua service.
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

    public List<LichSuThanhToanView> getLichSuThanhToan(int hoaDonId) throws SQLException {
        return hoaDonDAO.findPaymentHistoryByHoaDonId(hoaDonId);
    }

    public List<LichSuHoaDonView> getLichSuHoaDon(int hoaDonId) throws SQLException {
        return hoaDonDAO.findHistoryByHoaDonId(hoaDonId);
    }

    public void updateTrangThai(int id, int trangThai, String ghiChu) throws SQLException {
        hoaDonDAO.updateStatus(id, trangThai, ghiChu);
    }
}
