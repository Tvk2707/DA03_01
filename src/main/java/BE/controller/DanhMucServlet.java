package BE.controller;

import BE.Entity.DanhMuc;
import BE.service.LookupService;
import BE.service.impl.LookupServiceImpl;
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
        List<DanhMuc> items = lookupService.layTatCaDanhMuc();
        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");    // Giữ menu cha mở và sáng lên
        request.setAttribute("activeSubMenu", "category"); // Làm sáng mục Danh mục
        request.getRequestDispatcher("/Admin/QuanLySanPham/DanhMuc.jsp").forward(request, response);
    }

    private void showAddDanhMuc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/Admin/QuanLySanPham/DanhMuc.jsp").forward(request, response);
    }

    private void showEditDanhMuc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        DanhMuc danhMuc = lookupService.layDanhMucTheoId(id);
        request.setAttribute("danhMuc", danhMuc);
        request.getRequestDispatcher("/Admin/QuanLySanPham/DanhMuc.jsp").forward(request, response);
    }

    private void insertDanhMuc(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DanhMuc danhMuc = getDanhMucFron(request);
        try {
            lookupService.themDanhMuc(danhMuc);
            response.sendRedirect(request.getContextPath() + "/DanhMuc");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("danhMuc", danhMuc);
            request.getRequestDispatcher("/Admin/QuanLySanPham/DanhMuc.jsp").forward(request, response);
        }
    }

    private void updateDanhMuc(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DanhMuc danhMuc = getDanhMucFron(request);
        danhMuc.setId(Integer.parseInt(request.getParameter("id")));
        try {
            lookupService.capNhatDanhMuc(danhMuc);
            response.sendRedirect(request.getContextPath() + "/DanhMuc");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("danhMuc", danhMuc);
            request.getRequestDispatcher("/Admin/QuanLySanPham/DanhMuc.jsp").forward(request, response);
        }
    }
    private void deleteDanhMuc(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            lookupService.xoaDanhMuc(id);

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

            // Chuyển tiếp về lại trang giao diện chứ không để trắng trang
            request.getRequestDispatcher("/Admin/QuanLySanPham/DanhMuc.jsp").forward(request, response);
        }
    }


    private DanhMuc getDanhMucFron(HttpServletRequest request) {
        String maDanhMuc = request.getParameter("maDanhMuc");
        String tenDanhMuc = request.getParameter("tenDanhMuc");
        Integer trangthai  = Integer.parseInt(request.getParameter("trangthai"));
        DanhMuc danhMuc = new DanhMuc();
        danhMuc.setMaDanhMuc(maDanhMuc);
        danhMuc.setTenDanhMuc(tenDanhMuc);
        danhMuc.setTrangThai(trangthai);
        return danhMuc;
    }
}