package BE.controller;

import BE.Entity.*;
import BE.service.SanPhamService;
import BE.service.LookupService;
import BE.service.impl.SanPhamServiceImpl;
import BE.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

// Thêm các thư viện Apache POI và IO cần thiết để xuất Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.OutputStream;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet(name = "SanPhamServlet", value = {
        "/SanPham",
        "/SanPham/new",
        "/SanPham/insert",
        "/SanPham/edit",
        "/SanPham/update",
        "/SanPham/delete",
        "/SanPham/search",
        "/SanPham/export" // <-- BƯỚC 1: Thêm đường dẫn export vào đây
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
            case "/SanPham/export": // <-- BƯỚC 2: Điều hướng hành động xuất Excel
                exportExcel(request, response);
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

    // <-- BƯỚC 3: Viết hàm xử lý xuất Excel ngay bên dưới
    private void exportExcel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy các tham số bộ lọc tương tự như hàm searchSanPham để xuất đúng dữ liệu đang tìm kiếm
        String tenSanPham = request.getParameter("tenSanPham");
        String danhMucIdStr = request.getParameter("danhMucId");
        String thuongHieuIdStr = request.getParameter("thuongHieuId");

        Integer danhMucId = (danhMucIdStr != null && !danhMucIdStr.isEmpty()) ? Integer.parseInt(danhMucIdStr) : null;
        Integer thuongHieuId = (thuongHieuIdStr != null && !thuongHieuIdStr.isEmpty()) ? Integer.parseInt(thuongHieuIdStr) : null;

        // Gọi service lấy danh sách sản phẩm theo bộ lọc (nếu các ô lọc trống, hàm này sẽ tự trả về tất cả sản phẩm)
        List<SanPham> items = sanPhamService.timKiem(tenSanPham, danhMucId, thuongHieuId);

        // Thiết lập Response Header báo cho trình duyệt biết đây là file Excel .xlsx
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Danh_Sach_San_Pham.xlsx");

        // Tiến hành tạo file Excel bằng Apache POI
        try (Workbook workbook = new XSSFWorkbook();
             OutputStream out = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("Sản Phẩm");

            // 1. Tạo dòng tiêu đề (Header)
            Row headerRow = sheet.createRow(0);
            String[] columns = {"STT", "Mã SP", "Tên sản phẩm", "Danh mục", "Thương hiệu", "Chất liệu", "Kiểu dáng", "Trạng thái"};

            // Thiết lập font chữ in đậm cho tiêu đề
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // 2. Đổ dữ liệu từ danh sách `items` vào các dòng tiếp theo
            int rowIdx = 1;
            for (SanPham sp : items) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(rowIdx - 1); // Số thứ tự
                row.createCell(1).setCellValue(sp.getMaSanPham() != null ? sp.getMaSanPham() : "");
                row.createCell(2).setCellValue(sp.getTenSanPham() != null ? sp.getTenSanPham() : "");
                row.createCell(3).setCellValue(sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : ""); // Giả định thực tế từ Entity của bạn
                row.createCell(4).setCellValue(sp.getThuongHieu() != null ? sp.getThuongHieu().getTenThuongHieu() : "");
                row.createCell(5).setCellValue(sp.getChatLieu() != null ? sp.getChatLieu().getTenChatLieu() : "");
                row.createCell(6).setCellValue(sp.getKieuDang() != null ? sp.getKieuDang().getTenKieuDang() : "");

                // Trạng thái (1: Hoạt động, 0: Ngừng bán chẳng hạn)
                String trangThaiText = (sp.getTrangThai() == 1) ? "Hoạt động" : "Ngừng bán";
                row.createCell(7).setCellValue(trangThaiText);
            }

            // Tự động căn chỉnh độ rộng các cột cho vừa vặn chữ
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi dữ liệu luồng ra trình duyệt để tự động tải về
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ShowSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageStr = request.getParameter("page");
        int page = (pageStr != null && !pageStr.isEmpty()) ? Integer.parseInt(pageStr) : 1;
        int pageSize = 10;

        List<SanPham> items = sanPhamService.layCoPhanTrang(page, pageSize);
        long totalCount = sanPhamService.timKiem("", null, null).size();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        setLookupAttributes(request);

        request.setAttribute("items", items);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);

        request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPham.jsp").forward(request, response);
    }

    private void showAddSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setLookupAttributes(request);
        request.setAttribute("action", "add");
        request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPham.jsp").forward(request, response);
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
        request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPham.jsp").forward(request, response);
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
            request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPham.jsp").forward(request, response);
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
            request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPham.jsp").forward(request, response);
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

        request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPham.jsp").forward(request, response);
    }

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
        String ngayTaoStr = request.getParameter("ngayTao");
        String ngaySuaStr = request.getParameter("ngaySua");

        SanPham sanPham = new SanPham();
        sanPham.setTenSanPham(tenSanPham);
        sanPham.setMaSanPham(maSanPham);
        sanPham.setMoTaChiTiet(moTaChiTiet);
        sanPham.setTrangThai((trangThaiStr != null && !trangThaiStr.isEmpty()) ? Integer.parseInt(trangThaiStr) : 1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        if (ngayTaoStr != null && !ngayTaoStr.isEmpty()) {
            try {
                sanPham.setNgayTao(LocalDateTime.parse(ngayTaoStr, formatter));
            } catch (DateTimeParseException e) {
                sanPham.setNgayTao(LocalDateTime.now());
            }
        }
        if (ngaySuaStr != null && !ngaySuaStr.isEmpty()) {
            try {
                sanPham.setNgaySua(LocalDateTime.parse(ngaySuaStr, formatter));
            } catch (DateTimeParseException e) {
                sanPham.setNgaySua(LocalDateTime.now());
            }
        }

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
            gongKinh.setId(Integer.parseInt(gongKinhIdStr));
            sanPham.setGongKinh(gongKinh);
        }
        if (trongKinhIdStr != null && !trongKinhIdStr.isEmpty()) {
            TrongKinh trongKinh = new TrongKinh();
            trongKinh.setId(Integer.parseInt(trongKinhIdStr));
            sanPham.setTrongKinh(trongKinh);
        }

        return sanPham;
    }

    private void setLookupAttributes(HttpServletRequest request) {
        request.setAttribute("danhMucList", lookupService.layTatCaDanhMuc());
        request.setAttribute("thuongHieuList", lookupService.layTatCaThuongHieu());
        request.setAttribute("chatLieuList", lookupService.layTatCaChatLieu());
        request.setAttribute("kieuDangList", lookupService.layTatCaKieuDang());
        request.setAttribute("gongKinhList", lookupService.layTatCaGongKinh());
        request.setAttribute("trongKinhList", lookupService.layTatCaTrongKinh());
    }
}