package BanHangTaiQuay.Dao;

import QuanLyHoaDon.Model.*;
import QuanLySanPham.jdbc.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {
    private final DatabaseConnectionManager connectionManager;

    public HoaDonDAO() {
        this.connectionManager = DatabaseConnectionManager.fromEnvironment();
    }

    // READ: lấy danh sách hóa đơn.
    public List<HoaDonView> findAll() throws SQLException {
        String sql = "SELECT hd.id, hd.ma_hoa_don, hd.ten_nguoi_nhan, hd.sdt_nguoi_nhan, "
                + "hd.id_nhan_vien, hd.tong_tien_thanh_toan, hd.trang_thai, hd.ngay_tao, hd.ghi_chu, "
                + "nv.ho_ten AS ten_nhan_vien, nv.ma_nhan_vien AS ma_nhan_vien, kh.ho_ten AS ten_khach_hang, "
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
                + "nv.ho_ten AS ten_nhan_vien, nv.ma_nhan_vien AS ma_nhan_vien, kh.ho_ten AS ten_khach_hang, "
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

    public List<ChiTietHoaDonView> findDetailsByHoaDonId(int hoaDonId) throws SQLException {
        String sql = "SELECT sp.ten_san_pham, spct.ma AS ma_san_pham_chi_tiet, "
                + "dm.ten_danh_muc, th.ten_thuong_hieu, cl.ten_chat_lieu, kd.ten_kieu_dang, "
                + "hdg.hinh_dang, "
                + "kqk.kieu_quai AS kieu_quai_kinh, "
                + "tk.loai_trong, ms.ten_mau, kc.ten_kich_co, "
                + "COALESCE(NULLIF(spct.hinh_anh, ''), ha.url_anh) AS hinh_anh_san_pham, "
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
        hoaDon.setMaNhanVien(resultSet.getString("ma_nhan_vien"));
        hoaDon.setTenKhachHang(resultSet.getString("ten_khach_hang"));
        hoaDon.setMaVoucher(resultSet.getString("ma_voucher"));
        hoaDon.setTenVoucher(resultSet.getString("ten_voucher"));

        return hoaDon;
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
