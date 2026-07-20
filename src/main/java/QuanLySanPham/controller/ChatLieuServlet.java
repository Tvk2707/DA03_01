package QuanLySanPham.controller;

import QuanLySanPham.Entity.ChatLieu;
import QuanLySanPham.Utils.ValidationException;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    private final LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/ChatLieu":
                showChatLieu(request, response);
                break;
            case "/ChatLieu/new":
                showAddChatLieu(request, response);
                break;
            case "/ChatLieu/edit":
                showEditChatLieu(request, response);
                break;
            default:
                showChatLieu(request, response);
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
            default:
                showChatLieu(request, response);
                break;
        }
    }

    private void showChatLieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<ChatLieu> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemChatLieu(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaChatLieu();
        }

        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");
        request.setAttribute("activeSubMenu", "material");
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
    }

    private void showAddChatLieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("chatLieu", new ChatLieu());
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
    }

    private void showEditChatLieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            ChatLieu chatLieu = lookupService.layChatLieuTheoId(id);
            if (chatLieu == null) {
                response.sendRedirect(request.getContextPath() + "/ChatLieu");
                return;
            }
            request.setAttribute("chatLieu", chatLieu);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ChatLieu");
        }
    }

    private void insertChatLieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ChatLieu chatLieu = getChatLieuFromRequest(request);
        try {
            lookupService.themChatLieu(chatLieu);
            response.sendRedirect(request.getContextPath() + "/ChatLieu");
        } catch (ValidationException e) {
            request.setAttribute("errors", e.getErrors());
            request.setAttribute("chatLieu", chatLieu);
            showChatLieu(request, response);
        }
    }

    private void updateChatLieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ChatLieu chatLieu = getChatLieuFromRequest(request);
        try {
            String idStr = request.getParameter("id");
            chatLieu.setId(Integer.parseInt(idStr));
            lookupService.capNhatChatLieu(chatLieu);
            response.sendRedirect(request.getContextPath() + "/ChatLieu");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ChatLieu");
        } catch (ValidationException e) {
            request.setAttribute("errors", e.getErrors());
            request.setAttribute("chatLieu", chatLieu);
            showChatLieu(request, response);
        }
    }

    private void deleteChatLieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            lookupService.xoaChatLieu(id);
            response.sendRedirect(request.getContextPath() + "/ChatLieu");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ChatLieu");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Không thể xóa chất liệu này vì có sản phẩm đang sử dụng.");
            showChatLieu(request, response);
        }
    }

    private ChatLieu getChatLieuFromRequest(HttpServletRequest request) {
        String tenChatLieu = request.getParameter("tenChatLieu");
        String trangThaiStr = request.getParameter("trangthai");

        ChatLieu chatLieu = new ChatLieu();
        chatLieu.setTenChatLieu(tenChatLieu);

        try {
            if (trangThaiStr != null && !trangThaiStr.isEmpty()) {
                chatLieu.setTrangThai(Integer.parseInt(trangThaiStr));
            }
        } catch (NumberFormatException e) {
            // Handle invalid status format if necessary
        }
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                chatLieu.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                // Ignore if ID is not a valid number
            }
        }

        return chatLieu;
    }
}
