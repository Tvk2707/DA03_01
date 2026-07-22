package QuanLySanPham.controller;

import QuanLySanPham.Entity.NhanVien;
import QuanLyNhanVien.service.NhanVienService;
import QuanLyNhanVien.service.impl.NhanVienServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "LoginServlet", value = {"/Login"})
public class LoginServlet extends HttpServlet {

    // Khóa lưu nhân viên đang đăng nhập trong session - dùng chung với PhanQuyenFilter, header.jsp...
    public static final String SESSION_KEY = "nhanVienDangNhap";

    private final NhanVienService nhanVienService = new NhanVienServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Nếu người dùng đã đăng nhập trước đó rồi mà cố tình truy cập lại URL /Login -> Tự động chuyển thẳng vào trong
        if (session != null && session.getAttribute(SESSION_KEY) != null) {
            redirectSauDangNhap(request, response, (NhanVien) session.getAttribute(SESSION_KEY));
            return;
        }
        request.getRequestDispatcher("/Login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String taiKhoan = request.getParameter("taiKhoan"); // Nhận vào email hoặc mã nhân viên
        String matKhau = request.getParameter("matKhau");

        try {
            // Thực hiện gọi service kiểm tra tài khoản và mật khẩu băm
            NhanVien nv = nhanVienService.dangNhap(taiKhoan, matKhau);

            // Đăng nhập thành công -> Tạo Session mới
            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_KEY, nv);
            session.setAttribute("idNhanVien", nv.getId());

// Lưu ID ca làm việc (Tạm thời set cứng là 1 để code chạy được, sau này nếu có bảng Ca Làm Việc thì bạn lấy động sau)
            session.setAttribute("idCa", 1);

            // Điều hướng người dùng tới trang đích phù hợp với vai trò
            redirectSauDangNhap(request, response, nv);
        } catch (Exception e) {
            // Nếu có lỗi (Sai tài khoản, sai mật khẩu, tài khoản bị khóa...) -> Bắn lỗi về giao diện login
            request.setAttribute("error", e.getMessage());
            request.setAttribute("taiKhoan", taiKhoan);
            request.getRequestDispatcher("/Login.jsp").forward(request, response);
        }
    }

    /**
     * Hàm xử lý logic điều hướng đường dẫn (URL) sau khi đăng nhập thành công
     */
    private void redirectSauDangNhap(HttpServletRequest request, HttpServletResponse response, NhanVien nv) throws IOException {
        if (nv.isQuanLy()) {
            // Quyền Quản lý: Đưa thẳng vào trang Thống kê tổng quan (Dashboard) của Admin
            response.sendRedirect(request.getContextPath() + "/admin/thong-ke");
        } else {
            // Quyền Nhân viên: Đưa vào phân khu Quản lý sản phẩm được phép truy cập
            response.sendRedirect(request.getContextPath() + "/SanPham");
        }
    }
}
