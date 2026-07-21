package QuanLySanPham.controller;

import QuanLySanPham.Entity.HinhDangGong;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "HinhDangGongServlet", value = {
        "/HinhDangGong",
        "/HinhDangGong/new",
        "/HinhDangGong/insert",
        "/HinhDangGong/edit",
        "/HinhDangGong/update",
        "/HinhDangGong/delete",
})
public class HinhDangGongServlet extends HttpServlet {
    private final LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/HinhDangGong":
                showHinhDangGong(request, response);
                break;
            case "/HinhDangGong/new":
                showAddHinhDangGong(request, response);
                break;
            case "/HinhDangGong/edit":
                showEditHinhDangGong(request, response);
                break;
            default:
                showHinhDangGong(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/HinhDangGong/insert":
                insertHinhDangGong(request, response);
                break;
            case "/HinhDangGong/update":
                updateHinhDangGong(request, response);
                break;
            case "/HinhDangGong/delete":
                deleteHinhDangGong(request, response);
                break;
            default:
                showHinhDangGong(request, response);
                break;
        }
    }

    private void showHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<HinhDangGong> items = lookupService.layTatCaHinhDangGong();
        request.setAttribute("items", items);
        request.getRequestDispatcher("/Admin/QuanLyBienThe/HinhDangGong.jsp").forward(request, response);
    }

    private void showAddHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("hinhDangGong", new HinhDangGong());
        request.getRequestDispatcher("/Admin/QuanLyBienThe/HinhDangGong.jsp").forward(request, response);
    }

    private void showEditHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            HinhDangGong hinhDangGong = lookupService.layHinhDangGongTheoId(id);
            if (hinhDangGong == null) {
                response.sendRedirect(request.getContextPath() + "/HinhDangGong");
                return;
            }
            request.setAttribute("hinhDangGong", hinhDangGong);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/HinhDangGong.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/HinhDangGong");
        }
    }

    private void insertHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HinhDangGong hinhDangGong = getHinhDangGongFromRequest(request);
        try {
            lookupService.themHinhDangGong(hinhDangGong);
            response.sendRedirect(request.getContextPath() + "/HinhDangGong");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("hinhDangGong", hinhDangGong);
            showHinhDangGong(request, response);
        }
    }

    private void updateHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HinhDangGong hinhDangGong = getHinhDangGongFromRequest(request);
        try {
            String idStr = request.getParameter("id");
            hinhDangGong.setId(Integer.parseInt(idStr));
            lookupService.capNhatHinhDangGong(hinhDangGong);
            response.sendRedirect(request.getContextPath() + "/HinhDangGong");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/HinhDangGong");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("hinhDangGong", hinhDangGong);
            showHinhDangGong(request, response);
        }
    }

    private void deleteHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            lookupService.xoaHinhDangGong(id);
            response.sendRedirect(request.getContextPath() + "/HinhDangGong");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/HinhDangGong");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Không thể xóa hình dáng gọng này vì có sản phẩm đang sử dụng.");
            showHinhDangGong(request, response);
        }
    }

    private HinhDangGong getHinhDangGongFromRequest(HttpServletRequest request) {
        String hinhDang = request.getParameter("hinhDang");
        HinhDangGong hinhDangGong = new HinhDangGong();
        hinhDangGong.setHinhDang(hinhDang);
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                hinhDangGong.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                // Ignore if ID is not a valid number
            }
        }
        
        return hinhDangGong;
    }
}
