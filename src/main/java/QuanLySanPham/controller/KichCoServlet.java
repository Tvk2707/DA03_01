package BE.controller;

import BE.Entity.KichCo;
import BE.service.LookupService;
import BE.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

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
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/KichCo":
                ShowKichCo(request, response);
                break;
            case "/KichCo/new":
                showAddKichCo(request, response);
                break;
            case "/KichCo/edit":
                showEditKichCo(request, response);
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
        }
    }

    private void ShowKichCo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<KichCo> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemKichCo(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaKichCo();
        }
        //List<KichCo> items = lookupService.layTatCaKichCo();
        request.setAttribute("items", items);
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KichCo.jsp").forward(request, response);
    }

    private void showAddKichCo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KichCo.jsp").forward(request, response);
    }

    private void showEditKichCo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        KichCo kichCo = lookupService.layKichCoTheoId(id);
        request.setAttribute("kichCo", kichCo);
        request.getRequestDispatcher("/Admin/QuanLyBienThe/KichCo.jsp").forward(request, response);
    }

    private void insertKichCo(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KichCo kichCo = getKichCoFron(request);
        try {
            lookupService.themKichCo(kichCo);
            response.sendRedirect(request.getContextPath() + "/KichCo");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kichCo", kichCo);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/KichCo.jsp").forward(request, response);
        }
    }

    private void updateKichCo(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KichCo kichCo = getKichCoFron(request);
        kichCo.setId(Integer.parseInt(request.getParameter("id")));
        try {
            lookupService.capNhatKichCo(kichCo);
            response.sendRedirect(request.getContextPath() + "/KichCo");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kichCo", kichCo);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/KichCo.jsp").forward(request, response);
        }
    }

    private void deleteKichCo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        lookupService.xoaKichCo(id);
        response.sendRedirect(request.getContextPath() + "/KichCo");
    }

    private KichCo getKichCoFron(HttpServletRequest request) {
        String tenKichCo = request.getParameter("tenKichCo");
        KichCo kichCo = new KichCo();
        kichCo.setTenKichCo(tenKichCo);
        return kichCo;
    }
}