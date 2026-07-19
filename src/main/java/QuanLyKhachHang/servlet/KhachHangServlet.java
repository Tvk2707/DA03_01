package QuanLyKhachHang.servlet;

import QuanLySanPham.Entity.KhachHang;
import QuanLyKhachHang.repository.KhachHangRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet({
        "/khach-hang/hien-thi",
        "/khach-hang/add",
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
            default:
                resp.sendRedirect(req.getContextPath() + "/khach-hang/hien-thi");
                break;
        }
    }

    private void hienThi(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        resp.sendRedirect(req.getContextPath() + "/khach-hang/hien-thi");
    }

    private void doiTrangThai(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = Integer.parseInt(req.getParameter("id"));
        repo.doiTrangThai(id);
        resp.sendRedirect(req.getContextPath() + "/khach-hang/hien-thi");
    }
}
