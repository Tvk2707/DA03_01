package BE.controller;

import BE.Entity.GongKinh;
import BE.Entity.KieuDang;
import BE.service.LookupService;
import BE.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

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
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/KieuDang":
                ShowKieuDang(request, response);
                break;
            case "/KieuDang/new":
                showAddKieuDang(request, response);
                break;
            case "/KieuDang/edit":
                showEditKieuDang(request, response);
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
        }
    }

    private void ShowKieuDang(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<KieuDang> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemKieuDang(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaKieuDang();
        }
        //  List<KieuDang> items = lookupService.layTatCaKieuDang();
        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");    // Giữ menu cha mở và sáng lên
        request.setAttribute("activeSubMenu", "category");
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KieuDang.jsp").forward(request, response);
    }

    private void showAddKieuDang(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KieuDang.jsp").forward(request, response);
    }

    private void showEditKieuDang(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        KieuDang kieuDang = lookupService.layKieuDangTheoId(id);
        request.setAttribute("kieuDang", kieuDang);
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KieuDang.jsp").forward(request, response);
    }

    private void insertKieuDang(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KieuDang kieuDang = getKieuDangFron(request);
        try {
            lookupService.themKieuDang(kieuDang);
            response.sendRedirect(request.getContextPath() + "/KieuDang");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kieuDang", kieuDang);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/KieuDang.jsp").forward(request, response);
        }
    }

    private void updateKieuDang(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KieuDang kieuDang = getKieuDangFron(request);
        kieuDang.setId(Integer.parseInt(request.getParameter("id")));
        try {
            lookupService.capNhatKieuDang(kieuDang);
            response.sendRedirect(request.getContextPath() + "/KieuDang");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kieuDang", kieuDang);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/KieuDang.jsp").forward(request, response);
        }
    }

    private void deleteKieuDang(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        lookupService.xoaKieuDang(id);
        response.sendRedirect(request.getContextPath() + "/KieuDang");
    }

    private KieuDang getKieuDangFron(HttpServletRequest request) {
        String tenKieuDang = request.getParameter("tenKieuDang");
        Integer trangThai = Integer.parseInt(request.getParameter("trangThai"));
        KieuDang kieuDang = new KieuDang();
        kieuDang.setTenKieuDang(tenKieuDang);
        kieuDang.setTrangThai(trangThai);
        return kieuDang;
    }
}