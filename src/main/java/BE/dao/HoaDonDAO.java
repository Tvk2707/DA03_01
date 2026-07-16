package BE.dao;

import BE.Model.HoaDonView;
import BE.jdbc.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {
    private final DatabaseConnectionManager connectionManager = DatabaseConnectionManager.fromEnvironment();

    public List<HoaDonView> findAll() throws SQLException {
        String sql = "SELECT hd.id, hd.ma_hoa_don, hd.ten_nguoi_nhan, hd.sdt_nguoi_nhan, "
                + "hd.ngay_tao, hd.tong_tien_thanh_toan, hd.trang_thai, "
                + "kh.ho_ten AS ten_khach_hang, kh.so_dien_thoai AS sdt_khach_hang, "
                + "nv.ho_ten AS ten_nhan_vien, nv.ma_nhan_vien, "
                + "COALESCE(SUM(ct.so_luong), 0) AS so_luong_san_pham "
                + "FROM hoa_don hd "
                + "LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id "
                + "LEFT JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id "
                + "LEFT JOIN chi_tiet_hoa_don ct ON hd.id = ct.id_hoa_don "
                + "WHERE ISNULL(hd.trang_thai, 1) <> 5 "
                + "GROUP BY hd.id, hd.ma_hoa_don, hd.ten_nguoi_nhan, hd.sdt_nguoi_nhan, "
                + "hd.ngay_tao, hd.tong_tien_thanh_toan, hd.trang_thai, "
                + "kh.ho_ten, kh.so_dien_thoai, nv.ho_ten, nv.ma_nhan_vien "
                + "ORDER BY hd.id DESC";

        List<HoaDonView> result = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            int stt = 1;
            while (rs.next()) {
                HoaDonView item = new HoaDonView();
                item.setStt(stt++);
                item.setId(rs.getInt("id"));
                item.setMaHoaDon(rs.getString("ma_hoa_don"));
                item.setTenNhanVien(valueOrDefault(rs.getString("ten_nhan_vien"), "System"));
                item.setMaNhanVien(valueOrDefault(rs.getString("ma_nhan_vien"), "SYSTEM"));
                item.setTenKhachHang(valueOrDefault(rs.getString("ten_khach_hang"), rs.getString("ten_nguoi_nhan")));
                item.setSoDienThoai(valueOrDefault(rs.getString("sdt_khach_hang"), rs.getString("sdt_nguoi_nhan")));
                item.setLoaiHoaDon("Tại quầy");
                item.setTongTien(rs.getBigDecimal("tong_tien_thanh_toan"));
                item.setTrangThai(rs.getInt("trang_thai"));
                item.setSoLuongSanPham(rs.getInt("so_luong_san_pham"));

                Timestamp ngayTao = rs.getTimestamp("ngay_tao");
                if (ngayTao != null) {
                    item.setNgayTao(ngayTao.toLocalDateTime());
                }
                result.add(item);
            }
        }
        return result;
    }

    private String valueOrDefault(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback == null || fallback.trim().isEmpty() ? "-" : fallback;
        }
        return value;
    }
}
