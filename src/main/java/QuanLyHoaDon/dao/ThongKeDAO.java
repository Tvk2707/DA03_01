package QuanLyHoaDon.dao;

import QuanLyHoaDon.Model.ThongKeCustomer;
import QuanLyHoaDon.Model.ThongKeOverview;
import QuanLyHoaDon.Model.ThongKeProduct;
import QuanLyHoaDon.Model.ThongKeSeriesPoint;
import BE.jdbc.DatabaseConnectionManager;

import java.math.BigDecimal;
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
        String sql = "WITH invoice_period AS ("
                + "SELECT hd.id, hd.trang_thai, hd.tong_tien_thanh_toan, "
                + "CASE WHEN hd.trang_thai = 3 "
                + "THEN COALESCE(hd.ngay_thanh_toan, hd.ngay_tao) ELSE hd.ngay_tao END AS period_date "
                + "FROM hoa_don hd"
                + "), filtered AS ("
                + "SELECT * FROM invoice_period WHERE period_date >= ? AND period_date < ?"
                + ") "
                + "SELECT COUNT(*) AS orders, "
                + "ISNULL(SUM(CASE WHEN trang_thai = 3 THEN ISNULL(tong_tien_thanh_toan, 0) ELSE 0 END), 0) AS revenue, "
                + "ISNULL((SELECT SUM(ct.so_luong) FROM chi_tiet_hoa_don ct "
                + "JOIN filtered paid ON paid.id = ct.id_hoa_don WHERE paid.trang_thai = 3), 0) AS products, "
                + "SUM(CASE WHEN trang_thai = 3 THEN 1 ELSE 0 END) AS done, "
                + "SUM(CASE WHEN trang_thai = 5 THEN 1 ELSE 0 END) AS cancelled, "
                + "SUM(CASE WHEN trang_thai NOT IN (3, 5) OR trang_thai IS NULL THEN 1 ELSE 0 END) AS processing "
                + "FROM filtered";

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

    public List<ThongKeProduct> getBestSellers(LocalDateTime from, LocalDateTime to) throws SQLException {
        String sql = "WITH sales AS ("
                + "SELECT sp.id AS id_san_pham, SUM(ct.so_luong) AS da_ban "
                + "FROM chi_tiet_hoa_don ct "
                + "JOIN hoa_don hd ON ct.id_hoa_don = hd.id "
                + "JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id "
                + "JOIN san_pham sp ON spct.id_san_pham = sp.id "
                + "WHERE hd.trang_thai = 3 "
                + "AND COALESCE(hd.ngay_thanh_toan, hd.ngay_tao) >= ? "
                + "AND COALESCE(hd.ngay_thanh_toan, hd.ngay_tao) < ? "
                + "GROUP BY sp.id"
                + "), stock AS ("
                + "SELECT id_san_pham, SUM(ISNULL(so_luong_ton, 0)) AS ton_kho "
                + "FROM san_pham_chi_tiet GROUP BY id_san_pham"
                + ") "
                + "SELECT TOP 5 sp.ten_san_pham, sales.da_ban, ISNULL(stock.ton_kho, 0) AS ton_kho "
                + "FROM sales JOIN san_pham sp ON sales.id_san_pham = sp.id "
                + "LEFT JOIN stock ON stock.id_san_pham = sp.id "
                + "ORDER BY sales.da_ban DESC, sp.ten_san_pham";
        return getProducts(sql, from, to);
    }

    public List<ThongKeProduct> getSlowStockProducts(LocalDateTime from, LocalDateTime to) throws SQLException {
        String sql = "WITH sales AS ("
                + "SELECT spct.id_san_pham, SUM(ct.so_luong) AS da_ban "
                + "FROM chi_tiet_hoa_don ct "
                + "JOIN hoa_don hd ON ct.id_hoa_don = hd.id "
                + "JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id "
                + "WHERE hd.trang_thai = 3 "
                + "AND COALESCE(hd.ngay_thanh_toan, hd.ngay_tao) >= ? "
                + "AND COALESCE(hd.ngay_thanh_toan, hd.ngay_tao) < ? "
                + "GROUP BY spct.id_san_pham"
                + "), stock AS ("
                + "SELECT id_san_pham, SUM(ISNULL(so_luong_ton, 0)) AS ton_kho "
                + "FROM san_pham_chi_tiet GROUP BY id_san_pham"
                + ") "
                + "SELECT TOP 5 sp.ten_san_pham, ISNULL(sales.da_ban, 0) AS da_ban, "
                + "ISNULL(stock.ton_kho, 0) AS ton_kho "
                + "FROM san_pham sp "
                + "LEFT JOIN sales ON sales.id_san_pham = sp.id "
                + "LEFT JOIN stock ON stock.id_san_pham = sp.id "
                + "WHERE ISNULL(sp.trang_thai, 1) = 1 "
                + "ORDER BY ISNULL(sales.da_ban, 0) ASC, ISNULL(stock.ton_kho, 0) DESC";
        return getProducts(sql, from, to);
    }

    public List<ThongKeCustomer> getTopCustomers(LocalDateTime from, LocalDateTime to) throws SQLException {
        String sql = "SELECT TOP 5 kh.ho_ten AS ten_khach_hang, "
                + "COUNT(DISTINCT hd.id) AS so_don, "
                + "ISNULL(SUM(ISNULL(hd.tong_tien_thanh_toan, 0)), 0) AS tong_chi_tieu "
                + "FROM hoa_don hd "
                + "JOIN khach_hang kh ON hd.id_khach_hang = kh.id "
                + "WHERE hd.trang_thai = 3 "
                + "AND COALESCE(hd.ngay_thanh_toan, hd.ngay_tao) >= ? "
                + "AND COALESCE(hd.ngay_thanh_toan, hd.ngay_tao) < ? "
                + "GROUP BY kh.id, kh.ho_ten "
                + "ORDER BY tong_chi_tieu DESC";

        List<ThongKeCustomer> items = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            setRange(ps, from, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeCustomer item = new ThongKeCustomer();
                    item.setTenKhachHang(rs.getString("ten_khach_hang"));
                    item.setSoDon(rs.getInt("so_don"));
                    item.setTongChiTieu(rs.getBigDecimal("tong_chi_tieu"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public List<ThongKeSeriesPoint> getRevenueSeries(
            LocalDateTime from, LocalDateTime to, boolean groupByMonth) throws SQLException {
        String datePart = groupByMonth ? "MONTH" : "DAY";
        String sql = "SELECT DATEPART(" + datePart + ", COALESCE(hd.ngay_thanh_toan, hd.ngay_tao)) AS period_label, "
                + "ISNULL(SUM(hd.tong_tien_thanh_toan), 0) AS revenue "
                + "FROM hoa_don hd "
                + "WHERE hd.trang_thai = 3 "
                + "AND COALESCE(hd.ngay_thanh_toan, hd.ngay_tao) >= ? "
                + "AND COALESCE(hd.ngay_thanh_toan, hd.ngay_tao) < ? "
                + "GROUP BY DATEPART(" + datePart + ", COALESCE(hd.ngay_thanh_toan, hd.ngay_tao)) "
                + "ORDER BY period_label";

        List<ThongKeSeriesPoint> points = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            setRange(ps, from, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    points.add(new ThongKeSeriesPoint(
                            rs.getString("period_label"),
                            defaultMoney(rs.getBigDecimal("revenue"))));
                }
            }
        }
        return points;
    }

    private List<ThongKeProduct> getProducts(
            String sql, LocalDateTime from, LocalDateTime to) throws SQLException {
        List<ThongKeProduct> items = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            setRange(ps, from, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeProduct item = new ThongKeProduct();
                    item.setTenSanPham(rs.getString("ten_san_pham"));
                    item.setDaBan(rs.getInt("da_ban"));
                    item.setTonKho(rs.getInt("ton_kho"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private void setRange(PreparedStatement ps, LocalDateTime from, LocalDateTime to) throws SQLException {
        ps.setTimestamp(1, Timestamp.valueOf(from));
        ps.setTimestamp(2, Timestamp.valueOf(to));
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
