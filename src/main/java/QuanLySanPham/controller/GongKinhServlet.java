package QuanLySanPham.controller;

import QuanLySanPham.Entity.GongKinh;
import QuanLySanPham.Entity.HinhDangGong;
import QuanLySanPham.Entity.KieuQuaiKinh;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
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
            case "/GongKinh/delete":
                deleteGongKinh(request, response);
                break;
        }
    }

    private void ShowGongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<GongKinh> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemGongKinh(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaGongKinh();
        }
        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "attribute");
        request.setAttribute("activeSubMenu", "frame");

        // Nạp danh sách cho 2 dropdown (form Thêm + form Sửa nằm chung trang này)
        napDanhSachDungChung(request);

        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/GongKinh.jsp").forward(request, response);
    }

    private void showAddGongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        napDanhSachDungChung(request);
        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/GongKinh.jsp").forward(request, response);
    }

    private void showEditGongKinh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        GongKinh gongKinh = lookupService.layGongKinhTheoId(id);
        request.setAttribute("gongKinh", gongKinh);
        napDanhSachDungChung(request);
        request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/GongKinh.jsp").forward(request, response);
    }

    private void insertGongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            GongKinh gongKinh = getGongKinhFron(request);
            lookupService.themGongKinh(gongKinh);
            response.sendRedirect(request.getContextPath() + "/GongKinh");
        } catch (Exception e) {
            // Bắt luôn cả NumberFormatException (chọn thiếu dropdown) lẫn RuntimeException từ service
            request.setAttribute("errorMessage", "Thêm gọng kính thất bại: " + e.getMessage());
            // Vẫn cần danh sách items để bảng không bị lỗi khi forward lại
            request.setAttribute("items", lookupService.layTatCaGongKinh());
            napDanhSachDungChung(request);
            request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/GongKinh.jsp").forward(request, response);
        }
    }

    private void updateGongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            GongKinh gongKinh = getGongKinhFron(request);
            gongKinh.setId(Integer.parseInt(request.getParameter("id")));
            lookupService.capNhatGongKinh(gongKinh);
            response.sendRedirect(request.getContextPath() + "/GongKinh");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Cập nhật gọng kính thất bại: " + e.getMessage());
            request.setAttribute("items", lookupService.layTatCaGongKinh());
            napDanhSachDungChung(request);
            request.getRequestDispatcher("/FE/Admin/QuanLyBienThe/GongKinh.jsp").forward(request, response);
        }
    }

    private void deleteGongKinh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        lookupService.xoaGongKinh(id);
        response.sendRedirect(request.getContextPath() + "/GongKinh");
    }

    /**
     * Nạp danh sách Hình dáng gọng + Kiểu quai kính dùng cho 2 dropdown
     * trong modal Thêm mới và modal Sửa (nằm chung trong GongKinh.jsp).
     */
    private void napDanhSachDungChung(HttpServletRequest request) {
        request.setAttribute("hinhDangGong", lookupService.layTatCaHinhDangGong());
        request.setAttribute("kieuQuaiKinh", lookupService.layTatCaKieuQuaiKinh());
    }

    /**
     * Đọc dữ liệu từ form (name="hinhDangGong", name="kieuQuaiKinh", name="trangThai")
     * và dựng lên đối tượng GongKinh tương ứng.
     */
    private GongKinh getGongKinhFron(HttpServletRequest request) {
        String hinhDangGongParam = request.getParameter("hinhDangGong");
        String kieuQuaiKinhParam = request.getParameter("kieuQuaiKinh");
        String trangThaiParam = request.getParameter("trangThai");

        if (hinhDangGongParam == null || hinhDangGongParam.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn hình dáng gọng");
        }
        if (kieuQuaiKinhParam == null || kieuQuaiKinhParam.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn kiểu quai kính");
        }
        if (trangThaiParam == null || trangThaiParam.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn trạng thái");
        }

        int hinhdanggong = Integer.parseInt(hinhDangGongParam);
        HinhDangGong hinhDangGong = lookupService.layHinhDangGongTheoId(hinhdanggong);
        if (hinhDangGong == null) {
            throw new RuntimeException("Hình dáng gọng không tồn tại");
        }

        int kieuquaikinh = Integer.parseInt(kieuQuaiKinhParam);
        KieuQuaiKinh kieuQuaiKinh = lookupService.layKieuQuaiKinhTheoId(kieuquaikinh);
        if (kieuQuaiKinh == null) {
            throw new RuntimeException("Kiểu quai kính không tồn tại");
        }

        int trangThai = Integer.parseInt(trangThaiParam);

        GongKinh gongKinh = new GongKinh();
        gongKinh.setHinhDangGong(hinhDangGong);
        gongKinh.setKieuQuaiKinh(kieuQuaiKinh);
        gongKinh.setTrangThai(trangThai);
        return gongKinh;
    }
}
