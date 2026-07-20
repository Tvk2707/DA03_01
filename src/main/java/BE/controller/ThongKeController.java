package BE.controller;

import BE.Model.ThongKeCustomer;
import BE.Model.ThongKeOverview;
import BE.Model.ThongKeProduct;
import BE.dao.ThongKeDAO.RevenuePoint;
import BE.service.ThongKeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class ThongKeController extends HttpServlet {
    private final ThongKeService thongKeService = new ThongKeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String api = request.getParameter("api");
        if ("report".equalsIgnoreCase(api)) {
            handleReport(request, response);
            return;
        }
        if ("chart".equalsIgnoreCase(api)) {
            handleChart(request, response);
            return;
        }
        if ("export".equalsIgnoreCase(api)) {
            handleExport(request, response);
            return;
        }
        showPage(request, response);
    }

    private void showPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDateTime reportFrom = monthStart.atStartOfDay();
        LocalDateTime reportTo = today.plusDays(1).atStartOfDay();

        try {
            ThongKeOverview yearOverview = thongKeService.getYearOverview();
            request.setAttribute("todayOverview", thongKeService.getTodayOverview());
            request.setAttribute("weekOverview", thongKeService.getWeekOverview());
            request.setAttribute("monthOverview", thongKeService.getMonthOverview());
            request.setAttribute("yearOverview", yearOverview);
            request.setAttribute("completionRate", thongKeService.calculateCompletionRate(yearOverview));
            request.setAttribute("bestSellers", thongKeService.getBestSellers(reportFrom, reportTo));
            request.setAttribute("topCustomers", thongKeService.getTopCustomers(reportFrom, reportTo));
            request.setAttribute("slowStockProducts", thongKeService.getSlowStockProducts(reportFrom, reportTo));
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Không lấy được dữ liệu thống kê từ database: " + e.getMessage());
            request.setAttribute("todayOverview", new ThongKeOverview());
            request.setAttribute("weekOverview", new ThongKeOverview());
            request.setAttribute("monthOverview", new ThongKeOverview());
            request.setAttribute("yearOverview", new ThongKeOverview());
            request.setAttribute("completionRate", 0);
            request.setAttribute("bestSellers", Collections.emptyList());
            request.setAttribute("topCustomers", Collections.emptyList());
            request.setAttribute("slowStockProducts", Collections.emptyList());
        }

        request.setAttribute("reportFrom", monthStart.toString());
        request.setAttribute("reportTo", today.toString());
        request.setAttribute("statisticsApi", request.getContextPath() + "/admin/thong-ke");
        request.getRequestDispatcher("/FE/Admin/Thongke.jsp").forward(request, response);
    }

    private void handleReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            DateRange range = readRange(request);
            String mode = readMode(request);
            ThongKeOverview overview = thongKeService.getOverview(range.from, range.toExclusive);
            List<ThongKeProduct> bestSellers = thongKeService.getBestSellers(range.from, range.toExclusive);
            List<ThongKeCustomer> topCustomers = thongKeService.getTopCustomers(range.from, range.toExclusive);
            List<ThongKeProduct> slowStock = thongKeService.getSlowStockProducts(range.from, range.toExclusive);
            List<RevenuePoint> series = thongKeService.getRevenueSeries(range.from, range.toExclusive, mode);
            writeJson(response, reportJson(overview, bestSellers, topCustomers, slowStock, series));
        } catch (DateTimeParseException | SQLException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Không thể lấy dữ liệu thống kê: " + e.getMessage());
        }
    }

    private void handleChart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            DateRange range = readRange(request);
            List<RevenuePoint> series = thongKeService.getRevenueSeries(range.from, range.toExclusive, readMode(request));
            writeJson(response, "{\"series\":" + seriesJson(series) + "}");
        } catch (DateTimeParseException | SQLException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Không thể lấy dữ liệu biểu đồ: " + e.getMessage());
        }
    }

    private void handleExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            DateRange range = readRange(request);
            ThongKeOverview overview = thongKeService.getOverview(range.from, range.toExclusive);
            List<ThongKeProduct> bestSellers = thongKeService.getBestSellers(range.from, range.toExclusive);
            List<ThongKeCustomer> topCustomers = thongKeService.getTopCustomers(range.from, range.toExclusive);
            List<ThongKeProduct> slowStock = thongKeService.getSlowStockProducts(range.from, range.toExclusive);

            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=bao-cao-thong-ke.csv");
            response.getWriter().write("\uFEFF");
            response.getWriter().println(csv("Khoảng thời gian", request.getParameter("from") + " đến " + request.getParameter("to")));
            response.getWriter().println();
            response.getWriter().println(csv("Tổng quan", "Giá trị"));
            response.getWriter().println(csv("Đơn hàng", overview.getOrders()));
            response.getWriter().println(csv("Doanh thu", overview.getRevenue()));
            response.getWriter().println(csv("Sản phẩm đã bán", overview.getProducts()));
            response.getWriter().println(csv("Hoàn thành", overview.getDone()));
            response.getWriter().println(csv("Đang xử lý", overview.getProcessing()));
            response.getWriter().println(csv("Đã hủy", overview.getCancelled()));
            response.getWriter().println();
            response.getWriter().println(csv("Top sản phẩm", "Đã bán", "Tồn kho"));
            for (ThongKeProduct item : bestSellers) {
                response.getWriter().println(csv(item.getTenSanPham(), item.getDaBan(), item.getTonKho()));
            }
            response.getWriter().println();
            response.getWriter().println(csv("Khách hàng", "Số đơn", "Tổng chi tiêu"));
            for (ThongKeCustomer item : topCustomers) {
                response.getWriter().println(csv(item.getTenKhachHang(), item.getSoDon(), item.getTongChiTieu()));
            }
            response.getWriter().println();
            response.getWriter().println(csv("Bán chậm & tồn kho", "Đã bán", "Tồn kho"));
            for (ThongKeProduct item : slowStock) {
                response.getWriter().println(csv(item.getTenSanPham(), item.getDaBan(), item.getTonKho()));
            }
        } catch (DateTimeParseException | SQLException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Không thể xuất báo cáo: " + e.getMessage());
        }
    }

    private DateRange readRange(HttpServletRequest request) {
        LocalDate today = LocalDate.now();
        LocalDate fromDate = parseDate(request.getParameter("from"), today.withDayOfMonth(1));
        LocalDate toDate = parseDate(request.getParameter("to"), today);
        if (toDate.isBefore(fromDate)) {
            throw new DateTimeParseException("Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu", toDate.toString(), 0);
        }
        return new DateRange(fromDate.atStartOfDay(), toDate.plusDays(1).atStartOfDay());
    }

    private LocalDate parseDate(String value, LocalDate fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return LocalDate.parse(value);
    }

    private String readMode(HttpServletRequest request) {
        String mode = request.getParameter("mode");
        return "year".equalsIgnoreCase(mode) || "quarter".equalsIgnoreCase(mode) ? mode.toLowerCase() : "month";
    }

    private String reportJson(ThongKeOverview overview, List<ThongKeProduct> bestSellers,
                              List<ThongKeCustomer> topCustomers, List<ThongKeProduct> slowStock,
                              List<RevenuePoint> series) {
        return "{\"overview\":" + overviewJson(overview)
                + ",\"completionRate\":" + completionRate(overview)
                + ",\"bestSellers\":" + productsJson(bestSellers)
                + ",\"topCustomers\":" + customersJson(topCustomers)
                + ",\"slowStockProducts\":" + productsJson(slowStock)
                + ",\"series\":" + seriesJson(series) + "}";
    }

    private String overviewJson(ThongKeOverview value) {
        return "{\"revenue\":" + decimal(value.getRevenue())
                + ",\"products\":" + value.getProducts()
                + ",\"orders\":" + value.getOrders()
                + ",\"done\":" + value.getDone()
                + ",\"cancelled\":" + value.getCancelled()
                + ",\"processing\":" + value.getProcessing() + "}";
    }

    private int completionRate(ThongKeOverview overview) {
        return thongKeService.calculateCompletionRate(overview);
    }

    private String productsJson(List<ThongKeProduct> items) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            ThongKeProduct item = items.get(i);
            if (i > 0) json.append(',');
            json.append("{\"name\":").append(jsonString(item.getTenSanPham()))
                    .append(",\"sold\":").append(item.getDaBan())
                    .append(",\"stock\":").append(item.getTonKho()).append('}');
        }
        return json.append(']').toString();
    }

    private String customersJson(List<ThongKeCustomer> items) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            ThongKeCustomer item = items.get(i);
            if (i > 0) json.append(',');
            json.append("{\"name\":").append(jsonString(item.getTenKhachHang()))
                    .append(",\"orders\":").append(item.getSoDon())
                    .append(",\"spending\":").append(decimal(item.getTongChiTieu())).append('}');
        }
        return json.append(']').toString();
    }

    private String seriesJson(List<RevenuePoint> items) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            RevenuePoint item = items.get(i);
            if (i > 0) json.append(',');
            json.append("{\"key\":").append(jsonString(item.getPeriodKey()))
                    .append(",\"value\":").append(decimal(item.getRevenue())).append('}');
        }
        return json.append(']').toString();
    }

    private String decimal(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).toPlainString();
    }

    private String jsonString(String value) {
        if (value == null) return "null";
        return "\"" + value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n") + "\"";
    }

    private String csv(Object... values) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) row.append(',');
            String value = values[i] == null ? "" : String.valueOf(values[i]);
            row.append('"').append(value.replace("\"", "\"\"" )).append('"');
        }
        return row.toString();
    }

    private void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        writeJson(response, "{\"error\":" + jsonString(message) + "}");
    }

    private static final class DateRange {
        private final LocalDateTime from;
        private final LocalDateTime toExclusive;

        private DateRange(LocalDateTime from, LocalDateTime toExclusive) {
            this.from = from;
            this.toExclusive = toExclusive;
        }
    }
}
