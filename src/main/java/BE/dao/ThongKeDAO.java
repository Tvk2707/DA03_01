package BE.dao;

import BE.Model.ThongKeCustomer;
import BE.Model.ThongKeOverview;
import BE.Model.ThongKeProduct;
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

    /**
     * Tổng hợp theo hóa đơn, tránh nhân doanh thu khi một hóa đơn có nhiều dòng sản phẩm.
     * Trạng thái hiện tại của module hóa đơn: 3 = đã thanh toán, 5 = đã hủy.
     */
    public ThongKeOverview getOverview(LocalDateTime from, LocalDateTime to) throws SQLException {
        String sql = "WITH filtered AS ("
                + " SELECT hd.id, ISNULL(hd.tong_tien_thanh_toan, 0) AS total, ISNULL(hd.trang_thai, 1) AS status"
                + " FROM hoa_don hd WHERE hd.ngay_tao >= ? AND hd.ngay_tao < ?"
                + ") SELECT COUNT(*) AS orders,"
                + " ISNULL(SUM(CASE WHEN status <> 5 THEN total ELSE 0 END), 0) AS revenue,"
                + " ISNULL((SELECT SUM(ISNULL(ct.so_luong, 0)) FROM chi_tiet_hoa_don ct"
                + "         JOIN filtered f ON f.id = ct.id_hoa_don AND f.status <> 5), 0) AS products,"
                + " ISNULL(SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END), 0) AS done,"
                + " ISNULL(SUM(CASE WHEN status = 5 THEN 1 ELSE 0 END), 0) AS cancelled,"
                + " ISNULL(SUM(CASE WHEN status NOT IN (3, 5) THEN 1 ELSE 0 END), 0) AS processing"
                + " FROM filtered";

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
        String sql = "WITH stock_totals AS (SELECT id_san_pham, SUM(ISNULL(so_luong_ton, 0)) AS ton_kho"
                + " FROM san_pham_chi_tiet GROUP BY id_san_pham)"
                + " SELECT TOP 5 sp.ten_san_pham,"
                + " ISNULL(SUM(ct.so_luong), 0) AS da_ban,"
                + " ISNULL(stock_totals.ton_kho, 0) AS ton_kho"
                + " FROM chi_tiet_hoa_don ct"
                + " JOIN hoa_don hd ON ct.id_hoa_don = hd.id"
                + " JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id"
                + " JOIN san_pham sp ON spct.id_san_pham = sp.id"
                + " JOIN stock_totals ON stock_totals.id_san_pham = sp.id"
                + " WHERE ISNULL(hd.trang_thai, 1) <> 5 AND hd.ngay_tao >= ? AND hd.ngay_tao < ?"
                + " GROUP BY sp.ten_san_pham, stock_totals.ton_kho ORDER BY da_ban DESC, sp.ten_san_pham";
        return getProducts(sql, from, to);
    }

    public List<ThongKeProduct> getSlowStockProducts(LocalDateTime from, LocalDateTime to) throws SQLException {
        String sql = "WITH stock_totals AS (SELECT id_san_pham, SUM(ISNULL(so_luong_ton, 0)) AS ton_kho"
                + " FROM san_pham_chi_tiet GROUP BY id_san_pham)"
                + " SELECT TOP 5 sp.ten_san_pham,"
                + " ISNULL(SUM(CASE WHEN hd.id IS NULL THEN 0 ELSE ISNULL(ct.so_luong, 0) END), 0) AS da_ban,"
                + " ISNULL(stock_totals.ton_kho, 0) AS ton_kho"
                + " FROM stock_totals"
                + " JOIN san_pham sp ON stock_totals.id_san_pham = sp.id"
                + " JOIN san_pham_chi_tiet spct ON spct.id_san_pham = stock_totals.id_san_pham"
                + " LEFT JOIN chi_tiet_hoa_don ct ON spct.id = ct.id_san_pham_chi_tiet"
                + " LEFT JOIN hoa_don hd ON ct.id_hoa_don = hd.id"
                + " AND hd.ngay_tao >= ? AND hd.ngay_tao < ? AND ISNULL(hd.trang_thai, 1) <> 5"
                + " GROUP BY sp.ten_san_pham, stock_totals.ton_kho"
                + " ORDER BY da_ban ASC, ton_kho DESC, sp.ten_san_pham";
        return getProducts(sql, from, to);
    }

    public List<ThongKeCustomer> getTopCustomers(LocalDateTime from, LocalDateTime to) throws SQLException {
        String sql = "SELECT TOP 5 COALESCE(kh.ho_ten, hd.ten_nguoi_nhan, N'Khách lẻ') AS ten_khach_hang,"
                + " COUNT(DISTINCT hd.id) AS so_don,"
                + " ISNULL(SUM(ISNULL(hd.tong_tien_thanh_toan, 0)), 0) AS tong_chi_tieu"
                + " FROM hoa_don hd LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id"
                + " WHERE ISNULL(hd.trang_thai, 1) <> 5 AND hd.ngay_tao >= ? AND hd.ngay_tao < ?"
                + " GROUP BY COALESCE(kh.ho_ten, hd.ten_nguoi_nhan, N'Khách lẻ')"
                + " ORDER BY tong_chi_tieu DESC, ten_khach_hang";

        List<ThongKeCustomer> items = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
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

    public List<RevenuePoint> getRevenueSeries(LocalDateTime from, LocalDateTime to, String mode) throws SQLException {
        boolean daily = "month".equalsIgnoreCase(mode);
        String periodExpression = daily
                ? "CONVERT(varchar(10), CAST(hd.ngay_tao AS date), 23)"
                : "CONVERT(varchar(7), hd.ngay_tao, 120)";
        String sql = "SELECT " + periodExpression + " AS period_key,"
                + " ISNULL(SUM(ISNULL(hd.tong_tien_thanh_toan, 0)), 0) AS revenue"
                + " FROM hoa_don hd WHERE hd.ngay_tao >= ? AND hd.ngay_tao < ?"
                + " AND ISNULL(hd.trang_thai, 1) <> 5"
                + " GROUP BY " + periodExpression + " ORDER BY period_key";

        List<RevenuePoint> items = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new RevenuePoint(rs.getString("period_key"), rs.getBigDecimal("revenue")));
                }
            }
        }
        return items;
    }

    private List<ThongKeProduct> getProducts(String sql, LocalDateTime from, LocalDateTime to) throws SQLException {
        List<ThongKeProduct> items = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
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

    public static final class RevenuePoint {
        private final String periodKey;
        private final BigDecimal revenue;

        public RevenuePoint(String periodKey, BigDecimal revenue) {
            this.periodKey = periodKey;
            this.revenue = revenue == null ? BigDecimal.ZERO : revenue;
        }

        public String getPeriodKey() {
            return periodKey;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }
    }
}
