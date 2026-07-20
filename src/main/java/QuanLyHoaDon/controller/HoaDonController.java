package QuanLyHoaDon.controller;

import QuanLyHoaDon.Model.HoaDonView;
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

@WebServlet("/admin/hoa-don")
public class HoaDonController extends HttpServlet {
    // Controller chỉ nhận request từ FE, sau đó gọi service để xử lý nghiệp vụ.
    private final HoaDonService hoaDonService = new HoaDonService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // GET /admin/hoa-don: lấy danh sách hóa đơn và chuyển sang trang JSP.
            request.setAttribute("hoaDonList", hoaDonService.getAllHoaDon());
            request.getRequestDispatcher("/FE/Admin/QuanLyHoaDon/quan_ly_hoa_don.jsp").forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Không thể lấy danh sách hóa đơn từ database.", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            // action được gửi từ form đổi trạng thái ở trang chi tiết.
            if ("changeStatus".equals(action)) {
                int id = parseInt(request.getParameter("id"), 0);
                int status = parseInt(request.getParameter("trangThai"), 1);
                String note = request.getParameter("ghiChu");

                hoaDonService.updateTrangThai(id, status, note);
                response.sendRedirect(request.getContextPath() + "/admin/hoa-don/chi-tiet?id=" + id);
                return;
            }

            response.sendRedirect(request.getContextPath() + "/admin/hoa-don");
        } catch (SQLException exception) {
            throw new ServletException("Không thể cập nhật hóa đơn trong database.", exception);
        }
    }

    private int parseInt(String value, int defaultValue) {
        // Nếu người dùng nhập sai số thì trả về giá trị mặc định để tránh lỗi 500.
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

}
