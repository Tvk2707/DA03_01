package BE.controller;

import BE.Model.ChiTietHoaDonInput;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/hoa-don")
public class HoaDonController extends HttpServlet {
    // Controller chi nhan request tu FE, sau do goi service de xu ly nghiep vu.
    private final HoaDonService hoaDonService = new HoaDonService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // GET /admin/hoa-don: lay danh sach hoa don va chuyen sang trang JSP.
            request.setAttribute("hoaDonList", hoaDonService.getAllHoaDon());
            request.setAttribute("nhanVienList", hoaDonService.getAllNhanVien());
            request.setAttribute("sanPhamList", hoaDonService.getAllSanPhamHoaDon());
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
            // action duoc gui tu cac form ben JSP: delete, save hoac changeStatus.
            if ("delete".equals(action)) {
                int id = parseInt(request.getParameter("id"), 0);
                if (id > 0) {
                    hoaDonService.huyHoaDon(id);
                }
            } else if ("changeStatus".equals(action)) {
                int id = parseInt(request.getParameter("id"), 0);
                int status = parseInt(request.getParameter("trangThai"), 1);
                String note = request.getParameter("ghiChu");

                hoaDonService.updateTrangThai(id, status, note);
                response.sendRedirect(request.getContextPath() + "/admin/hoa-don/chi-tiet?id=" + id);
                return;
            } else if ("save".equals(action)) {
                hoaDonService.saveHoaDon(readForm(request), readProductLines(request));
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
        hoaDon.setIdNhanVien(parseInt(request.getParameter("idNhanVien"), 0));
        hoaDon.setTongTienThanhToan(parseMoney(request.getParameter("tongTienThanhToan")));
        hoaDon.setTrangThai(parseInt(request.getParameter("trangThai"), 1));
        hoaDon.setGhiChu(request.getParameter("ghiChu"));

        return hoaDon;
    }

    private List<ChiTietHoaDonInput> readProductLines(HttpServletRequest request) {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("productQuantity");
        List<ChiTietHoaDonInput> lines = new ArrayList<>();

        if (productIds == null || quantities == null) {
            return lines;
        }

        for (int i = 0; i < productIds.length && i < quantities.length; i++) {
            int productId = parseInt(productIds[i], 0);
            int quantity = parseInt(quantities[i], 0);
            if (productId > 0 && quantity > 0) {
                lines.add(new ChiTietHoaDonInput(productId, quantity));
            }
        }

        return lines;
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
