package BanHangTaiQuay.Test;

import BanHangTaiQuay.Controller.BanHangController;
import BanHangTaiQuay.Controller.ThanhToanServlet;
import jakarta.servlet.annotation.WebServlet;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteMappingTest {

    @Test
    void banHangControllerCoDuCacRouteChinh() {
        WebServlet servlet = BanHangController.class.getAnnotation(WebServlet.class);
        List<String> routes = Arrays.asList(servlet.value());

        assertTrue(routes.contains("/ban-hang"));
        assertTrue(routes.contains("/ban-hang/tao-hoa-don"));
        assertTrue(routes.contains("/ban-hang/tim-san-pham"));
        assertTrue(routes.contains("/ban-hang/them-san-pham"));
        assertTrue(routes.contains("/ban-hang/xoa-san-pham"));
        assertTrue(routes.contains("/ban-hang/cap-nhat-so-luong"));
        assertTrue(routes.contains("/ban-hang/tra-cuu-khach-hang"));
        assertTrue(routes.contains("/ban-hang/ap-voucher"));
        assertTrue(routes.contains("/ban-hang/huy-hoa-don"));
        assertTrue(routes.contains("/ban-hang/lay-hoa-don-cho"));
    }

    @Test
    void thanhToanServletDungRoute() {
        WebServlet servlet = ThanhToanServlet.class.getAnnotation(WebServlet.class);

        assertTrue(Arrays.asList(servlet.value()).contains("/thanh-toan/thanh-toan"));
    }
}
