package QuanLySanPham.controller;

import QuanLySanPham.Entity.MauSac;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MauSacServlet", value = {
        "/MauSac",
        "/MauSac/new",
        "/MauSac/insert",
        "/MauSac/edit",
        "/MauSac/update",
        "/MauSac/delete",
})
public class MauSacServlet extends HttpServlet {
    private final LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/MauSac":
                showMauSac(request, response);
                break;
            case "/MauSac/new":
                showAddMauSac(request, response);
                break;
            case "/MauSac/edit":
                showEditMauSac(request, response);
                break;
            default:
                showMauSac(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/MauSac/insert":
                insertMauSac(request, response);
                break;
            case "/MauSac/update":
                updateMauSac(request, response);
                break;
            case "/MauSac/delete":
                deleteMauSac(request, response);
                break;
            default:
                showMauSac(request, response);
                break;
        }
    }

    private void showMauSac(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<MauSac> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemMauSac(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaMauSac();
        }

        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");
        request.setAttribute("activeSubMenu", "color");
        request.getRequestDispatcher("/Admin/QuanLyBienThe/MauSac.jsp").forward(request, response);
    }

    private void showAddMauSac(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("mauSac", new MauSac());
        request.getRequestDispatcher("/Admin/QuanLyBienThe/MauSac.jsp").forward(request, response);
    }

    private void showEditMauSac(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            MauSac mauSac = lookupService.layMauSacTheoId(id);
            if (mauSac == null) {
                response.sendRedirect(request.getContextPath() + "/MauSac");
                return;
            }
            request.setAttribute("mauSac", mauSac);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/MauSac.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/MauSac");
        }
    }

    private void insertMauSac(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        MauSac mauSac = getMauSacFromRequest(request);
        try {
            lookupService.themMauSac(mauSac);
            response.sendRedirect(request.getContextPath() + "/MauSac");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("mauSac", mauSac);
            showMauSac(request, response);
        }
    }

    private void updateMauSac(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        MauSac mauSac = getMauSacFromRequest(request);
        try {
            String idStr = request.getParameter("id");
            mauSac.setId(Integer.parseInt(idStr));
            lookupService.capNhatMauSac(mauSac);
            response.sendRedirect(request.getContextPath() + "/MauSac");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/MauSac");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("mauSac", mauSac);
            showMauSac(request, response);
        }
    }

    private void deleteMauSac(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            lookupService.xoaMauSac(id);
            response.sendRedirect(request.getContextPath() + "/MauSac");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/MauSac");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Không thể xóa màu sắc này vì có sản phẩm đang sử dụng.");
            showMauSac(request, response);
        }
    }

    private MauSac getMauSacFromRequest(HttpServletRequest request) {
        String maMau = request.getParameter("maMau");
        String tenMau = request.getParameter("tenMau");
        String trangThaiStr = request.getParameter("trangthai");

        MauSac mauSac = new MauSac();
        mauSac.setMaMau(maMau);
        mauSac.setTenMau(tenMau);

        try {
            if (trangThaiStr != null && !trangThaiStr.isEmpty()) {
                mauSac.setTrangThai(Integer.parseInt(trangThaiStr));
            }
        } catch (NumberFormatException e) {
            // Handle invalid status format if necessary
        }
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                mauSac.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                // Ignore if ID is not a valid number
            }
        }

        return mauSac;
    }
}
