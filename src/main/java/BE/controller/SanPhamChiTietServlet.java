package BE.controller;

import BE.Entity.*;
import BE.service.SanPhamChiTietService;
import BE.service.LookupService;
import BE.service.impl.SanPhamChiTietServiceImpl;
import BE.service.impl.LookupServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

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
            case "/SanPhamChiTiet/insert":
                insertSanPhamChiTiet(request, response);
                break;
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

        request.setAttribute("items", items);
        request.setAttribute("sanPhamId", sanPhamId);

        // Giữ lại giá trị đã lọc để hiển thị lại trên form
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
    private void insertSanPhamChiTiet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Integer sanPhamId = Integer.parseInt(request.getParameter("sanPhamId"));
        SanPhamChiTiet sanPhamChiTiet = getSanPhamChiTietFron(request, sanPhamId);

        try {
            if (sanPhamChiTiet.getSoLuongTon() != null && sanPhamChiTiet.getSoLuongTon() < 0) {
                throw new RuntimeException("Số lượng tồn phải >= 0!");
            }
            sanPhamChiTietService.themBienThe(sanPhamChiTiet);
            response.sendRedirect(request.getContextPath() + "/SanPhamChiTiet?sanPhamId=" + sanPhamId);
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("sanPhamChiTiet", sanPhamChiTiet);
            setLookupAttributes(request);
            request.setAttribute("sanPhamId", sanPhamId);
            request.setAttribute("action", "add");
            request.getRequestDispatcher("/Admin/QuanLySanPham/QuanLySanPhamChiTiet.jsp").forward(request, response);
        }
    }

    private void updateSanPhamChiTiet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Integer sanPhamId = Integer.parseInt(request.getParameter("sanPhamId"));
        Integer id = Integer.parseInt(request.getParameter("id"));
        SanPhamChiTiet sanPhamChiTiet = getSanPhamChiTietFron(request, sanPhamId);
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

        sanPhamChiTietService.capNhatBienThe(sanPhamChiTiet);
        response.sendRedirect(request.getContextPath() + "/SanPhamChiTiet?sanPhamId=" + sanPhamId);
    }
    private void deleteSanPhamChiTiet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String idStr = request.getParameter("id");


        try {
            // 2. Chỉ thực hiện xóa nếu nhận được ID biến thể hợp lệ
            if (idStr != null && !idStr.trim().isEmpty()) {
                Integer id = Integer.parseInt(idStr);
                sanPhamChiTietService.xoaBienThe(id); // Gọi xóa mềm

                // 3. Chỉ redirect khi thành công hoàn toàn
             response.sendRedirect(request.getContextPath() + "/SanPhamChiTiet");
            } else {
                throw new IllegalArgumentException("Không nhận được ID biến thể để xóa.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // 1. In lỗi ra Console của IDE để xem nguyên nhân gốc

            // 2. Forward thay vì redirect để giữ lại thông báo lỗi hiển thị lên JSP
            request.setAttribute("error", "Lỗi chi tiết: " + e.getMessage());

            // Bạn nhớ kiểm tra lại đường dẫn file JSP biến thể này đã chuẩn với project của bạn chưa nhé
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
     * Lấy dữ liệu SanPhamChiTiet từ request (dùng chung cho insert/update)
     */
    private SanPhamChiTiet getSanPhamChiTietFron(HttpServletRequest request, Integer sanPhamId) {
        String ma = request.getParameter("ma");
        String mauSacIdStr = request.getParameter("mauSacId");
        String kichCoIdStr = request.getParameter("kichCoId");
        String giaNhapStr = request.getParameter("giaNhap");
        String giaBanStr = request.getParameter("giaBan");
        String soLuongTonStr = request.getParameter("soLuongTon");
        String trongLuongStr = request.getParameter("trongLuong");
        String hinhAnh = request.getParameter("hinhAnh");
        String trangThaiStr = request.getParameter("trangThai");

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
        sanPhamChiTiet.setHinhAnh(hinhAnh);
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

    /**
     * Tìm biến thể theo sanPhamId + id
     */
    private SanPhamChiTiet timBienThe(Integer sanPhamId, Integer id) {
        return sanPhamChiTietService.layTheoSanPham(sanPhamId)
                .stream()
                .filter(ct -> ct.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Đổ dữ liệu dropdown cho form thêm/sửa
     */
    private void setLookupAttributes(HttpServletRequest request) {
        request.setAttribute("mauSacList", lookupService.layTatCaMauSac());
        request.setAttribute("kichCoList", lookupService.layTatCaKichCo());
    }
}