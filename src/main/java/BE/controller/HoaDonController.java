package BE.controller;

import BE.Model.HoaDonView;
import BE.service.HoaDonService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class HoaDonController extends HttpServlet {
    private final HoaDonService hoaDonService = new HoaDonService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<HoaDonView> hoaDonList = hoaDonService.getAllHoaDon();
            request.setAttribute("hoaDonList", hoaDonList);
            request.setAttribute("tongHoaDon", hoaDonList.size());
            request.setAttribute("doanhThu", hoaDonService.tinhTongDoanhThu(hoaDonList));
            request.setAttribute("dangXuLy", hoaDonService.demDangXuLy(hoaDonList));
            request.setAttribute("tongSanPham", hoaDonService.demSanPham(hoaDonList));
        } catch (SQLException e) {
            request.setAttribute("hoaDonList", Collections.emptyList());
            request.setAttribute("tongHoaDon", 0);
            request.setAttribute("doanhThu", java.math.BigDecimal.ZERO);
            request.setAttribute("dangXuLy", 0);
            request.setAttribute("tongSanPham", 0);
            request.setAttribute("errorMessage", "Không lấy được dữ liệu hóa đơn từ database: " + e.getMessage());
        }

        request.getRequestDispatcher("/FE/Admin/QuanLyHoaDon/quan_ly_hoa_don.jsp").forward(request, response);
    }
}
