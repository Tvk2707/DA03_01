package BanHangTaiQuay.Controller;

import BanHangTaiQuay.Model.ThanhToanRequest;
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

/**
 * Legacy implementation kept for source compatibility.
 * The active payment route is handled by BanHangController.
 */
public class ThanhToanServlet extends HttpServlet {

    private final BanHangService banHangService = new BanHangServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        thanhToan(req, resp);
    }

    private void thanhToan(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> response = new HashMap<>();

        try {
            ThanhToanRequest request = new ThanhToanRequest();
            request.setIdHoaDon(requirePositiveInt(req, "idHoaDon"));
            request.setMaPttt(requireText(req, "maPttt"));
            request.setSoTienKhachDua(requirePositiveAmount(req, "soTienKhachDua"));
            request.setMaGiaoDich(optionalText(req, "maGiaoDich"));
            request.setGhiChu(optionalText(req, "ghiChu"));

            banHangService.xacNhanThanhToan(
                    request.getIdHoaDon(),
                    request.getMaPttt(),
                    request.getSoTienKhachDua(),
                    request.getMaGiaoDich(),
                    request.getGhiChu());
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

    private BigDecimal requirePositiveAmount(HttpServletRequest req, String parameterName) {
        String value = requireText(req, parameterName);
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
}
