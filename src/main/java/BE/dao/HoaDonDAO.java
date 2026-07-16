package BE.dao;

import BE.Model.ChiTietHoaDonView;
import BE.Model.ChiTietHoaDonInput;
import BE.Model.HoaDonView;
import BE.Model.LichSuHoaDonView;
import BE.Model.LichSuThanhToanView;
import BE.Model.NhanVienView;
import BE.Model.SanPhamHoaDonView;
import BE.Model.ThanhToanHoaDonView;
import BE.jdbc.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {
    private static final String SOFT_DELETE_REASON = "X\u00f3a m\u1ec1m t\u1eeb m\u00e0n h\u00ecnh qu\u1ea3n l\u00fd h\u00f3a \u0111\u01a1n";
    private static final String SOFT_DELETE_ACTION = "X\u00f3a m\u1ec1m h\u00f3a \u0111\u01a1n";
    private static final String SOFT_DELETE_PREFIX = "X\u00f3a m\u1ec1m%";

    private final DatabaseConnectionManager connectionManager;

    public HoaDonDAO() {
        this.connectionManager = DatabaseConnectionManager.fromEnvironment();
    }

    // READ: lấy danh sách hóa đơn.
    public List<HoaDonView> findAll() throws SQLException {
        String sql = "SELECT hd.id, hd.ma_hoa_don, hd.ten_nguoi_nhan, hd.sdt_nguoi_nhan, "
                + "hd.id_nhan_vien, hd.tong_tien_thanh_toan, hd.trang_thai, hd.ngay_tao, hd.ghi_chu, "
                + "nv.ho_ten AS ten_nhan_vien, kh.ho_ten AS ten_khach_hang, "
                + "pgg.ma_voucher, pgg.ten_voucher "
                + "FROM hoa_don hd "
                + "LEFT JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id "
                + "LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id "
                + "LEFT JOIN phieu_giam_gia pgg ON hd.id_phieu_giam_gia = pgg.id "
                + "WHERE NOT (hd.trang_thai = 5 AND hd.ly_do_huy LIKE N'Xóa mềm%') "
                + "ORDER BY hd.id DESC";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<HoaDonView> invoices = new ArrayList<>();

            while (resultSet.next()) {
                invoices.add(mapHoaDon(resultSet));
            }

            return invoices;
        }
    }

    // READ: lấy thông tin 1 hóa đơn theo id.
    public HoaDonView findById(int id) throws SQLException {
        String sql = "SELECT hd.id, hd.ma_hoa_don, hd.ten_nguoi_nhan, hd.sdt_nguoi_nhan, "
                + "hd.id_nhan_vien, hd.tong_tien_thanh_toan, hd.trang_thai, hd.ngay_tao, hd.ghi_chu, "
                + "nv.ho_ten AS ten_nhan_vien, kh.ho_ten AS ten_khach_hang, "
                + "pgg.ma_voucher, pgg.ten_voucher "
                + "FROM hoa_don hd "
                + "LEFT JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id "
                + "LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id "
                + "LEFT JOIN phieu_giam_gia pgg ON hd.id_phieu_giam_gia = pgg.id "
                + "WHERE hd.id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapHoaDon(resultSet);
                }
            }
        }

        return null;
    }

    // READ: lấy danh sách sản phẩm trong hóa đơn.
    public List<NhanVienView> findAllNhanVien() throws SQLException {
        String sql = "SELECT id, ma_nhan_vien, ho_ten "
                + "FROM nhan_vien "
                + "ORDER BY ho_ten";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<NhanVienView> employees = new ArrayList<>();

            while (resultSet.next()) {
                NhanVienView employee = new NhanVienView();
                employee.setId(resultSet.getInt("id"));
                employee.setMaNhanVien(resultSet.getString("ma_nhan_vien"));
                employee.setHoTen(resultSet.getString("ho_ten"));
                employees.add(employee);
            }

            return employees;
        }
    }

    // READ: lấy các sản phẩm còn hàng để chọn khi thêm hóa đơn.
    public List<SanPhamHoaDonView> findAllSanPhamHoaDon() throws SQLException {
        String sql = "SELECT spct.id, spct.ma, sp.ten_san_pham, spct.gia_ban, spct.so_luong_ton, "
                + "COALESCE(NULLIF(spct.hinh_anh, ''), ha.url_anh, "
                + "CASE spct.ma "
                + "WHEN 'SPCT001' THEN 'sp001_den.jpg' "
                + "WHEN 'SPCT002' THEN 'sp001_vang.jpg' "
                + "WHEN 'SPCT003' THEN 'sp002_bac.jpg' "
                + "WHEN 'SPCT004' THEN 'sp003_navy.jpg' "
                + "WHEN 'SPCT005' THEN 'sp004_nau.jpg' "
                + "WHEN 'SPCT006' THEN 'sp005_den.jpg' "
                + "END) AS hinh_anh "
                + "FROM san_pham_chi_tiet spct "
                + "LEFT JOIN san_pham sp ON spct.id_san_pham = sp.id "
                + "OUTER APPLY (SELECT TOP 1 url_anh FROM hinh_anh_san_pham "
                + "WHERE id_san_pham = sp.id ORDER BY is_anh_chinh DESC, id ASC) ha "
                + "WHERE spct.trang_thai = 1 AND spct.so_luong_ton > 0 "
                + "ORDER BY sp.ten_san_pham, spct.ma";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<SanPhamHoaDonView> products = new ArrayList<>();
            while (resultSet.next()) {
                SanPhamHoaDonView product = new SanPhamHoaDonView();
                product.setId(resultSet.getInt("id"));
                product.setMa(resultSet.getString("ma"));
                product.setTenSanPham(resultSet.getString("ten_san_pham"));
                product.setGiaBan(resultSet.getBigDecimal("gia_ban"));
                product.setSoLuongTon(resultSet.getInt("so_luong_ton"));
                product.setHinhAnh(resultSet.getString("hinh_anh"));
                products.add(product);
            }
            return products;
        }
    }

    public List<ChiTietHoaDonView> findDetailsByHoaDonId(int hoaDonId) throws SQLException {
        String sql = "SELECT sp.ten_san_pham, spct.ma AS ma_san_pham_chi_tiet, "
                + "dm.ten_danh_muc, th.ten_thuong_hieu, cl.ten_chat_lieu, kd.ten_kieu_dang, "
                + "hdg.hinh_dang, "
                + "CASE "
                + "WHEN kqk.quai_thang = 1 THEN N'Quai thẳng' "
                + "WHEN kqk.quai_gap = 1 THEN N'Quai gập' "
                + "WHEN kqk.quai_loxo = 1 THEN N'Quai lò xo' "
                + "ELSE NULL END AS kieu_quai_kinh, "
                + "tk.loai_trong, ms.ten_mau, kc.ten_kich_co, "
                + "COALESCE(NULLIF(spct.hinh_anh, ''), ha.url_anh, "
                + "CASE spct.ma "
                + "WHEN 'SPCT001' THEN 'sp001_den.jpg' "
                + "WHEN 'SPCT002' THEN 'sp001_vang.jpg' "
                + "WHEN 'SPCT003' THEN 'sp002_bac.jpg' "
                + "WHEN 'SPCT004' THEN 'sp003_navy.jpg' "
                + "WHEN 'SPCT005' THEN 'sp004_nau.jpg' "
                + "WHEN 'SPCT006' THEN 'sp005_den.jpg' "
                + "END) AS hinh_anh_san_pham, "
                + "cthd.so_luong, cthd.don_gia, cthd.tong_tien "
                + "FROM chi_tiet_hoa_don cthd "
                + "LEFT JOIN san_pham_chi_tiet spct ON cthd.id_san_pham_chi_tiet = spct.id "
                + "LEFT JOIN san_pham sp ON spct.id_san_pham = sp.id "
                + "LEFT JOIN danh_muc dm ON sp.id_danh_muc = dm.id "
                + "LEFT JOIN thuong_hieu th ON sp.id_thuong_hieu = th.id "
                + "LEFT JOIN chat_lieu cl ON sp.id_chat_lieu = cl.id "
                + "LEFT JOIN kieu_dang kd ON sp.id_kieu_dang = kd.id "
                + "LEFT JOIN gong_kinh gk ON sp.id_gong_kinh = gk.id "
                + "LEFT JOIN hinh_dang_gong hdg ON gk.id_hinh_dang_gong = hdg.id "
                + "LEFT JOIN kieu_quai_kinh kqk ON gk.id_kieu_quai_kinh = kqk.id "
                + "LEFT JOIN trong_kinh tk ON sp.id_trong_kinh = tk.id "
                + "LEFT JOIN mau_sac ms ON spct.id_mau_sac = ms.id "
                + "LEFT JOIN kich_co kc ON spct.id_kich_co = kc.id "
                + "OUTER APPLY (SELECT TOP 1 url_anh FROM hinh_anh_san_pham "
                + "WHERE id_san_pham = sp.id ORDER BY is_anh_chinh DESC, id ASC) ha "
                + "WHERE cthd.id_hoa_don = ? "
                + "ORDER BY cthd.id";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hoaDonId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<ChiTietHoaDonView> details = new ArrayList<>();

                while (resultSet.next()) {
                    ChiTietHoaDonView detail = new ChiTietHoaDonView();
                    detail.setTenSanPham(resultSet.getString("ten_san_pham"));
                    detail.setMaSanPhamChiTiet(resultSet.getString("ma_san_pham_chi_tiet"));
                    detail.setDanhMuc(resultSet.getString("ten_danh_muc"));
                    detail.setThuongHieu(resultSet.getString("ten_thuong_hieu"));
                    detail.setChatLieu(resultSet.getString("ten_chat_lieu"));
                    detail.setKieuDang(resultSet.getString("ten_kieu_dang"));
                    detail.setHinhDangGong(resultSet.getString("hinh_dang"));
                    detail.setKieuQuaiKinh(resultSet.getString("kieu_quai_kinh"));
                    detail.setLoaiTrong(resultSet.getString("loai_trong"));
                    detail.setMauSac(resultSet.getString("ten_mau"));
                    detail.setKichCo(resultSet.getString("ten_kich_co"));
                    detail.setHinhAnhSanPham(resultSet.getString("hinh_anh_san_pham"));
                    detail.setSoLuong(resultSet.getInt("so_luong"));
                    detail.setDonGia(resultSet.getBigDecimal("don_gia"));
                    detail.setTongTien(resultSet.getBigDecimal("tong_tien"));
                    details.add(detail);
                }

                return details;
            }
        }
    }

    // READ: lấy danh sách thanh toán của hóa đơn.
    public List<ThanhToanHoaDonView> findPaymentsByHoaDonId(int hoaDonId) throws SQLException {
        String sql = "SELECT tt.so_tien, tt.thoi_gian, tt.ma_giao_dich, ht.ten_pttt, tt.ghi_chu "
                + "FROM thanh_toan_hoa_don tt "
                + "LEFT JOIN hinh_thuc_thanh_toan ht ON tt.id_pttt = ht.id "
                + "WHERE tt.id_hoa_don = ? "
                + "ORDER BY tt.thoi_gian DESC";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hoaDonId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<ThanhToanHoaDonView> payments = new ArrayList<>();

                while (resultSet.next()) {
                    Timestamp thoiGian = resultSet.getTimestamp("thoi_gian");
                    ThanhToanHoaDonView payment = new ThanhToanHoaDonView();
                    payment.setSoTien(defaultMoney(resultSet.getBigDecimal("so_tien")));
                    payment.setThoiGian(thoiGian == null ? null : thoiGian.toLocalDateTime());
                    payment.setMaGiaoDich(resultSet.getString("ma_giao_dich"));
                    payment.setPhuongThuc(resultSet.getString("ten_pttt"));
                    payment.setGhiChu(resultSet.getString("ghi_chu"));
                    payments.add(payment);
                }

                return payments;
            }
        }
    }

    // READ: lấy lịch sử thanh toán từ bảng lich_su_thanh_toan.
    public List<LichSuThanhToanView> findPaymentHistoryByHoaDonId(int hoaDonId) throws SQLException {
        String sql = "SELECT so_tien, phuong_thuc_thanh_toan, trang_thai_thanh_toan, ngay_thanh_toan, ghi_chu "
                + "FROM lich_su_thanh_toan "
                + "WHERE id_hoa_don = ? "
                + "ORDER BY ngay_thanh_toan DESC, id DESC";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hoaDonId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<LichSuThanhToanView> history = new ArrayList<>();

                while (resultSet.next()) {
                    Timestamp ngayThanhToan = resultSet.getTimestamp("ngay_thanh_toan");
                    LichSuThanhToanView item = new LichSuThanhToanView();
                    item.setSoTien(defaultMoney(resultSet.getBigDecimal("so_tien")));
                    item.setPhuongThucThanhToan(resultSet.getString("phuong_thuc_thanh_toan"));
                    item.setTrangThaiThanhToan(resultSet.getInt("trang_thai_thanh_toan"));
                    item.setNgayThanhToan(ngayThanhToan == null ? null : ngayThanhToan.toLocalDateTime());
                    item.setGhiChu(resultSet.getString("ghi_chu"));
                    history.add(item);
                }

                return history;
            }
        }
    }

    // READ: lấy lịch sử xử lý hóa đơn.
    public List<LichSuHoaDonView> findHistoryByHoaDonId(int hoaDonId) throws SQLException {
        String sql = "SELECT hanh_dong, ngay_tao, ghi_chu "
                + "FROM lich_su_hoa_don "
                + "WHERE id_hoa_don = ? "
                + "ORDER BY ngay_tao DESC, id DESC";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hoaDonId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<LichSuHoaDonView> history = new ArrayList<>();

                while (resultSet.next()) {
                    Timestamp ngayTao = resultSet.getTimestamp("ngay_tao");
                    LichSuHoaDonView item = new LichSuHoaDonView();
                    item.setHanhDong(resultSet.getString("hanh_dong"));
                    item.setNgayTao(ngayTao == null ? null : ngayTao.toLocalDateTime());
                    item.setGhiChu(resultSet.getString("ghi_chu"));
                    history.add(item);
                }

                return history;
            }
        }
    }

    // CREATE: thêm hóa đơn mới.
    public int insert(HoaDonView hoaDon) throws SQLException {
        String sql = "INSERT INTO hoa_don (ma_hoa_don, ten_nguoi_nhan, sdt_nguoi_nhan, "
                + "tong_tien_thanh_toan, trang_thai, ghi_chu, id_nhan_vien) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setFormParameters(statement, hoaDon);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Không lấy được id hóa đơn vừa tạo.");
    }

    // CREATE: lưu sản phẩm vào chi tiết hóa đơn, giá lấy lại từ database.
    public void insertChiTietHoaDon(int idHoaDon, List<ChiTietHoaDonInput> productLines) throws SQLException {
        String sql = "INSERT INTO chi_tiet_hoa_don "
                + "(id_hoa_don, id_san_pham_chi_tiet, so_luong, don_gia, gia_ban_ra, tong_tien, trang_thai) "
                + "SELECT ?, id, ?, gia_ban, gia_ban, gia_ban * ?, 1 "
                + "FROM san_pham_chi_tiet "
                + "WHERE id = ? AND trang_thai = 1 AND so_luong_ton >= ?";

        String totalSql = "UPDATE hoa_don SET tong_tien_thanh_toan = "
                + "(SELECT COALESCE(SUM(tong_tien), 0) FROM chi_tiet_hoa_don WHERE id_hoa_don = ?) "
                + "WHERE id = ?";

        try (Connection connection = connectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (ChiTietHoaDonInput line : productLines) {
                    statement.setInt(1, idHoaDon);
                    statement.setInt(2, line.getSoLuong());
                    statement.setInt(3, line.getSoLuong());
                    statement.setInt(4, line.getIdSanPhamChiTiet());
                    statement.setInt(5, line.getSoLuong());
                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("Sản phẩm không tồn tại hoặc không đủ số lượng tồn.");
                    }
                }
            }
            try (PreparedStatement totalStatement = connection.prepareStatement(totalSql)) {
                totalStatement.setInt(1, idHoaDon);
                totalStatement.setInt(2, idHoaDon);
                totalStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException exception) {
            throw exception;
        }
    }

    public boolean existsByMaHoaDon(String maHoaDon, Integer exceptId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM hoa_don WHERE ma_hoa_don = ?"
                + (exceptId == null ? "" : " AND id <> ?");

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maHoaDon);

            if (exceptId != null) {
                statement.setInt(2, exceptId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    public String generateNextMaHoaDon() throws SQLException {
        String sql = "SELECT MAX(TRY_CONVERT(INT, SUBSTRING(ma_hoa_don, 3, 20))) "
                + "FROM hoa_don WHERE ma_hoa_don LIKE 'HD[0-9]%'";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            int nextNumber = 1;

            if (resultSet.next()) {
                int currentMax = resultSet.getInt(1);

                if (!resultSet.wasNull()) {
                    nextNumber = currentMax + 1;
                }
            }

            return String.format("HD%03d", nextNumber);
        }
    }

    // UPDATE: sửa thông tin cơ bản của hóa đơn.
    public void update(HoaDonView hoaDon) throws SQLException {
        String sql = "UPDATE hoa_don SET ma_hoa_don = ?, ten_nguoi_nhan = ?, sdt_nguoi_nhan = ?, "
                + "tong_tien_thanh_toan = ?, trang_thai = ?, ghi_chu = ?, id_nhan_vien = ? WHERE id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setFormParameters(statement, hoaDon);
            statement.setInt(8, hoaDon.getId());
            statement.executeUpdate();
        }
    }

    // DELETE: xóa mềm hóa đơn bằng cách đổi trang_thai = 5.
    public void delete(int id) throws SQLException {
        String sql = "UPDATE hoa_don SET trang_thai = 5, ly_do_huy = N'Xóa mềm từ màn hình quản lý hóa đơn' WHERE id = ?";
        String historySql = "INSERT INTO lich_su_hoa_don (id_hoa_don, hanh_dong, ghi_chu) VALUES (?, ?, ?)";

        try (Connection connection = connectionManager.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 PreparedStatement historyStatement = connection.prepareStatement(historySql)) {
                statement.setInt(1, id);
                statement.executeUpdate();

                historyStatement.setInt(1, id);
                historyStatement.setString(2, "Xóa mềm hóa đơn");
                historyStatement.setString(3, "Hóa đơn được xóa mềm từ màn hình quản lý hóa đơn");
                historyStatement.executeUpdate();

                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    // UPDATE: cập nhật trạng thái và ghi thêm lịch sử hóa đơn.
    public void updateStatus(int id, int status, String note) throws SQLException {
        String action = status == 5 ? "H\u1ee7y h\u00f3a \u0111\u01a1n" : "C\u1eadp nh\u1eadt tr\u1ea1ng th\u00e1i";
        String sql = "UPDATE hoa_don SET trang_thai = ?, ghi_chu = ? WHERE id = ?";
        String historySql = "INSERT INTO lich_su_hoa_don (id_hoa_don, hanh_dong, ghi_chu) VALUES (?, ?, ?)";

        try (Connection connection = connectionManager.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 PreparedStatement historyStatement = connection.prepareStatement(historySql)) {
                statement.setInt(1, status);
                statement.setString(2, note);
                statement.setInt(3, id);
                statement.executeUpdate();

                historyStatement.setInt(1, id);
                historyStatement.setString(2, action);
                historyStatement.setString(3, note);
                historyStatement.executeUpdate();

                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private void setFormParameters(PreparedStatement statement, HoaDonView hoaDon) throws SQLException {
        statement.setString(1, hoaDon.getMaHoaDon());
        statement.setString(2, hoaDon.getTenNguoiNhan());
        statement.setString(3, hoaDon.getSoDienThoai());
        statement.setBigDecimal(4, hoaDon.getTongTienThanhToan());
        statement.setInt(5, hoaDon.getTrangThai());

        if (hoaDon.getGhiChu() == null || hoaDon.getGhiChu().trim().isEmpty()) {
            statement.setNull(6, Types.NVARCHAR);
        } else {
            statement.setString(6, hoaDon.getGhiChu());
        }

        if (hoaDon.getIdNhanVien() == null || hoaDon.getIdNhanVien() <= 0) {
            statement.setNull(7, Types.INTEGER);
        } else {
            statement.setInt(7, hoaDon.getIdNhanVien());
        }
    }

    private HoaDonView mapHoaDon(ResultSet resultSet) throws SQLException {
        HoaDonView hoaDon = new HoaDonView();
        Timestamp ngayTao = resultSet.getTimestamp("ngay_tao");

        hoaDon.setId(resultSet.getInt("id"));
        hoaDon.setMaHoaDon(resultSet.getString("ma_hoa_don"));
        hoaDon.setTenNguoiNhan(resultSet.getString("ten_nguoi_nhan"));
        hoaDon.setSoDienThoai(resultSet.getString("sdt_nguoi_nhan"));
        hoaDon.setTongTienThanhToan(defaultMoney(resultSet.getBigDecimal("tong_tien_thanh_toan")));
        hoaDon.setTrangThai(resultSet.getInt("trang_thai"));
        hoaDon.setNgayTao(ngayTao == null ? null : ngayTao.toLocalDateTime());
        hoaDon.setGhiChu(resultSet.getString("ghi_chu"));
        int idNhanVien = resultSet.getInt("id_nhan_vien");
        hoaDon.setIdNhanVien(resultSet.wasNull() ? null : idNhanVien);
        hoaDon.setTenNhanVien(resultSet.getString("ten_nhan_vien"));
        hoaDon.setTenKhachHang(resultSet.getString("ten_khach_hang"));
        hoaDon.setMaVoucher(resultSet.getString("ma_voucher"));
        hoaDon.setTenVoucher(resultSet.getString("ten_voucher"));

        return hoaDon;
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
