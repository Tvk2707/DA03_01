package BE.controller;

import BE.Entity.MauSac;
import BE.service.LookupService;
import BE.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MauSacServlet", value = {
        "/MauSac",
        "/MauSac/new",
        "/MauSac/insert",
        "/MauSac/edit",
        "/MauSac/update",
        "/MauSac/delete",
})
public class MauSacServlet extends HttpServlet {
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/MauSac":
                ShowMauSac(request, response);
                break;
            case "/MauSac/new":
                showAddMauSac(request, response);
                break;
            case "/MauSac/edit":
                showEditMauSac(request, response);
                break;
            case "/MauSac/delete":
                deleteMauSac(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/MauSac/insert":
                insertMauSac(request, response);
                break;
            case "/MauSac/update":
                updateMauSac(request, response);
                break;
        }
    }

    private void ShowMauSac(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MauSac> items = lookupService.layTatCaMauSac();
        request.setAttribute("items", items);
        request.getRequestDispatcher("/view/mausac/List.jsp").forward(request, response);
    }

    private void showAddMauSac(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/view/mausac/Add.jsp").forward(request, response);
    }

    private void showEditMauSac(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        MauSac mauSac = lookupService.layMauSacTheoId(id);
        request.setAttribute("mauSac", mauSac);
        request.getRequestDispatcher("/view/mausac/Edit.jsp").forward(request, response);
    }

    private void insertMauSac(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        MauSac mauSac = getMauSacFron(request);
        try {
            lookupService.themMauSac(mauSac);
            response.sendRedirect(request.getContextPath() + "/MauSac");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("mauSac", mauSac);
            request.getRequestDispatcher("/view/mausac/Add.jsp").forward(request, response);
        }
    }

    private void updateMauSac(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        MauSac mauSac = getMauSacFron(request);
        mauSac.setId(Integer.parseInt(request.getParameter("id")));
        try {
            lookupService.capNhatMauSac(mauSac);
            response.sendRedirect(request.getContextPath() + "/MauSac");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("mauSac", mauSac);
            request.getRequestDispatcher("/view/mausac/Edit.jsp").forward(request, response);
        }
    }

    private void deleteMauSac(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        lookupService.xoaMauSac(id);
        response.sendRedirect(request.getContextPath() + "/MauSac");
    }

    private MauSac getMauSacFron(HttpServletRequest request) {
        String tenMau = request.getParameter("tenMau");
        MauSac mauSac = new MauSac();
        mauSac.setTenMau(tenMau);
        return mauSac;
    }
}