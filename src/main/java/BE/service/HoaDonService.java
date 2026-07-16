package BE.service;

import BE.Model.ChiTietHoaDonView;
import BE.Model.ChiTietHoaDonInput;
import BE.Model.HoaDonView;
import BE.Model.LichSuHoaDonView;
import BE.Model.LichSuThanhToanView;
import BE.Model.NhanVienView;
import BE.Model.SanPhamHoaDonView;
import BE.Model.ThanhToanHoaDonView;
import BE.dao.HoaDonDAO;

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

    public List<NhanVienView> getAllNhanVien() throws SQLException {
        return hoaDonDAO.findAllNhanVien();
    }

    public List<SanPhamHoaDonView> getAllSanPhamHoaDon() throws SQLException {
        return hoaDonDAO.findAllSanPhamHoaDon();
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

    public void saveHoaDon(HoaDonView hoaDon) throws SQLException {
        saveHoaDon(hoaDon, java.util.Collections.emptyList());
    }

    public void saveHoaDon(HoaDonView hoaDon, List<ChiTietHoaDonInput> productLines) throws SQLException {
        if (isBlank(hoaDon.getMaHoaDon()) || hoaDonDAO.existsByMaHoaDon(hoaDon.getMaHoaDon(), null)) {
            hoaDon.setMaHoaDon(hoaDonDAO.generateNextMaHoaDon());
        }
        int invoiceId = hoaDonDAO.insert(hoaDon);
        if (!productLines.isEmpty()) {
            hoaDonDAO.insertChiTietHoaDon(invoiceId, productLines);
        }
    }

    public void updateTrangThai(int id, int trangThai, String ghiChu) throws SQLException {
        hoaDonDAO.updateStatus(id, trangThai, ghiChu);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
