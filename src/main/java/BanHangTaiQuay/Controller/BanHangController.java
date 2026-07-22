package BanHangTaiQuay.Controller;

import BanHangTaiQuay.Service.BanHangService;
import BanHangTaiQuay.Service.BanHangServiceImpl;
import BanHangTaiQuay.Service.CaLamViecService;
import BanHangTaiQuay.Service.CaLamViecServiceImpl;
import BanHangTaiQuay.Model.ApVoucherRequest;
import BanHangTaiQuay.Model.ChonKhachHangRequest;
import BanHangTaiQuay.Model.ThanhToanRequest;
import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.KhachHang;
import QuanLySanPham.Entity.NhanVien;
import QuanLySanPham.Entity.SanPhamChiTiet;
import QuanLySanPham.service.SanPhamChiTietService;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import QuanLySanPham.service.impl.SanPhamChiTietServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({
        "/ban-hang",
        "/ban-hang/tao-hoa-don",
        "/ban-hang/tim-san-pham",
        "/ban-hang/quet-qr",
        "/ban-hang/them-san-pham",
        "/ban-hang/xoa-san-pham",
        "/ban-hang/cap-nhat-so-luong",
        "/ban-hang/tra-cuu-khach-hang",
        "/ban-hang/ap-voucher",
        "/ban-hang/huy-hoa-don",
        "/ban-hang/lay-hoa-don-cho",
        "/ban-hang/gan-khach-hang",
        "/ban-hang/chon-khach-le",
        "/thanh-toan/thanh-toan"
})
public class BanHangController extends HttpServlet {

    private final BanHangService banHangService = new BanHangServiceImpl();
    private final CaLamViecService caLamViecService = new CaLamViecServiceImpl();
    private final SanPhamChiTietService sanPhamChiTietService = new SanPhamChiTietServiceImpl();
    private final LookupService lookupService = new LookupServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getServletPath();

        if (action == null || action.equals("/ban-hang")) {
            HttpSession session = req.getSession();
            Integer idNhanVien = layIdNhanVien(session);
            List<HoaDon> danhSachHoaDonCho = idNhanVien == null
                    ? List.of()
                    : banHangService.layDanhSachHoaDonCho(idNhanVien);
            req.setAttribute("danhSachHoaDonCho", danhSachHoaDonCho);

            Integer idHoaDonDangTao = parseOptionalPositiveInt(req.getParameter("id"));
            if (idHoaDonDangTao == null && !danhSachHoaDonCho.isEmpty()) {
                idHoaDonDangTao = danhSachHoaDonCho.get(0).getId();
            }
            HoaDon hoaDonDangTao = idHoaDonDangTao == null
                    ? null
                    : banHangService.layHoaDonTheoId(idHoaDonDangTao);
            req.setAttribute("idHoaDonDangTao", idHoaDonDangTao);
            req.setAttribute("hoaDonDangTao", hoaDonDangTao);
            req.setAttribute("danhSachSanPham",
                    sanPhamChiTietService.timKiem(null, null, null, null, 1));
            req.setAttribute("danhSachDanhMuc", lookupService.layTatCaDanhMuc().stream()
                    .filter(danhMuc -> danhMuc.getTrangThai() == null || danhMuc.getTrangThai() == 1)
                    .toList());
            req.getRequestDispatcher("/Admin/BanHangTaiQuay/ban-hang.jsp").forward(req, resp);
            return;
        }

