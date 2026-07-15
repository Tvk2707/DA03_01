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

        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");    // Giữ menu cha mở và sáng lên
        request.setAttribute("activeSubMenu", "category");
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
    }

    private void showAddChatLieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
    }

    private void showEditChatLieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            Integer id = Integer.parseInt(idStr.trim());
            ChatLieu chatLieu = lookupService.layChatLieuTheoId(id);
            request.setAttribute("chatLieu", chatLieu);
        }
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
    }

    private void insertChatLieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ChatLieu chatLieu = new ChatLieu();
        try {
            // Đưa hàm lấy dữ liệu vào trong try để bắt lỗi validate
            chatLieu = getChatLieuFron(request);
            lookupService.themChatLieu(chatLieu);
            response.sendRedirect(request.getContextPath() + "/ChatLieu");
        } catch (RuntimeException e) {
            // Giữ lại dữ liệu đang nhập dở
            chatLieu.setTenChatLieu(request.getParameter("tenChatLieu"));
            String trangthaiStr = request.getParameter("trangthai");
            if (trangthaiStr != null && !trangthaiStr.trim().isEmpty()) {
                try { chatLieu.setTrangThai(Integer.parseInt(trangthaiStr)); } catch (Exception ignored) {}
            }

            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("chatLieu", chatLieu);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
        }
    }

    private void updateChatLieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ChatLieu chatLieu = new ChatLieu();
        try {
            chatLieu = getChatLieuFron(request);

            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy ID chất liệu cần cập nhật!");
            }
            chatLieu.setId(Integer.parseInt(idStr.trim()));

            lookupService.capNhatChatLieu(chatLieu);
            response.sendRedirect(request.getContextPath() + "/ChatLieu");
        } catch (RuntimeException e) {
            // Giữ lại dữ liệu đang nhập dở
            chatLieu.setTenChatLieu(request.getParameter("tenChatLieu"));
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.trim().isEmpty()) {
                try { chatLieu.setId(Integer.parseInt(idStr)); } catch (Exception ignored) {}
            }
            String trangthaiStr = request.getParameter("trangthai");
            if (trangthaiStr != null && !trangthaiStr.trim().isEmpty()) {
                try { chatLieu.setTrangThai(Integer.parseInt(trangthaiStr)); } catch (Exception ignored) {}
            }

            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("chatLieu", chatLieu);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/ChatLieu.jsp").forward(request, response);
        }
    }

    private void deleteChatLieu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            Integer id = Integer.parseInt(idStr.trim());
            lookupService.xoaChatLieu(id);
        }
        response.sendRedirect(request.getContextPath() + "/ChatLieu");
    }

    private ChatLieu getChatLieuFron(HttpServletRequest request) {
        String tenChatLieu = request.getParameter("tenChatLieu");
        String trangthaiStr = request.getParameter("trangthai");

        // Validate chuỗi null, rỗng hoặc khoảng trắng
        if (tenChatLieu == null || tenChatLieu.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chất liệu không được để trống!");
        }
        if (trangthaiStr == null || trangthaiStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn trạng thái!");
        }

        ChatLieu chatLieu = new ChatLieu();
        chatLieu.setTenChatLieu(tenChatLieu.trim());

        // Validate kiểu số cho trạng thái và set vào Object (đã fix lỗi thiếu setTrangThai)
        try {
            chatLieu.setTrangThai(Integer.parseInt(trangthaiStr.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ!");
        }

        return chatLieu;
    }
}