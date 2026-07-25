package QuanLyHoaDon.service;

import QuanLyHoaDon.Model.ThongKeCustomer;
import QuanLyHoaDon.Model.ThongKeOverview;
import QuanLyHoaDon.Model.ThongKeProduct;
import QuanLyHoaDon.Model.ThongKeSeriesPoint;
import QuanLyHoaDon.dao.ThongKeDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        LocalDate today = LocalDate.now();
        return getBestSellers(today.withDayOfYear(1), today);
    }

    public List<ThongKeProduct> getSlowStockProducts() throws SQLException {
        LocalDate today = LocalDate.now();
        return getSlowStockProducts(today.withDayOfYear(1), today);
    }

    public List<ThongKeCustomer> getTopCustomers() throws SQLException {
        LocalDate today = LocalDate.now();
        return getTopCustomers(today.withDayOfYear(1), today);
    }

    public ThongKeOverview getOverview(LocalDate from, LocalDate to) throws SQLException {
        validateRange(from, to);
        return thongKeDAO.getOverview(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }

    public List<ThongKeProduct> getBestSellers(LocalDate from, LocalDate to) throws SQLException {
        validateRange(from, to);
        return thongKeDAO.getBestSellers(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }

    public List<ThongKeProduct> getSlowStockProducts(LocalDate from, LocalDate to) throws SQLException {
        validateRange(from, to);
        return thongKeDAO.getSlowStockProducts(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }

    public List<ThongKeCustomer> getTopCustomers(LocalDate from, LocalDate to) throws SQLException {
        validateRange(from, to);
        return thongKeDAO.getTopCustomers(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }

    public List<ThongKeSeriesPoint> getMonthRevenueSeries(int year, int month) throws SQLException {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate endExclusive = start.plusMonths(1);
        Map<Integer, BigDecimal> values = toValueMap(
                thongKeDAO.getRevenueSeries(start.atStartOfDay(), endExclusive.atStartOfDay(), false));
        List<ThongKeSeriesPoint> result = new ArrayList<>();
        for (int day = 1; day <= start.lengthOfMonth(); day++) {
            result.add(new ThongKeSeriesPoint(String.valueOf(day),
                    values.getOrDefault(day, BigDecimal.ZERO)));
        }
        return result;
    }

    public List<ThongKeSeriesPoint> getQuarterRevenueSeries(int year, int quarter) throws SQLException {
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quý phải nằm trong khoảng từ 1 đến 4.");
        }
        int startMonth = (quarter - 1) * 3 + 1;
        LocalDate start = LocalDate.of(year, startMonth, 1);
        LocalDate endExclusive = start.plusMonths(3);
        Map<Integer, BigDecimal> values = toValueMap(
                thongKeDAO.getRevenueSeries(start.atStartOfDay(), endExclusive.atStartOfDay(), true));
        List<ThongKeSeriesPoint> result = new ArrayList<>();
        for (int month = startMonth; month < startMonth + 3; month++) {
            result.add(new ThongKeSeriesPoint("T" + month,
                    values.getOrDefault(month, BigDecimal.ZERO)));
        }
        return result;
    }

    public List<ThongKeSeriesPoint> getYearRevenueSeries(int year) throws SQLException {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate endExclusive = start.plusYears(1);
        Map<Integer, BigDecimal> values = toValueMap(
                thongKeDAO.getRevenueSeries(start.atStartOfDay(), endExclusive.atStartOfDay(), true));
        List<ThongKeSeriesPoint> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            result.add(new ThongKeSeriesPoint("T" + month,
                    values.getOrDefault(month, BigDecimal.ZERO)));
        }
        return result;
    }

    public List<ThongKeSeriesPoint> getRevenueSeries(LocalDate from, LocalDate to) throws SQLException {
        validateRange(from, to);
        long days = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1;
        boolean groupByMonth = days > 62;
        List<ThongKeSeriesPoint> raw = thongKeDAO.getRevenueSeriesByRange(
                from.atStartOfDay(), to.plusDays(1).atStartOfDay(), groupByMonth);
        Map<String, BigDecimal> values = raw.stream().collect(Collectors.toMap(
                ThongKeSeriesPoint::getLabel,
                ThongKeSeriesPoint::getValue,
                BigDecimal::add,
                LinkedHashMap::new));

        List<ThongKeSeriesPoint> result = new ArrayList<>();
        if (groupByMonth) {
            YearMonth current = YearMonth.from(from);
            YearMonth last = YearMonth.from(to);
            DateTimeFormatter crossYearLabel = DateTimeFormatter.ofPattern("MM/yy");
            while (!current.isAfter(last)) {
                String key = current.toString();
                String label = from.getYear() == to.getYear()
                        ? "T" + current.getMonthValue() : current.format(crossYearLabel);
                result.add(new ThongKeSeriesPoint(label, values.getOrDefault(key, BigDecimal.ZERO)));
                current = current.plusMonths(1);
            }
        } else {
            DateTimeFormatter dayLabel = DateTimeFormatter.ofPattern("dd/MM");
            LocalDate current = from;
            while (!current.isAfter(to)) {
                result.add(new ThongKeSeriesPoint(
                        current.format(dayLabel), values.getOrDefault(current.toString(), BigDecimal.ZERO)));
                current = current.plusDays(1);
            }
        }
        return result;
    }

    public int calculateCompletionRate(ThongKeOverview overview) {
        if (overview == null || overview.getOrders() == 0) {
            return 0;
        }
        return Math.round((overview.getDone() * 100f) / overview.getOrders());
    }

    private Map<Integer, BigDecimal> toValueMap(List<ThongKeSeriesPoint> points) {
        Map<Integer, BigDecimal> values = new HashMap<>();
        for (ThongKeSeriesPoint point : points) {
            values.put(Integer.parseInt(point.getLabel()), point.getValue());
        }
        return values;
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Khoảng ngày thống kê không được để trống.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Từ ngày không được lớn hơn đến ngày.");
        }
        if (from.plusYears(10).isBefore(to)) {
            throw new IllegalArgumentException("Khoảng ngày thống kê không được vượt quá 10 năm.");
        }
    }
}
