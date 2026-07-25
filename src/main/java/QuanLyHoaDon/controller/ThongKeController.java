package QuanLyHoaDon.controller;

import QuanLyHoaDon.Model.ThongKeSeriesPoint;
import QuanLyHoaDon.Model.ThongKeOverview;
import QuanLyHoaDon.service.ThongKeService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/thong-ke")
public class ThongKeController extends HttpServlet {
    private final ThongKeService thongKeService = new ThongKeService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("revenue-series".equals(request.getParameter("action"))) {
            writeRevenueSeries(request, response);
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate filterFrom = parseDate(request.getParameter("from"), today.withDayOfMonth(1));
        LocalDate filterTo = parseDate(request.getParameter("to"), today);

        try {
            ThongKeOverview yearOverview = thongKeService.getYearOverview();
            ThongKeOverview reportOverview = thongKeService.getOverview(filterFrom, filterTo);
            request.setAttribute("todayOverview", thongKeService.getTodayOverview());
            request.setAttribute("weekOverview", thongKeService.getWeekOverview());
            request.setAttribute("monthOverview", thongKeService.getMonthOverview());
            request.setAttribute("yearOverview", yearOverview);
            request.setAttribute("reportOverview", reportOverview);
            request.setAttribute("completionRate", thongKeService.calculateCompletionRate(reportOverview));
            request.setAttribute("bestSellers", thongKeService.getBestSellers(filterFrom, filterTo));
            request.setAttribute("topCustomers", thongKeService.getTopCustomers(filterFrom, filterTo));
            request.setAttribute("slowStockProducts", thongKeService.getSlowStockProducts(filterFrom, filterTo));
        } catch (SQLException | IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Không lấy được dữ liệu thống kê từ database: " + e.getMessage());
            request.setAttribute("reportOverview", new ThongKeOverview());
            request.setAttribute("bestSellers", Collections.emptyList());
            request.setAttribute("topCustomers", Collections.emptyList());
            request.setAttribute("slowStockProducts", Collections.emptyList());
        }

        request.setAttribute("filterFrom", filterFrom);
        request.setAttribute("filterTo", filterTo);
        request.setAttribute("currentDate", today);
        request.getRequestDispatcher("/FE/Admin/Thongke.jsp").forward(request, response);
    }

    private void writeRevenueSeries(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            String mode = request.getParameter("mode");
            int year = parseNumber(request.getParameter("year"), LocalDate.now().getYear());
            if (year < 2000 || year > 2100) {
                throw new IllegalArgumentException("Năm thống kê không hợp lệ.");
            }

            List<ThongKeSeriesPoint> series;
            String description;
            if ("quarter".equals(mode)) {
                int quarter = parseNumber(request.getParameter("quarter"), 1);
                series = thongKeService.getQuarterRevenueSeries(year, quarter);
                description = "Doanh thu theo tháng trong quý " + quarter + "/" + year;
            } else if ("year".equals(mode)) {
                series = thongKeService.getYearRevenueSeries(year);
                description = "Doanh thu theo tháng trong năm " + year;
            } else {
                int month = parseNumber(request.getParameter("month"), LocalDate.now().getMonthValue());
                if (month < 1 || month > 12) {
                    throw new IllegalArgumentException("Tháng thống kê không hợp lệ.");
                }
                series = thongKeService.getMonthRevenueSeries(year, month);
                description = "Doanh thu theo ngày trong tháng "
                        + String.format("%02d", month) + "/" + year;
                mode = "month";
            }

            result.put("success", true);
            result.put("mode", mode);
            result.put("description", description);
            result.put("series", series);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "Không truy vấn được doanh thu: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        response.getWriter().write(gson.toJson(result));
    }

    private LocalDate parseDate(String value, LocalDate defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return defaultValue;
        }
    }

    private int parseNumber(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Tham số thời gian không hợp lệ.");
        }
    }
}
