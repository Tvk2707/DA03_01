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
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/HinhDangGong":
                ShowHinhDangGong(request, response);
                break;
            case "/HinhDangGong/new":
                showAddHinhDangGong(request, response);
                break;
            case "/HinhDangGong/edit":
                showEditHinhDangGong(request, response);
                break;
            case "/HinhDangGong/delete":
                deleteHinhDangGong(request, response);
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
        }
    }

    private void ShowHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<HinhDangGong> items = lookupService.layTatCaHinhDangGong();
        request.setAttribute("items", items);
        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/HinhDangGong.jsp").forward(request, response);
    }

    private void showAddHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/HinhDangGong.jsp").forward(request, response);
    }

    private void showEditHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        HinhDangGong hinhDangGong = lookupService.layHinhDangGongTheoId(id);
        request.setAttribute("hinhDangGong", hinhDangGong);
        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/HinhDangGong.jsp").forward(request, response);
    }

    private void insertHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HinhDangGong hinhDangGong = getHinhDangGongFron(request);
        try {
            lookupService.themHinhDangGong(hinhDangGong);
            response.sendRedirect(request.getContextPath() + "/HinhDangGong");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("hinhDangGong", hinhDangGong);
            request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/HinhDangGong.jsp").forward(request, response);
        }
    }

    private void updateHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HinhDangGong hinhDangGong = getHinhDangGongFron(request);
        hinhDangGong.setId(Integer.parseInt(request.getParameter("id")));
        try {
            lookupService.capNhatHinhDangGong(hinhDangGong);
            response.sendRedirect(request.getContextPath() + "/HinhDangGong");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("hinhDangGong", hinhDangGong);
            request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/HinhDangGong.jsp").forward(request, response);
        }
    }

    private void deleteHinhDangGong(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        lookupService.xoaHinhDangGong(id);
        response.sendRedirect(request.getContextPath() + "/HinhDangGong");
    }

    private HinhDangGong getHinhDangGongFron(HttpServletRequest request) {
        String hinhDang = request.getParameter("hinhDang");
        HinhDangGong hinhDangGong = new HinhDangGong();
        hinhDangGong.setHinhDang(hinhDang);
        return hinhDangGong;
    }
}
