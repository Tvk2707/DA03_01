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
        // List<ThuongHieu> items = lookupService.layTatCaThuongHieu();
        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");    // Giữ menu cha mở và sáng lên
        request.setAttribute("activeSubMenu", "category");
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
    }

    private void showAddThuongHieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
    }

    private void showEditThuongHieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        ThuongHieu thuongHieu = lookupService.layThuongHieuTheoId(id);
        request.setAttribute("thuongHieu", thuongHieu);
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
    }

    private void insertThuongHieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ThuongHieu thuongHieu = getThuongHieuFron(request);
        try {
            lookupService.themThuongHieu(thuongHieu);
            response.sendRedirect(request.getContextPath() + "/ThuongHieu");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("thuongHieu", thuongHieu);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
        }
    }

    private void updateThuongHieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ThuongHieu thuongHieu = getThuongHieuFron(request);
        thuongHieu.setId(Integer.parseInt(request.getParameter("id")));
        try {
            lookupService.capNhatThuongHieu(thuongHieu);
            response.sendRedirect(request.getContextPath() + "/ThuongHieu");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("thuongHieu", thuongHieu);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
        }
    }

    private void deleteThuongHieu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        lookupService.xoaThuongHieu(id);
        response.sendRedirect(request.getContextPath() + "/ThuongHieu");
    }

    private ThuongHieu getThuongHieuFron(HttpServletRequest request) {
        String maThuonHieu = request.getParameter("maThuongHieu");
        String tenThuongHieu = request.getParameter("tenThuongHieu");
        Integer trangthai = Integer.parseInt(request.getParameter("trangthai"));
        ThuongHieu thuongHieu = new ThuongHieu();
        thuongHieu.setMaThuongHieu(maThuonHieu);
        thuongHieu.setTenThuongHieu(tenThuongHieu);
        thuongHieu.setTrangThai(trangthai);
        return thuongHieu;
    }
}