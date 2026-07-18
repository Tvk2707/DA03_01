package QuanLySanPham.controller;

import QuanLySanPham.Entity.*;
import QuanLySanPham.Utils.ValidationException;
import QuanLySanPham.service.SanPhamChiTietService;
import QuanLySanPham.service.SanPhamService;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.SanPhamChiTietServiceImpl;
import QuanLySanPham.service.impl.SanPhamServiceImpl;
import QuanLySanPham.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.annotation.MultipartConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class SanPhamServlet extends HttpServlet {
    private final SanPhamService sanPhamService = new SanPhamServiceImpl();
    private final LookupService lookupService = new LookupServiceImpl();
    private final SanPhamChiTietService sanPhamChiTietService = new SanPhamChiTietServiceImpl();

    private static final String UPLOAD_DIR = "File_Anh/images";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/SanPham":
                showSanPham(request, response);
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
            default:
                showSanPham(request, response);
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
            default:
                showSanPham(request, response);
                break;
        }
    }

    private void showSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageStr = request.getParameter("page");
        int page = (pageStr != null && !pageStr.isEmpty()) ? Integer.parseInt(pageStr) : 1;
        int pageSize = 10;

        List<SanPham> items = sanPhamService.layCoPhanTrang(page, pageSize);
        long totalCount = sanPhamService.timKiem("", null, null, null, null).size();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        setLookupAttributes(request);
        request.setAttribute("items", items);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);
        request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPham.jsp").forward(request, response);
    }

    private void showAddSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int count = sanPhamService.timKiem("", null, null, null, null).size();
        String autoMa = String.format("SP%03d", count + 1);
        request.setAttribute("autoMaSanPham", autoMa);
        request.setAttribute("sanPham", new SanPham());
        setLookupAttributes(request);
        request.setAttribute("action", "add");
        request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamAdd.jsp").forward(request, response);
    }

    private void showEditSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            SanPham sanPham = sanPhamService.timTheoId(id);
            if (sanPham == null) {
                response.sendRedirect(request.getContextPath() + "/SanPham");
                return;
            }
            List<SanPhamChiTiet> sanPhamChiTietList = sanPhamChiTietService.timBienTheTheoSanPhamId(id);
            request.setAttribute("sanPham", sanPham);
            request.setAttribute("sanPhamChiTietList", sanPhamChiTietList);
            setLookupAttributes(request);
            request.setAttribute("action", "edit");
            request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamAdd.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/SanPham");
        }
    }

    private void insertSanPham(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SanPham sanPham = getSanPhamFromRequest(request);
        sanPham.setNgayTao(LocalDateTime.now());
        sanPham.setNgaySua(LocalDateTime.now());
        List<SanPhamChiTiet> danhSachBienThe = getDanhSachBienTheFromRequest(request, null, new HashMap<>());

        try {
            Map<String, String> anhTheoMau = xuLyUploadAnhTheoMau(request);
            // Cập nhật lại ảnh cho biến thể sau khi upload
            danhSachBienThe.forEach(spct -> {
                String tenFileAnh = anhTheoMau.get(String.valueOf(spct.getMauSac().getId()));
                if (tenFileAnh != null) {
                    spct.setHinhAnh(tenFileAnh);
                }
            });

            sanPhamService.themSanPhamVaBienThe(sanPham, danhSachBienThe);
            response.sendRedirect(request.getContextPath() + "/SanPham?success=Thêm sản phẩm thành công");
        } catch (ValidationException e) {
            e.printStackTrace();
            request.setAttribute("errors", e.getErrors());
            request.setAttribute("sanPham", sanPham);
            request.setAttribute("sanPhamChiTietList", danhSachBienThe); // Gửi lại danh sách biến thể đã nhập
            setLookupAttributes(request);
            request.setAttribute("action", "add");
            request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamAdd.jsp").forward(request, response);
        }
    }

    private void updateSanPham(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int sanPhamId = Integer.parseInt(request.getParameter("id"));
        SanPham sanPham = getSanPhamFromRequest(request);
        sanPham.setId(sanPhamId);
        sanPham.setNgaySua(LocalDateTime.now());
        List<SanPhamChiTiet> danhSachBienTheTuForm = getDanhSachBienTheFromRequest(request, sanPhamId, new HashMap<>());

        try {
            sanPhamService.capNhatSanPham(sanPham);

            Map<String, String> anhTheoMau = xuLyUploadAnhTheoMau(request);
            danhSachBienTheTuForm.forEach(spct -> {
                String tenFileAnh = anhTheoMau.get(String.valueOf(spct.getMauSac().getId()));
                if (tenFileAnh != null) {
                    spct.setHinhAnh(tenFileAnh);
                }
            });

            List<Integer> existingIdsInDb = sanPhamChiTietService.timBienTheTheoSanPhamId(sanPhamId)
                    .stream()
                    .map(SanPhamChiTiet::getId)
                    .collect(Collectors.toList());

            List<SanPhamChiTiet> listUpdate = danhSachBienTheTuForm.stream().filter(spct -> spct.getId() != null).collect(Collectors.toList());
            List<SanPhamChiTiet> listInsert = danhSachBienTheTuForm.stream().filter(spct -> spct.getId() == null).collect(Collectors.toList());

            if (!listUpdate.isEmpty()) {
                sanPhamChiTietService.capNhatDanhSachBienThe(listUpdate);
            }
            if (!listInsert.isEmpty()) {
                sanPhamChiTietService.themBienThe(listInsert);
            }

            List<Integer> idsFromForm = listUpdate.stream().map(SanPhamChiTiet::getId).collect(Collectors.toList());
            existingIdsInDb.stream().filter(idInDb -> !idsFromForm.contains(idInDb)).forEach(sanPhamChiTietService::xoaBienThe);

            response.sendRedirect(request.getContextPath() + "/SanPham");

        } catch (ValidationException e) {
            e.printStackTrace();
            request.setAttribute("errors", e.getErrors());
            request.setAttribute("sanPham", sanPham);
            request.setAttribute("sanPhamChiTietList", danhSachBienTheTuForm);
            setLookupAttributes(request);
            request.setAttribute("action", "edit");
            request.getRequestDispatcher("/Admin/QuanLySanPham/SanPhamAdd.jsp").forward(request, response);
        }
    }

    private void deleteSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            sanPhamService.xoaSanPham(id);
            response.sendRedirect(request.getContextPath() + "/SanPham");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/SanPham");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            showSanPham(request, response);
        }
    }

    private SanPham getSanPhamFromRequest(HttpServletRequest request) {
        SanPham sanPham = new SanPham();
        sanPham.setTenSanPham(request.getParameter("tenSanPham"));
        sanPham.setMaSanPham(request.getParameter("maSanPham"));
        sanPham.setMoTaChiTiet(request.getParameter("moTaChiTiet"));
        sanPham.setTrangThai(Integer.parseInt(request.getParameter("trangThai")));

        String danhMucIdStr = request.getParameter("danhMucId");
        if (danhMucIdStr != null && !danhMucIdStr.isEmpty()) {
            sanPham.setDanhMuc(new DanhMuc(Integer.parseInt(danhMucIdStr)));
        }
        String thuongHieuIdStr = request.getParameter("thuongHieuId");
        if (thuongHieuIdStr != null && !thuongHieuIdStr.isEmpty()) {
            sanPham.setThuongHieu(new ThuongHieu(Integer.parseInt(thuongHieuIdStr)));
        }
        String chatLieuIdStr = request.getParameter("chatLieuId");
        if (chatLieuIdStr != null && !chatLieuIdStr.isEmpty()) {
            sanPham.setChatLieu(new ChatLieu(Integer.parseInt(chatLieuIdStr)));
        }
        String kieuDangIdStr = request.getParameter("kieuDangId");
        if (kieuDangIdStr != null && !kieuDangIdStr.isEmpty()) {
            sanPham.setKieuDang(new KieuDang(Integer.parseInt(kieuDangIdStr)));
        }
        String trongKinhIdStr = request.getParameter("trongKinhId");
        if (trongKinhIdStr != null && !trongKinhIdStr.isEmpty()) {
            sanPham.setTrongKinh(new TrongKinh(Integer.parseInt(trongKinhIdStr)));
        }
        return sanPham;
    }

    private List<SanPhamChiTiet> getDanhSachBienTheFromRequest(HttpServletRequest request, Integer sanPhamId, Map<String, String> anhTheoMau) {
        List<SanPhamChiTiet> danhSach = new ArrayList<>();
        String[] mauSacIds = request.getParameterValues("mauSacId[]");
        if (mauSacIds == null) return danhSach;

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
            if (sanPhamChiTietIds != null && i < sanPhamChiTietIds.length && sanPhamChiTietIds[i] != null && !sanPhamChiTietIds[i].isEmpty()) {
                spct.setId(Integer.parseInt(sanPhamChiTietIds[i]));
            }
            if (sanPhamId != null) {
                spct.setSanPham(new SanPham(sanPhamId));
            }
            spct.setMauSac(new MauSac(Integer.parseInt(mauSacIds[i])));
            spct.setKichCo(new KichCo(Integer.parseInt(kichCoIds[i])));
            spct.setMa((ma != null && i < ma.length && ma[i] != null && !ma[i].trim().isEmpty()) ? ma[i] : "SP-" + mauSacIds[i] + "-" + kichCoIds[i]);
            spct.setGiaNhap(new BigDecimal(giaNhap[i]));
            spct.setGiaBan(new BigDecimal(giaBan[i]));
            spct.setSoLuongTon(Integer.parseInt(soLuongTon[i]));
            spct.setTrongLuong(Integer.parseInt(trongLuong[i]));
            spct.setTrangThai((trangThaiChiTiet != null && i < trangThaiChiTiet.length && !trangThaiChiTiet[i].isEmpty()) ? Integer.parseInt(trangThaiChiTiet[i]) : 1);

            String tenFileAnh = anhTheoMau.get(mauSacIds[i]);
            if (tenFileAnh != null) {
                spct.setHinhAnh(tenFileAnh);
            } else if (hinhAnhCu != null && i < hinhAnhCu.length) {
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
        if (!uploadDir.exists()) uploadDir.mkdirs();

        for (Part part : request.getParts()) {
            if (part.getName().startsWith("fileAnh_") && part.getSize() > 0) {
                String mauSacId = part.getName().substring("fileAnh_".length());
                String tenFileGoc = getFileName(part);
                if (tenFileGoc != null && !tenFileGoc.isEmpty()) {
                    String tenFileMoi = System.currentTimeMillis() + "_" + tenFileGoc;
                    String duongDanDayDu = uploadPath + File.separator + tenFileMoi;
                    try (InputStream input = part.getInputStream(); FileOutputStream fos = new FileOutputStream(duongDanDayDu)) {
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
        String submittedFileName = part.getSubmittedFileName();
        return submittedFileName != null ? Paths.get(submittedFileName).getFileName().toString() : null;
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
    
    // Các phương thức còn lại không thay đổi
    private void exportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ...
    }
    private void searchSanPham(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ...
    }
}
