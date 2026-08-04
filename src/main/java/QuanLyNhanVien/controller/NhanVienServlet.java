package QuanLyNhanVien.controller;

import QuanLySanPham.Entity.NhanVien;
import QuanLyNhanVien.service.NhanVienService;
import QuanLyNhanVien.service.impl.NhanVienServiceImpl;
import QuanLySanPham.Utils.EmailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "NhanVienServlet", value = {"/NhanVien","/NhanVien/new","/NhanVien/insert","/NhanVien/edit","/NhanVien/update","/NhanVien/delete","/NhanVien/search","/NhanVien/export"})
public class NhanVienServlet extends HttpServlet {
    private final NhanVienService nhanVienService = new NhanVienServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/NhanVien":
                showList(request, response);
                break;
            case "/NhanVien/new":
                showAdd(request, response);
                break;
            case "/NhanVien/edit":
                showEdit(request, response);
                break;
            case "/NhanVien/export":
                exportExcel(request, response);
                break;
            default:
                showList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        switch (path) {
            case "/NhanVien/insert":
                insert(request, response);
                break;
            case "/NhanVien/update":
                update(request, response);
                break;
            case "/NhanVien/delete":
                delete(request, response);
                break;
            case "/NhanVien/search":
                search(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/NhanVien");
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageStr = request.getParameter("page");
        int page = (pageStr != null && !pageStr.isEmpty()) ? Integer.parseInt(pageStr) : 1;
        int pageSize = 10;
        List<NhanVien> items = nhanVienService.layCoPhanTrang(page, pageSize);
        long totalCount = nhanVienService.timKiem("").size();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // Set active menu for sidebar highlight
        request.setAttribute("activeMenu", "employee");
        request.setAttribute("items", items);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);
        request.getRequestDispatcher("/Admin/QuanLyNhanVien/QuanLyNhanVien.jsp").forward(request, response);
    }

