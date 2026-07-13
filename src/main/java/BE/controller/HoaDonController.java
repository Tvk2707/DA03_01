package BE.controller;

import BE.Model.HoaDonView;
import BE.service.HoaDonService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/admin/hoa-don")
public class HoaDonController extends HttpServlet {
    private final HoaDonService hoaDonService = new HoaDonService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("hoaDonList", hoaDonService.getAllHoaDon());
            request.getRequestDispatcher("/FE/Admin/QuanLyHoaDon/quan_ly_hoa_don.jsp").forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Khong the lay danh sach hoa don tu database.", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("delete".equals(action)) {
                hoaDonService.huyHoaDon(parseInt(request.getParameter("id"), 0));
            } else if ("changeStatus".equals(action)) {
                int id = parseInt(request.getParameter("id"), 0);
                int status = parseInt(request.getParameter("trangThai"), 1);
                String note = request.getParameter("ghiChu");

                hoaDonService.updateTrangThai(id, status, note);
                response.sendRedirect(request.getContextPath() + "/admin/hoa-don/chi-tiet?id=" + id);
                return;
            } else if ("save".equals(action)) {
                hoaDonService.saveHoaDon(readForm(request));
            }

            response.sendRedirect(request.getContextPath() + "/admin/hoa-don");
        } catch (SQLException exception) {
            throw new ServletException("Khong the luu hoa don vao database.", exception);
        }
    }

    private HoaDonView readForm(HttpServletRequest request) {
        HoaDonView hoaDon = new HoaDonView();
        int id = parseInt(request.getParameter("id"), 0);

        if (id > 0) {
            hoaDon.setId(id);
        }

        hoaDon.setMaHoaDon(request.getParameter("maHoaDon"));
        hoaDon.setTenNguoiNhan(request.getParameter("tenNguoiNhan"));
        hoaDon.setSoDienThoai(request.getParameter("soDienThoai"));
        hoaDon.setTongTienThanhToan(parseMoney(request.getParameter("tongTienThanhToan")));
        hoaDon.setTrangThai(parseInt(request.getParameter("trangThai"), 1));
        hoaDon.setGhiChu(request.getParameter("ghiChu"));

        return hoaDon;
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    private BigDecimal parseMoney(String value) {
        try {
            return new BigDecimal(value);
        } catch (Exception exception) {
            return BigDecimal.ZERO;
        }
    }
}
