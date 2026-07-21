package QuanLyHoaDon.service;

import QuanLyHoaDon.Model.ThongKeCustomer;
import QuanLyHoaDon.Model.ThongKeOverview;
import QuanLyHoaDon.Model.ThongKeProduct;
import QuanLyHoaDon.dao.ThongKeDAO;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class ThongKeService {
    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

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
        return thongKeDAO.getBestSellers();
    }

    public List<ThongKeProduct> getSlowStockProducts() throws SQLException {
        return thongKeDAO.getSlowStockProducts();
    }

    public List<ThongKeCustomer> getTopCustomers() throws SQLException {
        return thongKeDAO.getTopCustomers();
    }

    public int calculateCompletionRate(ThongKeOverview overview) {
        if (overview == null || overview.getOrders() == 0) {
            return 0;
        }
        return Math.round((overview.getDone() * 100f) / overview.getOrders());
    }
}
