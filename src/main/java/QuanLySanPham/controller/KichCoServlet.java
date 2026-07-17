package QuanLySanPham.controller;

import QuanLySanPham.Entity.KichCo;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "KichCoServlet", value = {
        "/KichCo",
        "/KichCo/new",
        "/KichCo/insert",
        "/KichCo/edit",
        "/KichCo/update",
        "/KichCo/delete",
})
public class KichCoServlet extends HttpServlet {
    private final LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/KichCo":
                showKichCo(request, response);
                break;
            case "/KichCo/new":
                showAddKichCo(request, response);
                break;
            case "/KichCo/edit":
                showEditKichCo(request, response);
                break;
            default:
                showKichCo(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/KichCo/insert":
                insertKichCo(request, response);
                break;
            case "/KichCo/update":
                updateKichCo(request, response);
                break;
            case "/KichCo/delete":
                deleteKichCo(request, response);
                break;
            default:
                showKichCo(request, response);
                break;
        }
    }

    private void showKichCo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<KichCo> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemKichCo(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaKichCo();
        }

        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");
        request.setAttribute("activeSubMenu", "size");
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KichCo.jsp").forward(request, response);
    }

    private void showAddKichCo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("kichCo", new KichCo());
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KichCo.jsp").forward(request, response);
    }

    private void showEditKichCo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            KichCo kichCo = lookupService.layKichCoTheoId(id);
            if (kichCo == null) {
                response.sendRedirect(request.getContextPath() + "/KichCo");
                return;
            }
            request.setAttribute("kichCo", kichCo);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/KichCo.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/KichCo");
        }
    }

    private void insertKichCo(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KichCo kichCo = getKichCoFromRequest(request);
        try {
            lookupService.themKichCo(kichCo);
            response.sendRedirect(request.getContextPath() + "/KichCo");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kichCo", kichCo);
            showKichCo(request, response);
        }
    }

    private void updateKichCo(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KichCo kichCo = getKichCoFromRequest(request);
        try {
            String idStr = request.getParameter("id");
            kichCo.setId(Integer.parseInt(idStr));
            lookupService.capNhatKichCo(kichCo);
            response.sendRedirect(request.getContextPath() + "/KichCo");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/KichCo");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kichCo", kichCo);
            showKichCo(request, response);
        }
    }

    private void deleteKichCo(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            lookupService.xoaKichCo(id);
            response.sendRedirect(request.getContextPath() + "/KichCo");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/KichCo");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Không thể xóa kích cỡ này vì có sản phẩm đang sử dụng.");
            showKichCo(request, response);
        }
    }

    private KichCo getKichCoFromRequest(HttpServletRequest request) {
        String tenKichCo = request.getParameter("tenKichCo");
        String trangThaiStr = request.getParameter("trangthai");

        KichCo kichCo = new KichCo();
        kichCo.setTenKichCo(tenKichCo);

        try {
            if (trangThaiStr != null && !trangThaiStr.isEmpty()) {
                kichCo.setTrangThai(Integer.parseInt(trangThaiStr));
            }
        } catch (NumberFormatException e) {
            // Handle invalid status format if necessary
        }
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                kichCo.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                // Ignore if ID is not a valid number
            }
        }

        return kichCo;
    }
}
