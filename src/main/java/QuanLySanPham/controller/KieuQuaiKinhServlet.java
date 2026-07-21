package QuanLySanPham.controller;

import QuanLySanPham.Entity.KieuQuaiKinh;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "KieuQuaiKinhServlet", value = {
        "/KieuQuaiKinh",
        "/KieuQuaiKinh/new",
        "/KieuQuaiKinh/insert",
        "/KieuQuaiKinh/edit",
        "/KieuQuaiKinh/update",
        "/KieuQuaiKinh/delete",
})
public class KieuQuaiKinhServlet extends HttpServlet {
    private final LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/KieuQuaiKinh":
                showKieuQuaiKinh(request, response);
                break;
            case "/KieuQuaiKinh/new":
                showAddKieuQuaiKinh(request, response);
                break;
            case "/KieuQuaiKinh/edit":
                showEditKieuQuaiKinh(request, response);
                break;
            default:
                showKieuQuaiKinh(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/KieuQuaiKinh/insert":
                insertKieuQuaiKinh(request, response);
                break;
            case "/KieuQuaiKinh/update":
                updateKieuQuaiKinh(request, response);
                break;
            case "/KieuQuaiKinh/delete":
                deleteKieuQuaiKinh(request, response);
                break;
            default:
                showKieuQuaiKinh(request, response);
                break;
        }
    }

    private void showKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<KieuQuaiKinh> items = lookupService.layTatCaKieuQuaiKinh();
        request.setAttribute("items", items);
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KieuQuaiKinh.jsp").forward(request, response);
    }

    private void showAddKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("kieuQuaiKinh", new KieuQuaiKinh());
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KieuQuaiKinh.jsp").forward(request, response);
    }

    private void showEditKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            KieuQuaiKinh kieuQuaiKinh = lookupService.layKieuQuaiKinhTheoId(id);
            if (kieuQuaiKinh == null) {
                response.sendRedirect(request.getContextPath() + "/KieuQuaiKinh");
                return;
            }
            request.setAttribute("kieuQuaiKinh", kieuQuaiKinh);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/KieuQuaiKinh.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/KieuQuaiKinh");
        }
    }

    private void insertKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KieuQuaiKinh kieuQuaiKinh = getKieuQuaiKinhFromRequest(request);
        try {
            lookupService.themKieuQuaiKinh(kieuQuaiKinh);
            response.sendRedirect(request.getContextPath() + "/KieuQuaiKinh");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kieuQuaiKinh", kieuQuaiKinh);
            showKieuQuaiKinh(request, response);
        }
    }

    private void updateKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KieuQuaiKinh kieuQuaiKinh = getKieuQuaiKinhFromRequest(request);
        try {
            String idStr = request.getParameter("id");
            kieuQuaiKinh.setId(Long.parseLong(idStr));
            lookupService.capNhatKieuQuaiKinh(kieuQuaiKinh);
            response.sendRedirect(request.getContextPath() + "/KieuQuaiKinh");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/KieuQuaiKinh");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kieuQuaiKinh", kieuQuaiKinh);
            showKieuQuaiKinh(request, response);
        }
    }

    private void deleteKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            lookupService.xoaKieuQuaiKinh(id);
            response.sendRedirect(request.getContextPath() + "/KieuQuaiKinh");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/KieuQuaiKinh");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Không thể xóa kiểu quai kính này vì có sản phẩm đang sử dụng.");
            showKieuQuaiKinh(request, response);
        }
    }

    private KieuQuaiKinh getKieuQuaiKinhFromRequest(HttpServletRequest request) {
        String tenKieuQuaiKinh = request.getParameter("tenKieuQuaiKinh");
        KieuQuaiKinh kieuQuaiKinh = new KieuQuaiKinh();
        kieuQuaiKinh.setKieuQuai(tenKieuQuaiKinh);
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                kieuQuaiKinh.setId(Long.parseLong(idStr));
            } catch (NumberFormatException e) {
                // Ignore if ID is not a valid number
            }
        }
        
        return kieuQuaiKinh;
    }
}
