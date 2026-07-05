package BE.controller;

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
            case "/KieuDang/delete":
                deleteKieuDang(request, response);
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
        }
    }

    private void ShowKieuDang(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<KieuDang> items = lookupService.layTatCaKieuDang();
        request.setAttribute("items", items);
        request.getRequestDispatcher("/view/kieudang/List.jsp").forward(request, response);
    }

    private void showAddKieuDang(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/view/kieudang/Add.jsp").forward(request, response);
    }

    private void showEditKieuDang(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        KieuDang kieuDang = lookupService.layKieuDangTheoId(id);
        request.setAttribute("kieuDang", kieuDang);
        request.getRequestDispatcher("/view/kieudang/Edit.jsp").forward(request, response);
    }

    private void insertKieuDang(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KieuDang kieuDang = getKieuDangFron(request);
        try {
            lookupService.themKieuDang(kieuDang);
            response.sendRedirect(request.getContextPath() + "/KieuDang");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kieuDang", kieuDang);
            request.getRequestDispatcher("/view/kieudang/Add.jsp").forward(request, response);
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
            request.getRequestDispatcher("/view/kieudang/Edit.jsp").forward(request, response);
        }
    }

    private void deleteKieuDang(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        lookupService.xoaKieuDang(id);
        response.sendRedirect(request.getContextPath() + "/KieuDang");
    }

    private KieuDang getKieuDangFron(HttpServletRequest request) {
        String tenKieuDang = request.getParameter("tenKieuDang");
        KieuDang kieuDang = new KieuDang();
        kieuDang.setTenKieuDang(tenKieuDang);
        return kieuDang;
    }
}