        switch (action) {
            case "/ban-hang/tim-san-pham":
                timSanPham(req, resp);
                break;
            case "/ban-hang/quet-qr":
                quetQr(req, resp);
                break;
            case "/ban-hang/tra-cuu-khach-hang":
                traCuuKhachHang(req, resp, false);
                break;
            case "/ban-hang/lay-hoa-don-cho":
                layHoaDonCho(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();
        switch (path) {
            case "/ban-hang/tao-hoa-don":
                taoHoaDon(req, resp);
                break;
            case "/ban-hang/them-san-pham":
                themSanPham(req, resp);
                break;
            case "/ban-hang/xoa-san-pham":
                xoaSanPham(req, resp);
                break;
            case "/ban-hang/cap-nhat-so-luong":
                capNhatSoLuong(req, resp);
                break;
            case "/ban-hang/tra-cuu-khach-hang":
                traCuuKhachHang(req, resp, true);
                break;
            case "/ban-hang/ap-voucher":
                apVoucher(req, resp);
                break;
            case "/ban-hang/huy-hoa-don":
                huyHoaDon(req, resp);
                break;
            case "/ban-hang/gan-khach-hang":
                ganKhachHang(req, resp);
                break;
            case "/ban-hang/chon-khach-le":
                chonKhachLe(req, resp);
                break;
            case "/thanh-toan/thanh-toan":
                thanhToan(req, resp);
                break;
        }
    }

    private void taoHoaDon(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            HttpSession session = req.getSession();
            Integer idNhanVien = layIdNhanVien(session);
            Integer idCa = idNhanVien == null ? null : caLamViecService.layCaDangMo(idNhanVien);

            if (idNhanVien == null) {
                throw new IllegalStateException("Nhân viên chưa đăng nhập.");
            }
            if (idCa == null) {
                session.removeAttribute("idCa");
                throw new IllegalStateException("Chưa có ca làm việc đang mở cho nhân viên này.");
            }
            session.setAttribute("idCa", idCa);

            HoaDon hoaDon = banHangService.taoHoaDonMoi(idNhanVien, idCa);
            response.put("success", true);
            //response.put("hoaDon", hoaDon);
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void timSanPham(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        Integer idDanhMuc = parseOptionalPositiveInt(req.getParameter("idDanhMuc"));
        List<SanPhamChiTiet> sanPhams = sanPhamChiTietService.timKiemTheoDanhMuc(keyword, idDanhMuc, 1);
        req.setAttribute("sanPhams", sanPhams);
        req.getRequestDispatcher("/Admin/BanHangTaiQuay/_product-grid.jsp").forward(req, resp);
    }

    private void quetQr(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, Object> response = new HashMap<>();

        try {
            String ma = requireText(req, "ma");
            SanPhamChiTiet sanPham = sanPhamChiTietService.timKiemTheoDanhMuc(ma, null, 1).stream()
                    .filter(item -> item.getMa() != null && item.getMa().equalsIgnoreCase(ma))
                    .findFirst()
                    .orElse(null);
            if (sanPham == null) {
                throw new IllegalArgumentException("Không tìm thấy sản phẩm theo mã QR: " + ma);
            }

            response.put("success", true);
            response.put("idSanPhamChiTiet", sanPham.getId());
            response.put("ma", sanPham.getMa());
            response.put("tenSanPham", sanPham.getSanPham() == null ? "" : sanPham.getSanPham().getTenSanPham());
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        resp.getWriter().print(gson.toJson(response));
    }

    private void themSanPham(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            int idHoaDon = Integer.parseInt(req.getParameter("idHoaDon"));
            int idSanPhamChiTiet = Integer.parseInt(req.getParameter("idSanPhamChiTiet"));
            int soLuong = Integer.parseInt(req.getParameter("soLuong"));

            banHangService.themSanPhamVaoGio(idHoaDon, idSanPhamChiTiet, soLuong);
            response.put("success", true);
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());

        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void xoaSanPham(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            int idHoaDon = Integer.parseInt(req.getParameter("idHoaDon"));
            int idChiTiet = Integer.parseInt(req.getParameter("idChiTiet"));

            banHangService.xoaSanPhamKhoiGio(idHoaDon, idChiTiet);
            response.put("success", true);
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());

        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void capNhatSoLuong(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            int idChiTiet = Integer.parseInt(req.getParameter("idChiTiet"));
            int soLuongMoi = Integer.parseInt(req.getParameter("soLuongMoi"));

            banHangService.capNhatSoLuong(idChiTiet, soLuongMoi);
            response.put("success", true);
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());

        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void traCuuKhachHang(HttpServletRequest req, HttpServletResponse resp, boolean createIfNotFound) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            ChonKhachHangRequest request = new ChonKhachHangRequest();
            request.setSoDienThoai(requireText(req, "soDienThoai"));
            request.setHoTen(createIfNotFound ? optionalText(req, "hoTen") : null);

            KhachHang khachHang = createIfNotFound
                    ? banHangService.traCuuHoacTaoKhachHang(request.getSoDienThoai(), request.getHoTen())
                    : banHangService.traCuuKhachHang(request.getSoDienThoai());
            response.put("success", true);
            response.put("khachHang", khachHang == null ? null : toKhachHangData(khachHang));
            response.put("found", khachHang != null);

        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void apVoucher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            ApVoucherRequest request = new ApVoucherRequest();
            request.setIdHoaDon(requirePositiveInt(req, "idHoaDon"));
            request.setMaVoucher(requireText(req, "maVoucher"));

            banHangService.apDungVoucher(request.getIdHoaDon(), request.getMaVoucher());
            response.put("success", true);
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());

        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void huyHoaDon(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            int idHoaDon = requirePositiveInt(req, "idHoaDon");
            String lyDo = optionalText(req, "lyDo");
            if (lyDo == null || lyDo.isEmpty()) {
                lyDo = "Không nêu lý do";
            }

            banHangService.huyHoaDon(idHoaDon, lyDo);
            response.put("success", true);
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void layHoaDonCho(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            HttpSession session = req.getSession();
            Integer idNhanVien = layIdNhanVien(session);

            if (idNhanVien == null) {
                throw new IllegalStateException("Nhân viên chưa đăng nhập.");
            }

            List<HoaDon> hoaDons = banHangService.layDanhSachHoaDonCho(idNhanVien);
            response.put("success", true);
            response.put("hoaDons", hoaDons.stream().map(this::toHoaDonData).toList());

        } catch (IllegalStateException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void ganKhachHang(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            int idHoaDon = requirePositiveInt(req, "idHoaDon");
            int idKhachHang = requirePositiveInt(req, "idKhachHang");
            banHangService.ganKhachHang(idHoaDon, idKhachHang);
            response.put("success", true);
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void chonKhachLe(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            int idHoaDon = requirePositiveInt(req, "idHoaDon");
            banHangService.chonKhachLe(idHoaDon);
            response.put("success", true);
            response.put("message", "Đã chuyển hóa đơn sang khách lẻ.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void thanhToan(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            ThanhToanRequest request = new ThanhToanRequest();
            request.setIdHoaDon(requirePositiveInt(req, "idHoaDon"));
            request.setMaPttt(requireText(req, "maPttt").toUpperCase());
            request.setSoTienKhachDua(requirePositiveAmount(req, "soTienKhachDua"));
            request.setMaGiaoDich(optionalText(req, "maGiaoDich"));
            request.setGhiChu(optionalText(req, "ghiChu"));

            HoaDon hoaDon = banHangService.layHoaDonTheoId(request.getIdHoaDon());
            if (hoaDon == null) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại.");
            }

            BigDecimal tongTien = hoaDon.getTongTienThanhToan() == null
                    ? BigDecimal.ZERO
                    : hoaDon.getTongTienThanhToan();
            banHangService.xacNhanThanhToan(
                    request.getIdHoaDon(),
                    request.getMaPttt(),
                    request.getSoTienKhachDua(),
                    request.getMaGiaoDich(),
                    request.getGhiChu());

            response.put("success", true);
            response.put("message", "Thanh toán thành công.");
            response.put("soTienThanhToan", tongTien);
            if ("PTTT001".equals(request.getMaPttt())) {
                response.put("tienThoi", request.getSoTienKhachDua()
                        .subtract(tongTien).setScale(0, RoundingMode.HALF_UP));
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private Integer layIdNhanVien(HttpSession session) {
        Object idNhanVien = session.getAttribute("idNhanVien");
        if (idNhanVien instanceof Number) {
            return ((Number) idNhanVien).intValue();
        }

        Object nhanVienDangNhap = session.getAttribute("nhanVienDangNhap");
        if (nhanVienDangNhap instanceof NhanVien) {
            Integer id = ((NhanVien) nhanVienDangNhap).getId();
            if (id != null) {
                session.setAttribute("idNhanVien", id);
                return id;
            }
        }
        return null;
    }

    private int requirePositiveInt(HttpServletRequest req, String parameterName) {
        String value = requireText(req, parameterName);
        try {
            int parsedValue = Integer.parseInt(value);
            if (parsedValue <= 0) {
                throw new IllegalArgumentException(parameterName + " phải lớn hơn 0.");
            }
            return parsedValue;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(parameterName + " phải là số nguyên hợp lệ.");
        }
    }

    private Integer parseOptionalPositiveInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String requireText(HttpServletRequest req, String parameterName) {
        String value = req.getParameter(parameterName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Thiếu tham số: " + parameterName + ".");
        }
        return value.trim();
    }

    private String optionalText(HttpServletRequest req, String parameterName) {
        String value = req.getParameter(parameterName);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private BigDecimal requirePositiveAmount(HttpServletRequest req, String parameterName) {
        String value = requireText(req, parameterName).replace(",", "").replace(".", "");
        try {
            BigDecimal amount = new BigDecimal(value);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(parameterName + " phải lớn hơn 0.");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(parameterName + " phải là số tiền hợp lệ.");
        }
    }

    private Map<String, Object> toKhachHangData(KhachHang khachHang) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", khachHang.getId());
        data.put("hoTen", khachHang.getHoTen());
        data.put("soDienThoai", khachHang.getSoDienThoai());
        data.put("email", khachHang.getEmail());
        return data;
    }

    private Map<String, Object> toHoaDonData(HoaDon hoaDon) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", hoaDon.getId());
        data.put("maHoaDon", hoaDon.getMaHoaDon());
        data.put("tongTienThanhToan", hoaDon.getTongTienThanhToan());
        data.put("trangThai", hoaDon.getTrangThai());
        data.put("ngayTao", hoaDon.getNgayTao() == null ? null : hoaDon.getNgayTao().toString());
        return data;
    }
}
