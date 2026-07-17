package BE.controller;

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
    private final HoaDonService hoaDonService = new HoaDonService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        try {
            request.setAttribute("hoaDon", hoaDonService.getHoaDonById(id));
            request.setAttribute("chiTietHoaDon", hoaDonService.getChiTietHoaDonById(id));
            request.getRequestDispatcher("/Admin/QuanLyHoaDon/chi_tiet_hoa_don.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Could not get invoice details", e);
        }
    }
}