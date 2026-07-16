package QuanLySanPham.controller;

import QuanLySanPham.Entity.ChatLieu;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ChatLieuServlet", value = {
        "/ChatLieu",
        "/ChatLieu/new",
        "/ChatLieu/insert",
        "/ChatLieu/edit",
        "/ChatLieu/update",
        "/ChatLieu/delete",
})
public class ChatLieuServlet extends HttpServlet {
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/ChatLieu":
                ShowChatLieu(request, response);
                break;
            case "/ChatLieu/new":
                showAddChatLieu(request, response);
                break;
            case "/ChatLieu/edit":
                showEditChatLieu(request, response);
                break;

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/ChatLieu/insert":
                insertChatLieu(request, response);
                break;
            case "/ChatLieu/update":
                updateChatLieu(request, response);
                break;
            case "/ChatLieu/delete":
                deleteChatLieu(request, response);
                break;
        }
    }

    private void ShowChatLieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<ChatLieu> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemChatLieu(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaChatLieu();
        }
        //List<ChatLieu> chatLieuItems = lookupService.layTatCaChatLieu();
        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "attribute");    // Giữ menu cha mở và sáng lên
        request.setAttribute("activeSubMenu", "material");
        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
    }

    private void showAddChatLieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
    }

    private void showEditChatLieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        ChatLieu chatLieu = lookupService.layChatLieuTheoId(id);
        request.setAttribute("chatLieu", chatLieu);
        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
    }

    private void insertChatLieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ChatLieu chatLieu = getChatLieuFron(request);
        try {
            lookupService.themChatLieu(chatLieu);
            response.sendRedirect(request.getContextPath() + "/ChatLieu");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("chatLieu", chatLieu);
            request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
        }
    }

    private void updateChatLieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ChatLieu chatLieu = getChatLieuFron(request);
        chatLieu.setId(Integer.parseInt(request.getParameter("id")));
        try {
            lookupService.capNhatChatLieu(chatLieu);
            response.sendRedirect(request.getContextPath() + "/ChatLieu");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("chatLieu", chatLieu);
            request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
        }
    }

    private void deleteChatLieu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        lookupService.xoaChatLieu(id);
        response.sendRedirect(request.getContextPath() + "/ChatLieu");
    }

    private ChatLieu getChatLieuFron(HttpServletRequest request) {
        String tenChatLieu = request.getParameter("tenChatLieu");
        Integer trangthai = Integer.parseInt(request.getParameter("trangthai"));
        ChatLieu chatLieu = new ChatLieu();
        chatLieu.setTenChatLieu(tenChatLieu);
        return chatLieu;
    }
}
