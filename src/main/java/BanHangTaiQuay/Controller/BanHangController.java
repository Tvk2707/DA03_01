package BanHangTaiQuay.Controller;

import BanHangTaiQuay.Service.BanHangService;
import BanHangTaiQuay.Service.BanHangServiceImpl;
import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.KhachHang;
import QuanLySanPham.Entity.NhanVien;
import QuanLySanPham.Entity.SanPhamChiTiet;
import QuanLySanPham.service.SanPhamChiTietService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({
        "/ban-hang",
        "/ban-hang/tao-hoa-don",
        "/ban-hang/tim-san-pham",
        "/ban-hang/them-san-pham",
        "/ban-hang/xoa-san-pham",
        "/ban-hang/cap-nhat-so-luong",
        "/ban-hang/tra-cuu-khach-hang",
        "/ban-hang/ap-voucher",
        "/ban-hang/huy-hoa-don",
        "/ban-hang/lay-hoa-don-cho"
})
public class BanHangController extends HttpServlet {

    private final BanHangService banHangService = new BanHangServiceImpl();
    private final SanPhamChiTietService sanPhamChiTietService = new SanPhamChiTietServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getServletPath();

        if (action == null || action.equals("/ban-hang")) {
            HttpSession session = req.getSession();
            NhanVien nhanVien = (NhanVien) session.getAttribute("nhanVienDangNhap");
            if (nhanVien != null) {
                List<HoaDon> danhSachHoaDonCho = banHangService.layDanhSachHoaDonCho(nhanVien.getId());
                req.setAttribute("danhSachHoaDonCho", danhSachHoaDonCho);
            }
            req.getRequestDispatcher("/Admin/BanHangTaiQuay/ban-hang.jsp").forward(req, resp);
            return;
        }

        switch (action) {
            case "/ban-hang/tim-san-pham":
                timSanPham(req, resp);
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
        }
    }

    private void taoHoaDon(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            HttpSession session = req.getSession();
            Integer idNhanVien = (Integer) session.getAttribute("idNhanVien");
            Integer idCa = (Integer) session.getAttribute("idCa");

            if (idNhanVien == null || idCa == null) {
                throw new IllegalStateException("Nhân viên hoặc ca làm việc chưa được thiết lập.");
            }

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
        List<SanPhamChiTiet> sanPhams = sanPhamChiTietService.timKiem(null, keyword, null, null, 1);
        req.setAttribute("sanPhams", sanPhams);
        req.getRequestDispatcher("/Admin/BanHangTaiQuay/_product-grid.jsp").forward(req, resp);
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
            String soDienThoai = req.getParameter("soDienThoai");
            String hoTen = createIfNotFound ? req.getParameter("hoTen") : null;

            KhachHang khachHang = banHangService.traCuuHoacTaoKhachHang(soDienThoai, hoTen);
            response.put("success", true);
            response.put("khachHang", khachHang);

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
            int idHoaDon = Integer.parseInt(req.getParameter("idHoaDon"));
            String maVoucher = req.getParameter("maVoucher");

            banHangService.apDungVoucher(idHoaDon, maVoucher);
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
            int idHoaDon = Integer.parseInt(req.getParameter("idHoaDon"));
            String lyDo = req.getParameter("lyDo");

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
            Integer idNhanVien = (Integer) session.getAttribute("idNhanVien");

            if (idNhanVien == null) {
                throw new IllegalStateException("Nhân viên chưa đăng nhập.");
            }

            List<HoaDon> hoaDons = banHangService.layDanhSachHoaDonCho(idNhanVien);
            response.put("success", true);
            response.put("hoaDons", hoaDons);

        } catch (IllegalStateException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        out.print(gson.toJson(response));
        out.flush();
    }
}
