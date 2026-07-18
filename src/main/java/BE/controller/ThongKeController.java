package BE.controller;

import BE.Model.HoaDonView;
import BE.Model.SanPhamHoaDonView;
import BE.Model.ThongKeOverview;
import BE.service.HoaDonService;
import BE.service.ThongKeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

public class ThongKeController extends HttpServlet {
    private final ThongKeService thongKeService = new ThongKeService();
    private final HoaDonService hoaDonService = new HoaDonService();

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

        // Trang tổng hợp dùng chung dữ liệu hóa đơn và sản phẩm, không cần sửa entity.
        try {
            request.setAttribute("hoaDonList", hoaDonService.getAllHoaDon());
            request.setAttribute("sanPhamList", hoaDonService.getAllSanPhamHoaDon());
        } catch (SQLException e) {
            request.setAttribute("combinedDataError", "Không lấy được danh sách hóa đơn hoặc sản phẩm: " + e.getMessage());
            request.setAttribute("hoaDonList", Collections.<HoaDonView>emptyList());
            request.setAttribute("sanPhamList", Collections.<SanPhamHoaDonView>emptyList());
        }

        request.getRequestDispatcher("/FE/Admin/Thongke.jsp").forward(request, response);
    }
}
