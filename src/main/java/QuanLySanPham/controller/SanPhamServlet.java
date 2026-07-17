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
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // 🛠️ BỔ SUNG: Đón thêm 2 tham số khoảng giá từ Request
        String giaTuStr = request.getParameter("giaTu");
        String giaDenStr = request.getParameter("giaDen");

        Integer danhMucId = (danhMucIdStr != null && !danhMucIdStr.isEmpty()) ? Integer.parseInt(danhMucIdStr) : null;
        Integer thuongHieuId = (thuongHieuIdStr != null && !thuongHieuIdStr.isEmpty()) ? Integer.parseInt(thuongHieuIdStr) : null;

        // 🛠️ XỬ LÝ AN TOÀN CHỐNG NULL: Nếu tham số trên URL trống thì gán mặc định null chứ không ép kiểu trực tiếp (Sửa lỗi dòng 103)
        Double giaTu = (giaTuStr != null && !giaTuStr.isEmpty()) ? Double.parseDouble(giaTuStr) : null;
        Double giaDen = (giaDenStr != null && !giaDenStr.isEmpty()) ? Double.parseDouble(giaDenStr) : null;

        // 🛠️ CẬP NHẬT: Truyền đầy đủ 5 tham số vào hàm timKiem của Service để xuất đúng danh sách đang lọc trên màn hình
        List<SanPham> items = sanPhamService.timKiem(tenSanPham, danhMucId, thuongHieuId, giaTu, giaDen);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Danh_Sach_San_Pham.xlsx");

        try (Workbook workbook = new XSSFWorkbook();
             ServletOutputStream out = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("Sản Phẩm");

            Row headerRow = sheet.createRow(0);
            // 🛠️ THAY ĐỔI: Thêm cột "Số lượng" và "Đơn giá" vào file Excel của bạn cho khớp giao diện mới
            String[] columns = {"STT", "Mã SP", "Tên sản phẩm", "Danh mục", "Thương hiệu", "Chất liệu", "Kiểu dáng", "Số lượng", "Đơn giá", "Trạng thái"};

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

                // 🛠️ GÁN GIÁ TRỊ: Xuất tổng số lượng biến thể
                row.createCell(7).setCellValue(sp.getTongSoLuong() != null ? sp.getTongSoLuong() : 0);

                // 🛠️ GÁN GIÁ TRỊ: Xuất chuỗi khoảng giá Min - Max rõ ràng sang Excel
                String giaText = "Chưa có giá";
                if (sp.getGiaMin() != null && sp.getGiaMax() != null) {
                    if (sp.getGiaMin().equals(sp.getGiaMax()) && sp.getGiaMin() > 0) {
                        giaText = String.format("%,.0f đ", sp.getGiaMin());
                    } else if (sp.getGiaMin() < sp.getGiaMax()) {
                        giaText = String.format("%,.0f - %,.0f đ", sp.getGiaMin(), sp.getGiaMax());
                    }
                }
                row.createCell(8).setCellValue(giaText);

                String trangThaiText = (sp.getTrangThai() == 1) ? "Đang kinh doanh" : "Ngừng bán";
                row.createCell(9).setCellValue(trangThaiText);
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
        long totalCount = sanPhamService.timKiem("", null, null,null,null).size();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        setLookupAttributes(request);

        request.setAttribute("items", items);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);

        request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPham.jsp").forward(request, response);
    }

    private void showAddSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Đếm tổng số lượng sản phẩm hiện có trong Database
        int count = sanPhamService.timKiem("", null, null,null,null).size();

        // 2. Sinh mã tự động (Ví dụ có 15 sản phẩm -> tạo mã SP016)
        String autoMa = String.format("SP%03d", count + 1);

        // 3. Gửi mã này sang file giao diện JSP
        request.setAttribute("autoMaSanPham", autoMa);

        setLookupAttributes(request);
        request.setAttribute("action", "add");
        request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamAdd.jsp").forward(request, response);
    }

    private void showEditSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        SanPham sanPham = sanPhamService.timTheoId(id);

        if (sanPham == null) {
            request.setAttribute("error", "Không tìm thấy sản phẩm");
            ShowSanPham(request, response);
            return;
        }

        List<SanPhamChiTiet> sanPhamChiTietList = sanPhamChiTietService.timBienTheTheoSanPhamId(id);

        request.setAttribute("sanPham", sanPham);
        request.setAttribute("sanPhamChiTietList", sanPhamChiTietList); // Sửa tên attribute
        setLookupAttributes(request);
        request.setAttribute("action", "edit");
        request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamEdit.jsp").forward(request, response);
    }

    private void insertSanPham(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SanPham sanPham = getSanPhamFrom(request);
        sanPham.setNgayTao(LocalDateTime.now());
        sanPham.setNgaySua(LocalDateTime.now());

        try {
            // Xử lý upload ảnh và lấy danh sách biến thể từ request
            Map<String, String> anhTheoMau = xuLyUploadAnhTheoMau(request);
            List<SanPhamChiTiet> danhSachBienThe = getDanhSachBienTheFromRequest(request, null, anhTheoMau);

            // Gọi service gộp để thực hiện trong 1 transaction
            sanPhamService.themSanPhamVaBienThe(sanPham, danhSachBienThe);

            response.sendRedirect(request.getContextPath() + "/SanPham?success=Th%C3%AAm%20s%E1%BA%A3n%20ph%E1%BA%A9m%20th%C3%A0nh%20c%C3%B4ng");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.setAttribute("sanPham", sanPham);
            setLookupAttributes(request);
            request.setAttribute("action", "add");
            request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamAdd.jsp").forward(request, response);
        }
    }

    private void updateSanPham(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("error", "Không tìm thấy ID sản phẩm để cập nhật!");
            showEditSanPham(request, response);
            return;
        }
        Integer sanPhamId = Integer.parseInt(idStr);

        SanPham sanPham = getSanPhamFrom(request);
        sanPham.setId(sanPhamId);
        sanPham.setNgaySua(LocalDateTime.now());

        // Lấy danh sách ID biến thể hiện có trong DB TRƯỚC KHI thay đổi
        List<Integer> existingIdsInDb = sanPhamChiTietService.timBienTheTheoSanPhamId(sanPhamId)
                .stream()
                .map(SanPhamChiTiet::getId)
                .collect(Collectors.toList());

        try {
            if (sanPham.getTenSanPham() == null || sanPham.getTenSanPham().isEmpty()) {
                throw new RuntimeException("Tên sản phẩm không được để trống!");
            }

            // 1. Cập nhật sản phẩm cha
            sanPhamService.capNhatSanPham(sanPham);

            // 2. Xử lý upload ảnh và lấy danh sách biến thể từ request
            Map<String, String> anhTheoMau = xuLyUploadAnhTheoMau(request);
            List<SanPhamChiTiet> danhSachBienTheTuForm = getDanhSachBienTheFromRequest(request, sanPham.getId(), anhTheoMau);

            // 3. Phân loại biến thể mới và biến thể cũ để xử lý
            List<SanPhamChiTiet> listUpdate = danhSachBienTheTuForm.stream().filter(spct -> spct.getId() != null).collect(Collectors.toList());
            List<SanPhamChiTiet> listInsert = danhSachBienTheTuForm.stream().filter(spct -> spct.getId() == null).collect(Collectors.toList());

            if (!listUpdate.isEmpty()) {
                sanPhamChiTietService.capNhatDanhSachBienThe(listUpdate);
            }
            if (!listInsert.isEmpty()) {
                sanPhamChiTietService.themBienThe(listInsert);
            }

            // 4. Xử lý xóa các biến thể đã bị loại bỏ khỏi form
            List<Integer> idsFromForm = listUpdate.stream().map(SanPhamChiTiet::getId).collect(Collectors.toList());
            for (Integer idInDb : existingIdsInDb) {
                if (!idsFromForm.contains(idInDb)) {
                    sanPhamChiTietService.xoaBienThe(idInDb);
                }
            }

            response.sendRedirect(request.getContextPath() + "/SanPham");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.setAttribute("sanPham", sanPham);
            // Tải lại danh sách biến thể cũ để hiển thị lại form nếu có lỗi
            request.setAttribute("sanPhamChiTietList", sanPhamChiTietService.timBienTheTheoSanPhamId(sanPhamId));
            setLookupAttributes(request);
            request.setAttribute("action", "edit");
            request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamEdit.jsp").forward(request, response);
        }
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

        // Đón 2 tham số khoảng giá dựa vào thuộc tính name="giaTu" và name="giaDen" từ form JSP
        String giaTuStr = request.getParameter("giaTu");
        String giaDenStr = request.getParameter("giaDen");

        Integer danhMucId = (danhMucIdStr != null && !danhMucIdStr.isEmpty()) ? Integer.parseInt(danhMucIdStr) : null;

        Integer thuongHieuId = (thuongHieuIdStr != null && !thuongHieuIdStr.isEmpty()) ? Integer.parseInt(thuongHieuIdStr) : null;
        String pageStr = request.getParameter("page");
        int page = (pageStr != null && !pageStr.isEmpty()) ? Integer.parseInt(pageStr) : 1;
        // Ép kiểu sang BigDecimal (hoặc Double tuỳ thuộc kiểu dữ liệu của cột giaBan trong DB)
        Double giaTu = (giaTuStr != null && !giaTuStr.isEmpty()) ? Double.parseDouble(giaTuStr) : null;
        Double giaDen = (giaDenStr != null && !giaDenStr.isEmpty()) ? Double.parseDouble(giaDenStr) : null;

        // Truyền thêm giaTu và giaDen xuống Service
        List<SanPham> items = sanPhamService.timKiem(tenSanPham, danhMucId, thuongHieuId, giaTu, giaDen);

        request.setAttribute("items", items);
        request.setAttribute("currentPage", page); // Thêm dòng này để sửa lỗi STT bị âm
        request.setAttribute("danhMucList", lookupService.layTatCaDanhMuc());
        request.setAttribute("thuongHieuList", lookupService.layTatCaThuongHieu());
        request.setAttribute("searchTenSanPham", tenSanPham);
        request.setAttribute("searchDanhMucId", danhMucId);
        request.setAttribute("searchThuongHieuId", thuongHieuId);

        // Đẩy ngược lại value để giữ trạng thái cho thanh kéo và ô input nhập số
        request.setAttribute("searchGiaTu", giaTuStr);
        request.setAttribute("searchGiaDen", giaDenStr);

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

    private List<SanPhamChiTiet> getDanhSachBienTheFromRequest(HttpServletRequest request, Integer sanPhamId, Map<String, String> anhTheoMau) {
        List<SanPhamChiTiet> danhSach = new ArrayList<>();
        String[] mauSacIds = request.getParameterValues("mauSacId[]");
        if (mauSacIds == null || mauSacIds.length == 0) {
            return danhSach;
        }

        String[] kichCoIds = request.getParameterValues("kichCoId[]");
        String[] giaNhap = request.getParameterValues("giaNhap[]");
        String[] giaBan = request.getParameterValues("giaBan[]");
        String[] soLuongTon = request.getParameterValues("soLuongTon[]");
        String[] trongLuong = request.getParameterValues("trongLuong[]");
        String[] ma = request.getParameterValues("ma[]");
        String[] sanPhamChiTietIds = request.getParameterValues("sanPhamChiTietId[]");
        String[] hinhAnhCu = request.getParameterValues("hinhAnhCu[]");
        String[] trangThaiChiTiet = request.getParameterValues("trangThaiChiTiet[]");

        for (int i = 0; i < mauSacIds.length; i++) {
            SanPhamChiTiet spct = new SanPhamChiTiet();

            if (sanPhamChiTietIds != null && sanPhamChiTietIds.length > i && sanPhamChiTietIds[i] != null && !sanPhamChiTietIds[i].isEmpty()) {
                spct.setId(Integer.parseInt(sanPhamChiTietIds[i]));
            }

            if(sanPhamId != null) {
                SanPham sanPham = new SanPham();
                sanPham.setId(sanPhamId);
                spct.setSanPham(sanPham);
            }

            MauSac mauSac = new MauSac();
            mauSac.setId(Integer.parseInt(mauSacIds[i]));
            spct.setMauSac(mauSac);

            KichCo kichCo = new KichCo();
            kichCo.setId(Integer.parseInt(kichCoIds[i]));
            spct.setKichCo(kichCo);

            spct.setMa((ma != null && ma.length > i && ma[i] != null && !ma[i].trim().isEmpty())
                    ? ma[i]
                    : "SP-" + mauSacIds[i] + "-" + kichCoIds[i]);
            spct.setGiaNhap(new BigDecimal(giaNhap[i]));
            spct.setGiaBan(new BigDecimal(giaBan[i]));
            spct.setSoLuongTon(Integer.parseInt(soLuongTon[i]));
            spct.setTrongLuong(Integer.parseInt(trongLuong[i]));
            spct.setTrangThai((trangThaiChiTiet != null && trangThaiChiTiet.length > i && !trangThaiChiTiet[i].isEmpty())
                    ? Integer.parseInt(trangThaiChiTiet[i])
                    : 1);

            // Xử lý ảnh
            String tenFileAnh = anhTheoMau.get(mauSacIds[i]);
            if (tenFileAnh != null) {
                spct.setHinhAnh(tenFileAnh);
            } else if (hinhAnhCu != null && hinhAnhCu.length > i) {
                spct.setHinhAnh(hinhAnhCu[i]);
            }

            danhSach.add(spct);
        }
        return danhSach;
    }

    private Map<String, String> xuLyUploadAnhTheoMau(HttpServletRequest request) throws IOException, ServletException {
        Map<String, String> uploadedFiles = new HashMap<>();
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (Part part : request.getParts()) {
            if (part.getName().startsWith("fileAnh_") && part.getSize() > 0) {
                String mauSacId = part.getName().substring("fileAnh_".length());
                String tenFileGoc = getFileName(part);
                if (tenFileGoc != null && !tenFileGoc.isEmpty()) {
                    String tenFileMoi = System.currentTimeMillis() + "_" + tenFileGoc;
                    String duongDanDayDu = uploadPath + File.separator + tenFileMoi;
                    try (InputStream input = part.getInputStream();
                         FileOutputStream fos = new FileOutputStream(duongDanDayDu)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = input.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                    uploadedFiles.put(mauSacId, tenFileMoi);
                }
            }
        }
        return uploadedFiles;
    }

    private String getFileName(Part part) {
        if (part.getSubmittedFileName() != null) {
            return Paths.get(part.getSubmittedFileName()).getFileName().toString();
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
