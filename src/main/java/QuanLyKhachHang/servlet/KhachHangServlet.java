package QuanLyKhachHang.servlet;

import QuanLySanPham.Entity.KhachHang;
import QuanLyKhachHang.repository.KhachHangRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet({
        "/khach-hang/hien-thi",
        "/khach-hang/add",
        "/khach-hang/sua", // Bổ sung đường dẫn sửa
        "/khach-hang/doi-trang-thai"
})
public class KhachHangServlet extends HttpServlet {

    private KhachHangRepository repo = new KhachHangRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/khach-hang/hien-thi":
                hienThi(req, resp);
                break;
            case "/khach-hang/sua":
                // Bắt luồng GET khi người dùng click vào nút Sửa trên bảng
                hienThiFormSua(req, resp);
                break;
            case "/khach-hang/doi-trang-thai":
                doiTrangThai(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/khach-hang/hien-thi");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getServletPath();
        switch (path) {
            case "/khach-hang/add":
                add(req, resp);
                break;
            case "/khach-hang/sua":
                // Bắt luồng POST khi người dùng ấn nút "Cập nhật" ở form
                update(req, resp);
                break;
            case "/khach-hang/doi-trang-thai":
                doiTrangThai(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/khach-hang/hien-thi");
                break;
        }
    }

    private void hienThi(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("successMessage") != null) {
            req.setAttribute("successMessage", session.getAttribute("successMessage"));
            session.removeAttribute("successMessage");
        }
        if (session.getAttribute("errorMessage") != null) {
            req.setAttribute("errorMessage", session.getAttribute("errorMessage"));
            session.removeAttribute("errorMessage");
        }

        req.setAttribute("listKhachHang", repo.getAll());
        req.getRequestDispatcher("/QuanLyKhachHang/quan_ly_khach_hang.jsp").forward(req, resp);
    }

    private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(req.getParameter("maKhachHang"));
        kh.setHoTen(req.getParameter("hoTen"));
        kh.setEmail(req.getParameter("email"));
        kh.setSoDienThoai(req.getParameter("soDienThoai"));
        kh.setMatKhau(req.getParameter("matKhau"));

        String ngaySinh = req.getParameter("ngaySinh");
        if (ngaySinh != null && !ngaySinh.isEmpty()) {
            kh.setNgaySinh(LocalDate.parse(ngaySinh));
        }

        String gioiTinh = req.getParameter("gioiTinh");
        if (gioiTinh != null && !gioiTinh.isEmpty()) {
            kh.setGioiTinh(Integer.parseInt(gioiTinh));
        }

        kh.setTrangThai(1);
        repo.add(kh);

        req.getSession().setAttribute("successMessage", "Thêm khách hàng thành công!");
        resp.sendRedirect(req.getContextPath() + "/khach-hang/hien-thi");
    }

    // --- HÀM MỚI: Hiển thị form sửa ---
    private void hienThiFormSua(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            Integer id = Integer.parseInt(idStr);
            KhachHang kh = repo.getById(id); // Lấy khách hàng theo ID từ CSDL

            if (kh != null) {
                // Đẩy đối tượng sang JSP để hiển thị Mã KH và Trạng thái
                req.setAttribute("editKhachHang", kh);

                // Bật cờ editMode để JSP tự động mở form và đổi action thành /khach-hang/sua
                req.setAttribute("customerEditMode", true);

                // Đổ lại dữ liệu cũ vào các trường input
                req.setAttribute("formHoTen", kh.getHoTen());
                req.setAttribute("formEmail", kh.getEmail());
                req.setAttribute("formSoDienThoai", kh.getSoDienThoai());
                req.setAttribute("formNgaySinh", kh.getNgaySinh());
                req.setAttribute("formGioiTinh", kh.getGioiTinh());
            }
        }
        // Gọi lại hàm hiển thị danh sách, form sửa sẽ tự mở nhờ logic trong JSP
        hienThi(req, resp);
    }

    // --- HÀM MỚI: Xử lý cập nhật vào Database ---
    private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = Integer.parseInt(req.getParameter("id"));

        KhachHang kh = new KhachHang();
        kh.setId(id);
        kh.setHoTen(req.getParameter("hoTen"));
        kh.setEmail(req.getParameter("email"));
        kh.setSoDienThoai(req.getParameter("soDienThoai"));

        String ngaySinh = req.getParameter("ngaySinh");
        if (ngaySinh != null && !ngaySinh.isEmpty()) {
            kh.setNgaySinh(LocalDate.parse(ngaySinh));
        }

        String gioiTinh = req.getParameter("gioiTinh");
        if (gioiTinh != null && !gioiTinh.isEmpty()) {
            kh.setGioiTinh(Integer.parseInt(gioiTinh));
        }

        repo.update(kh); // Cập nhật vào CSDL
        req.getSession().setAttribute("successMessage", "Cập nhật thông tin khách hàng thành công!");
        resp.sendRedirect(req.getContextPath() + "/khach-hang/hien-thi");
    }

    private void doiTrangThai(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = Integer.parseInt(req.getParameter("id"));
        repo.doiTrangThai(id);
        req.getSession().setAttribute("successMessage", "Xóa khách hàng thành công!");
        resp.sendRedirect(req.getContextPath() + "/khach-hang/hien-thi");
    }
}