package QuanLySanPham.controller;

import QuanLySanPham.Entity.PhieuGiamGia;
import QuanLySanPham.dao.PhieuGiamGiaDao;
import QuanLySanPham.dao.impl.PhieuGiamGiaDaoImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@WebServlet(name = "PhieuGiamGiaServlet", value = {
        "/PhieuGiamGia",
        "/PhieuGiamGia/new",
        "/PhieuGiamGia/insert",
        "/PhieuGiamGia/edit",
        "/PhieuGiamGia/update",
        "/PhieuGiamGia/change-status",
        "/PhieuGiamGia/export-excel"
})
public class PhieuGiamGiaServlet extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final int PUBLIC_COUPON_TYPE = 0;
    private static final int PERSONAL_COUPON_TYPE = 1;
    private static final String PAGE_TITLE_LIST = "Quản lý phiếu giảm giá";
    private static final String PAGE_TITLE_CREATE = "Quản lý phiếu giảm giá / Thêm phiếu giảm giá";
    private static final String PAGE_TITLE_EDIT = "Quản lý phiếu giảm giá / Cập nhật phiếu giảm giá";
    private static final String DISCOUNT_PERCENT = "Giảm phần trăm";
    private static final String DISCOUNT_AMOUNT = "Giảm tiền";

    private final PhieuGiamGiaDao phieuGiamGiaDao = new PhieuGiamGiaDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        applyUtf8(request, response);
        String path = request.getServletPath();
        if ("/PhieuGiamGia/new".equals(path)) {
            showCreateForm(request, response);
            return;
        }
        if ("/PhieuGiamGia/edit".equals(path)) {
            showEditForm(request, response);
            return;
        }
        if ("/PhieuGiamGia/export-excel".equals(path)) {
            exportExcel(request, response);
            return;
        }
        showList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        applyUtf8(request, response);
        String path = request.getServletPath();
        if ("/PhieuGiamGia/insert".equals(path)) {
            insertCoupon(request, response);
            return;
        }
        if ("/PhieuGiamGia/update".equals(path)) {
            updateCoupon(request, response);
            return;
        }
        if ("/PhieuGiamGia/change-status".equals(path)) {
            changeStatus(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/PhieuGiamGia");
    }

    private void applyUtf8(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    private void showList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = trim(request.getParameter("keyword"));
        String maVoucher = trim(request.getParameter("maVoucher"));
        String tenVoucher = trim(request.getParameter("tenVoucher"));
        String loaiPhieu = normalizeOption(request.getParameter("loaiPhieu"), new String[]{"public", "personal"});
        String loaiGiamGia = normalizeOption(request.getParameter("loaiGiamGia"), new String[]{"percent", "amount"});
        String trangThai = normalizeOption(request.getParameter("trangThai"), new String[]{"active", "upcoming", "expired", "inactive"});
        LocalDate denNgay = parseOptionalDate(request.getParameter("denNgay"));
        int pageSize = parsePageSize(request.getParameter("size"));
        int page = parsePositiveInteger(request.getParameter("page"), 1);

        int totalRecords = phieuGiamGiaDao.countFiltered(keyword, maVoucher, tenVoucher, loaiPhieu, loaiGiamGia, trangThai, denNgay);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages < 1) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        List<PhieuGiamGia> coupons = phieuGiamGiaDao.searchAndFilter(
                keyword, maVoucher, tenVoucher, loaiPhieu, loaiGiamGia, trangThai, denNgay, page, pageSize);

        request.setAttribute("items", coupons);
        request.setAttribute("keyword", keyword);
        request.setAttribute("maVoucher", maVoucher);
        request.setAttribute("tenVoucher", tenVoucher);
        request.setAttribute("loaiPhieu", loaiPhieu);
        request.setAttribute("loaiGiamGia", loaiGiamGia);
        request.setAttribute("trangThai", trangThai);
        request.setAttribute("denNgay", denNgay == null ? "" : denNgay.toString());
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("startIndex", (page - 1) * pageSize);
        request.setAttribute("pageUrlPrefix", buildPageUrlPrefix(request));
        request.setAttribute("exportUrl", buildActionUrl(request, "/PhieuGiamGia/export-excel"));
        request.setAttribute("currentQueryString", buildFilterQueryString(request, true));
        request.setAttribute("activeMenu", "discount");
        request.setAttribute("pageTitle", PAGE_TITLE_LIST);
        readFlashMessages(request);

        request.getRequestDispatcher("/Admin/QuanLyMaGiamGia/phieu-giam-gia-list.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PhieuGiamGia coupon = new PhieuGiamGia();
        coupon.setLoaiGiamGia(DISCOUNT_PERCENT);
        coupon.setLoaiPhieu(PUBLIC_COUPON_TYPE);
        coupon.setTrangThai(1);
        coupon.setSoLuong(1);
        coupon.setSoLuongDaDung(0);
        coupon.setMaVoucher(phieuGiamGiaDao.generateNextVoucherCode());
        request.setAttribute("coupon", coupon);
        request.setAttribute("formMode", "create");
        request.setAttribute("formAction", request.getContextPath() + "/PhieuGiamGia/insert");
        request.setAttribute("pageTitle", PAGE_TITLE_CREATE);
        setFormCommonAttributes(request);
        request.getRequestDispatcher("/Admin/QuanLyMaGiamGia/phieu-giam-gia-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = parseInteger(request.getParameter("id"));
        if (id == null) {
            setFlashMessage(request.getSession(), "errorMessage", "Thiếu ID phiếu giảm giá cần sửa.");
            response.sendRedirect(request.getContextPath() + "/PhieuGiamGia");
            return;
        }

        PhieuGiamGia coupon = phieuGiamGiaDao.getById(id);
        if (coupon == null) {
            setFlashMessage(request.getSession(), "errorMessage", "Không tìm thấy phiếu giảm giá.");
            response.sendRedirect(request.getContextPath() + "/PhieuGiamGia");
            return;
        }

        request.setAttribute("coupon", coupon);
        request.setAttribute("formMode", "edit");
        request.setAttribute("formAction", request.getContextPath() + "/PhieuGiamGia/update");
        request.setAttribute("pageTitle", PAGE_TITLE_EDIT);
        setFormCommonAttributes(request);
        request.getRequestDispatcher("/Admin/QuanLyMaGiamGia/phieu-giam-gia-form.jsp").forward(request, response);
    }

    private void insertCoupon(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errors = new LinkedHashMap<>();
        PhieuGiamGia coupon = readCouponFromRequest(request, false, errors);

        prepareGeneratedCodeForInsert(coupon, errors);

        if (!errors.isEmpty()) {
            forwardFormWithErrors(request, response, coupon, errors, "create");
            return;
        }

        phieuGiamGiaDao.insert(coupon);
        setFlashMessage(request.getSession(), "successMessage", "Thêm phiếu giảm giá thành công.");
        response.sendRedirect(request.getContextPath() + "/PhieuGiamGia");
    }

    private void updateCoupon(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errors = new LinkedHashMap<>();
        Integer id = parseInteger(request.getParameter("id"));
        if (id == null) {
            errors.put("id", "Thiếu ID phiếu giảm giá cần cập nhật.");
            PhieuGiamGia coupon = readCouponFromRequest(request, true, errors);
            forwardFormWithErrors(request, response, coupon, errors, "edit");
            return;
        }

        PhieuGiamGia current = phieuGiamGiaDao.getById(id);
        if (current == null) {
            setFlashMessage(request.getSession(), "errorMessage", "Không tìm thấy phiếu giảm giá cần cập nhật.");
            response.sendRedirect(request.getContextPath() + "/PhieuGiamGia");
            return;
        }

        PhieuGiamGia coupon = readCouponFromRequest(request, true, errors);
        coupon.setId(id);
        if (coupon.getMaVoucher() == null || coupon.getMaVoucher().trim().isEmpty()) {
            coupon.setMaVoucher(current.getMaVoucher());
        }
        if (phieuGiamGiaDao.checkDuplicateCode(coupon.getMaVoucher(), id)) {
            errors.put("maVoucher", "Mã giảm giá đã tồn tại.");
        }

        if (!errors.isEmpty()) {
            forwardFormWithErrors(request, response, coupon, errors, "edit");
            return;
        }

        phieuGiamGiaDao.updateCoupon(coupon);
        setFlashMessage(request.getSession(), "successMessage", "Cập nhật phiếu giảm giá thành công.");
        response.sendRedirect(request.getContextPath() + "/PhieuGiamGia");
    }

    private void prepareGeneratedCodeForInsert(PhieuGiamGia coupon, Map<String, String> errors) {
        String code = trim(coupon.getMaVoucher());
        if (isGeneratedVoucherCode(code) && !phieuGiamGiaDao.checkDuplicateCode(code, null)) {
            coupon.setMaVoucher(code);
            return;
        }

        for (int i = 0; i < 3; i++) {
            String nextCode = phieuGiamGiaDao.generateNextVoucherCode();
            if (!phieuGiamGiaDao.checkDuplicateCode(nextCode, null)) {
                coupon.setMaVoucher(nextCode);
                errors.remove("maVoucher");
                return;
            }
        }
        errors.put("maVoucher", "Không tạo được mã giảm giá mới. Vui lòng thử lại.");
    }

    private boolean isGeneratedVoucherCode(String code) {
        return code != null && code.matches("VC\\d{6}");
    }

    private void changeStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = parseInteger(request.getParameter("id"));
        Integer currentStatus = parseInteger(request.getParameter("currentStatus"));
        if (id != null && currentStatus != null) {
            int nextStatus = currentStatus == 1 ? 0 : 1;
            phieuGiamGiaDao.updateStatus(id, nextStatus);
            setFlashMessage(request.getSession(), "successMessage",
                    nextStatus == 1 ? "Đã bật phiếu giảm giá." : "Đã ngừng áp dụng phiếu giảm giá.");
        }
        response.sendRedirect(request.getContextPath() + "/PhieuGiamGia" + buildReturnQuery(request));
    }

    private void exportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String keyword = trim(request.getParameter("keyword"));
        String maVoucher = trim(request.getParameter("maVoucher"));
        String tenVoucher = trim(request.getParameter("tenVoucher"));
        String loaiPhieu = normalizeOption(request.getParameter("loaiPhieu"), new String[]{"public", "personal"});
        String loaiGiamGia = normalizeOption(request.getParameter("loaiGiamGia"), new String[]{"percent", "amount"});
        String trangThai = normalizeOption(request.getParameter("trangThai"), new String[]{"active", "upcoming", "expired", "inactive"});
        LocalDate denNgay = parseOptionalDate(request.getParameter("denNgay"));

        List<PhieuGiamGia> coupons = phieuGiamGiaDao.searchAndFilterForExport(
                keyword, maVoucher, tenVoucher, loaiPhieu, loaiGiamGia, trangThai, denNgay);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=phieu-giam-gia.xlsx");

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream outputStream = response.getOutputStream()) {
            Sheet sheet = workbook.createSheet("Phiếu giảm giá");
            String[] columns = {"STT", "Mã giảm giá", "Tên giảm giá", "Loại phiếu", "Loại giảm",
                    "Giá trị giảm", "Đơn hàng tối thiểu", "Số lượng", "Ngày bắt đầu",
                    "Ngày kết thúc", "Trạng thái"};

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (int i = 0; i < coupons.size(); i++) {
                PhieuGiamGia coupon = coupons.get(i);
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(blankIfNull(coupon.getMaVoucher()));
                row.createCell(2).setCellValue(blankIfNull(coupon.getTenVoucher()));
                row.createCell(3).setCellValue(coupon.getLoaiPhieuText());
                row.createCell(4).setCellValue(coupon.getLoaiGiamGiaText());
                row.createCell(5).setCellValue(formatDiscountValue(coupon));
                row.createCell(6).setCellValue(formatMoney(coupon.getDonToiThieu()));
                row.createCell(7).setCellValue((coupon.getSoLuongDaDung() == null ? 0 : coupon.getSoLuongDaDung())
                        + "/" + (coupon.getSoLuong() == null ? 0 : coupon.getSoLuong()));
                row.createCell(8).setCellValue(coupon.getNgayBatDauText());
                row.createCell(9).setCellValue(coupon.getNgayKetThucText());
                row.createCell(10).setCellValue(coupon.getTrangThaiHienThi());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
        }
    }

    private PhieuGiamGia readCouponFromRequest(HttpServletRequest request, boolean editMode, Map<String, String> errors) {
        PhieuGiamGia coupon = new PhieuGiamGia();
        coupon.setId(parseInteger(request.getParameter("id")));
        coupon.setMaVoucher(validateCode(request.getParameter("maVoucher"), errors));
        coupon.setTenVoucher(validateRequiredText(request.getParameter("tenVoucher"), "tenVoucher", "Tên phiếu không được để trống.", 250, errors));
        coupon.setSoLuong(validatePositiveInteger(request.getParameter("soLuong"), "soLuong", "Số lượng phải là số nguyên lớn hơn 0.", errors));
        coupon.setSoLuongDaDung(0);
        coupon.setNgayBatDau(validateDate(request.getParameter("ngayBatDau"), "ngayBatDau", false, errors));
        coupon.setNgayKetThuc(validateDate(request.getParameter("ngayKetThuc"), "ngayKetThuc", true, errors));
        coupon.setLoaiGiamGia(validateDiscountType(request.getParameter("loaiGiamGia"), errors));
        coupon.setGiaTriGiam(validateDiscountValue(request.getParameter("giaTriGiam"), coupon.getLoaiGiamGia(), errors));
        coupon.setGiamToiDa(validateMoney(request.getParameter("giamToiDa"), "giamToiDa", "Giảm tối đa không được âm.", errors));
        coupon.setDonToiThieu(validateMoney(request.getParameter("donToiThieu"), "donToiThieu", "Đơn hàng tối thiểu không được âm.", errors));
        coupon.setLoaiPhieu(validateCouponType(request.getParameter("loaiPhieu"), errors));
        coupon.setTrangThai(validateStatus(request.getParameter("trangThai"), errors));
        coupon.setNgayTao(LocalDateTime.now());

        if (DISCOUNT_AMOUNT.equals(coupon.getLoaiGiamGia())) {
            coupon.setGiamToiDa(BigDecimal.ZERO);
        }
        if (coupon.getNgayBatDau() != null && coupon.getNgayKetThuc() != null
                && coupon.getNgayKetThuc().isBefore(coupon.getNgayBatDau())) {
            errors.put("ngayKetThuc", "Ngày kết thúc không được trước ngày bắt đầu.");
        }
        if (editMode && (coupon.getMaVoucher() == null || coupon.getMaVoucher().trim().isEmpty())) {
            errors.remove("maVoucher");
        }
        return coupon;
    }

    private String validateCode(String value, Map<String, String> errors) {
        String code = trim(value);
        if (code.isEmpty()) {
            errors.put("maVoucher", "Mã giảm giá không được để trống.");
            return code;
        }
        if (code.length() > 50) {
            errors.put("maVoucher", "Mã giảm giá không được vượt quá 50 ký tự.");
        }
        return code;
    }

    private String validateRequiredText(String value, String field, String message, int maxLength, Map<String, String> errors) {
        String text = trim(value);
        if (text.isEmpty()) {
            errors.put(field, message);
            return text;
        }
        if (text.length() > maxLength) {
            errors.put(field, "Nội dung không được vượt quá " + maxLength + " ký tự.");
        }
        return text;
    }

    private Integer validatePositiveInteger(String value, String field, String message, Map<String, String> errors) {
        Integer number = parseInteger(value);
        if (number == null || number <= 0) {
            errors.put(field, message);
            return 0;
        }
        return number;
    }

    private LocalDateTime validateDate(String value, String field, boolean endOfDay, Map<String, String> errors) {
        String text = trim(value);
        if (text.isEmpty()) {
            errors.put(field, "Ngày không được để trống.");
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(text);
            return endOfDay ? date.atTime(LocalTime.MAX) : date.atStartOfDay();
        } catch (DateTimeParseException e) {
            errors.put(field, "Ngày không đúng định dạng.");
            return null;
        }
    }

    private String validateDiscountType(String value, Map<String, String> errors) {
        if ("percent".equals(value)) {
            return DISCOUNT_PERCENT;
        }
        if ("amount".equals(value)) {
            return DISCOUNT_AMOUNT;
        }
        errors.put("loaiGiamGia", "Loại giảm không hợp lệ.");
        return DISCOUNT_PERCENT;
    }

    private BigDecimal validateDiscountValue(String value, String discountType, Map<String, String> errors) {
        if (DISCOUNT_AMOUNT.equals(discountType)) {
            return validateMoney(value, "giaTriGiam", "Giá trị giảm phải là số tiền hợp lệ.", errors);
        }

        BigDecimal number = parsePlainNumber(value);
        if (number == null || number.compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("giaTriGiam", "Giá trị giảm phải là số lớn hơn 0.");
            return BigDecimal.ZERO;
        }
        if (DISCOUNT_PERCENT.equals(discountType) && number.compareTo(BigDecimal.valueOf(100)) > 0) {
            errors.put("giaTriGiam", "Giá trị giảm phần trăm phải từ 1 đến 100.");
        }
        return number;
    }

    private BigDecimal validateMoney(String value, String field, String message, Map<String, String> errors) {
        String text = trim(value);
        if (text.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal number = parseMoney(text);
        if (number == null || number.compareTo(BigDecimal.ZERO) < 0) {
            errors.put(field, message);
            return BigDecimal.ZERO;
        }
        return number;
    }

    private Integer validateCouponType(String value, Map<String, String> errors) {
        if ("public".equals(value)) {
            return PUBLIC_COUPON_TYPE;
        }
        if ("personal".equals(value)) {
            return PERSONAL_COUPON_TYPE;
        }
        errors.put("loaiPhieu", "Loại phiếu không hợp lệ.");
        return PUBLIC_COUPON_TYPE;
    }

    private Integer validateStatus(String value, Map<String, String> errors) {
        if ("1".equals(value)) {
            return 1;
        }
        if ("0".equals(value)) {
            return 0;
        }
        errors.put("trangThai", "Trạng thái không hợp lệ.");
        return 1;
    }

    private void forwardFormWithErrors(HttpServletRequest request, HttpServletResponse response,
                                       PhieuGiamGia coupon, Map<String, String> errors, String formMode)
            throws ServletException, IOException {
        request.setAttribute("coupon", coupon);
        request.setAttribute("errors", errors);
        request.setAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin phiếu giảm giá.");
        request.setAttribute("formMode", formMode);
        request.setAttribute("formAction", request.getContextPath()
                + ("edit".equals(formMode) ? "/PhieuGiamGia/update" : "/PhieuGiamGia/insert"));
        request.setAttribute("pageTitle", "edit".equals(formMode) ? PAGE_TITLE_EDIT : PAGE_TITLE_CREATE);
        setFormCommonAttributes(request);
        request.getRequestDispatcher("/Admin/QuanLyMaGiamGia/phieu-giam-gia-form.jsp").forward(request, response);
    }

    private void setFormCommonAttributes(HttpServletRequest request) {
        request.setAttribute("activeMenu", "discount");
        request.setAttribute("returnQueryString", trim(request.getParameter("returnQuery")));
    }

    private String buildPageUrlPrefix(HttpServletRequest request) {
        String query = buildFilterQueryString(request, false);
        if (query.isEmpty()) {
            return request.getContextPath() + "/PhieuGiamGia?page=";
        }
        return request.getContextPath() + "/PhieuGiamGia?" + query + "&page=";
    }

    private String buildActionUrl(HttpServletRequest request, String action) {
        String query = buildFilterQueryString(request, false);
        if (query.isEmpty()) {
            return request.getContextPath() + action;
        }
        return request.getContextPath() + action + "?" + query;
    }

    private String buildFilterQueryString(HttpServletRequest request, boolean includePage) {
        List<String> params = new ArrayList<>();
        appendParam(params, "keyword", request.getParameter("keyword"));
        appendParam(params, "maVoucher", request.getParameter("maVoucher"));
        appendParam(params, "tenVoucher", request.getParameter("tenVoucher"));
        appendParam(params, "loaiPhieu", request.getParameter("loaiPhieu"));
        appendParam(params, "loaiGiamGia", request.getParameter("loaiGiamGia"));
        appendParam(params, "trangThai", request.getParameter("trangThai"));
        appendParam(params, "denNgay", request.getParameter("denNgay"));
        appendParam(params, "size", String.valueOf(parsePageSize(request.getParameter("size"))));
        if (includePage) {
            appendParam(params, "page", String.valueOf(parsePositiveInteger(request.getParameter("page"), 1)));
        }
        return joinParams(params);
    }

    private String buildReturnQuery(HttpServletRequest request) {
        String returnQuery = trim(request.getParameter("returnQuery"));
        if (returnQuery.isEmpty()) {
            return "";
        }
        if (returnQuery.startsWith("?")) {
            return returnQuery;
        }
        return "?" + returnQuery;
    }

    private void appendParam(List<String> params, String key, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        params.add(URLEncoder.encode(key, StandardCharsets.UTF_8) + "="
                + URLEncoder.encode(value.trim(), StandardCharsets.UTF_8));
    }

    private String joinParams(List<String> params) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                builder.append("&");
            }
            builder.append(params.get(i));
        }
        return builder.toString();
    }

    private String normalizeOption(String value, String[] allowedValues) {
        String text = trim(value);
        for (int i = 0; i < allowedValues.length; i++) {
            if (allowedValues[i].equals(text)) {
                return text;
            }
        }
        return "";
    }

    private int parsePageSize(String value) {
        int size = parsePositiveInteger(value, DEFAULT_PAGE_SIZE);
        if (size == 5 || size == 10 || size == 20) {
            return size;
        }
        return DEFAULT_PAGE_SIZE;
    }

    private int parsePositiveInteger(String value, int defaultValue) {
        Integer number = parseInteger(value);
        if (number == null || number < 1) {
            return defaultValue;
        }
        return number;
    }

    private Integer parseInteger(String value) {
        try {
            String text = trim(value);
            if (text.isEmpty()) {
                return null;
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parsePlainNumber(String value) {
        try {
            String text = trim(value);
            if (text.isEmpty()) {
                return null;
            }
            if (!text.matches("\\d+")) {
                return null;
            }
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseMoney(String value) {
        try {
            String text = trim(value).replace(".", "").replace(",", "");
            if (text.isEmpty()) {
                return null;
            }
            if (!text.matches("\\d+")) {
                return null;
            }
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseOptionalDate(String value) {
        try {
            String text = trim(value);
            if (text.isEmpty()) {
                return null;
            }
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String blankIfNull(String value) {
        return value == null ? "" : value;
    }

    private String formatDiscountValue(PhieuGiamGia coupon) {
        if (coupon.isGiamPhanTram()) {
            return formatPlainNumber(coupon.getGiaTriGiam()) + "%";
        }
        return formatMoney(coupon.getGiaTriGiam());
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(value) + " đ";
    }

    private String formatPlainNumber(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private void setFlashMessage(HttpSession session, String key, String message) {
        session.setAttribute(key, message);
    }

    private void readFlashMessages(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        moveFlashMessage(session, request, "successMessage");
        moveFlashMessage(session, request, "errorMessage");
    }

    private void moveFlashMessage(HttpSession session, HttpServletRequest request, String key) {
        Object message = session.getAttribute(key);
        if (message != null) {
            request.setAttribute(key, message);
            session.removeAttribute(key);
        }
    }
}
