package QuanLySanPham.controller;


import QuanLySanPham.Entity.*;
import QuanLySanPham.service.SanPhamChiTietService;
import QuanLySanPham.service.SanPhamService;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.SanPhamChiTietServiceImpl;
import QuanLySanPham.service.impl.SanPhamServiceImpl;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

// Thư viện Apache POI để xuất Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Thư viện để xử lý upload file ảnh
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
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10,  // 10 MB
        maxRequestSize = 1024 * 1024 * 50 // 50 MB
)
public class SanPhamServlet extends HttpServlet {
    private SanPhamService sanPhamService = new SanPhamServiceImpl();
    private LookupService lookupService = new LookupServiceImpl();
    private SanPhamChiTietService sanPhamChiTietService = new SanPhamChiTietServiceImpl();

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
            request.setAttribute("error", "Tên sản phẩm không được để trống!");
            request.setAttribute("sanPham", sanPham);
            setLookupAttributes(request);
            request.setAttribute("action", "add");
            request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamAdd.jsp").forward(request, response);
            return;
        }

        sanPhamService.themSanPham(sanPham);

        SanPhamChiTiet sanPhamChiTiet = getSanPhamChiTietFrom(request, sanPham.getId());
        sanPhamChiTietService.themBienThe(sanPhamChiTiet);
        response.sendRedirect(request.getContextPath() + "/SanPham");
    }

    private void updateSanPham(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SanPham sanPham = getSanPhamFrom(request);

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("error", "Không tìm thấy ID sản phẩm để cập nhật!");
            showEditSanPham(request, response);
            return;
        }

        sanPham.setId(Integer.parseInt(idStr));
        sanPham.setNgaySua(LocalDateTime.now());

        if (sanPham.getTenSanPham() == null || sanPham.getTenSanPham().isEmpty()) {
            request.setAttribute("error", "Tên sản phẩm không được để trống!");
            request.setAttribute("sanPham", sanPham);
            setLookupAttributes(request);
            request.setAttribute("action", "edit");
            request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamEdit.jsp").forward(request, response);
            return;
        }

        // 1. Cập nhật bảng sản phẩm cha trước
        sanPhamService.capNhatSanPham(sanPham);

        // 2. Lấy dữ liệu biến thể chi tiết điền từ Form
        SanPhamChiTiet sanPhamChiTiet = getSanPhamChiTietFrom(request, sanPham.getId());

        // 3. Kiểm tra thông minh: Nếu chưa từng có biến thể cũ (hoặc đã bị xóa hết) -> Tự động chuyển hướng thêm mới
        String spctIdStr = request.getParameter("sanPhamChiTietId");
        if (spctIdStr != null && !spctIdStr.trim().isEmpty()) {
            sanPhamChiTietService.capNhatBienThe(sanPhamChiTiet);
        } else {
            sanPhamChiTietService.themBienThe(sanPhamChiTiet);
        }

        response.sendRedirect(request.getContextPath() + "/SanPham");
    }

    private void deleteSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        try {
            sanPhamService.xoaSanPham(id);
            response.sendRedirect(request.getContextPath() + "/SanPham");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi chi tiết: " + e.getMessage());
            request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPham.jsp").forward(request, response);
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

    private SanPhamChiTiet getSanPhamChiTietFrom(HttpServletRequest request, Integer sanPhamId) {
        SanPhamChiTiet sanPhamChiTiet = new SanPhamChiTiet();

        String spctIdStr = request.getParameter("sanPhamChiTietId");
        if (spctIdStr != null && !spctIdStr.isEmpty()) {
            sanPhamChiTiet.setId(Integer.parseInt(spctIdStr));
        }

        SanPham sanPham = new SanPham();
        sanPham.setId(sanPhamId);
        sanPhamChiTiet.setSanPham(sanPham);

        String ma = request.getParameter("ma");
        sanPhamChiTiet.setMa(ma);

        String mauSacIdStr = request.getParameter("mauSacId");
        if (mauSacIdStr != null && !mauSacIdStr.isEmpty()) {
            MauSac mauSac = new MauSac();
            mauSac.setId(Integer.parseInt(mauSacIdStr));
            sanPhamChiTiet.setMauSac(mauSac);
        }

        String kichCoIdStr = request.getParameter("kichCoId");
        if (kichCoIdStr != null && !kichCoIdStr.isEmpty()) {
            KichCo kichCo = new KichCo();
            kichCo.setId(Integer.parseInt(kichCoIdStr));
            sanPhamChiTiet.setKichCo(kichCo);
        }

        String giaNhapStr = request.getParameter("giaNhap");
        if (giaNhapStr != null && !giaNhapStr.isEmpty()) {
            sanPhamChiTiet.setGiaNhap(new BigDecimal(giaNhapStr));
        }

        String giaBanStr = request.getParameter("giaBan");
        if (giaBanStr != null && !giaBanStr.isEmpty()) {
            sanPhamChiTiet.setGiaBan(new BigDecimal(giaBanStr));
        }

        String soLuongTonStr = request.getParameter("soLuongTon");
        if (soLuongTonStr != null && !soLuongTonStr.isEmpty()) {
            sanPhamChiTiet.setSoLuongTon(Integer.parseInt(soLuongTonStr));
        }

        String trongLuongStr = request.getParameter("trongLuong");
        if (trongLuongStr != null && !trongLuongStr.isEmpty()) {
            sanPhamChiTiet.setTrongLuong(Integer.parseInt(trongLuongStr));
        }

        String trangThaiChiTietStr = request.getParameter("trangThaiChiTiet");
        if (trangThaiChiTietStr != null && !trangThaiChiTietStr.isEmpty()) {
            sanPhamChiTiet.setTrangThai(Integer.parseInt(trangThaiChiTietStr));
        } else {
            sanPhamChiTiet.setTrangThai(1);
        }

        String tenFileAnh = xuLyUploadAnh(request);
        if (tenFileAnh != null && !tenFileAnh.isEmpty()) {
            sanPhamChiTiet.setHinhAnh(tenFileAnh);
        } else {
            String hinhAnhCu = request.getParameter("hinhAnhCu");
            if (hinhAnhCu != null && !hinhAnhCu.isEmpty()) {
                sanPhamChiTiet.setHinhAnh(hinhAnhCu);
            }
        }

        return sanPhamChiTiet;
    }

    private String xuLyUploadAnh(HttpServletRequest request) {
        try {
            Part filePart = request.getPart("fileAnh");
            if (filePart == null || filePart.getSize() == 0) {
                return null;
            }

            String tenFileGoc = getFileName(filePart);
            if (tenFileGoc == null || tenFileGoc.isEmpty()) {
                return null;
            }

            String tenFileMoi = System.currentTimeMillis() + "_" + tenFileGoc;
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

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

    private String getFileName(Part part) {
        if (part.getSubmittedFileName() != null) {
            return part.getSubmittedFileName();
        }
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