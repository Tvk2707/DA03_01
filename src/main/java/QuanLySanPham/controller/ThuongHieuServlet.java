package QuanLySanPham.controller;

import QuanLySanPham.Entity.ThuongHieu;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ThuongHieuServlet", value = {
        "/ThuongHieu",
        "/ThuongHieu/new",
        "/ThuongHieu/insert",
        "/ThuongHieu/edit",
        "/ThuongHieu/update",
        "/ThuongHieu/delete",
})
public class ThuongHieuServlet extends HttpServlet {
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/ThuongHieu":
                ShowThuongHieu(request, response);
                break;
            case "/ThuongHieu/new":
                showAddThuongHieu(request, response);
                break;
            case "/ThuongHieu/edit":
                showEditThuongHieu(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/ThuongHieu/insert":
                insertThuongHieu(request, response);
                break;
            case "/ThuongHieu/update":
                updateThuongHieu(request, response);
                break;
            case "/ThuongHieu/delete":
                deleteThuongHieu(request, response);
                break;
        }
    }

    private void ShowThuongHieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<ThuongHieu> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemThuongHieu(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaThuongHieu();
        }

        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");    // Giữ menu cha mở và sáng lên
        request.setAttribute("activeSubMenu", "category");
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
    }

    private void showAddThuongHieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
    }

    private void showEditThuongHieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            Integer id = Integer.parseInt(idStr.trim());
            ThuongHieu thuongHieu = lookupService.layThuongHieuTheoId(id);
            request.setAttribute("thuongHieu", thuongHieu);
        }
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
    }

    private void insertThuongHieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ThuongHieu thuongHieu = new ThuongHieu();
        try {
            thuongHieu = getThuongHieuFron(request);
            lookupService.themThuongHieu(thuongHieu);
            response.sendRedirect(request.getContextPath() + "/ThuongHieu");
        } catch (RuntimeException e) {
            // Giữ lại dữ liệu người dùng đã nhập trên form khi có lỗi
            thuongHieu.setMaThuongHieu(request.getParameter("maThuongHieu"));
            thuongHieu.setTenThuongHieu(request.getParameter("tenThuongHieu"));
            String trangthaiStr = request.getParameter("trangthai");
            if (trangthaiStr != null && !trangthaiStr.trim().isEmpty()) {
                try { thuongHieu.setTrangThai(Integer.parseInt(trangthaiStr)); } catch (Exception ignored) {}
            }

            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("thuongHieu", thuongHieu);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
        }
    }

    private void updateThuongHieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ThuongHieu thuongHieu = new ThuongHieu();
        try {
            thuongHieu = getThuongHieuFron(request);

            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy ID thương hiệu cần cập nhật!");
            }
            thuongHieu.setId(Integer.parseInt(idStr.trim()));

            lookupService.capNhatThuongHieu(thuongHieu);
            response.sendRedirect(request.getContextPath() + "/ThuongHieu");
        } catch (RuntimeException e) {
            // Giữ lại dữ liệu người dùng đã nhập trên form khi có lỗi
            thuongHieu.setMaThuongHieu(request.getParameter("maThuongHieu"));
            thuongHieu.setTenThuongHieu(request.getParameter("tenThuongHieu"));
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.trim().isEmpty()) {
                try { thuongHieu.setId(Integer.parseInt(idStr)); } catch (Exception ignored) {}
            }
            String trangthaiStr = request.getParameter("trangthai");
            if (trangthaiStr != null && !trangthaiStr.trim().isEmpty()) {
                try { thuongHieu.setTrangThai(Integer.parseInt(trangthaiStr)); } catch (Exception ignored) {}
            }

            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("thuongHieu", thuongHieu);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
        }
    }

    private void deleteThuongHieu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            Integer id = Integer.parseInt(idStr.trim());
            lookupService.xoaThuongHieu(id);
        }
        response.sendRedirect(request.getContextPath() + "/ThuongHieu");
    }

    private ThuongHieu getThuongHieuFron(HttpServletRequest request) {
        String maThuongHieu = request.getParameter("maThuongHieu");
        String tenThuongHieu = request.getParameter("tenThuongHieu");
        String trangthaiStr = request.getParameter("trangthai");

        // Validate chuỗi null, rỗng hoặc chỉ chứa khoảng trắng
        if (maThuongHieu == null || maThuongHieu.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã thương hiệu không được để trống!");
        }
        if (tenThuongHieu == null || tenThuongHieu.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thương hiệu không được để trống!");
        }
        if (trangthaiStr == null || trangthaiStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn trạng thái!");
        }

        ThuongHieu thuongHieu = new ThuongHieu();
        // Dùng trim() để tự động cắt bỏ khoảng trắng thừa ở hai đầu trước khi lưu
        thuongHieu.setMaThuongHieu(maThuongHieu.trim());
        thuongHieu.setTenThuongHieu(tenThuongHieu.trim());

        // Validate kiểu số cho trạng thái
        try {
            thuongHieu.setTrangThai(Integer.parseInt(trangthaiStr.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ!");
        }

        return thuongHieu;
    }
}