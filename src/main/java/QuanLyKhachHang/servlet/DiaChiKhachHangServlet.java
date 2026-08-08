package QuanLyKhachHang.servlet;

import QuanLySanPham.Entity.DiaChiKhachHang;
import QuanLySanPham.Entity.KhachHang;
import QuanLyKhachHang.repository.DiaChiKhachHangRepository;
import QuanLyKhachHang.repository.KhachHangRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet({
        "/dia-chi-khach-hang/hien-thi",
        "/dia-chi-khach-hang/add",
        "/dia-chi-khach-hang/dat-mac-dinh"
})
public class DiaChiKhachHangServlet extends HttpServlet {

    private DiaChiKhachHangRepository repo = new DiaChiKhachHangRepository();
    private KhachHangRepository khachHangRepo = new KhachHangRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path) {
            case "/dia-chi-khach-hang/hien-thi":
                hienThi(req, resp);
                break;
            case "/dia-chi-khach-hang/dat-mac-dinh":
                datMacDinh(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/khach-hang/hien-thi");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getServletPath();

        switch (path) {
            case "/dia-chi-khach-hang/add":
                add(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/khach-hang/hien-thi");
                break;
        }
    }

    private void hienThi(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer idKhachHang = Integer.parseInt(req.getParameter("idKhachHang"));
        List<DiaChiKhachHang> listDiaChi = repo.getByKhachHangId(idKhachHang);
        KhachHang khachHang = khachHangRepo.getById(idKhachHang);

        req.setAttribute("listDiaChi", listDiaChi);
        req.setAttribute("idKhachHang", idKhachHang);
        req.setAttribute("khachHang", khachHang);
        req.getRequestDispatcher("/QuanLyKhachHang/dia_chi_khach_hang.jsp").forward(req, resp);
    }

    private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer idKhachHang = Integer.parseInt(req.getParameter("idKhachHang"));

        DiaChiKhachHang diaChi = new DiaChiKhachHang();
        diaChi.setTinhThanh(req.getParameter("tinhThanh"));       // Tên tỉnh (từ hidden input)
        diaChi.setProvinceCode(req.getParameter("provinceCode"));  // Mã tỉnh từ API
        diaChi.setQuanHuyen("");                                   // Giữ NOT NULL, để trống vì form không còn thu thập
        diaChi.setPhuongXa(req.getParameter("phuongXa"));         // Tên phường/xã (từ hidden input)
        diaChi.setWardCode(req.getParameter("wardCode"));          // Mã phường/xã từ API
        diaChi.setDiaChiCuThe(req.getParameter("diaChiCuThe"));

        String loaiDiaChi = req.getParameter("loaiDiaChi");
        if (loaiDiaChi != null && !loaiDiaChi.isEmpty()) {
            diaChi.setLoaiDiaChi(Integer.parseInt(loaiDiaChi));
        }

        String isMacDinh = req.getParameter("isMacDinh");
        if (isMacDinh != null && !isMacDinh.isEmpty()) {
            diaChi.setIsMacDinh(Integer.parseInt(isMacDinh));
        } else {
            diaChi.setIsMacDinh(0);
        }

        repo.add(idKhachHang, diaChi);
        resp.sendRedirect(req.getContextPath() + "/dia-chi-khach-hang/hien-thi?idKhachHang=" + idKhachHang);
    }

    private void datMacDinh(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer idDiaChi = Integer.parseInt(req.getParameter("idDiaChi"));
        Integer idKhachHang = Integer.parseInt(req.getParameter("idKhachHang"));

        repo.datMacDinh(idDiaChi, idKhachHang);
        resp.sendRedirect(req.getContextPath() + "/dia-chi-khach-hang/hien-thi?idKhachHang=" + idKhachHang);
    }
}
