package QuanLyHoaDon.dao;

import QuanLyHoaDon.Model.ThongKeCustomer;
import QuanLyHoaDon.Model.ThongKeOverview;
import QuanLyHoaDon.Model.ThongKeProduct;
import BE.jdbc.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {
    private final DatabaseConnectionManager connectionManager = DatabaseConnectionManager.fromEnvironment();

    public ThongKeOverview getOverview(LocalDateTime from, LocalDateTime to) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT hd.id) AS orders, "
                + "ISNULL(SUM(ISNULL(hd.tong_tien_thanh_toan, 0)), 0) AS revenue, "
                + "ISNULL(SUM(ISNULL(ct.so_luong, 0)), 0) AS products, "
                + "SUM(CASE WHEN hd.trang_thai = 4 THEN 1 ELSE 0 END) AS done, "
                + "SUM(CASE WHEN hd.trang_thai = 5 THEN 1 ELSE 0 END) AS cancelled, "
                + "SUM(CASE WHEN ISNULL(hd.trang_thai, 1) BETWEEN 1 AND 3 THEN 1 ELSE 0 END) AS processing "
                + "FROM hoa_don hd "
                + "LEFT JOIN chi_tiet_hoa_don ct ON hd.id = ct.id_hoa_don "
                + "WHERE hd.ngay_tao >= ? AND hd.ngay_tao < ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                ThongKeOverview item = new ThongKeOverview();
                if (rs.next()) {
                    item.setOrders(rs.getInt("orders"));
                    item.setRevenue(rs.getBigDecimal("revenue"));
                    item.setProducts(rs.getInt("products"));
                    item.setDone(rs.getInt("done"));
                    item.setCancelled(rs.getInt("cancelled"));
                    item.setProcessing(rs.getInt("processing"));
                }
                return item;
            }
        }
    }

    public List<ThongKeProduct> getBestSellers() throws SQLException {
        String sql = "SELECT TOP 5 sp.ten_san_pham, "
                + "ISNULL(SUM(ct.so_luong), 0) AS da_ban, "
                + "ISNULL(MAX(spct.so_luong_ton), 0) AS ton_kho "
                + "FROM chi_tiet_hoa_don ct "
                + "JOIN hoa_don hd ON ct.id_hoa_don = hd.id "
                + "JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id "
                + "JOIN san_pham sp ON spct.id_san_pham = sp.id "
                + "WHERE ISNULL(hd.trang_thai, 1) <> 5 "
                + "GROUP BY sp.ten_san_pham "
                + "ORDER BY da_ban DESC";
        return getProducts(sql);
    }

    public List<ThongKeProduct> getSlowStockProducts() throws SQLException {
        String sql = "SELECT TOP 5 sp.ten_san_pham, "
                + "ISNULL(SUM(ct.so_luong), 0) AS da_ban, "
                + "ISNULL(MAX(spct.so_luong_ton), 0) AS ton_kho "
                + "FROM san_pham_chi_tiet spct "
                + "JOIN san_pham sp ON spct.id_san_pham = sp.id "
                + "LEFT JOIN chi_tiet_hoa_don ct ON spct.id = ct.id_san_pham_chi_tiet "
                + "LEFT JOIN hoa_don hd ON ct.id_hoa_don = hd.id AND ISNULL(hd.trang_thai, 1) <> 5 "
                + "GROUP BY sp.ten_san_pham "
                + "ORDER BY da_ban ASC, ton_kho DESC";
        return getProducts(sql);
    }

    public List<ThongKeCustomer> getTopCustomers() throws SQLException {
        String sql = "SELECT TOP 5 COALESCE(kh.ho_ten, hd.ten_nguoi_nhan, N'Khách lẻ') AS ten_khach_hang, "
                + "COUNT(DISTINCT hd.id) AS so_don, "
                + "ISNULL(SUM(ISNULL(hd.tong_tien_thanh_toan, 0)), 0) AS tong_chi_tieu "
                + "FROM hoa_don hd "
                + "LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id "
                + "WHERE ISNULL(hd.trang_thai, 1) <> 5 "
                + "GROUP BY COALESCE(kh.ho_ten, hd.ten_nguoi_nhan, N'Khách lẻ') "
                + "ORDER BY tong_chi_tieu DESC";

        List<ThongKeCustomer> items = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ThongKeCustomer item = new ThongKeCustomer();
                item.setTenKhachHang(rs.getString("ten_khach_hang"));
                item.setSoDon(rs.getInt("so_don"));
                item.setTongChiTieu(rs.getBigDecimal("tong_chi_tieu"));
                items.add(item);
            }
        }
        return items;
    }

    private List<ThongKeProduct> getProducts(String sql) throws SQLException {
        List<ThongKeProduct> items = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ThongKeProduct item = new ThongKeProduct();
                item.setTenSanPham(rs.getString("ten_san_pham"));
                item.setDaBan(rs.getInt("da_ban"));
                item.setTonKho(rs.getInt("ton_kho"));
                items.add(item);
            }
        }
        return items;
    }
}
