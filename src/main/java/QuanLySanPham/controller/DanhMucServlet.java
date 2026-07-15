package QuanLySanPham.controller;

import QuanLySanPham.Entity.DanhMuc;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "DanhMucServlet", value = {
        "/DanhMuc",
        "/DanhMuc/new",
        "/DanhMuc/insert",
        "/DanhMuc/edit",
        "/DanhMuc/update",
        "/DanhMuc/delete",
})
public class DanhMucServlet extends HttpServlet {
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/DanhMuc":
                ShowDanhMuc(request, response);
                break;
            case "/DanhMuc/new":
                showAddDanhMuc(request, response);
                break;
            case "/DanhMuc/edit":
                showEditDanhMuc(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/DanhMuc/insert":
                insertDanhMuc(request, response);
                break;
            case "/DanhMuc/update":
                updateDanhMuc(request, response);
                break;
            case "/DanhMuc/delete":
                deleteDanhMuc(request, response);
                break;
        }
    }

    private void ShowDanhMuc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<DanhMuc> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemDanhMuc(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaDanhMuc();
        }

        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");    // Giữ menu cha mở và sáng lên
        request.setAttribute("activeSubMenu", "category"); // Làm sáng mục Danh mục
        request.getRequestDispatcher("/Admin/QuanLyBienThe/DanhMuc.jsp").forward(request, response);
    }

    private void showAddDanhMuc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/Admin/QuanLyBienThe/DanhMuc.jsp").forward(request, response);
    }

    private void showEditDanhMuc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            Integer id = Integer.parseInt(idStr.trim());
            DanhMuc danhMuc = lookupService.layDanhMucTheoId(id);
            request.setAttribute("danhMuc", danhMuc);
        }
        request.getRequestDispatcher("/Admin/QuanLyBienThe/DanhMuc.jsp").forward(request, response);
    }

    private void insertDanhMuc(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DanhMuc danhMuc = new DanhMuc();
        try {
            // Đưa việc lấy dữ liệu vào trong try để bắt lỗi validate
            danhMuc = getDanhMucFron(request);
            lookupService.themDanhMuc(danhMuc);
            response.sendRedirect(request.getContextPath() + "/DanhMuc");
        } catch (RuntimeException e) {
            // Phục hồi dữ liệu đang nhập trên form
            danhMuc.setMaDanhMuc(request.getParameter("maDanhMuc"));
            danhMuc.setTenDanhMuc(request.getParameter("tenDanhMuc"));
            String trangthaiStr = request.getParameter("trangthai");
            if (trangthaiStr != null && !trangthaiStr.trim().isEmpty()) {
                try { danhMuc.setTrangThai(Integer.parseInt(trangthaiStr)); } catch (Exception ignored) {}
            }

            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("danhMuc", danhMuc);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/DanhMuc.jsp").forward(request, response);
        }
    }

    private void updateDanhMuc(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DanhMuc danhMuc = new DanhMuc();
        try {
            danhMuc = getDanhMucFron(request);

            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy ID danh mục cần cập nhật!");
            }
            danhMuc.setId(Integer.parseInt(idStr.trim()));

            lookupService.capNhatDanhMuc(danhMuc);
            response.sendRedirect(request.getContextPath() + "/DanhMuc");
        } catch (RuntimeException e) {
            // Phục hồi dữ liệu đang nhập trên form
            danhMuc.setMaDanhMuc(request.getParameter("maDanhMuc"));
            danhMuc.setTenDanhMuc(request.getParameter("tenDanhMuc"));

            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.trim().isEmpty()) {
                try { danhMuc.setId(Integer.parseInt(idStr)); } catch (Exception ignored) {}
            }
            String trangthaiStr = request.getParameter("trangthai");
            if (trangthaiStr != null && !trangthaiStr.trim().isEmpty()) {
                try { danhMuc.setTrangThai(Integer.parseInt(trangthaiStr)); } catch (Exception ignored) {}
            }

            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("danhMuc", danhMuc);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/DanhMuc.jsp").forward(request, response);
        }
    }

    private void deleteDanhMuc(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.trim().isEmpty()) {
                Integer id = Integer.parseInt(idStr.trim());
                lookupService.xoaDanhMuc(id);
            }
            // Nếu xóa thành công, quay về trang danh sách
            response.sendRedirect(request.getContextPath() + "/DanhMuc");
        } catch (Exception e) {
            // In chi tiết lỗi ra Console để bạn dễ theo dõi
            e.printStackTrace();

            // Bắn câu thông báo lỗi về lại file JSP
            request.setAttribute("errorMessage", "Không thể xóa danh mục này vì dữ liệu đang được sử dụng ở bảng khác (Lỗi khóa ngoại)!");

            // Load lại danh sách danh mục để giao diện vẫn hiển thị bình thường
            List<DanhMuc> items = lookupService.layTatCaDanhMuc();
            request.setAttribute("items", items);
            request.setAttribute("activeMenu", "product");
            request.setAttribute("activeSubMenu", "category");

            // Chuyển tiếp về lại trang giao diện chứ không để trắng trang
            request.getRequestDispatcher("/Admin/QuanLyBienThe/DanhMuc.jsp").forward(request, response);
        }
    }

    private DanhMuc getDanhMucFron(HttpServletRequest request) {
        String maDanhMuc = request.getParameter("maDanhMuc");
        String tenDanhMuc = request.getParameter("tenDanhMuc");
        String trangthaiStr = request.getParameter("trangthai");

        // Validate
        if (maDanhMuc == null || maDanhMuc.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã danh mục không được để trống!");
        }
        if (tenDanhMuc == null || tenDanhMuc.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }
        if (trangthaiStr == null || trangthaiStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn trạng thái!");
        }

        DanhMuc danhMuc = new DanhMuc();
        danhMuc.setMaDanhMuc(maDanhMuc.trim());
        danhMuc.setTenDanhMuc(tenDanhMuc.trim());

        try {
            danhMuc.setTrangThai(Integer.parseInt(trangthaiStr.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ!");
        }

        return danhMuc;
    }
}