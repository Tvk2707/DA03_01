package QuanLySanPham.dao.impl;

import QuanLySanPham.Entity.PhieuGiamGia;
import QuanLySanPham.dao.PhieuGiamGiaDao;
import QuanLySanPham.jdbc.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhieuGiamGiaDaoImpl extends GenericDaoImpl<PhieuGiamGia, Integer> implements PhieuGiamGiaDao {

    private static final int PUBLIC_COUPON_TYPE = 0;
    private static final int PERSONAL_COUPON_TYPE = 1;
    private static final String VOUCHER_CODE_PREFIX = "VC";

    private final DatabaseConnectionManager connectionManager =
            new DatabaseConnectionManager("quan_ly_ban_kinh", "sa", "123");

    public PhieuGiamGiaDaoImpl() {
        super(PhieuGiamGia.class);
    }

    @Override
    public List<PhieuGiamGia> findAllPublicCoupons() {
        return searchAndFilter("", "", "", "public", "", "", null, 1, Integer.MAX_VALUE);
    }

    @Override
    public PhieuGiamGia findPublicCouponById(Integer id) {
        PhieuGiamGia coupon = getById(id);
        if (coupon == null || (coupon.getLoaiPhieu() != null && coupon.getLoaiPhieu() == PERSONAL_COUPON_TYPE)) {
            return null;
        }
        return coupon;
    }

    @Override
    public PhieuGiamGia savePublicCoupon(PhieuGiamGia coupon) {
        coupon.setLoaiPhieu(PUBLIC_COUPON_TYPE);
        insert(coupon);
        return coupon;
    }

    @Override
    public PhieuGiamGia updatePublicCoupon(PhieuGiamGia coupon) {
        coupon.setLoaiPhieu(PUBLIC_COUPON_TYPE);
        updateCoupon(coupon);
        return coupon;
    }

    @Override
    public void updatePublicCouponStatus(Integer couponId, Integer status) {
        updateStatus(couponId, status);
    }

    @Override
    public List<PhieuGiamGia> findAllPersonalCoupons() {
        return searchAndFilter("", "", "", "personal", "", "", null, 1, Integer.MAX_VALUE);
    }

    @Override
    public PhieuGiamGia findPersonalCouponById(Integer id) {
        PhieuGiamGia coupon = getById(id);
        if (coupon == null || coupon.getLoaiPhieu() == null || coupon.getLoaiPhieu() != PERSONAL_COUPON_TYPE) {
            return null;
        }
        return coupon;
    }

    @Override
    public PhieuGiamGia findByMaVoucher(String maVoucher) {
        String sql = "SELECT " + selectColumns() + " FROM phieu_giam_gia WHERE UPPER(ma_voucher) = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maVoucher == null ? "" : maVoucher.trim().toUpperCase(Locale.ROOT));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCoupon(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm phiếu giảm giá theo mã", e);
        }
    }

    @Override
    public String generateNextVoucherCode() {
        String sql = "SELECT MAX(CAST(SUBSTRING(UPPER(ma_voucher), 3, 6) AS INT)) AS max_number "
                + "FROM phieu_giam_gia "
                + "WHERE UPPER(ma_voucher) LIKE ? AND LEN(ma_voucher) = 8";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, VOUCHER_CODE_PREFIX + "[0-9][0-9][0-9][0-9][0-9][0-9]");
            try (ResultSet resultSet = statement.executeQuery()) {
                int maxNumber = 0;
                if (resultSet.next()) {
                    maxNumber = resultSet.getInt("max_number");
                }
                return VOUCHER_CODE_PREFIX + String.format(Locale.ROOT, "%06d", maxNumber + 1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tạo mã phiếu giảm giá", e);
        }
    }

    @Override
    public PhieuGiamGia savePersonalCoupon(PhieuGiamGia coupon, Integer customerId) {
        coupon.setLoaiPhieu(PERSONAL_COUPON_TYPE);
        insert(coupon);
        insertCustomerCouponLink(coupon.getId(), customerId, coupon.getTrangThai());
        return coupon;
    }

    @Override
    public PhieuGiamGia updatePersonalCoupon(PhieuGiamGia coupon, Integer customerId) {
        coupon.setLoaiPhieu(PERSONAL_COUPON_TYPE);
        updateCoupon(coupon);
        return coupon;
    }

    @Override
    public void updatePersonalCouponStatus(Integer couponId, Integer status) {
        updateStatus(couponId, status);
        updateCustomerCouponLinkStatus(couponId, status);
    }

    @Override
    public List<PhieuGiamGia> searchAndFilter(String keyword, String maVoucher, String tenVoucher,
                                              String loaiPhieu, String loaiGiamGia, String trangThai,
                                              LocalDate denNgay, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT ")
                .append(selectColumns())
                .append(" FROM phieu_giam_gia WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendFilterConditions(sql, params, keyword, maVoucher, tenVoucher, loaiPhieu, loaiGiamGia, trangThai, denNgay);
        sql.append(" ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        int safePage = page < 1 ? 1 : page;
        int safePageSize = pageSize < 1 ? 5 : pageSize;
        params.add((safePage - 1) * safePageSize);
        params.add(safePageSize);

        return queryCoupons(sql.toString(), params);
    }

    @Override
    public List<PhieuGiamGia> searchAndFilterForExport(String keyword, String maVoucher, String tenVoucher,
                                                       String loaiPhieu, String loaiGiamGia, String trangThai,
                                                       LocalDate denNgay) {
        StringBuilder sql = new StringBuilder("SELECT ")
                .append(selectColumns())
                .append(" FROM phieu_giam_gia WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendFilterConditions(sql, params, keyword, maVoucher, tenVoucher, loaiPhieu, loaiGiamGia, trangThai, denNgay);
        sql.append(" ORDER BY id DESC");
        return queryCoupons(sql.toString(), params);
    }

    @Override
    public int countFiltered(String keyword, String maVoucher, String tenVoucher,
                             String loaiPhieu, String loaiGiamGia, String trangThai,
                             LocalDate denNgay) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM phieu_giam_gia WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendFilterConditions(sql, params, keyword, maVoucher, tenVoucher, loaiPhieu, loaiGiamGia, trangThai, denNgay);

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            setParameters(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm phiếu giảm giá", e);
        }
    }

    @Override
    public PhieuGiamGia getById(Integer id) {
        String sql = "SELECT " + selectColumns() + " FROM phieu_giam_gia WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCoupon(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy phiếu giảm giá theo ID", e);
        }
    }

    @Override
    public void insert(PhieuGiamGia coupon) {
        String sql = "INSERT INTO phieu_giam_gia (ma_voucher, ten_voucher, loai_giam_gia, gia_tri_giam, "
                + "giam_toi_da, don_toi_thieu, so_luong, so_luong_da_dung, loai_phieu, ngay_bat_dau, "
                + "ngay_ket_thuc, ngay_tao, trang_thai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillSaveParameters(statement, coupon, true);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    coupon.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm phiếu giảm giá", e);
        }
    }

    @Override
    public void updateCoupon(PhieuGiamGia coupon) {
        String sql = "UPDATE phieu_giam_gia SET ten_voucher = ?, loai_giam_gia = ?, gia_tri_giam = ?, "
                + "giam_toi_da = ?, don_toi_thieu = ?, so_luong = ?, so_luong_da_dung = ?, loai_phieu = ?, "
                + "ngay_bat_dau = ?, ngay_ket_thuc = ?, trang_thai = ? WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = fillSaveParameters(statement, coupon, false);
            statement.setInt(index, coupon.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật phiếu giảm giá", e);
        }
    }

    @Override
    public void updateStatus(Integer couponId, Integer status) {
        String sql = "UPDATE phieu_giam_gia SET trang_thai = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, status);
            statement.setInt(2, couponId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật trạng thái phiếu giảm giá", e);
        }
    }

    @Override
    public boolean checkDuplicateCode(String maVoucher, Integer currentId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM phieu_giam_gia WHERE UPPER(ma_voucher) = ?");
        List<Object> params = new ArrayList<>();
        params.add(maVoucher == null ? "" : maVoucher.trim().toUpperCase(Locale.ROOT));
        if (currentId != null) {
            sql.append(" AND id <> ?");
            params.add(currentId);
        }

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            setParameters(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra trùng mã phiếu giảm giá", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    private String selectColumns() {
        return "id, ma_voucher, ten_voucher, loai_giam_gia, gia_tri_giam, giam_toi_da, "
                + "don_toi_thieu, so_luong, so_luong_da_dung, loai_phieu, ngay_bat_dau, "
                + "ngay_ket_thuc, ngay_tao, trang_thai";
    }

    private void appendFilterConditions(StringBuilder sql, List<Object> params, String keyword,
                                        String maVoucher, String tenVoucher, String loaiPhieu,
                                        String loaiGiamGia, String trangThai, LocalDate denNgay) {
        if (hasText(keyword)) {
            sql.append(" AND (ma_voucher LIKE ? OR ten_voucher LIKE ?)");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }
        if (hasText(maVoucher)) {
            sql.append(" AND ma_voucher LIKE ?");
            params.add("%" + maVoucher.trim() + "%");
        }
        if (hasText(tenVoucher)) {
            sql.append(" AND ten_voucher LIKE ?");
            params.add("%" + tenVoucher.trim() + "%");
        }
        if ("public".equals(loaiPhieu)) {
            sql.append(" AND (loai_phieu IS NULL OR loai_phieu <> 1)");
        } else if ("personal".equals(loaiPhieu)) {
            sql.append(" AND loai_phieu = 1");
        }
        if ("percent".equals(loaiGiamGia)) {
            sql.append(" AND (loai_giam_gia LIKE '%[%]%' OR LOWER(loai_giam_gia) LIKE ? OR LOWER(loai_giam_gia) LIKE ?)");
            params.add("%phần%");
            params.add("%phan%");
        } else if ("amount".equals(loaiGiamGia)) {
            sql.append(" AND (loai_giam_gia IS NULL OR NOT (loai_giam_gia LIKE '%[%]%' OR LOWER(loai_giam_gia) LIKE ? OR LOWER(loai_giam_gia) LIKE ?))");
            params.add("%phần%");
            params.add("%phan%");
        }
        if ("inactive".equals(trangThai)) {
            sql.append(" AND ISNULL(trang_thai, 0) <> 1");
        } else if ("upcoming".equals(trangThai)) {
            sql.append(" AND ISNULL(trang_thai, 0) = 1 AND ngay_bat_dau > GETDATE()");
        } else if ("expired".equals(trangThai)) {
            sql.append(" AND ISNULL(trang_thai, 0) = 1 AND ngay_ket_thuc < GETDATE()");
        } else if ("active".equals(trangThai)) {
            sql.append(" AND ISNULL(trang_thai, 0) = 1 AND ngay_bat_dau <= GETDATE() AND ngay_ket_thuc >= GETDATE()");
        }
        if (denNgay != null) {
            sql.append(" AND ngay_ket_thuc <= ?");
            params.add(Timestamp.valueOf(denNgay.atTime(LocalTime.MAX)));
        }
    }

    private List<PhieuGiamGia> queryCoupons(String sql, List<Object> params) {
        List<PhieuGiamGia> coupons = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    coupons.add(mapCoupon(resultSet));
                }
            }
            return coupons;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách phiếu giảm giá", e);
        }
    }

    private void setParameters(PreparedStatement statement, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);
            if (value instanceof Integer) {
                statement.setInt(i + 1, (Integer) value);
            } else if (value instanceof Timestamp) {
                statement.setTimestamp(i + 1, (Timestamp) value);
            } else if (value instanceof Date) {
                statement.setDate(i + 1, (Date) value);
            } else {
                statement.setString(i + 1, value == null ? null : value.toString());
            }
        }
    }

    private int fillSaveParameters(PreparedStatement statement, PhieuGiamGia coupon,
                                   boolean insertMode) throws SQLException {
        int index = 1;
        if (insertMode) {
            statement.setString(index++, coupon.getMaVoucher());
        }
        statement.setString(index++, coupon.getTenVoucher());
        statement.setString(index++, coupon.getLoaiGiamGia());
        setBigDecimal(statement, index++, coupon.getGiaTriGiam());
        setBigDecimal(statement, index++, coupon.getGiamToiDa());
        setBigDecimal(statement, index++, coupon.getDonToiThieu());
        statement.setInt(index++, coupon.getSoLuong());
        statement.setInt(index++, coupon.getSoLuongDaDung() == null ? 0 : coupon.getSoLuongDaDung());
        statement.setInt(index++, coupon.getLoaiPhieu() == null ? PUBLIC_COUPON_TYPE : coupon.getLoaiPhieu());
        statement.setTimestamp(index++, toTimestamp(coupon.getNgayBatDau()));
        statement.setTimestamp(index++, toTimestamp(coupon.getNgayKetThuc()));
        if (insertMode) {
            statement.setTimestamp(index++, toTimestamp(coupon.getNgayTao() == null ? LocalDateTime.now() : coupon.getNgayTao()));
        }
        statement.setInt(index++, coupon.getTrangThai() == null ? 1 : coupon.getTrangThai());
        return index;
    }

    private void setBigDecimal(PreparedStatement statement, int index, BigDecimal value) throws SQLException {
        if (value == null) {
            statement.setBigDecimal(index, BigDecimal.ZERO);
        } else {
            statement.setBigDecimal(index, value);
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private PhieuGiamGia mapCoupon(ResultSet resultSet) throws SQLException {
        PhieuGiamGia coupon = new PhieuGiamGia();
        coupon.setId(resultSet.getInt("id"));
        coupon.setMaVoucher(resultSet.getString("ma_voucher"));
        coupon.setTenVoucher(resultSet.getString("ten_voucher"));
        coupon.setLoaiGiamGia(resultSet.getString("loai_giam_gia"));
        coupon.setGiaTriGiam(resultSet.getBigDecimal("gia_tri_giam"));
        coupon.setGiamToiDa(resultSet.getBigDecimal("giam_toi_da"));
        coupon.setDonToiThieu(resultSet.getBigDecimal("don_toi_thieu"));
        coupon.setSoLuong((Integer) resultSet.getObject("so_luong"));
        coupon.setSoLuongDaDung((Integer) resultSet.getObject("so_luong_da_dung"));
        coupon.setLoaiPhieu((Integer) resultSet.getObject("loai_phieu"));
        coupon.setNgayBatDau(toLocalDateTime(resultSet.getTimestamp("ngay_bat_dau")));
        coupon.setNgayKetThuc(toLocalDateTime(resultSet.getTimestamp("ngay_ket_thuc")));
        coupon.setNgayTao(toLocalDateTime(resultSet.getTimestamp("ngay_tao")));
        coupon.setTrangThai((Integer) resultSet.getObject("trang_thai"));
        return coupon;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void insertCustomerCouponLink(Integer couponId, Integer customerId, Integer status) {
        if (couponId == null || customerId == null) {
            return;
        }
        String sql = "INSERT INTO khach_hang_phieu_giam_gia (id_khach_hang, id_phieu_giam_gia, trang_thai, ngay_su_dung) "
                + "VALUES (?, ?, ?, NULL)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setInt(2, couponId);
            statement.setInt(3, status == null ? 1 : status);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi gán phiếu giảm giá cho khách hàng", e);
        }
    }

    private void updateCustomerCouponLinkStatus(Integer couponId, Integer status) {
        String sql = "UPDATE khach_hang_phieu_giam_gia SET trang_thai = ? WHERE id_phieu_giam_gia = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, status == null ? 0 : status);
            statement.setInt(2, couponId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đồng bộ trạng thái phiếu cá nhân", e);
        }
    }
}
