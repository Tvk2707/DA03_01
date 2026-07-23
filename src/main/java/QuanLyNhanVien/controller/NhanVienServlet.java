package QuanLyNhanVien.controller;

import QuanLySanPham.Entity.NhanVien;
import QuanLyNhanVien.service.NhanVienService;
import QuanLyNhanVien.service.impl.NhanVienServiceImpl;
import QuanLySanPham.Utils.EmailService; // Thêm import EmailService
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "NhanVienServlet", value = {"/NhanVien","/NhanVien/new","/NhanVien/insert","/NhanVien/edit","/NhanVien/update","/NhanVien/delete","/NhanVien/search"})
public class NhanVienServlet extends HttpServlet {
    private final NhanVienService nhanVienService = new NhanVienServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/NhanVien":
                showList(request, response);
                break;
            case "/NhanVien/new":
                showAdd(request, response);
                break;
            case "/NhanVien/edit":
                showEdit(request, response);
                break;
            default:
                showList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        switch (path) {
            case "/NhanVien/insert":
                insert(request, response);
                break;
            case "/NhanVien/update":
                update(request, response);
                break;
            case "/NhanVien/delete":
                delete(request, response);
                break;
            case "/NhanVien/search":
                search(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/NhanVien");
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageStr = request.getParameter("page");
        int page = (pageStr != null && !pageStr.isEmpty()) ? Integer.parseInt(pageStr) : 1;
        int pageSize = 10;
        List<NhanVien> items = nhanVienService.layCoPhanTrang(page, pageSize);
        long totalCount = nhanVienService.timKiem("", "").size();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // Set active menu for sidebar highlight
        request.setAttribute("activeMenu", "employee");
        request.setAttribute("items", items);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);
        request.getRequestDispatcher("/Admin/QuanLyNhanVien/QuanLyNhanVien.jsp").forward(request, response);
    }

    private void showAdd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set active menu for sidebar highlight
        request.setAttribute("activeMenu", "employee");
        request.setAttribute("action", "add");
        request.getRequestDispatcher("/Admin/QuanLyNhanVien/NhanVienAdd.jsp").forward(request, response);
    }

    private void showEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        NhanVien nv = nhanVienService.timTheoId(id);
        if (nv == null) {
            request.setAttribute("error", "Không tìm thấy nhân viên");
            showList(request, response);
            return;
        }
        // Set active menu for sidebar highlight
        request.setAttribute("activeMenu", "employee");
        request.setAttribute("nhanVien", nv);
        request.setAttribute("action", "edit");
        request.getRequestDispatcher("/Admin/QuanLyNhanVien/NhanVienEdit.jsp").forward(request, response);
    }

    private void insert(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            NhanVien nv = getNhanVienFrom(request);
            // 1. Lưu nhân viên vào Database
            nhanVienService.themNhanVien(nv);

            // 2. Gửi Email thông báo chạy ngầm cho nhân viên mới
            if (nv.getEmail() != null && !nv.getEmail().trim().isEmpty()) {
                new Thread(() -> {
                    String tieuDe = "Thông báo: Tài khoản nhân viên mới đã được khởi tạo";
                    String noiDung = "<h3>Xin chào " + nv.getHoTen() + ",</h3>"
                            + "<p>Tài khoản nhân viên của bạn đã được khởi tạo trên hệ thống quản lý.</p>"
                            + "<ul>"
                            + "  <li><b>Mã nhân viên:</b> " + nv.getMaNhanVien() + "</li>"
                            + "  <li><b>Email đăng nhập:</b> " + nv.getEmail() + "</li>"
                            + "  <li><b>Chức vụ:</b> " + (nv.getChucVu() != null ? nv.getChucVu() : "Chưa cập nhật") + "</li>"
                            + "</ul>"
                            + "<p>Vui lòng đăng nhập hệ thống hoặc liên hệ Quản lý để nhận mật khẩu làm việc.</p>";

                    EmailService.sendEmail(nv.getEmail(), tieuDe, noiDung);
                }).start();
            }

            response.sendRedirect(request.getContextPath() + "/NhanVien");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("nhanVien", getNhanVienFrom(request));
            request.setAttribute("action", "add");
            request.getRequestDispatcher("/Admin/QuanLyNhanVien/NhanVienAdd.jsp").forward(request, response);
        }
    }

    private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            NhanVien nv = getNhanVienFrom(request);
            nv.setId(Integer.parseInt(request.getParameter("id")));
            nhanVienService.capNhatNhanVien(nv);
            response.sendRedirect(request.getContextPath() + "/NhanVien");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("nhanVien", getNhanVienFrom(request));
            request.setAttribute("action", "edit");
            request.getRequestDispatcher("/Admin/QuanLyNhanVien/NhanVienEdit.jsp").forward(request, response);
        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            nhanVienService.xoaNhanVien(id);
            response.sendRedirect(request.getContextPath() + "/NhanVien");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            showList(request, response);
        }
    }

    private void search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String hoTen = request.getParameter("hoTen");
        String email = request.getParameter("email");
        List<NhanVien> items = nhanVienService.timKiem(hoTen, email);
        request.setAttribute("items", items);
        request.getRequestDispatcher("/Admin/QuanLyNhanVien/QuanLyNhanVien.jsp").forward(request, response);
    }

    private NhanVien getNhanVienFrom(HttpServletRequest request) {
        String ma = request.getParameter("maNhanVien");
        String hoTen = request.getParameter("hoTen");
        String email = request.getParameter("email");
        String soDienThoai = request.getParameter("soDienThoai");
        String matKhau = request.getParameter("matKhau");
        String ngaySinhStr = request.getParameter("ngaySinh");
        String gioiTinhStr = request.getParameter("gioiTinh");
        String diaChi = request.getParameter("diaChi");
        String chucVu = request.getParameter("chucVu");
        String anh = request.getParameter("anhDaiDien");
        String trangThaiStr = request.getParameter("trangThai");

        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(ma);
        nv.setHoTen(hoTen);
        nv.setEmail(email);
        nv.setSoDienThoai(soDienThoai);
        nv.setMatKhau(matKhau);
        if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
            try {
                nv.setNgaySinh(java.time.LocalDate.parse(ngaySinhStr));
            } catch (Exception ignored) {}
        }
        nv.setGioiTinh((gioiTinhStr != null && !gioiTinhStr.isEmpty()) ? Integer.parseInt(gioiTinhStr) : 1);
        nv.setDiaChi(diaChi);
        nv.setChucVu(chucVu);
        nv.setAnhDaiDien(anh);
        nv.setTrangThai((trangThaiStr != null && !trangThaiStr.isEmpty()) ? Integer.parseInt(trangThaiStr) : 1);
        return nv;
    }
}