    private void showAdd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set active menu for sidebar highlight
        request.setAttribute("activeMenu", "employee");
        request.setAttribute("action", "add");
        request.getRequestDispatcher("/Admin/QuanLyNhanVien/NhanVienAdd.jsp").forward(request, response);
    }

    private void showEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        NhanVien nv = nhanVienService.timTheoId(id);
        if (nv == null) {
            request.setAttribute("error", "Không tìm thấy nhân viên");
            showList(request, response);
            return;
        }
        // Set active menu for sidebar highlight
        request.setAttribute("activeMenu", "employee");
        request.setAttribute("nhanVien", nv);
        request.setAttribute("action", "edit");
        request.getRequestDispatcher("/Admin/QuanLyNhanVien/NhanVienEdit.jsp").forward(request, response);
    }

    private void insert(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            NhanVien nv = getNhanVienFrom(request);
            // 1. Lưu nhân viên vào Database
            nhanVienService.themNhanVien(nv);

            // 2. Gửi Email thông báo chạy ngầm cho nhân viên mới
            if (nv.getEmail() != null && !nv.getEmail().trim().isEmpty()) {
                new Thread(() -> {
                    String tieuDe = "Thông báo: Tài khoản nhân viên mới đã được khởi tạo";
                    String noiDung = "<h3>Xin chào " + nv.getHoTen() + ",</h3>"
                            + "<p>Tài khoản nhân viên của bạn đã được khởi tạo trên hệ thống quản lý.</p>"
                            + "<ul>"
                            + "  <li><b>Mã nhân viên:</b> " + nv.getMaNhanVien() + "</li>"
                            + "  <li><b>Email đăng nhập:</b> " + nv.getEmail() + "</li>"
                            + "  <li><b>Chức vụ:</b> " + (nv.getChucVu() != null ? nv.getChucVu() : "Chưa cập nhật") + "</li>"
                            + "</ul>"
                            + "<p>Vui lòng đăng nhập hệ thống hoặc liên hệ Quản lý để nhận mật khẩu làm việc.</p>";

                    EmailService.sendEmail(nv.getEmail(), tieuDe, noiDung);
                }).start();
            }

            response.sendRedirect(request.getContextPath() + "/NhanVien");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("nhanVien", getNhanVienFrom(request));
            request.setAttribute("action", "add");
            request.getRequestDispatcher("/Admin/QuanLyNhanVien/NhanVienAdd.jsp").forward(request, response);
        }
    }

    private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            NhanVien nv = getNhanVienFrom(request);
            nv.setId(Integer.parseInt(request.getParameter("id")));
            nhanVienService.capNhatNhanVien(nv);
            response.sendRedirect(request.getContextPath() + "/NhanVien");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("nhanVien", getNhanVienFrom(request));
            request.setAttribute("action", "edit");
            request.getRequestDispatcher("/Admin/QuanLyNhanVien/NhanVienEdit.jsp").forward(request, response);
        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            // Lấy tên nhân viên trước khi xóa để hiện toast
            NhanVien nv = nhanVienService.timTheoId(id);
            String hoTen = (nv != null) ? nv.getHoTen() : "Nhân viên";
            nhanVienService.xoaNhanVien(id);
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":true,\"hoTen\":\"" + hoTen + "\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/NhanVien");
            }
        } catch (Exception e) {
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
            } else {
                request.setAttribute("error", e.getMessage());
                showList(request, response);
            }
        }
    }

    private void search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tuKhoa = request.getParameter("tuKhoa");
        List<NhanVien> items = nhanVienService.timKiem(tuKhoa);
        request.setAttribute("items", items);
        request.setAttribute("tuKhoa", tuKhoa);
        request.setAttribute("activeMenu", "employee");
        request.getRequestDispatcher("/Admin/QuanLyNhanVien/QuanLyNhanVien.jsp").forward(request, response);
    }

    private NhanVien getNhanVienFrom(HttpServletRequest request) {
        String ma = request.getParameter("maNhanVien");
        String hoTen = request.getParameter("hoTen");
        String email = request.getParameter("email");
        String soDienThoai = request.getParameter("soDienThoai");
        String matKhau = request.getParameter("matKhau");
        String ngaySinhStr = request.getParameter("ngaySinh");
        String gioiTinhStr = request.getParameter("gioiTinh");
        String diaChi = request.getParameter("diaChi");
        String chucVu = request.getParameter("chucVu");
        String anh = request.getParameter("anhDaiDien");
        String trangThaiStr = request.getParameter("trangThai");

        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(ma);
        nv.setHoTen(hoTen);
        nv.setEmail(email);
        nv.setSoDienThoai(soDienThoai);
        nv.setMatKhau(matKhau);
        if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
            try {
                nv.setNgaySinh(java.time.LocalDate.parse(ngaySinhStr));
            } catch (Exception ignored) {}
        }
        nv.setGioiTinh((gioiTinhStr != null && !gioiTinhStr.isEmpty()) ? Integer.parseInt(gioiTinhStr) : 1);
        nv.setDiaChi(diaChi);
        nv.setChucVu(chucVu);
        nv.setAnhDaiDien(anh);
        nv.setTrangThai((trangThaiStr != null && !trangThaiStr.isEmpty()) ? Integer.parseInt(trangThaiStr) : 1);
        return nv;
    }

    // =========================================================
    // XUẤT EXCEL DANH SÁCH NHÂN VIÊN
    // =========================================================
    private void exportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<NhanVien> list = nhanVienService.timKiem(""); // Lấy toàn bộ NV đang hoạt động

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sach nhan vien");

            // ── Style: tiêu đề lớn ──────────────────────────────
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleFont.setColor(IndexedColors.WHITE.getIndex());
            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // ── Style: header cột ───────────────────────────────
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BROWN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // ── Style: dữ liệu thường ───────────────────────────
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setWrapText(true);

            // ── Style: dòng xen kẽ ──────────────────────────────
            CellStyle dataStyleAlt = workbook.createCellStyle();
            dataStyleAlt.cloneStyleFrom(dataStyle);
            dataStyleAlt.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
            dataStyleAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // ── Style: căn giữa ─────────────────────────────────
            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(dataStyle);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle centerStyleAlt = workbook.createCellStyle();
            centerStyleAlt.cloneStyleFrom(dataStyleAlt);
            centerStyleAlt.setAlignment(HorizontalAlignment.CENTER);

            // ── Row 0: Tiêu đề lớn ──────────────────────────────
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DANH SÁCH NHÂN VIÊN");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            // ── Row 1: Ngày xuất ────────────────────────────────
            Row dateRow = sheet.createRow(1);
            CellStyle dateStyle = workbook.createCellStyle();
            Font dateFont = workbook.createFont();
            dateFont.setItalic(true);
            dateFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dateStyle.setFont(dateFont);
            dateStyle.setAlignment(HorizontalAlignment.CENTER);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Ngày xuất: " +
                java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            dateCell.setCellStyle(dateStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

            // ── Row 2: trống ────────────────────────────────────
            sheet.createRow(2);

            // ── Row 3: Header cột ───────────────────────────────
            String[] headers = {"STT", "Mã NV", "Họ và tên", "Chức vụ",
                                 "Giới tính", "Ngày sinh", "Số điện thoại", "Email", "Địa chỉ"};
            Row headerRow = sheet.createRow(3);
            headerRow.setHeightInPoints(22);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ── Rows dữ liệu ────────────────────────────────────
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (int i = 0; i < list.size(); i++) {
                NhanVien nv = list.get(i);
                Row row = sheet.createRow(i + 4);
                row.setHeightInPoints(18);
                boolean isAlt = (i % 2 == 1);

                // STT
                Cell sttCell = row.createCell(0);
                sttCell.setCellValue(i + 1);
                sttCell.setCellStyle(isAlt ? centerStyleAlt : centerStyle);

                // Mã NV
                Cell maCell = row.createCell(1);
                maCell.setCellValue(nv.getMaNhanVien() != null ? nv.getMaNhanVien() : "");
                maCell.setCellStyle(isAlt ? centerStyleAlt : centerStyle);

                // Họ tên
                Cell tenCell = row.createCell(2);
                tenCell.setCellValue(nv.getHoTen() != null ? nv.getHoTen() : "");
                tenCell.setCellStyle(isAlt ? dataStyleAlt : dataStyle);

                // Chức vụ
                Cell chucVuCell = row.createCell(3);
                chucVuCell.setCellValue(nv.getChucVu() != null ? nv.getChucVu() : "");
                chucVuCell.setCellStyle(isAlt ? dataStyleAlt : dataStyle);

                // Giới tính
                Cell gioiTinhCell = row.createCell(4);
                gioiTinhCell.setCellValue(
                    nv.getGioiTinh() != null && nv.getGioiTinh() == 1 ? "Nam" : "Nữ");
                gioiTinhCell.setCellStyle(isAlt ? centerStyleAlt : centerStyle);

                // Ngày sinh
                Cell ngaySinhCell = row.createCell(5);
                ngaySinhCell.setCellValue(
                    nv.getNgaySinh() != null ? nv.getNgaySinh().format(dtf) : "");
                ngaySinhCell.setCellStyle(isAlt ? centerStyleAlt : centerStyle);

                // SĐT
                Cell sdtCell = row.createCell(6);
                sdtCell.setCellValue(nv.getSoDienThoai() != null ? nv.getSoDienThoai() : "");
                sdtCell.setCellStyle(isAlt ? centerStyleAlt : centerStyle);

                // Email
                Cell emailCell = row.createCell(7);
                emailCell.setCellValue(nv.getEmail() != null ? nv.getEmail() : "");
                emailCell.setCellStyle(isAlt ? dataStyleAlt : dataStyle);

                // Địa chỉ
                Cell diaChiCell = row.createCell(8);
                diaChiCell.setCellValue(nv.getDiaChi() != null ? nv.getDiaChi() : "");
                diaChiCell.setCellStyle(isAlt ? dataStyleAlt : dataStyle);
            }

            // ── Auto-size cột ───────────────────────────────────
            int[] colWidths = {8, 12, 25, 22, 12, 14, 16, 30, 22};
            for (int i = 0; i < colWidths.length; i++) {
                sheet.setColumnWidth(i, colWidths[i] * 256);
            }

            // ── Ghi response ────────────────────────────────────
            String fileName = "DanhSachNhanVien_" +
                java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + fileName);
            workbook.write(response.getOutputStream());
        }
    }
}