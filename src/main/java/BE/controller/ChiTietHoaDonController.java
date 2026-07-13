package BE.controller;

import BE.Model.HoaDonView;
import BE.service.HoaDonService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/hoa-don/chi-tiet")
public class ChiTietHoaDonController extends HttpServlet {
    // Controller cho trang chi tiet hoa don.
    private final HoaDonService hoaDonService = new HoaDonService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // id duoc truyen tren URL, vi du: /admin/hoa-don/chi-tiet?id=1
        int id = parseInt(request.getParameter("id"), 0);

        try {
            HoaDonView hoaDon = hoaDonService.getHoaDonById(id);

            if (hoaDon == null) {
                response.sendRedirect(request.getContextPath() + "/admin/hoa-don");
                return;
            }

            // Dua du lieu vao request de JSP co the hien thi.
            request.setAttribute("hoaDon", hoaDon);
            request.setAttribute("chiTietList", hoaDonService.getChiTietHoaDon(id));
            request.setAttribute("paymentList", hoaDonService.getThanhToanHoaDon(id));
            request.setAttribute("paymentHistoryList", hoaDonService.getLichSuThanhToan(id));
            request.setAttribute("historyList", hoaDonService.getLichSuHoaDon(id));
            request.getRequestDispatcher("/FE/Admin/QuanLyHoaDon/chi_tiet_hoa_don.jsp").forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Khong the lay chi tiet hoa don tu database.", exception);
        }
    }

    private int parseInt(String value, int defaultValue) {
        // Ham phu tro giup doc id an toan tu request parameter.
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }
}
