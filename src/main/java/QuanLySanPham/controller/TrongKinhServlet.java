package QuanLySanPham.controller;

import QuanLySanPham.Entity.TrongKinh;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

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
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/TrongKinh":
                ShowTrongKinh(request, response);
                break;
            case "/TrongKinh/new":
                showAddTrongKinh(request, response);
                break;
            case "/TrongKinh/edit":
                showEditTrongKinh(request, response);
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
        }
    }

    private void ShowTrongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<TrongKinh> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemTrongKinh(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaTrongKinh();
        }
        // List<TrongKinh> items = lookupService.layTatCaTrongKinh();
        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "attribute");
        request.setAttribute("activeSubMenu", "lens");
        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/TrongKinh.jsp").forward(request, response);
    }

    private void showAddTrongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/TrongKinh.jsp").forward(request, response);
    }

    private void showEditTrongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        TrongKinh trongKinh = lookupService.layTrongKinhTheoId(id);
        request.setAttribute("trongKinh", trongKinh);
        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/TrongKinh.jsp").forward(request, response);
    }

    private void insertTrongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TrongKinh trongKinh = getTrongKinhFron(request);
        try {
            lookupService.themTrongKinh(trongKinh);
            response.sendRedirect(request.getContextPath() + "/TrongKinh");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("trongKinh", trongKinh);
            request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/TrongKinh.jsp").forward(request, response);
        }
    }

    private void updateTrongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TrongKinh trongKinh = getTrongKinhFron(request);
        trongKinh.setId(Integer.parseInt(request.getParameter("id")));
        try {
            lookupService.capNhatTrongKinh(trongKinh);
            response.sendRedirect(request.getContextPath() + "/TrongKinh");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("trongKinh", trongKinh);
            request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/TrongKinh.jsp").forward(request, response);
        }
    }

    private void deleteTrongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        lookupService.xoaTrongKinh(id);
        response.sendRedirect(request.getContextPath() + "/TrongKinh");
    }

    private TrongKinh getTrongKinhFron(HttpServletRequest request) {
        String loaiTrong = request.getParameter("loaiTrong");
        Integer trangthai = Integer.parseInt(request.getParameter("trangthai"));
        TrongKinh trongKinh = new TrongKinh();
        trongKinh.setLoaiTrong(loaiTrong);
        trongKinh.setTrangThai(trangthai);
        return trongKinh;
    }
}
