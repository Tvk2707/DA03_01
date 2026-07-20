package BE.service;

import BE.Model.ThongKeCustomer;
import BE.Model.ThongKeOverview;
import BE.Model.ThongKeProduct;
import BE.dao.ThongKeDAO.RevenuePoint;
import BE.dao.ThongKeDAO;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class ThongKeService {
    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    public ThongKeOverview getOverview(LocalDateTime from, LocalDateTime to) throws SQLException {
        return thongKeDAO.getOverview(from, to);
    }

    public ThongKeOverview getTodayOverview() throws SQLException {
        LocalDate today = LocalDate.now();
        return thongKeDAO.getOverview(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
    }

    public ThongKeOverview getWeekOverview() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return thongKeDAO.getOverview(start.atStartOfDay(), start.plusDays(7).atStartOfDay());
    }

    public ThongKeOverview getMonthOverview() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(1);
        return thongKeDAO.getOverview(start.atStartOfDay(), start.plusMonths(1).atStartOfDay());
    }

    public ThongKeOverview getYearOverview() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfYear(1);
        return thongKeDAO.getOverview(start.atStartOfDay(), start.plusYears(1).atStartOfDay());
    }

    public List<ThongKeProduct> getBestSellers() throws SQLException {
        LocalDate today = LocalDate.now();
        return thongKeDAO.getBestSellers(today.withDayOfMonth(1).atStartOfDay(), today.plusDays(1).atStartOfDay());
    }

    public List<ThongKeProduct> getSlowStockProducts() throws SQLException {
        LocalDate today = LocalDate.now();
        return thongKeDAO.getSlowStockProducts(today.withDayOfMonth(1).atStartOfDay(), today.plusDays(1).atStartOfDay());
    }

    public List<ThongKeCustomer> getTopCustomers() throws SQLException {
        LocalDate today = LocalDate.now();
        return thongKeDAO.getTopCustomers(today.withDayOfMonth(1).atStartOfDay(), today.plusDays(1).atStartOfDay());
    }

    public List<ThongKeProduct> getBestSellers(LocalDateTime from, LocalDateTime to) throws SQLException {
        return thongKeDAO.getBestSellers(from, to);
    }

    public List<ThongKeProduct> getSlowStockProducts(LocalDateTime from, LocalDateTime to) throws SQLException {
        return thongKeDAO.getSlowStockProducts(from, to);
    }

    public List<ThongKeCustomer> getTopCustomers(LocalDateTime from, LocalDateTime to) throws SQLException {
        return thongKeDAO.getTopCustomers(from, to);
    }

    public List<RevenuePoint> getRevenueSeries(LocalDateTime from, LocalDateTime to, String mode) throws SQLException {
        return thongKeDAO.getRevenueSeries(from, to, mode);
    }

    public int calculateCompletionRate(ThongKeOverview overview) {
        if (overview == null || overview.getOrders() == 0) {
            return 0;
        }
        return Math.round((overview.getDone() * 100f) / overview.getOrders());
    }
}
