package QuanLySanPham.controller;

import QuanLySanPham.Entity.TrongKinh;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "TrongKinhServlet", value = {
        "/TrongKinh",
        "/TrongKinh/new",
        "/TrongKinh/insert",
        "/TrongKinh/edit",
        "/TrongKinh/update",
        "/TrongKinh/delete",
})
public class TrongKinhServlet extends HttpServlet {
    private final LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/TrongKinh":
                showTrongKinh(request, response);
                break;
            case "/TrongKinh/new":
                showAddTrongKinh(request, response);
                break;
            case "/TrongKinh/edit":
                showEditTrongKinh(request, response);
                break;
            default:
                showTrongKinh(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/TrongKinh/insert":
                insertTrongKinh(request, response);
                break;
            case "/TrongKinh/update":
                updateTrongKinh(request, response);
                break;
            case "/TrongKinh/delete":
                deleteTrongKinh(request, response);
                break;
            default:
                showTrongKinh(request, response);
                break;
        }
    }

    private void showTrongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<TrongKinh> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemTrongKinh(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaTrongKinh();
        }

        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");
        request.setAttribute("activeSubMenu", "lens");
        request.getRequestDispatcher("/Admin/QuanLyBienThe/TrongKinh.jsp").forward(request, response);
    }

    private void showAddTrongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("trongKinh", new TrongKinh());
        request.getRequestDispatcher("/Admin/QuanLyBienThe/TrongKinh.jsp").forward(request, response);
    }

    private void showEditTrongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            TrongKinh trongKinh = lookupService.layTrongKinhTheoId(id);
            if (trongKinh == null) {
                response.sendRedirect(request.getContextPath() + "/TrongKinh");
                return;
            }
            request.setAttribute("trongKinh", trongKinh);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/TrongKinh.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/TrongKinh");
        }
    }

    private void insertTrongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TrongKinh trongKinh = getTrongKinhFromRequest(request);
        try {
            lookupService.themTrongKinh(trongKinh);
            response.sendRedirect(request.getContextPath() + "/TrongKinh");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("trongKinh", trongKinh);
            showTrongKinh(request, response);
        }
    }

    private void updateTrongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TrongKinh trongKinh = getTrongKinhFromRequest(request);
        try {
            String idStr = request.getParameter("id");
            trongKinh.setId(Integer.parseInt(idStr));
            lookupService.capNhatTrongKinh(trongKinh);
            response.sendRedirect(request.getContextPath() + "/TrongKinh");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/TrongKinh");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("trongKinh", trongKinh);
            showTrongKinh(request, response);
        }
    }

    private void deleteTrongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            lookupService.xoaTrongKinh(id);
            response.sendRedirect(request.getContextPath() + "/TrongKinh");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/TrongKinh");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Không thể xóa tròng kính này vì có sản phẩm đang sử dụng.");
            showTrongKinh(request, response);
        }
    }

    private TrongKinh getTrongKinhFromRequest(HttpServletRequest request) {
        String loaiTrong = request.getParameter("loaiTrong");
        String trangThaiStr = request.getParameter("trangthai");

        TrongKinh trongKinh = new TrongKinh();
        trongKinh.setLoaiTrong(loaiTrong);

        try {
            if (trangThaiStr != null && !trangThaiStr.isEmpty()) {
                trongKinh.setTrangThai(Integer.parseInt(trangThaiStr));
            }
        } catch (NumberFormatException e) {
            // Handle invalid status format if necessary
        }
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                trongKinh.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                // Ignore if ID is not a valid number
            }
        }

        return trongKinh;
    }
}
