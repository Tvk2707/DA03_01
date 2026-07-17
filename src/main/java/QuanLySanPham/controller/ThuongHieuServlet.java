package QuanLySanPham.controller;

import QuanLySanPham.Entity.ThuongHieu;
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

@WebServlet(name = "ThuongHieuServlet", value = {
        "/ThuongHieu",
        "/ThuongHieu/new",
        "/ThuongHieu/insert",
        "/ThuongHieu/edit",
        "/ThuongHieu/update",
        "/ThuongHieu/delete",
})
public class ThuongHieuServlet extends HttpServlet {
    private final LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/ThuongHieu":
                showThuongHieu(request, response);
                break;
            case "/ThuongHieu/new":
                showAddThuongHieu(request, response);
                break;
            case "/ThuongHieu/edit":
                showEditThuongHieu(request, response);
                break;
            default:
                showThuongHieu(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/ThuongHieu/insert":
                insertThuongHieu(request, response);
                break;
            case "/ThuongHieu/update":
                updateThuongHieu(request, response);
                break;
            case "/ThuongHieu/delete":
                deleteThuongHieu(request, response);
                break;
            default:
                showThuongHieu(request, response);
                break;
        }
    }

    private void showThuongHieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<ThuongHieu> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lookupService.timKiemThuongHieu(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            items = lookupService.layTatCaThuongHieu();
        }

        request.setAttribute("items", items);
        request.setAttribute("activeMenu", "product");
        request.setAttribute("activeSubMenu", "brand");
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
    }

    private void showAddThuongHieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("thuongHieu", new ThuongHieu());
        request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
    }

    private void showEditThuongHieu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            ThuongHieu thuongHieu = lookupService.layThuongHieuTheoId(id);
            if (thuongHieu == null) {
                response.sendRedirect(request.getContextPath() + "/ThuongHieu");
                return;
            }
            request.setAttribute("thuongHieu", thuongHieu);
            request.getRequestDispatcher("/Admin/QuanLyBienThe/ThuongHieu.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ThuongHieu");
        }
    }

    private void insertThuongHieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ThuongHieu thuongHieu = getThuongHieuFromRequest(request);
        try {
            lookupService.themThuongHieu(thuongHieu);
            response.sendRedirect(request.getContextPath() + "/ThuongHieu");
        } catch (ValidationException e) {
            request.setAttribute("errors", e.getErrors());
            request.setAttribute("thuongHieu", thuongHieu);
            showThuongHieu(request, response);
        }
    }

    private void updateThuongHieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ThuongHieu thuongHieu = getThuongHieuFromRequest(request);
        try {
            String idStr = request.getParameter("id");
            thuongHieu.setId(Integer.parseInt(idStr));
            lookupService.capNhatThuongHieu(thuongHieu);
            response.sendRedirect(request.getContextPath() + "/ThuongHieu");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ThuongHieu");
        } catch (ValidationException e) {
            request.setAttribute("errors", e.getErrors());
            request.setAttribute("thuongHieu", thuongHieu);
            showThuongHieu(request, response);
        }
    }

    private void deleteThuongHieu(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            lookupService.xoaThuongHieu(id);
            response.sendRedirect(request.getContextPath() + "/ThuongHieu");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ThuongHieu");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Không thể xóa thương hiệu này vì có sản phẩm đang sử dụng.");
            showThuongHieu(request, response);
        }
    }

    private ThuongHieu getThuongHieuFromRequest(HttpServletRequest request) {
        String maThuongHieu = request.getParameter("maThuongHieu");
        String tenThuongHieu = request.getParameter("tenThuongHieu");
        String trangThaiStr = request.getParameter("trangthai");

        ThuongHieu thuongHieu = new ThuongHieu();
        thuongHieu.setMaThuongHieu(maThuongHieu);
        thuongHieu.setTenThuongHieu(tenThuongHieu);

        try {
            if (trangThaiStr != null && !trangThaiStr.isEmpty()) {
                thuongHieu.setTrangThai(Integer.parseInt(trangThaiStr));
            }
        } catch (NumberFormatException e) {
            // Handle invalid status format if necessary
        }
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                thuongHieu.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                // Ignore if ID is not a valid number
            }
        }

        return thuongHieu;
    }
}
