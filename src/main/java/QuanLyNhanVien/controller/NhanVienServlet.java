package QuanLyNhanVien.controller;

import QuanLySanPham.Entity.NhanVien;
import QuanLySanPham.controller.LoginServlet;
import QuanLyNhanVien.service.NhanVienService;
import QuanLyNhanVien.service.impl.NhanVienServiceImpl;
import QuanLySanPham.Utils.EmailService;
import QuanLySanPham.dao.NhanVienDao;
import QuanLySanPham.dao.impl.NhanVienDaoImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.JsonObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@WebServlet(name = "NhanVienServlet", value = {"/NhanVien","/NhanVien/new","/NhanVien/insert","/NhanVien/edit","/NhanVien/update","/NhanVien/delete","/NhanVien/search","/NhanVien/export","/NhanVien/role"})
public class NhanVienServlet extends HttpServlet {
    private static final String FLASH_SUCCESS_KEY = "nhanVienSuccessMessage";
    private final NhanVienService nhanVienService = new NhanVienServiceImpl();
    private final NhanVienDao nhanVienDao = new NhanVienDaoImpl();
    private static final List<String> CHUC_VU_HOP_LE = List.of(
            "Quản lý cửa hàng",
            "Nhân viên bán hàng",
            "Nhân viên thu ngân"
    );
    private static final Set<String> CHUC_VU_HOP_LE_SET = Set.copyOf(CHUC_VU_HOP_LE);

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
            case "/NhanVien/role":
                updateRole(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/NhanVien");
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        moveFlashMessageToRequest(request, FLASH_SUCCESS_KEY, "successMessage");

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
        request.setAttribute("chucVuHopLe", CHUC_VU_HOP_LE);

        // Tự sinh mã nhân viên
        String generatedMa = generateMaNhanVien();
        request.setAttribute("generatedMaNV", generatedMa);

        // Tự sinh mật khẩu ngẫu nhiên 8 ký tự
        String generatedPass = generatePassword(8);
        request.setAttribute("generatedMatKhau", generatedPass);

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
        request.setAttribute("chucVuHopLe", CHUC_VU_HOP_LE);
        request.getRequestDispatcher("/Admin/QuanLyNhanVien/NhanVienEdit.jsp").forward(request, response);
    }

