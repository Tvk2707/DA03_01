package BE.controller;

import BE.Entity.GongKinh;
import BE.service.LookupService;
import BE.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "GongKinhServlet", value = {
        "/GongKinh",
        "/GongKinh/new",
        "/GongKinh/insert",
        "/GongKinh/edit",
        "/GongKinh/update",
        "/GongKinh/delete",
})
public class GongKinhServlet extends HttpServlet {
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/GongKinh":
                ShowGongKinh(request, response);
                break;
            case "/GongKinh/new":
                showAddGongKinh(request, response);
                break;
            case "/GongKinh/edit":
                showEditGongKinh(request, response);
                break;
            case "/GongKinh/delete":
                deleteGongKinh(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/GongKinh/insert":
                insertGongKinh(request, response);
                break;
            case "/GongKinh/update":
                updateGongKinh(request, response);
                break;
        }
    }

    private void ShowGongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<GongKinh> items = lookupService.layTatCaGongKinh();
        request.setAttribute("items", items);
        request.getRequestDispatcher("/view/gongkinh/List.jsp").forward(request, response);
    }

    private void showAddGongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/view/gongkinh/Add.jsp").forward(request, response);
    }

    private void showEditGongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        GongKinh gongKinh = lookupService.layGongKinhTheoId(id);
        request.setAttribute("gongKinh", gongKinh);
        request.getRequestDispatcher("/view/gongkinh/Edit.jsp").forward(request, response);
    }

    private void insertGongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        GongKinh gongKinh = getGongKinhFron(request);
        try {
            lookupService.themGongKinh(gongKinh);
            response.sendRedirect(request.getContextPath() + "/GongKinh");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("gongKinh", gongKinh);
            request.getRequestDispatcher("/view/gongkinh/Add.jsp").forward(request, response);
        }
    }

    private void updateGongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        GongKinh gongKinh = getGongKinhFron(request);
        gongKinh.setId((Integer ) Integer.parseInt(request.getParameter("id")));
        try {
            lookupService.capNhatGongKinh(gongKinh);
            response.sendRedirect(request.getContextPath() + "/GongKinh");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("gongKinh", gongKinh);
            request.getRequestDispatcher("/view/gongkinh/Edit.jsp").forward(request, response);
        }
    }

    private void deleteGongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        lookupService.xoaGongKinh(id);
        response.sendRedirect(request.getContextPath() + "/GongKinh");
    }

    /**
     * Lưu ý: GongKinh không validate tên trong service (chỉ check null),
     * cần chỉnh sửa các trường tương ứng theo entity thực tế của bạn.
     */
    private GongKinh getGongKinhFron(HttpServletRequest request) {
        int tenGongKinh = Integer.parseInt(request.getParameter("tenGongKinh"));
        GongKinh gongKinh = new GongKinh();
        gongKinh.setId((Integer ) tenGongKinh);
        return gongKinh;
    }
}