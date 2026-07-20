package QuanLyHoaDon.controller;

import QuanLyHoaDon.Model.ThongKeOverview;
import QuanLyHoaDon.service.ThongKeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
@WebServlet("/admin/thong-ke")
public class ThongKeController extends HttpServlet {
    private final ThongKeService thongKeService = new ThongKeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ThongKeOverview yearOverview = thongKeService.getYearOverview();
            request.setAttribute("todayOverview", thongKeService.getTodayOverview());
            request.setAttribute("weekOverview", thongKeService.getWeekOverview());
            request.setAttribute("monthOverview", thongKeService.getMonthOverview());
            request.setAttribute("yearOverview", yearOverview);
            request.setAttribute("completionRate", thongKeService.calculateCompletionRate(yearOverview));
            request.setAttribute("bestSellers", thongKeService.getBestSellers());
            request.setAttribute("topCustomers", thongKeService.getTopCustomers());
            request.setAttribute("slowStockProducts", thongKeService.getSlowStockProducts());
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Không lấy được dữ liệu thống kê từ database: " + e.getMessage());
            request.setAttribute("bestSellers", Collections.emptyList());
            request.setAttribute("topCustomers", Collections.emptyList());
            request.setAttribute("slowStockProducts", Collections.emptyList());
        }

        request.getRequestDispatcher("/FE/Admin/Thongke.jsp").forward(request, response);
    }
}
