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
        "/SanPhamChiTiet/delete",
        "/SanPhamChiTiet/tonkho",
        "/SanPhamChiTiet/tonkho/update",
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
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {

            case "/SanPhamChiTiet/update":
                updateSanPhamChiTiet(request, response);
                break;
            case "/SanPhamChiTiet/tonkho/update":
                updateTonKho(request, response);
                break;
            case "/SanPhamChiTiet/delete":
                deleteSanPhamChiTiet(request, response);
                break;
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
        // Neu dang loc theo sanPhamId va tim thay it nhat 1 bien the,
        // lay thong tin San pham cha gui ra giao dien de hien thi ten/ma tren thong bao
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

        // Chuyển hướng kèm theo sanPhamId để tải lại đúng danh sách biến thể của sản phẩm đó
        response.sendRedirect(request.getContextPath() + "/SanPhamChiTiet");
    }

    private void deleteSanPhamChiTiet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String idStr = request.getParameter("id");
        String sanPhamIdStr = request.getParameter("sanPhamId");

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

    /**
     * Lấy dữ liệu SanPhamChiTiet từ request và xử lý logic Upload ảnh đa phần (Multipart)
     */
    private SanPhamChiTiet getSanPhamChiTietForm(HttpServletRequest request, Integer sanPhamId) throws IOException, ServletException {
        String ma = request.getParameter("ma");
        String mauSacIdStr = request.getParameter("mauSacId");
        String kichCoIdStr = request.getParameter("kichCoId");
        String giaNhapStr = request.getParameter("giaNhap");
        String giaBanStr = request.getParameter("giaBan");
        String soLuongTonStr = request.getParameter("soLuongTon");
        String trongLuongStr = request.getParameter("trongLuong");
        String trangThaiStr = request.getParameter("trangThai");

        // Đọc trường dữ liệu ẩn chứa tên ảnh hiện tại từ JSP
        String hinhAnhCu = request.getParameter("hinhAnhCu");

        String tenFileAnhHienTai = "";

        // Xử lý đọc File nhị phân từ input file name="hinhAnh"
        Part filePart = request.getPart("hinhAnh");
        if (filePart != null && filePart.getSize() > 0) {
            // Trường hợp 1: Người dùng CÓ chọn file ảnh mới upload lên
            String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

            // Định dạng tên file tránh trùng: Sử dụng thời gian hệ thống + tên gốc
            tenFileAnhHienTai = System.currentTimeMillis() + "_" + originalFileName;

            // Đường dẫn vật lý lưu ảnh thực tế trên server của bạn
            String uploadPath = getServletContext().getRealPath("/") + "File_Anh" + File.separator + "images";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Tự động tạo thư mục File_Anh/images nếu chưa tồn tại
            }

            // Ghi dữ liệu file thực tế xuống ổ đĩa cứng
            filePart.write(uploadPath + File.separator + tenFileAnhHienTai);
        } else {
            // Trường hợp 2: Người dùng KHÔNG chọn ảnh mới -> Giữ lại ảnh cũ để tránh lỗi No Image
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
        sanPhamChiTiet.setHinhAnh(tenFileAnhHienTai); // Đưa tên file ảnh chuẩn vào Entity để lưu DB
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