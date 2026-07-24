package BanHangTaiQuay.Controller;

import BanHangTaiQuay.Service.BanHangService;
import BanHangTaiQuay.Service.BanHangServiceImpl;
import BanHangTaiQuay.Service.CaLamViecService;
import BanHangTaiQuay.Service.CaLamViecServiceImpl;
import BanHangTaiQuay.Model.ApVoucherRequest;
import BanHangTaiQuay.Model.ChonKhachHangRequest;
import BanHangTaiQuay.Model.CapNhatSoLuongRequest;
import BanHangTaiQuay.Model.HoaDonCreateRequest;
import BanHangTaiQuay.Model.ThanhToanRequest;
import BanHangTaiQuay.Model.ThemSanPhamRequest;
import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.KhachHang;
import QuanLySanPham.Entity.NhanVien;
import QuanLySanPham.Entity.SanPhamChiTiet;
import QuanLySanPham.service.SanPhamChiTietService;
import QuanLySanPham.service.LookupService;
import QuanLySanPham.service.impl.LookupServiceImpl;
import QuanLySanPham.service.impl.SanPhamChiTietServiceImpl;
import QuanLyNhanVien.service.NhanVienService;
import QuanLyNhanVien.service.impl.NhanVienServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@WebServlet({
        "/ban-hang",
        "/ban-hang/tao-hoa-don",
        "/ban-hang/tim-san-pham",
        "/ban-hang/quet-qr",
        "/ban-hang/them-san-pham",
        "/ban-hang/them-nhieu-san-pham",
        "/ban-hang/xoa-san-pham",
        "/ban-hang/cap-nhat-so-luong",
        "/ban-hang/tra-cuu-khach-hang",
        "/ban-hang/tim-khach-hang",
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
    private final NhanVienService nhanVienService = new NhanVienServiceImpl();
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
            Integer idHoaDonDangTao = parseOptionalPositiveInt(req.getParameter("id"));
            if (idHoaDonDangTao == null && idNhanVien == null) {
                idHoaDonDangTao = parseSessionInvoiceId(session);
            }
            if (idHoaDonDangTao == null && !danhSachHoaDonCho.isEmpty()) {
                idHoaDonDangTao = danhSachHoaDonCho.get(0).getId();
            }
            HoaDon hoaDonDangTao = idHoaDonDangTao == null
                    ? null
                    : banHangService.layHoaDonTheoId(idHoaDonDangTao);
            if (idNhanVien == null && hoaDonDangTao != null && hoaDonDangTao.getTrangThai() == 0) {
                danhSachHoaDonCho = List.of(hoaDonDangTao);
            }
            req.setAttribute("danhSachHoaDonCho", danhSachHoaDonCho);
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
            case "/ban-hang/tim-khach-hang":
                timKhachHang(req, resp);
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
            case "/ban-hang/them-nhieu-san-pham":
                themNhieuSanPham(req, resp);
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
            Integer idCa = idNhanVien == null ? null : caLamViecService.layHoacTaoCaDangMo(idNhanVien);

            if (idCa != null) {
                session.setAttribute("idCa", idCa);
            }

            HoaDonCreateRequest request = new HoaDonCreateRequest();
            request.setIdNhanVien(idNhanVien);
            request.setIdCa(idCa);
            HoaDon hoaDon = banHangService.taoHoaDonMoi(request.getIdNhanVien(), request.getIdCa());
            session.setAttribute("idHoaDonDangTao", hoaDon.getId());
            response.put("success", true);
            response.put("idHoaDon", hoaDon.getId());
            response.put("maHoaDon", hoaDon.getMaHoaDon());
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void timSanPham(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = optionalText(req, "keyword");
        Integer idDanhMuc = parseOptionalPositiveInt(req.getParameter("idDanhMuc"));
        List<SanPhamChiTiet> sanPhams = sanPhamChiTietService
                .timKiemTheoDanhMuc(null, idDanhMuc, 1);

        if (keyword != null) {
            String tuKhoa = keyword.toLowerCase(Locale.ROOT);
            sanPhams = sanPhams.stream()
                    .filter(item -> (item.getMa() != null
                            && item.getMa().toLowerCase(Locale.ROOT).contains(tuKhoa))
                            || (item.getSanPham() != null
                            && item.getSanPham().getTenSanPham() != null
                            && item.getSanPham().getTenSanPham().toLowerCase(Locale.ROOT).contains(tuKhoa)))
                    .toList();
        }
        req.setAttribute("sanPhams", sanPhams);
        req.getRequestDispatcher("/Admin/BanHangTaiQuay/_product-grid.jsp").forward(req, resp);
    }

    private void quetQr(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, Object> response = new HashMap<>();

        try {
            String ma = requireText(req, "ma");
            SanPhamChiTiet sanPham = sanPhamChiTietService.timKiem(null, ma, null, null, 1).stream()
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
            ThemSanPhamRequest request = new ThemSanPhamRequest();
            request.setIdHoaDon(requirePositiveInt(req, "idHoaDon"));
            request.setIdSanPhamChiTiet(requirePositiveInt(req, "idSanPhamChiTiet"));
            request.setSoLuong(requirePositiveInt(req, "soLuong"));

            banHangService.themSanPhamVaoGio(
                    request.getIdHoaDon(), request.getIdSanPhamChiTiet(), request.getSoLuong());
            response.put("success", true);
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.put("success", false);
            response.put("message", thongBaoLoi(e));
            getServletContext().log("Lỗi khi thêm sản phẩm vào hóa đơn POS.", e);
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
            int idHoaDon = requirePositiveInt(req, "idHoaDon");
            int idChiTiet = requirePositiveInt(req, "idChiTiet");

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
            CapNhatSoLuongRequest request = new CapNhatSoLuongRequest();
            request.setIdChiTiet(requirePositiveInt(req, "idChiTiet"));
            request.setSoLuongMoi(requirePositiveInt(req, "soLuongMoi"));

            banHangService.capNhatSoLuong(request.getIdChiTiet(), request.getSoLuongMoi());
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
            request.setSoDienThoai(createIfNotFound
                    ? optionalText(req, "soDienThoai")
                    : requireText(req, "soDienThoai"));
            request.setHoTen(createIfNotFound ? optionalText(req, "hoTen") : null);

            KhachHang khachHang;
            if (createIfNotFound) {
                request.setEmail(optionalText(req, "email"));
                request.setMatKhau(optionalText(req, "matKhau"));
                request.setNgaySinh(optionalText(req, "ngaySinh"));
                request.setGioiTinh(parseOptionalGioiTinh(optionalText(req, "gioiTinh")));
                khachHang = banHangService.traCuuHoacTaoKhachHang(
                        request.getSoDienThoai(),
                        request.getHoTen(),
                        request.getEmail(),
                        parseOptionalNgaySinh(request.getNgaySinh()),
                        request.getGioiTinh(),
                        request.getMatKhau()
                );
            } else {
                khachHang = banHangService.traCuuKhachHang(request.getSoDienThoai());
            }
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

    private void themNhieuSanPham(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            int idHoaDon = requirePositiveInt(req, "idHoaDon");
            String rawItems = requireText(req, "danhSachSanPham");
            List<ThemSanPhamRequest> items = gson.fromJson(
                    rawItems,
                    new TypeToken<List<ThemSanPhamRequest>>() { }.getType()
            );
            if (items == null || items.isEmpty()) {
                throw new IllegalArgumentException("Danh sách sản phẩm không được để trống.");
            }

            Map<Integer, Integer> sanPhamSoLuong = new LinkedHashMap<>();
            for (ThemSanPhamRequest item : items) {
                if (item == null) {
                    throw new IllegalArgumentException("Thông tin sản phẩm không hợp lệ.");
                }
                sanPhamSoLuong.merge(
                        item.getIdSanPhamChiTiet(),
                        item.getSoLuong(),
                        Integer::sum
                );
            }

            banHangService.themNhieuSanPhamVaoGio(idHoaDon, sanPhamSoLuong);
            response.put("success", true);
        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.put("success", false);
            response.put("message", thongBaoLoi(e));
            getServletContext().log("Lỗi khi thêm nhiều sản phẩm vào hóa đơn POS.", e);
        }

        out.print(gson.toJson(response));
        out.flush();
    }

    private void timKhachHang(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            String tuKhoa = optionalText(req, "tuKhoa");
            List<Map<String, Object>> khachHangs = banHangService.timKhachHangTheoTuKhoa(tuKhoa)
                    .stream()
                    .map(this::toKhachHangData)
                    .toList();
            response.put("success", true);
            response.put("khachHangs", khachHangs);
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
            xoaSessionInvoiceNeuTrung(req.getSession(), idHoaDon);
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

            List<HoaDon> hoaDons;
            if (idNhanVien == null) {
                Integer idHoaDon = parseSessionInvoiceId(session);
                HoaDon hoaDon = idHoaDon == null ? null : banHangService.layHoaDonTheoId(idHoaDon);
                hoaDons = hoaDon == null || hoaDon.getTrangThai() != 0 ? List.of() : List.of(hoaDon);
            } else {
                hoaDons = banHangService.layDanhSachHoaDonCho(idNhanVien);
            }
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
            xoaSessionInvoiceNeuTrung(req.getSession(), request.getIdHoaDon());

            response.put("success", true);
            response.put("message", "Thanh toán thành công.");
            response.put("idHoaDon", request.getIdHoaDon());
            response.put("maHoaDon", hoaDon.getMaHoaDon());
            response.put("detailUrl", req.getContextPath()
                    + "/admin/hoa-don/chi-tiet?id=" + request.getIdHoaDon());
            response.put("printUrl", req.getContextPath()
                    + "/admin/hoa-don/chi-tiet?id=" + request.getIdHoaDon() + "&print=1");
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

        // POS không yêu cầu đăng nhập: lấy nhân viên đang hoạt động từ database.
        NhanVien nhanVienMacDinh = nhanVienService.layTatCa().stream()
                .filter(nhanVien -> nhanVien != null
                        && nhanVien.getId() != null
                        && nhanVien.getId() > 0
                        && (nhanVien.getTrangThai() == null || nhanVien.getTrangThai() == 1))
                .findFirst()
                .orElse(null);
        if (nhanVienMacDinh != null) {
            session.setAttribute("idNhanVien", nhanVienMacDinh.getId());
            session.setAttribute("nhanVienDangNhap", nhanVienMacDinh);
            return nhanVienMacDinh.getId();
        }
        return null;
    }

    private Integer parseSessionInvoiceId(HttpSession session) {
        Object value = session.getAttribute("idHoaDonDangTao");
        if (value instanceof Number) {
            int id = ((Number) value).intValue();
            return id > 0 ? id : null;
        }
        return parseOptionalPositiveInt(value == null ? null : value.toString());
    }

    private void xoaSessionInvoiceNeuTrung(HttpSession session, int idHoaDon) {
        Integer idHoaDonDangTao = parseSessionInvoiceId(session);
        if (idHoaDonDangTao != null && idHoaDonDangTao == idHoaDon) {
            session.removeAttribute("idHoaDonDangTao");
        }
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

    private Integer parseOptionalGioiTinh(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed != 0 && parsed != 1) {
                throw new IllegalArgumentException("Giới tính không hợp lệ.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giới tính không hợp lệ.");
        }
    }

    private LocalDate parseOptionalNgaySinh(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
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

    private String thongBaoLoi(RuntimeException exception) {
        Throwable cause = exception;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause.getMessage() == null || cause.getMessage().isBlank()
                ? "Không thể thêm sản phẩm. Vui lòng kiểm tra log Tomcat."
                : cause.getMessage();
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
        data.put("maKhachHang", khachHang.getMaKhachHang());
        data.put("hoTen", khachHang.getHoTen());
        data.put("soDienThoai", khachHang.getSoDienThoai());
        data.put("email", khachHang.getEmail());
        data.put("ngaySinh", khachHang.getNgaySinh());
        data.put("gioiTinh", khachHang.getGioiTinh());
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
