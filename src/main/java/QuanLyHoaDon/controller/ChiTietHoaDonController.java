package QuanLyHoaDon.controller;

import QuanLyHoaDon.service.HoaDonService;

import QuanLyHoaDon.Model.HoaDonView;
import QuanLyHoaDon.service.HoaDonService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@WebServlet("/admin/hoa-don/chi-tiet")
public class ChiTietHoaDonController extends HttpServlet {
    // Controller cho trang chi tiết hóa đơn.
    private final HoaDonService hoaDonService = new HoaDonService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // id được truyền trên URL, ví dụ: /admin/hoa-don/chi-tiet?id=1
        int id = parseInt(request.getParameter("id"), 0);

        try {
            HoaDonView hoaDon = hoaDonService.getHoaDonById(id);

            if (hoaDon == null) {
                response.sendRedirect(request.getContextPath() + "/admin/hoa-don");
                return;
            }

            // Đưa dữ liệu vào request để JSP có thể hiển thị.
            request.setAttribute("hoaDon", hoaDon);
            request.setAttribute("chiTietList", loadList(() -> hoaDonService.getChiTietHoaDon(id), request));
            request.setAttribute("paymentList", loadList(() -> hoaDonService.getThanhToanHoaDon(id), request));
            request.setAttribute("paymentHistoryList", loadList(() -> hoaDonService.getLichSuThanhToan(id), request));
            request.setAttribute("historyList", loadList(() -> hoaDonService.getLichSuHoaDon(id), request));
            request.getRequestDispatcher("/FE/Admin/QuanLyHoaDon/chi_tiet_hoa_don.jsp").forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Không thể lấy chi tiết hóa đơn từ database.", exception);
        }
    }

    private <T> List<T> loadList(SqlListSupplier<T> supplier, HttpServletRequest request) {
        try {
            return supplier.get();
        } catch (SQLException exception) {
            request.setAttribute("detailLoadError", "Một phần dữ liệu chi tiết chưa tải được.");
            getServletContext().log("Khong the tai du lieu phu cua chi tiet hoa don.", exception);
            return Collections.emptyList();
        }
    }

    @FunctionalInterface
    private interface SqlListSupplier<T> {
        List<T> get() throws SQLException;
    }

    private int parseInt(String value, int defaultValue) {
        // Hàm phụ trợ giúp đọc id an toàn từ request parameter.
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }
}
