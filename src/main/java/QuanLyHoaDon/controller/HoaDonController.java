package QuanLyHoaDon.controller;

import QuanLyHoaDon.Model.ChiTietHoaDonInput;
import QuanLyHoaDon.Model.HoaDonView;
import QuanLyHoaDon.service.HoaDonService;
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
    // Controller chỉ nhận request từ FE, sau đó gọi service để xử lý nghiệp vụ.
    private final HoaDonService hoaDonService = new HoaDonService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // GET /admin/hoa-don: lấy danh sách hóa đơn và chuyển sang trang JSP.
            request.setAttribute("hoaDonList", hoaDonService.getAllHoaDon());
            request.setAttribute("nhanVienList", hoaDonService.getAllNhanVien());
            request.setAttribute("sanPhamList", hoaDonService.getAllSanPhamHoaDon());
            request.getRequestDispatcher("/Admin/Admin/QuanLyHoaDon/quan_ly_hoa_don.jsp").forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Không thể lấy danh sách hóa đơn từ database.", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            // action được gửi từ các form bên JSP: save hoặc changeStatus.
            if ("changeStatus".equals(action)) {
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
            throw new ServletException("Không thể lưu hóa đơn vào database.", exception);
        }
    }

    private HoaDonView readForm(HttpServletRequest request) {
        // Chuyển dữ liệu từ form HTML thành object HoaDonView để gửi xuống service/dao.
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
        // Nếu người dùng nhập sai số thì trả về giá trị mặc định để tránh lỗi 500.
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    private BigDecimal parseMoney(String value) {
        // Chuyển chuỗi tiền từ form thành BigDecimal để lưu vào cột DECIMAL trong SQL Server.
        try {
            return new BigDecimal(value);
        } catch (Exception exception) {
            return BigDecimal.ZERO;
        }
    }
}