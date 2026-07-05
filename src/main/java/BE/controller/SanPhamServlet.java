package BE.controller;

import BE.Entity.*;
import BE.service.SanPhamService;
import BE.service.LookupService;
import BE.service.impl.SanPhamServiceImpl;
import BE.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "SanPhamServlet", value = {
        "/SanPham",
        "/SanPham/new",
        "/SanPham/insert",
        "/SanPham/edit",
        "/SanPham/update",
        "/SanPham/delete",
        "/SanPham/search",
})
public class SanPhamServlet extends HttpServlet {
    private SanPhamService sanPhamService = new SanPhamServiceImpl();
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/SanPham":
                ShowSanPham(request, response);
                break;
            case "/SanPham/new":
                showAddSanPham(request, response);
                break;
            case "/SanPham/edit":
                showEditSanPham(request, response);
                break;
            case "/SanPham/delete":
                deleteSanPham(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/SanPham/insert":
                insertSanPham(request, response);
                break;
            case "/SanPham/update":
                updateSanPham(request, response);
                break;
            case "/SanPham/search":
                searchSanPham(request, response);
                break;
        }
    }

    private void ShowSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageStr = request.getParameter("page");
        int page = (pageStr != null && !pageStr.isEmpty()) ? Integer.parseInt(pageStr) : 1;
        int pageSize = 10;

        List<SanPham> items = sanPhamService.layCoPhanTrang(page, pageSize);
        long totalCount = items.size();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        request.setAttribute("items", items);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);

        request.getRequestDispatcher("/WEB-INF/views/product/sanpham-list.jsp").forward(request, response);
    }

    private void showAddSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setLookupAttributes(request);
        request.setAttribute("action", "add");
        request.getRequestDispatcher("/WEB-INF/views/product/sanpham-form.jsp").forward(request, response);
    }

    private void showEditSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        SanPham sanPham = sanPhamService.timTheoId(id);

        if (sanPham == null) {
            request.setAttribute("error", "Không tìm thấy sản phẩm");
            ShowSanPham(request, response);
            return;
        }

        request.setAttribute("sanPham", sanPham);
        setLookupAttributes(request);
        request.setAttribute("action", "edit");
        request.getRequestDispatcher("/WEB-INF/views/product/sanpham-form.jsp").forward(request, response);
    }

    private void insertSanPham(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SanPham sanPham = getSanPhamFron(request);
        sanPham.setNgayTao(LocalDateTime.now());
        sanPham.setNgaySua(LocalDateTime.now());

        if (sanPham.getTenSanPham() == null || sanPham.getTenSanPham().isEmpty()) {
            request.setAttribute("errorMessage", "Tên sản phẩm không được để trống!");
            request.setAttribute("sanPham", sanPham);
            setLookupAttributes(request);
            request.setAttribute("action", "add");
            request.getRequestDispatcher("/WEB-INF/views/product/sanpham-form.jsp").forward(request, response);
            return;
        }

        sanPhamService.themSanPham(sanPham);
        response.sendRedirect(request.getContextPath() + "/SanPham");
    }

    private void updateSanPham(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SanPham sanPham = getSanPhamFron(request);
        sanPham.setId(Integer.parseInt(request.getParameter("id")));
        sanPham.setNgaySua(LocalDateTime.now());

        if (sanPham.getTenSanPham() == null || sanPham.getTenSanPham().isEmpty()) {
            request.setAttribute("errorMessage", "Tên sản phẩm không được để trống!");
            request.setAttribute("sanPham", sanPham);
            setLookupAttributes(request);
            request.setAttribute("action", "edit");
            request.getRequestDispatcher("/WEB-INF/views/product/sanpham-form.jsp").forward(request, response);
            return;
        }

        sanPhamService.capNhatSanPham(sanPham);
        response.sendRedirect(request.getContextPath() + "/SanPham");
    }

    private void deleteSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        try {
            sanPhamService.xoaSanPham(id);
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/SanPham");
    }

    private void searchSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tenSanPham = request.getParameter("tenSanPham");
        String danhMucIdStr = request.getParameter("danhMucId");
        String thuongHieuIdStr = request.getParameter("thuongHieuId");

        Integer danhMucId = (danhMucIdStr != null && !danhMucIdStr.isEmpty()) ? Integer.parseInt(danhMucIdStr) : null;
        Integer thuongHieuId = (thuongHieuIdStr != null && !thuongHieuIdStr.isEmpty()) ? Integer.parseInt(thuongHieuIdStr) : null;

        List<SanPham> items = sanPhamService.timKiem(tenSanPham, danhMucId, thuongHieuId);

        request.setAttribute("items", items);
        request.setAttribute("danhMucList", lookupService.layTatCaDanhMuc());
        request.setAttribute("thuongHieuList", lookupService.layTatCaThuongHieu());
        request.setAttribute("searchTenSanPham", tenSanPham);
        request.setAttribute("searchDanhMucId", danhMucId);
        request.setAttribute("searchThuongHieuId", thuongHieuId);

        request.getRequestDispatcher("/WEB-INF/views/product/sanpham-list.jsp").forward(request, response);
    }

    /**
     * Lấy dữ liệu SanPham từ request (dùng chung cho insert/update)
     */
    private SanPham getSanPhamFron(HttpServletRequest request) {
        String tenSanPham = request.getParameter("tenSanPham");
        String maSanPham = request.getParameter("maSanPham");
        String moTaChiTiet = request.getParameter("moTaChiTiet");
        String danhMucIdStr = request.getParameter("danhMucId");
        String thuongHieuIdStr = request.getParameter("thuongHieuId");
        String chatLieuIdStr = request.getParameter("chatLieuId");
        String kieuDangIdStr = request.getParameter("kieuDangId");
        String gongKinhIdStr = request.getParameter("gongKinhId");
        String trongKinhIdStr = request.getParameter("trongKinhId");
        String trangThaiStr = request.getParameter("trangThai");

        SanPham sanPham = new SanPham();
        sanPham.setTenSanPham(tenSanPham);
        sanPham.setMaSanPham(maSanPham);
        sanPham.setMoTaChiTiet(moTaChiTiet);
        sanPham.setTrangThai((trangThaiStr != null && !trangThaiStr.isEmpty()) ? Integer.parseInt(trangThaiStr) : 1);

        if (danhMucIdStr != null && !danhMucIdStr.isEmpty()) {
            DanhMuc danhMuc = new DanhMuc();
            danhMuc.setId(Integer.parseInt(danhMucIdStr));
            sanPham.setDanhMuc(danhMuc);
        }
        if (thuongHieuIdStr != null && !thuongHieuIdStr.isEmpty()) {
            ThuongHieu thuongHieu = new ThuongHieu();
            thuongHieu.setId(Integer.parseInt(thuongHieuIdStr));
            sanPham.setThuongHieu(thuongHieu);
        }
        if (chatLieuIdStr != null && !chatLieuIdStr.isEmpty()) {
            ChatLieu chatLieu = new ChatLieu();
            chatLieu.setId(Integer.parseInt(chatLieuIdStr));
            sanPham.setChatLieu(chatLieu);
        }
        if (kieuDangIdStr != null && !kieuDangIdStr.isEmpty()) {
            KieuDang kieuDang = new KieuDang();
            kieuDang.setId(Integer.parseInt(kieuDangIdStr));
            sanPham.setKieuDang(kieuDang);
        }
        if (gongKinhIdStr != null && !gongKinhIdStr.isEmpty()) {
            GongKinh gongKinh = new GongKinh();
            gongKinh.setId((Integer ) Integer.parseInt(gongKinhIdStr));
            sanPham.setGongKinh(gongKinh);
        }
        if (trongKinhIdStr != null && !trongKinhIdStr.isEmpty()) {
            TrongKinh trongKinh = new TrongKinh();
            trongKinh.setId(Integer.parseInt(trongKinhIdStr));
            sanPham.setTrongKinh(trongKinh);
        }

        return sanPham;
    }

    /**
     * Đổ dữ liệu dropdown cho form thêm/sửa
     */
    private void setLookupAttributes(HttpServletRequest request) {
        request.setAttribute("danhMucList", lookupService.layTatCaDanhMuc());
        request.setAttribute("thuongHieuList", lookupService.layTatCaThuongHieu());
        request.setAttribute("chatLieuList", lookupService.layTatCaChatLieu());
        request.setAttribute("kieuDangList", lookupService.layTatCaKieuDang());
        request.setAttribute("gongKinhList", lookupService.layTatCaGongKinh());
        request.setAttribute("trongKinhList", lookupService.layTatCaTrongKinh());
    }
}