package QuanLySanPham.controller;

import QuanLySanPham.Entity.NhanVien;
import QuanLyNhanVien.service.NhanVienService;
import QuanLyNhanVien.service.impl.NhanVienServiceImpl;
import QuanLySanPham.Utils.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "RegisterServlet", value = {"/Register"})
public class RegisterServlet extends HttpServlet {

    private final NhanVienService nhanVienService = new NhanVienServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/Register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String maNhanVien = request.getParameter("maNhanVien");
        String hoTen = request.getParameter("hoTen");
        String email = request.getParameter("email");
        String soDienThoai = request.getParameter("soDienThoai");
        String matKhau = request.getParameter("matKhau");
        String xacNhanMatKhau = request.getParameter("xacNhanMatKhau");

        try {
            if (hoTen == null || hoTen.trim().isEmpty()) {
                throw new Exception("Vui lòng nhập họ tên");
            }
            if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
                throw new Exception("Vui lòng nhập mã nhân viên");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new Exception("Vui lòng nhập email");
            }
            if (matKhau == null || matKhau.length() < 6) {
                throw new Exception("Mật khẩu phải có ít nhất 6 ký tự");
            }
            if (!matKhau.equals(xacNhanMatKhau)) {
                throw new Exception("Mật khẩu xác nhận không khớp");
            }

            NhanVien nv = new NhanVien();
            nv.setMaNhanVien(maNhanVien.trim());
            nv.setHoTen(hoTen.trim());
            nv.setEmail(email.trim());
            nv.setSoDienThoai(soDienThoai);
            nv.setMatKhau(PasswordUtil.hash(matKhau));
            nv.setTrangThai(1);
            // Tự đăng ký luôn là Nhân viên thường. Muốn thành Quản lý phải nhờ Quản lý khác nâng quyền
            // trong màn "Quản lý nhân viên" -> tránh việc ai cũng tự cấp quyền quản lý cho mình.
            nv.setVaiTro(NhanVien.VAI_TRO_NHAN_VIEN);

            nhanVienService.themNhanVien(nv);

            response.sendRedirect(request.getContextPath() + "/Login?registered=1");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("maNhanVien", maNhanVien);
            request.setAttribute("hoTen", hoTen);
            request.setAttribute("email", email);
            request.setAttribute("soDienThoai", soDienThoai);
            request.getRequestDispatcher("/Register.jsp").forward(request, response);
        }
    }
}
