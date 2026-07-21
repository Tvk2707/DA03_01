package BanHangTaiQuay.Controller;

import BanHangTaiQuay.Service.BanHangService;
import BanHangTaiQuay.Service.BanHangServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/thanh-toan/thanh-toan")
public class ThanhToanServlet extends HttpServlet {

    private final BanHangService banHangService = new BanHangServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        thanhToan(req, resp);
    }

    private void thanhToan(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            int idHoaDon = Integer.parseInt(req.getParameter("idHoaDon"));
            String maPttt = req.getParameter("maPttt");
            BigDecimal soTienKhachDua = new BigDecimal(req.getParameter("soTienKhachDua"));

            banHangService.xacNhanThanhToan(idHoaDon, maPttt, soTienKhachDua);
            response.put("success", true);
            response.put("message", "Thanh toán hóa đơn thành công!");

        } catch (IllegalStateException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        out.print(gson.toJson(response));
        out.flush();
    }
}
