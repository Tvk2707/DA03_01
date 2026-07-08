package BE.controller;

import BE.Entity.KieuQuaiKinh;
import BE.service.LookupService;
import BE.service.impl.LookupServiceImpl;
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
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/KieuQuaiKinh":
                ShowKieuQuaiKinh(request, response);
                break;
            case "/KieuQuaiKinh/new":
                showAddKieuQuaiKinh(request, response);
                break;
            case "/KieuQuaiKinh/edit":
                showEditKieuQuaiKinh(request, response);
                break;
            case "/KieuQuaiKinh/delete":
                deleteKieuQuaiKinh(request, response);
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
        }
    }

    private void ShowKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<KieuQuaiKinh> items = lookupService.layTatCaKieuQuaiKinh();
        request.setAttribute("items", items);
        request.getRequestDispatcher("/view/kieuquaikinh/List.jsp").forward(request, response);
    }

    private void showAddKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/view/kieuquaikinh/Add.jsp").forward(request, response);
    }

    private void showEditKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        KieuQuaiKinh kieuQuaiKinh = lookupService.layKieuQuaiKinhTheoId(id);
        request.setAttribute("kieuQuaiKinh", kieuQuaiKinh);
        request.getRequestDispatcher("/view/kieuquaikinh/Edit.jsp").forward(request, response);
    }

    private void insertKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KieuQuaiKinh kieuQuaiKinh = getKieuQuaiKinhFron(request);
        try {
            lookupService.themKieuQuaiKinh(kieuQuaiKinh);
            response.sendRedirect(request.getContextPath() + "/KieuQuaiKinh");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kieuQuaiKinh", kieuQuaiKinh);
            request.getRequestDispatcher("/view/kieuquaikinh/Add.jsp").forward(request, response);
        }
    }

    private void updateKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        KieuQuaiKinh kieuQuaiKinh = getKieuQuaiKinhFron(request);
        kieuQuaiKinh.setId((long) Integer.parseInt(request.getParameter("id")));
        try {
            lookupService.capNhatKieuQuaiKinh(kieuQuaiKinh);
            response.sendRedirect(request.getContextPath() + "/KieuQuaiKinh");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("kieuQuaiKinh", kieuQuaiKinh);
            request.getRequestDispatcher("/view/kieuquaikinh/Edit.jsp").forward(request, response);
        }
    }

    private void deleteKieuQuaiKinh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        lookupService.xoaKieuQuaiKinh(id);
        response.sendRedirect(request.getContextPath() + "/KieuQuaiKinh");
    }

    private KieuQuaiKinh getKieuQuaiKinhFron(HttpServletRequest request) {
        String tenKieuQuaiKinh = request.getParameter("tenKieuQuaiKinh");
        KieuQuaiKinh kieuQuaiKinh = new KieuQuaiKinh();
        kieuQuaiKinh.setKieuQuai(tenKieuQuaiKinh);
        return kieuQuaiKinh;
    }
}