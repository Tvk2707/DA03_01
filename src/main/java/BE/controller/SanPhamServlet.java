package BE.controller;

import BE.Entity.*;
import BE.service.SanPhamChiTietService;
import BE.service.SanPhamService;
import BE.service.LookupService;
import BE.service.impl.SanPhamChiTietServiceImpl;
import BE.service.impl.SanPhamServiceImpl;
import BE.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

// Thư viện Apache POI để xuất Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// [SỬA] Thêm thư viện để xử lý upload file ảnh
import jakarta.servlet.annotation.MultipartConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.io.IOException;
import java.math.BigDecimal;
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
        "/SanPham/export"
})
// [SỬA] Thêm annotation để hỗ trợ upload file
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10,  // 10 MB
        maxRequestSize = 1024 * 1024 * 50 // 50 MB
)
public class SanPhamServlet extends HttpServlet {
    private SanPhamService sanPhamService = new SanPhamServiceImpl();
    private LookupService lookupService = new LookupServiceImpl();
    private SanPhamChiTietService sanPhamChiTietService = new SanPhamChiTietServiceImpl();

    // [SỬA] Đường dẫn lưu ảnh - điều chỉnh theo project của bạn
    private static final String UPLOAD_DIR = "File_Anh/images";

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

            case "/SanPham/export":
                exportExcel(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // [SỬA] Set encoding UTF-8 để xử lý tiếng Việt đúng khi upload multipart
        request.setCharacterEncoding("UTF-8");

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
            case "/SanPham/delete":
                deleteSanPham(request, response);
                break;
        }
    }

    private void exportExcel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tenSanPham = request.getParameter("tenSanPham");
        String danhMucIdStr = request.getParameter("danhMucId");
        String thuongHieuIdStr = request.getParameter("thuongHieuId");

        Integer danhMucId = (danhMucIdStr != null && !danhMucIdStr.isEmpty()) ? Integer.parseInt(danhMucIdStr) : null;
        Integer thuongHieuId = (thuongHieuIdStr != null && !thuongHieuIdStr.isEmpty()) ? Integer.parseInt(thuongHieuIdStr) : null;

        List<SanPham> items = sanPhamService.timKiem(tenSanPham, danhMucId, thuongHieuId);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Danh_Sach_San_Pham.xlsx");

        try (Workbook workbook = new XSSFWorkbook();
             ServletOutputStream out = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("Sản Phẩm");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"STT", "Mã SP", "Tên sản phẩm", "Danh mục", "Thương hiệu", "Chất liệu", "Kiểu dáng", "Trạng thái"};

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            for (SanPham sp : items) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(sp.getMaSanPham() != null ? sp.getMaSanPham() : "");
                row.createCell(2).setCellValue(sp.getTenSanPham() != null ? sp.getTenSanPham() : "");
                row.createCell(3).setCellValue(sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : "");
                row.createCell(4).setCellValue(sp.getThuongHieu() != null ? sp.getThuongHieu().getTenThuongHieu() : "");
                row.createCell(5).setCellValue(sp.getChatLieu() != null ? sp.getChatLieu().getTenChatLieu() : "");
                row.createCell(6).setCellValue(sp.getKieuDang() != null ? sp.getKieuDang().getTenKieuDang() : "");

                String trangThaiText = (sp.getTrangThai() == 1) ? "Hoạt động" : "Ngừng bán";
                row.createCell(7).setCellValue(trangThaiText);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

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
        request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamAdd.jsp").forward(request, response);
    }

    private void showEditSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        SanPham sanPham = sanPhamService.timTheoId(id);
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietService.timBienTheTheoSanPhamId(id);

        if (sanPham == null) {
            request.setAttribute("error", "Không tìm thấy sản phẩm");
            ShowSanPham(request, response);
            return;
        }

        request.setAttribute("sanPham", sanPham);
        request.setAttribute("sanPhamChiTiet", sanPhamChiTiet);
        setLookupAttributes(request);
        request.setAttribute("action", "edit");
        request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamEdit.jsp").forward(request, response);
    }

    private void insertSanPham(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SanPham sanPham = getSanPhamFrom(request);
        sanPham.setNgayTao(LocalDateTime.now());
        sanPham.setNgaySua(LocalDateTime.now());

        if (sanPham.getTenSanPham() == null || sanPham.getTenSanPham().isEmpty()) {
            request.setAttribute("error", "Tên sản phẩm không được để trống!"); // [SỬA] đổi thành "error" để khớp JSP
            request.setAttribute("sanPham", sanPham);
            setLookupAttributes(request);
            request.setAttribute("action", "add");
            // [SỬA] Forward về đúng trang Add thay vì trang list
            request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamAdd.jsp").forward(request, response);
            return;
        }

        sanPhamService.themSanPham(sanPham);

        // [SỬA] Truyền thêm request để xử lý upload ảnh
        SanPhamChiTiet sanPhamChiTiet = getSanPhamChiTietFrom(request, sanPham.getId());
        sanPhamChiTietService.themBienThe(sanPhamChiTiet);
        response.sendRedirect(request.getContextPath() + "/SanPham");
    }

    private void updateSanPham(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SanPham sanPham = getSanPhamFrom(request);
        sanPham.setId(Integer.parseInt(request.getParameter("id")));
        sanPham.setNgaySua(LocalDateTime.now());

        if (sanPham.getTenSanPham() == null || sanPham.getTenSanPham().isEmpty()) {
            request.setAttribute("error", "Tên sản phẩm không được để trống!"); // [SỬA] đổi thành "error"
            request.setAttribute("sanPham", sanPham);
            setLookupAttributes(request);
            request.setAttribute("action", "edit");
            // [SỬA] Forward về đúng trang Edit thay vì trang list
            request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamEdit.jsp").forward(request, response);
            return;
        }

        sanPhamService.capNhatSanPham(sanPham);

        // [SỬA] Truyền thêm request để xử lý upload ảnh
        SanPhamChiTiet sanPhamChiTiet = getSanPhamChiTietFrom(request, sanPham.getId());
        sanPhamChiTietService.capNhatBienThe(sanPhamChiTiet);
        response.sendRedirect(request.getContextPath() + "/SanPham");
    }
    private void deleteSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        try {
            sanPhamService.xoaSanPham(id);
            // Chỉ redirect khi thành công
            response.sendRedirect(request.getContextPath() + "/SanPham");
        } catch (Exception e) {
            e.printStackTrace(); // 1. In lỗi ra Console của IDE để xem nguyên nhân gốc

            // 2. Forward thay vì redirect để giữ lại thông báo lỗi hiển thị lên JSP
            request.setAttribute("error", "Lỗi chi tiết: " + e.getMessage());
            request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPham.jsp").forward(request, response);
            // (Lưu ý: Thay đường dẫn JSP cho đúng với project của bạn)
        }
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

    // [SỬA] Đổi tên method cho đúng chính tả (Fron -> From)
    private SanPham getSanPhamFrom(HttpServletRequest request) {
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

    /**
     * [SỬA LỚN] Viết lại hoàn toàn hàm này để sửa các lỗi sau:
     *  1. Lỗi NullPointerException: sanPhamChiTiet.setId() được gọi TRƯỚC khi new object
     *  2. Lỗi đọc sai field: form gửi "trangThaiChiTiet" nhưng code đọc "trangThai"
     *  3. Thiếu xử lý upload file ảnh
     *  4. Thiếu xử lý ảnh cũ (hinhAnhCu) khi không upload ảnh mới
     */
    private SanPhamChiTiet getSanPhamChiTietFrom(HttpServletRequest request, Integer sanPhamId) {
        // [SỬA] 1. Khởi tạo object TRƯỚC khi sử dụng (trước đây gọi setId() khi chưa new)
        SanPhamChiTiet sanPhamChiTiet = new SanPhamChiTiet();

        // [SỬA] 2. Đọc ID biến thể (phải đặt SAU khi new object)
        String spctIdStr = request.getParameter("sanPhamChiTietId");
        if (spctIdStr != null && !spctIdStr.isEmpty()) {
            sanPhamChiTiet.setId(Integer.parseInt(spctIdStr));
        }

        // Set sản phẩm cha
        SanPham sanPham = new SanPham();
        sanPham.setId(sanPhamId);
        sanPhamChiTiet.setSanPham(sanPham);

        // Các field cơ bản
        String ma = request.getParameter("ma");
        sanPhamChiTiet.setMa(ma);

        // Màu sắc
        String mauSacIdStr = request.getParameter("mauSacId");
        if (mauSacIdStr != null && !mauSacIdStr.isEmpty()) {
            MauSac mauSac = new MauSac();
            mauSac.setId(Integer.parseInt(mauSacIdStr));
            sanPhamChiTiet.setMauSac(mauSac);
        }

        // Kích cỡ
        String kichCoIdStr = request.getParameter("kichCoId");
        if (kichCoIdStr != null && !kichCoIdStr.isEmpty()) {
            KichCo kichCo = new KichCo();
            kichCo.setId(Integer.parseInt(kichCoIdStr));
            sanPhamChiTiet.setKichCo(kichCo);
        }

        // Giá nhập
        String giaNhapStr = request.getParameter("giaNhap");
        if (giaNhapStr != null && !giaNhapStr.isEmpty()) {
            sanPhamChiTiet.setGiaNhap(new BigDecimal(giaNhapStr));
        }

        // Giá bán
        String giaBanStr = request.getParameter("giaBan");
        if (giaBanStr != null && !giaBanStr.isEmpty()) {
            sanPhamChiTiet.setGiaBan(new BigDecimal(giaBanStr));
        }

        // Số lượng tồn
        String soLuongTonStr = request.getParameter("soLuongTon");
        if (soLuongTonStr != null && !soLuongTonStr.isEmpty()) {
            sanPhamChiTiet.setSoLuongTon(Integer.parseInt(soLuongTonStr));
        }

        // Trọng lượng
        String trongLuongStr = request.getParameter("trongLuong");
        if (trongLuongStr != null && !trongLuongStr.isEmpty()) {
            sanPhamChiTiet.setTrongLuong(Integer.parseInt(trongLuongStr));
        }

        // [SỬA] 3. Đọc đúng field "trangThaiChiTiet" cho trạng thái biến thể
        // (Trước đây code đọc "trangThai" → bị nhầm với trạng thái của sản phẩm cha)
        String trangThaiChiTietStr = request.getParameter("trangThaiChiTiet");
        if (trangThaiChiTietStr != null && !trangThaiChiTietStr.isEmpty()) {
            sanPhamChiTiet.setTrangThai(Integer.parseInt(trangThaiChiTietStr));
        } else {
            sanPhamChiTiet.setTrangThai(1); // Mặc định: Hoạt động
        }

        // [SỬA] 4. Xử lý upload file ảnh
        String tenFileAnh = xuLyUploadAnh(request);

        if (tenFileAnh != null && !tenFileAnh.isEmpty()) {
            // Có upload ảnh mới → dùng ảnh mới
            sanPhamChiTiet.setHinhAnh(tenFileAnh);
        } else {
            // Không upload ảnh mới → giữ lại ảnh cũ (nếu có)
            String hinhAnhCu = request.getParameter("hinhAnhCu");
            if (hinhAnhCu != null && !hinhAnhCu.isEmpty()) {
                sanPhamChiTiet.setHinhAnh(hinhAnhCu);
            }
        }

        return sanPhamChiTiet;
    }

    /**
     * [SỬA] Hàm mới: Xử lý upload file ảnh từ form multipart/form-data
     * Trả về tên file đã lưu, hoặc null nếu không có file upload
     */
    private String xuLyUploadAnh(HttpServletRequest request) {
        try {
            Part filePart = request.getPart("fileAnh");

            // Kiểm tra có file được chọn không
            if (filePart == null || filePart.getSize() == 0) {
                return null;
            }

            // Lấy tên file gốc
            String tenFileGoc = getFileName(filePart);
            if (tenFileGoc == null || tenFileGoc.isEmpty()) {
                return null;
            }

            // Tạo tên file duy nhất để tránh trùng lặp
            String tenFileMoi = System.currentTimeMillis() + "_" + tenFileGoc;

            // Đường dẫn tuyệt đối đến thư mục upload
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Lưu file vào server
            String duongDanDayDu = uploadPath + File.separator + tenFileMoi;
            try (InputStream input = filePart.getInputStream();
                 FileOutputStream fos = new FileOutputStream(duongDanDayDu)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }

            return tenFileMoi;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * [SỬA] Hàm helper: Lấy tên file từ Part (do getPart().getSubmittedFileName()
     * có thể không hoạt động trên một số server cũ)
     */
    private String getFileName(Part part) {
        // Cách 1: Dùng method có sẵn (Servlet 3.1+)
        if (part.getSubmittedFileName() != null) {
            return part.getSubmittedFileName();
        }

        // Cách 2: Parse từ header Content-Disposition (fallback)
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp != null) {
            for (String content : contentDisp.split(";")) {
                if (content.trim().startsWith("filename")) {
                    return content.substring(content.indexOf("=") + 2, content.length() - 1);
                }
            }
        }
        return null;
    }

    private void setLookupAttributes(HttpServletRequest request) {
        request.setAttribute("danhMucList", lookupService.layTatCaDanhMuc());
        request.setAttribute("thuongHieuList", lookupService.layTatCaThuongHieu());
        request.setAttribute("chatLieuList", lookupService.layTatCaChatLieu());
        request.setAttribute("kieuDangList", lookupService.layTatCaKieuDang());
        request.setAttribute("gongKinhList", lookupService.layTatCaGongKinh());
        request.setAttribute("trongKinhList", lookupService.layTatCaTrongKinh());
        request.setAttribute("mauSacList", lookupService.layTatCaMauSac());
        request.setAttribute("kichCoList", lookupService.layTatCaKichCo());
    }
}