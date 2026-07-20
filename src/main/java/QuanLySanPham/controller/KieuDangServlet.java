package QuanLySanPham.controller;

import QuanLySanPham.Entity.KieuDang;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "KieuDangServlet", value = {
        "/KieuDang",
        "/KieuDang/new",
        "/KieuDang/insert",
        "/KieuDang/edit",
        "/KieuDang/update",
        "/KieuDang/delete",
})
public class KieuDangServlet extends HttpServlet {
    private final LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/KieuDang":
                showKieuDang(request, response);
                break;
            case "/KieuDang/new":
                showAddKieuDang(request, response);
                break;
            case "/KieuDang/edit":
                showEditKieuDang(request, response);
                break;
            default:
                showKieuDang(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/KieuDang/insert":
                insertKieuDang(request, response);
                break;
            case "/KieuDang/update":
                updateKieuDang(request, response);
                break;
            case "/KieuDang/delete":
                deleteKieuDang(request, response);
                break;
            default:
                showKieuDang(request, response);
                break;
        }
    }

    private void showKieuDang(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<KieuDang> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemKieuDang(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaKieuDang();
        }

        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");
        request.setAttribute("activeSubMenu", "style");
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KieuDang.jsp").forward(request, response);
    }

    private void showAddKieuDang(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("kieuDang", new KieuDang());
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KieuDang.jsp").forward(request, response);
    }

    private void showEditKieuDang(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            KieuDang kieuDang = lookupService.layKieuDangTheoId(id);
            if (kieuDang == null) {
                response.sendRedirect(request.getContextPath() + "/KieuDang");
                return;
            }
            request.setAttribute("kieuDang", kieuDang);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/KieuDang.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/KieuDang");
        }
    }

    private void insertKieuDang(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KieuDang kieuDang = getKieuDangFromRequest(request);
        try {
            lookupService.themKieuDang(kieuDang);
            response.sendRedirect(request.getContextPath() + "/KieuDang");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kieuDang", kieuDang);
            showKieuDang(request, response);
        }
    }

    private void updateKieuDang(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KieuDang kieuDang = getKieuDangFromRequest(request);
        try {
            String idStr = request.getParameter("id");
            kieuDang.setId(Integer.parseInt(idStr));
            lookupService.capNhatKieuDang(kieuDang);
            response.sendRedirect(request.getContextPath() + "/KieuDang");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/KieuDang");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kieuDang", kieuDang);
            showKieuDang(request, response);
        }
    }

    private void deleteKieuDang(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            lookupService.xoaKieuDang(id);
            response.sendRedirect(request.getContextPath() + "/KieuDang");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/KieuDang");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Không thể xóa kiểu dáng này vì có sản phẩm đang sử dụng.");
            showKieuDang(request, response);
        }
    }

    private KieuDang getKieuDangFromRequest(HttpServletRequest request) {
        String tenKieuDang = request.getParameter("tenKieuDang");
        String trangThaiStr = request.getParameter("trangthai");

        KieuDang kieuDang = new KieuDang();
        kieuDang.setTenKieuDang(tenKieuDang);

        try {
            if (trangThaiStr != null && !trangThaiStr.isEmpty()) {
                kieuDang.setTrangThai(Integer.parseInt(trangThaiStr));
            }
        } catch (NumberFormatException e) {
            // Handle invalid status format if necessary
        }
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                kieuDang.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                // Ignore if ID is not a valid number
            }
        }

        return kieuDang;
    }
}