    private void insert(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            NhanVien nv = getNhanVienFrom(request);
            validateChucVu(nv.getChucVu());
            String matKhauTam = nv.getMatKhau();
            // 1. Lưu nhân viên vào Database
            nv.setMaNhanVien(generateMaNhanVien());
            nv.setVaiTro(NhanVien.VAI_TRO_NHAN_VIEN);
            nhanVienService.themNhanVien(nv);

            // 2. Gửi Email thông báo chạy ngầm cho nhân viên mới (kèm mật khẩu)
            if (nv.getEmail() != null && !nv.getEmail().trim().isEmpty()) {
                final String matKhauGui = matKhauTam;
                new Thread(() -> {
                    String tieuDe = "Thông báo: Tài khoản nhân viên mới đã được khởi tạo";
                    String noiDung = "<h3>Xin chào " + nv.getHoTen() + ",</h3>"
                            + "<p>Tài khoản nhân viên của bạn đã được khởi tạo trên hệ thống quản lý.</p>"
                            + "<ul>"
                            + "  <li><b>Mã nhân viên:</b> " + nv.getMaNhanVien() + "</li>"
                            + "  <li><b>Email đăng nhập:</b> " + nv.getEmail() + "</li>"
                            + "  <li><b>Mật khẩu:</b> " + matKhauGui + "</li>"
                            + "  <li><b>Chức vụ:</b> " + (nv.getChucVu() != null ? nv.getChucVu() : "Chưa cập nhật") + "</li>"
                            + "</ul>"
                            + "<p style='color:#d32f2f;'><b>Lưu ý:</b> Vui lòng đổi mật khẩu sau lần đăng nhập đầu tiên để đảm bảo an toàn.</p>";

                    EmailService.sendEmail(nv.getEmail(), tieuDe, noiDung);
                }).start();
            }

            request.getSession().setAttribute(FLASH_SUCCESS_KEY, "Thêm nhân viên thành công.");
            response.sendRedirect(request.getContextPath() + "/NhanVien");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            NhanVien nhanVienForm = getNhanVienFrom(request);
            request.setAttribute("nhanVien", nhanVienForm);
            request.setAttribute("action", "add");
            request.setAttribute("chucVuHopLe", CHUC_VU_HOP_LE);
            request.setAttribute("generatedMaNV", nhanVienForm.getMaNhanVien());
            request.setAttribute("generatedMatKhau", nhanVienForm.getMatKhau());
            request.getRequestDispatcher("/Admin/QuanLyNhanVien/NhanVienAdd.jsp").forward(request, response);
        }
    }

    private void moveFlashMessageToRequest(HttpServletRequest request, String sessionKey, String requestKey) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        Object message = session.getAttribute(sessionKey);
        if (message != null) {
            request.setAttribute(requestKey, message);
            session.removeAttribute(sessionKey);
        }
    }

    private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            NhanVien nv = getNhanVienFrom(request);
            validateChucVu(nv.getChucVu());
            nv.setId(Integer.parseInt(request.getParameter("id")));
            NhanVien currentUser = getCurrentUser(request);
            if (currentUser != null && currentUser.getId().equals(nv.getId())
                    && nv.getTrangThai() != null && nv.getTrangThai() == 0) {
                throw new IllegalArgumentException("Bạn không thể tự vô hiệu hóa tài khoản của chính mình");
            }
            nhanVienService.capNhatNhanVien(nv);
            response.sendRedirect(request.getContextPath() + "/NhanVien");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            NhanVien nhanVienForm = getNhanVienFrom(request);
            String id = request.getParameter("id");
            if (id != null && !id.isBlank()) {
                nhanVienForm.setId(Integer.parseInt(id));
            }
            request.setAttribute("nhanVien", nhanVienForm);
            request.setAttribute("action", "edit");
            request.setAttribute("chucVuHopLe", CHUC_VU_HOP_LE);
            request.getRequestDispatcher("/Admin/QuanLyNhanVien/NhanVienEdit.jsp").forward(request, response);
        }
    }

    private void updateRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        JsonObject result = new JsonObject();
        try {
            HttpSession session = request.getSession(false);
            Object currentUser = session == null ? null : session.getAttribute(LoginServlet.SESSION_KEY);
            if (!(currentUser instanceof NhanVien)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                throw new IllegalStateException("Phiên đăng nhập đã hết hạn");
            }

            Integer nhanVienId = Integer.valueOf(request.getParameter("id"));
            Integer vaiTroMoi = Integer.valueOf(request.getParameter("vaiTro"));
            NhanVien updated = nhanVienService.capNhatVaiTro(
                    nhanVienId, vaiTroMoi, ((NhanVien) currentUser).getId());

            result.addProperty("success", true);
            result.addProperty("message", "Đã cập nhật quyền thành " + updated.getTenVaiTro());
            result.addProperty("vaiTro", updated.getVaiTro());
            result.addProperty("tenVaiTro", updated.getTenVaiTro());
        } catch (Exception e) {
            if (response.getStatus() < 400) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            result.addProperty("success", false);
            result.addProperty("message", e.getMessage());
        }
        response.getWriter().write(result.toString());
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            NhanVien currentUser = getCurrentUser(request);
            if (currentUser != null && currentUser.getId().equals(id)) {
                throw new IllegalArgumentException("Bạn không thể tự vô hiệu hóa tài khoản của chính mình");
            }
            // Lấy tên nhân viên trước khi xóa để hiện toast
            NhanVien nv = nhanVienService.timTheoId(id);
            String hoTen = (nv != null) ? nv.getHoTen() : "Nhân viên";
            nhanVienService.xoaNhanVien(id);
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                JsonObject result = new JsonObject();
                result.addProperty("success", true);
                result.addProperty("hoTen", hoTen);
                response.getWriter().write(result.toString());
            } else {
                response.sendRedirect(request.getContextPath() + "/NhanVien");
            }
        } catch (Exception e) {
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject result = new JsonObject();
                result.addProperty("success", false);
                result.addProperty("message", e.getMessage());
                response.getWriter().write(result.toString());
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
        nv.setChucVu(chucVu == null ? null : chucVu.trim());
        nv.setAnhDaiDien(anh);
        nv.setTrangThai((trangThaiStr != null && !trangThaiStr.isEmpty()) ? Integer.parseInt(trangThaiStr) : 1);
        return nv;
    }

    private NhanVien getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute(LoginServlet.SESSION_KEY);
        return value instanceof NhanVien ? (NhanVien) value : null;
    }

    private void validateChucVu(String chucVu) {
        if (chucVu == null || !CHUC_VU_HOP_LE_SET.contains(chucVu.trim())) {
            throw new IllegalArgumentException("Vui lòng chọn chức vụ hợp lệ.");
        }
    }

    /**
     * Tự sinh mã nhân viên dạng MNV0001, MNV0002,...
     */
    private String generateMaNhanVien() {
        String maxMa = nhanVienDao.findMaxMaNhanVien();
        int nextNumber = 1;
        if (maxMa != null && maxMa.startsWith("MNV")) {
            try {
                nextNumber = Integer.parseInt(maxMa.substring(3)) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("MNV%04d", nextNumber);
    }

    /**
     * Tự sinh mật khẩu ngẫu nhiên gồm chữ hoa, chữ thường và số
     */
    private String generatePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
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
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

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
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

            // ── Row 2: trống ────────────────────────────────────
            sheet.createRow(2);

            // ── Row 3: Header cột ───────────────────────────────
            String[] headers = {"STT", "Mã NV", "Họ và tên", "Chức vụ", "Quyền hệ thống",
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

                // Quyền hệ thống
                Cell vaiTroCell = row.createCell(4);
                vaiTroCell.setCellValue(nv.getTenVaiTro());
                vaiTroCell.setCellStyle(isAlt ? centerStyleAlt : centerStyle);

                // Giới tính
                Cell gioiTinhCell = row.createCell(5);
                gioiTinhCell.setCellValue(
                    nv.getGioiTinh() != null && nv.getGioiTinh() == 1 ? "Nam" : "Nữ");
                gioiTinhCell.setCellStyle(isAlt ? centerStyleAlt : centerStyle);

                // Ngày sinh
                Cell ngaySinhCell = row.createCell(6);
                ngaySinhCell.setCellValue(
                    nv.getNgaySinh() != null ? nv.getNgaySinh().format(dtf) : "");
                ngaySinhCell.setCellStyle(isAlt ? centerStyleAlt : centerStyle);

                // SĐT
                Cell sdtCell = row.createCell(7);
                sdtCell.setCellValue(nv.getSoDienThoai() != null ? nv.getSoDienThoai() : "");
                sdtCell.setCellStyle(isAlt ? centerStyleAlt : centerStyle);

                // Email
                Cell emailCell = row.createCell(8);
                emailCell.setCellValue(nv.getEmail() != null ? nv.getEmail() : "");
                emailCell.setCellStyle(isAlt ? dataStyleAlt : dataStyle);

                // Địa chỉ
                Cell diaChiCell = row.createCell(9);
                diaChiCell.setCellValue(nv.getDiaChi() != null ? nv.getDiaChi() : "");
                diaChiCell.setCellStyle(isAlt ? dataStyleAlt : dataStyle);
            }

            // ── Auto-size cột ───────────────────────────────────
            int[] colWidths = {8, 12, 25, 22, 18, 12, 14, 16, 30, 22};
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
