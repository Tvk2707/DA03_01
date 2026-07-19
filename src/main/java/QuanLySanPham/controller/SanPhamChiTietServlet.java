package QuanLySanPham.controller;

import QuanLySanPham.Entity.KichCo;
import QuanLySanPham.Entity.MauSac;
import QuanLySanPham.Entity.SanPham;
import QuanLySanPham.Entity.SanPhamChiTiet;
import QuanLySanPham.service.SanPhamChiTietService;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.SanPhamChiTietServiceImpl;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2 MB
        maxFileSize = 1024 * 1024 * 10,       // 10 MB
        maxRequestSize = 1024 * 1024 * 50     // 50 MB
)
@WebServlet(name = "SanPhamChiTietServlet", value = {
        "/SanPhamChiTiet",
        "/SanPhamChiTiet/new",
        "/SanPhamChiTiet/insert",
        "/SanPhamChiTiet/edit",
        "/SanPhamChiTiet/update",
        "/SanPhamChiTiet/update-status", // 🆕 Thêm URL đón nhận request Switch biến thể
        "/SanPhamChiTiet/delete",
        "/SanPhamChiTiet/tonkho",
        "/SanPhamChiTiet/tonkho/update",
        "/SanPhamChiTiet/export" // 🆕 Thêm URL cho tính năng xuất Excel
})
public class SanPhamChiTietServlet extends HttpServlet {
    private SanPhamChiTietService sanPhamChiTietService = new SanPhamChiTietServiceImpl();
    private LookupService lookupService = new LookupServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/SanPhamChiTiet":
                ShowSanPhamChiTiet(request, response);
                break;
            case "/SanPhamChiTiet/new":
                showAddSanPhamChiTiet(request, response);
                break;
            case "/SanPhamChiTiet/edit":
                showEditSanPhamChiTiet(request, response);
                break;
            case "/SanPhamChiTiet/tonkho":
                showTonKho(request, response);
                break;
            case "/SanPhamChiTiet/export": // 🆕 Xử lý request xuất Excel
                exportExcel(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/SanPhamChiTiet/update":
                updateSanPhamChiTiet(request, response);
                break;
            case "/SanPhamChiTiet/update-status": // 🆕 Xử lý thay đổi trạng thái AJAX từ Switch cho biến thể
                updateStatus(request, response);
                break;
            case "/SanPhamChiTiet/tonkho/update":
                updateTonKho(request, response);
                break;
            case "/SanPhamChiTiet/delete":
                deleteSanPhamChiTiet(request, response);
                break;
        }
    }

    // 🆕 PHƯƠNG THỨC MỚI: Xuất dữ liệu biến thể sản phẩm ra file Excel
    private void exportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Lấy các tham số lọc tương tự như khi hiển thị danh sách
        String sanPhamIdStr = request.getParameter("sanPhamId");
        Integer sanPhamId = (sanPhamIdStr != null && !sanPhamIdStr.isEmpty()) ? Integer.parseInt(sanPhamIdStr) : null;
        String ma = request.getParameter("ma");
        String mauSacIdStr = request.getParameter("mauSacId");
        Integer mauSacId = (mauSacIdStr != null && !mauSacIdStr.isEmpty()) ? Integer.parseInt(mauSacIdStr) : null;
        String kichCoIdStr = request.getParameter("kichCoId");
        Integer kichCoId = (kichCoIdStr != null && !kichCoIdStr.isEmpty()) ? Integer.parseInt(kichCoIdStr) : null;
        String trangThaiStr = request.getParameter("trangThai");
        Integer trangThai = (trangThaiStr != null && !trangThaiStr.isEmpty()) ? Integer.parseInt(trangThaiStr) : null;

        // Lấy danh sách dữ liệu đã được lọc
        List<SanPhamChiTiet> items = sanPhamChiTietService.timKiem(sanPhamId, ma, mauSacId, kichCoId, trangThai);

        // Tạo workbook và sheet mới
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Biến thể sản phẩm");

        // Tạo hàng tiêu đề
        String[] headers = {"STT", "Mã biến thể", "Sản phẩm", "Màu sắc", "Kích cỡ", "Giá nhập", "Giá bán", "Tồn kho", "Trạng thái"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Đổ dữ liệu vào các hàng
        int rowNum = 1;
        for (SanPhamChiTiet ct : items) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(ct.getMa());
            row.createCell(2).setCellValue(ct.getSanPham().getTenSanPham());
            row.createCell(3).setCellValue(ct.getMauSac().getTenMau());
            row.createCell(4).setCellValue(ct.getKichCo().getTenKichCo());
            row.createCell(5).setCellValue(ct.getGiaNhap() != null ? ct.getGiaNhap().doubleValue() : 0);
            row.createCell(6).setCellValue(ct.getGiaBan() != null ? ct.getGiaBan().doubleValue() : 0);
            row.createCell(7).setCellValue(ct.getSoLuongTon() != null ? ct.getSoLuongTon() : 0);
            row.createCell(8).setCellValue(ct.getTrangThai() == 1 ? "Đang kinh doanh" : "Ngừng bán");
        }

        // Thiết lập response headers để trình duyệt nhận diện file Excel
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"DS_BienTheSanPham.xlsx\"");

        // Ghi workbook ra output stream của response
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    // 🆕 PHƯƠNG THỨC MỚI: Xử lý cập nhật trạng thái nhanh cho Biến thể qua Switch
    private void updateStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idStr = request.getParameter("id");
            String trangThaiStr = request.getParameter("trangThai");

            if (idStr != null && trangThaiStr != null) {
                Integer id = Integer.parseInt(idStr);
                Integer trangThai = Integer.parseInt(trangThaiStr);

                // Tìm biến thể dựa trên ID nhận về
                // (Vì phương thức timBienThe của bạn yêu cầu cả sanPhamId, nên ở đây ta gọi trực tiếp Service tìm theo danh sách hoặc lấy đối tượng cũ)
                List<SanPhamChiTiet> danhSachHienTai = sanPhamChiTietService.timKiem(null, null, null, null, null);
                SanPhamChiTiet spct = danhSachHienTai.stream()
                        .filter(ct -> ct.getId().equals(id))
                        .findFirst()
                        .orElse(null);

                if (spct != null) {
                    spct.setTrangThai(trangThai);
                    // Thực hiện cập nhật biến thể vào Database thông qua service có sẵn của bạn
                    sanPhamChiTietService.capNhatDanhSachBienThe(List.of(spct));

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Cập nhật trạng thái biến thể thành công!");
                    return;
                }
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Dữ liệu không hợp lệ!");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void ShowSanPhamChiTiet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sanPhamIdStr = request.getParameter("sanPhamId");
        Integer sanPhamId = (sanPhamIdStr != null && !sanPhamIdStr.isEmpty()) ? Integer.parseInt(sanPhamIdStr) : null;

        String ma = request.getParameter("ma");
        String mauSacIdStr = request.getParameter("mauSacId");
        String kichCoIdStr = request.getParameter("kichCoId");
        String trangThaiStr = request.getParameter("trangThai");

        Integer mauSacId = (mauSacIdStr != null && !mauSacIdStr.isEmpty()) ? Integer.parseInt(mauSacIdStr) : null;
        Integer kichCoId = (kichCoIdStr != null && !kichCoIdStr.isEmpty()) ? Integer.parseInt(kichCoIdStr) : null;
        Integer trangThai = (trangThaiStr != null && !trangThaiStr.isEmpty()) ? Integer.parseInt(trangThaiStr) : null;

        List<SanPhamChiTiet> items = sanPhamChiTietService.timKiem(sanPhamId, ma, mauSacId, kichCoId, trangThai);
        if (sanPhamId != null && items != null && !items.isEmpty()) {
            SanPham sanPhamDuocLoc = items.get(0).getSanPham();
            request.setAttribute("sanPhamDuocLoc", sanPhamDuocLoc);
        }

        request.setAttribute("items", items);
        request.setAttribute("sanPhamId", sanPhamId);

        request.setAttribute("searchMa", ma);
        request.setAttribute("searchMauSacId", mauSacId);
        request.setAttribute("searchKichCoId", kichCoId);
        request.setAttribute("searchTrangThai", trangThaiStr);

        setLookupAttributes(request);

        request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPhamChiTiet.jsp").forward(request, response);
    }

    private void showAddSanPhamChiTiet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer sanPhamId = Integer.parseInt(request.getParameter("sanPhamId"));
        setLookupAttributes(request);
        request.setAttribute("sanPhamId", sanPhamId);
        request.setAttribute("action", "add");
        request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPhamChiTiet.jsp").forward(request, response);
    }

    private void showEditSanPhamChiTiet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer sanPhamId = Integer.parseInt(request.getParameter("sanPhamId"));
        Integer id = Integer.parseInt(request.getParameter("id"));

        SanPhamChiTiet sanPhamChiTiet = timBienThe(sanPhamId, id);
        if (sanPhamChiTiet == null) {
            request.setAttribute("error", "Không tìm thấy biến thể");
            ShowSanPhamChiTiet(request, response);
            return;
        }

        request.setAttribute("sanPhamChiTiet", sanPhamChiTiet);
        setLookupAttributes(request);
        request.setAttribute("sanPhamId", sanPhamId);
        request.setAttribute("action", "edit");
        request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPhamChiTiet.jsp").forward(request, response);
    }


    private void updateSanPhamChiTiet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Integer sanPhamId = Integer.parseInt(request.getParameter("sanPhamId"));
        Integer id = Integer.parseInt(request.getParameter("id"));

        SanPhamChiTiet sanPhamChiTiet = getSanPhamChiTietForm(request, sanPhamId);
        sanPhamChiTiet.setId(id);

        if (sanPhamChiTiet.getSoLuongTon() != null && sanPhamChiTiet.getSoLuongTon() < 0) {
            request.setAttribute("errorMessage", "Số lượng tồn phải >= 0!");
            request.setAttribute("sanPhamChiTiet", sanPhamChiTiet);
            setLookupAttributes(request);
            request.setAttribute("sanPhamId", sanPhamId);
            request.setAttribute("action", "edit");
            request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPhamChiTiet.jsp").forward(request, response);
            return;
        }

        sanPhamChiTietService.capNhatDanhSachBienThe(List.of(sanPhamChiTiet));
        response.sendRedirect(request.getContextPath() + "/SanPhamChiTiet");
    }

    private void deleteSanPhamChiTiet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String idStr = request.getParameter("id");

        try {
            if (idStr != null && !idStr.trim().isEmpty()) {
                Integer id = Integer.parseInt(idStr);
                sanPhamChiTietService.xoaBienThe(id);
                response.sendRedirect(request.getContextPath() + "/SanPhamChiTiet");
            } else {
                throw new IllegalArgumentException("Không nhận được ID biến thể để xóa.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi chi tiết: " + e.getMessage());
            request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPhamChiTiet.jsp").forward(request, response);
        }
    }

    private void showTonKho(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer sanPhamId = Integer.parseInt(request.getParameter("sanPhamId"));
        Integer id = Integer.parseInt(request.getParameter("id"));

        SanPhamChiTiet sanPhamChiTiet = timBienThe(sanPhamId, id);
        if (sanPhamChiTiet == null) {
            request.setAttribute("error", "Không tìm thấy biến thể");
            ShowSanPhamChiTiet(request, response);
            return;
        }

        request.setAttribute("sanPhamChiTiet", sanPhamChiTiet);
        request.setAttribute("sanPhamId", sanPhamId);
        request.setAttribute("action", "tonkho");
        request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPhamChiTiet.jsp").forward(request, response);
    }

    private void updateTonKho(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Integer sanPhamId = Integer.parseInt(request.getParameter("sanPhamId"));
        Integer id = Integer.parseInt(request.getParameter("id"));
        Integer soLuongThayDoi = Integer.parseInt(request.getParameter("soLuongThayDoi"));

        sanPhamChiTietService.capNhatTonKho(id, soLuongThayDoi);
        response.sendRedirect(request.getContextPath() + "/SanPhamChiTiet?sanPhamId=" + sanPhamId);
    }

    private SanPhamChiTiet getSanPhamChiTietForm(HttpServletRequest request, Integer sanPhamId) throws IOException, ServletException {
        String ma = request.getParameter("ma");
        String mauSacIdStr = request.getParameter("mauSacId");
        String kichCoIdStr = request.getParameter("kichCoId");
        String giaNhapStr = request.getParameter("giaNhap");
        String giaBanStr = request.getParameter("giaBan");
        String soLuongTonStr = request.getParameter("soLuongTon");
        String trongLuongStr = request.getParameter("trongLuong");
        String trangThaiStr = request.getParameter("trangThai");

        String hinhAnhCu = request.getParameter("hinhAnhCu");
        String tenFileAnhHienTai = "";

        Part filePart = request.getPart("hinhAnh");
        if (filePart != null && filePart.getSize() > 0) {
            String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            tenFileAnhHienTai = System.currentTimeMillis() + "_" + originalFileName;

            String uploadPath = getServletContext().getRealPath("/") + "File_Anh" + File.separator + "images";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            filePart.write(uploadPath + File.separator + tenFileAnhHienTai);
        } else {
            tenFileAnhHienTai = hinhAnhCu;
        }

        SanPhamChiTiet sanPhamChiTiet = new SanPhamChiTiet();

        SanPham sanPham = new SanPham();
        sanPham.setId(sanPhamId);
        sanPhamChiTiet.setSanPham(sanPham);

        if (mauSacIdStr != null && !mauSacIdStr.isEmpty()) {
            MauSac mauSac = new MauSac();
            mauSac.setId(Integer.parseInt(mauSacIdStr));
            sanPhamChiTiet.setMauSac(mauSac);
        }
        if (kichCoIdStr != null && !kichCoIdStr.isEmpty()) {
            KichCo kichCo = new KichCo();
            kichCo.setId(Integer.parseInt(kichCoIdStr));
            sanPhamChiTiet.setKichCo(kichCo);
        }

        sanPhamChiTiet.setMa(ma);
        sanPhamChiTiet.setHinhAnh(tenFileAnhHienTai);
        sanPhamChiTiet.setTrangThai((trangThaiStr != null && !trangThaiStr.isEmpty()) ? Integer.parseInt(trangThaiStr) : 1);

        if (giaNhapStr != null && !giaNhapStr.isEmpty()) {
            sanPhamChiTiet.setGiaNhap(new BigDecimal(giaNhapStr));
        }
        if (giaBanStr != null && !giaBanStr.isEmpty()) {
            sanPhamChiTiet.setGiaBan(new BigDecimal(giaBanStr));
        }
        if (soLuongTonStr != null && !soLuongTonStr.isEmpty()) {
            sanPhamChiTiet.setSoLuongTon(Integer.parseInt(soLuongTonStr));
        }
        if (trongLuongStr != null && !trongLuongStr.isEmpty()) {
            sanPhamChiTiet.setTrongLuong(Integer.parseInt(trongLuongStr));
        }

        return sanPhamChiTiet;
    }

    private void setLookupAttributes(HttpServletRequest request) {
        request.setAttribute("mauSacList", lookupService.layTatCaMauSac());
        request.setAttribute("kichCoList", lookupService.layTatCaKichCo());
    }

    private SanPhamChiTiet timBienThe(Integer sanPhamId, Integer id) {
        return sanPhamChiTietService.timBienTheTheoSanPhamId(sanPhamId)
                .stream()
                .filter(ct -> ct.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}