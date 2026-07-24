package BanHangTaiQuay.Test;

import BanHangTaiQuay.Service.BanHangService;
import BanHangTaiQuay.Service.BanHangServiceImpl;
import QuanLySanPham.Entity.ChiTietHoaDon;
import QuanLySanPham.Entity.HoaDon;
import QuanLySanPham.Entity.SanPhamChiTiet;
import QuanLySanPham.Utils.EntityManagerUtlis;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Kiểm thử với SQL Server thật.
 *
 * Mặc định các test này được bỏ qua. Để chạy trên dữ liệu test riêng, đặt:
 * POS_DB_TESTS=true
 * POS_TEST_HOA_DON_ID=<id hóa đơn chờ>
 * POS_TEST_CHI_TIET_ID=<id chi tiết thuộc hóa đơn trên>
 * POS_TEST_SPCT_HET_HANG_ID=<id sản phẩm có tồn kho 0>
 * POS_TEST_CHI_TIET_KHAC_HOA_DON_ID=<id chi tiết thuộc hóa đơn khác>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BanHangDatabaseScenarioTest {

    private final BanHangService service = new BanHangServiceImpl();

    @Test
    @Order(1)
    void themSanPhamHetHangBiTuChoi() {
        int hoaDonId = requiredId("POS_TEST_HOA_DON_ID");
        int sanPhamHetHangId = requiredId("POS_TEST_SPCT_HET_HANG_ID");

        assertThrows(IllegalStateException.class,
                () -> service.themSanPhamVaoGio(hoaDonId, sanPhamHetHangId, 1));
    }

    @Test
    @Order(2)
    void xoaChiTietKhongThuocHoaDonBiTuChoi() {
        int hoaDonId = requiredId("POS_TEST_HOA_DON_ID");
        int chiTietCuaHoaDonKhac = requiredId("POS_TEST_CHI_TIET_KHAC_HOA_DON_ID");

        assertThrows(IllegalArgumentException.class,
                () -> service.xoaSanPhamKhoiGio(hoaDonId, chiTietCuaHoaDonKhac));
    }

    @Test
    @Order(3)
    void capNhatVuotTonKhoBiTuChoi() {
        int chiTietId = requiredId("POS_TEST_CHI_TIET_ID");
        ChiTietHoaDon chiTiet = loadChiTiet(chiTietId);

        assertThrows(IllegalStateException.class,
                () -> service.capNhatSoLuong(chiTietId, Integer.MAX_VALUE));
    }

    @Test
    @Order(4)
    void xoaSanPhamHoanLaiTonKho() {
        int hoaDonId = requiredId("POS_TEST_HOA_DON_ID");
        int chiTietId = requiredId("POS_TEST_CHI_TIET_ID");
        ChiTietHoaDon chiTiet = loadChiTiet(chiTietId);
        int sanPhamChiTietId = chiTiet.getSanPhamChiTiet().getId();
        int soLuong = chiTiet.getSoLuong();
        int tonKhoTruoc = loadTonKho(sanPhamChiTietId);

        try {
            service.xoaSanPhamKhoiGio(hoaDonId, chiTietId);
            assertEquals(tonKhoTruoc + soLuong, loadTonKho(sanPhamChiTietId));
        } finally {
            // Đưa fixture về trạng thái ban đầu để test sau không bị ảnh hưởng.
            service.themSanPhamVaoGio(hoaDonId, sanPhamChiTietId, soLuong);
        }
    }

    @Test
    @Order(5)
    void tongTienThayDoiChinhXacKhiDoiSoLuong() {
        int hoaDonId = requiredId("POS_TEST_HOA_DON_ID");
        int chiTietId = requiredId("POS_TEST_CHI_TIET_ID");
        ChiTietHoaDon chiTiet = loadChiTiet(chiTietId);
        int soLuongCu = chiTiet.getSoLuong();
        int sanPhamChiTietId = chiTiet.getSanPhamChiTiet().getId();
        int tonKho = loadTonKho(sanPhamChiTietId);
        Assumptions.assumeTrue(tonKho > 0, "Fixture cần còn ít nhất 1 sản phẩm trong kho.");

        try {
            service.capNhatSoLuong(chiTietId, soLuongCu + 1);
            HoaDon hoaDon = service.layHoaDonTheoId(hoaDonId);
            BigDecimal tongMongDoi = hoaDon.getChiTietHoaDons().stream()
                    .map(ChiTietHoaDon::getTongTien)
                    .filter(tongTien -> tongTien != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            assertEquals(0, tongMongDoi.compareTo(hoaDon.getTongTienThanhToan()));
        } finally {
            service.capNhatSoLuong(chiTietId, soLuongCu);
        }
    }

    private static int requiredId(String name) {
        Assumptions.assumeTrue("true".equalsIgnoreCase(System.getenv("POS_DB_TESTS")),
                "Đặt POS_DB_TESTS=true để chạy integration test với SQL Server.");
        String value = System.getenv(name);
        Assumptions.assumeTrue(value != null && !value.isBlank(),
                "Thiếu biến môi trường " + name + ".");
        return Integer.parseInt(value);
    }

    private static ChiTietHoaDon loadChiTiet(int id) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            ChiTietHoaDon chiTiet = em.find(ChiTietHoaDon.class, id);
            Assumptions.assumeTrue(chiTiet != null, "Không tìm thấy chi tiết test: " + id);
            Assumptions.assumeTrue(chiTiet.getHoaDon() != null, "Chi tiết test chưa gắn hóa đơn.");
            Assumptions.assumeTrue(chiTiet.getSanPhamChiTiet() != null,
                    "Chi tiết test chưa gắn sản phẩm.");
            return chiTiet;
        } finally {
            em.close();
        }
    }

    private static int loadTonKho(int idSanPhamChiTiet) {
        EntityManager em = EntityManagerUtlis.getEntityManager();
        try {
            SanPhamChiTiet sanPhamChiTiet = em.find(SanPhamChiTiet.class, idSanPhamChiTiet);
            Assumptions.assumeTrue(sanPhamChiTiet != null,
                    "Không tìm thấy sản phẩm chi tiết test: " + idSanPhamChiTiet);
            return sanPhamChiTiet.getSoLuongTon() == null ? 0 : sanPhamChiTiet.getSoLuongTon();
        } finally {
            em.close();
        }
    }
}
