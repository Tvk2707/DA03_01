package QuanLySanPham.controller;

import QuanLySanPham.Entity.DanhMuc;
import QuanLySanPham.Utils.ValidationException;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    private final LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/DanhMuc":
                showDanhMuc(request, response);
                break;
            case "/DanhMuc/new":
                showAddDanhMuc(request, response);
                break;
            case "/DanhMuc/edit":
                showEditDanhMuc(request, response);
                break;
            default:
                showDanhMuc(request, response);
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
            default:
                showDanhMuc(request, response);
                break;
        }
    }

    private void showDanhMuc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<DanhMuc> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemDanhMuc(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaDanhMuc();
        }

        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");
        request.setAttribute("activeSubMenu", "category");
        request.getRequestDispatcher("/Admin/QuanLyBienThe/DanhMuc.jsp").forward(request, response);
    }

    private void showAddDanhMuc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("danhMuc", new DanhMuc());
        request.getRequestDispatcher("/Admin/QuanLyBienThe/DanhMuc.jsp").forward(request, response);
    }

    private void showEditDanhMuc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            DanhMuc danhMuc = lookupService.layDanhMucTheoId(id);
            if (danhMuc == null) {
                response.sendRedirect(request.getContextPath() + "/DanhMuc");
                return;
            }
            request.setAttribute("danhMuc", danhMuc);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/DanhMuc.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/DanhMuc");
        }
    }

    private void insertDanhMuc(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DanhMuc danhMuc = getDanhMucFromRequest(request);
        try {
            lookupService.themDanhMuc(danhMuc);
            response.sendRedirect(request.getContextPath() + "/DanhMuc");
        } catch (ValidationException e) {
            request.setAttribute("errors", e.getErrors());
            request.setAttribute("danhMuc", danhMuc);
            showDanhMuc(request, response);
        }
    }

    private void updateDanhMuc(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DanhMuc danhMuc = getDanhMucFromRequest(request);
        try {
            String idStr = request.getParameter("id");
            danhMuc.setId(Integer.parseInt(idStr));
            lookupService.capNhatDanhMuc(danhMuc);
            response.sendRedirect(request.getContextPath() + "/DanhMuc");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/DanhMuc");
        } catch (ValidationException e) {
            request.setAttribute("errors", e.getErrors());
            request.setAttribute("danhMuc", danhMuc);
            showDanhMuc(request, response);
        }
    }

    private void deleteDanhMuc(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            lookupService.xoaDanhMuc(id);
            response.sendRedirect(request.getContextPath() + "/DanhMuc");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/DanhMuc");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Không thể xóa danh mục này vì có sản phẩm đang sử dụng.");
            showDanhMuc(request, response);
        }
    }

    private DanhMuc getDanhMucFromRequest(HttpServletRequest request) {
        String maDanhMuc = request.getParameter("maDanhMuc");
        String tenDanhMuc = request.getParameter("tenDanhMuc");
        String trangThaiStr = request.getParameter("trangthai");

        DanhMuc danhMuc = new DanhMuc();
        danhMuc.setMaDanhMuc(maDanhMuc);
        danhMuc.setTenDanhMuc(tenDanhMuc);

        try {
            if (trangThaiStr != null && !trangThaiStr.isEmpty()) {
                danhMuc.setTrangThai(Integer.parseInt(trangThaiStr));
            }
        } catch (NumberFormatException e) {
            // Handle invalid status format if necessary
        }
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                danhMuc.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                // Ignore if ID is not a valid number
            }
        }

        return danhMuc;
    }
}
