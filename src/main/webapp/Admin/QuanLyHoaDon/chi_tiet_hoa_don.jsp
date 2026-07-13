<%@ page import="BE.Model.ChiTietHoaDonView" %>
<%@ page import="BE.Model.HoaDonView" %>
<%@ page import="BE.Model.LichSuHoaDonView" %>
<%@ page import="BE.Model.ThanhToanHoaDonView" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    if (request.getAttribute("hoaDon") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/hoa-don");
        return;
    }

    request.setAttribute("pageTitle", "Chi tiết hóa đơn");
    request.setAttribute("activeMenu", "hoadon");

    HoaDonView hoaDon = (HoaDonView) request.getAttribute("hoaDon");
    List<ChiTietHoaDonView> chiTietList = (List<ChiTietHoaDonView>) request.getAttribute("chiTietList");
    List<ThanhToanHoaDonView> paymentList = (List<ThanhToanHoaDonView>) request.getAttribute("paymentList");
    List<LichSuHoaDonView> historyList = (List<LichSuHoaDonView>) request.getAttribute("historyList");
    DecimalFormat moneyFormat = new DecimalFormat("#,###");
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    int productCount = 0;
    BigDecimal paidTotal = BigDecimal.ZERO;

    for (ChiTietHoaDonView detail : chiTietList) {
        productCount += detail.getSoLuong() == null ? 0 : detail.getSoLuong();
    }

    for (ThanhToanHoaDonView payment : paymentList) {
        paidTotal = paidTotal.add(payment.getSoTien() == null ? BigDecimal.ZERO : payment.getSoTien());
    }
%>
<%!
    private String text(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String statusText(Integer status) {
        if (status != null && status == 3) {
            return "Đã thanh toán";
        }

        if (status != null && status == 5) {
            return "Đã hủy";
        }

        return "Chờ thanh toán";
    }

    private String statusClass(Integer status) {
        if (status != null && status == 3) {
            return "invoice-status--done";
        }

        if (status != null && status == 5) {
            return "invoice-status--cancelled";
        }

        return "invoice-status--waiting";
    }

    private String stepClass(Integer status, int step) {
        int current = status == null ? 1 : status;

        if (current == 5 && step == 5) {
            return " is-current";
        }

        if (current == 3 && step <= 3) {
            return step == 3 ? " is-current" : " is-done";
        }

        if (current == 1 && step == 1) {
            return " is-current";
        }

        return "";
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết hóa đơn</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/layout.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/sidebar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/hoa_don.css">
</head>
<body>
<%@ include file="../layout/sidebar.jsp" %>

<div class="main-content">
    <%@ include file="../layout/header.jsp" %>

    <main id="page-content" class="invoice-page invoice-detail-page">
        <section class="invoice-page-header">
            <div>
                <h1 class="invoice-title">Chi tiết hóa đơn</h1>
                <p class="invoice-subtitle">Màn xử lý hóa đơn theo bố cục trong video, dữ liệu lấy từ SQL Server.</p>
            </div>
            <a class="invoice-btn invoice-btn--outline" href="<%= request.getContextPath() %>/admin/hoa-don">
                <i class="fas fa-arrow-left"></i>
                Quay lại
            </a>
        </section>

        <section class="invoice-workspace">
            <div class="invoice-workspace__left">
                <div class="invoice-list-card invoice-detail-section">
                    <div class="invoice-card-heading invoice-card-heading--compact">
                        <div>
                            <h2>Sản phẩm trong hóa đơn</h2>
                            <p>Danh sách sản phẩm tương tự khu vực trên cùng trong video.</p>
                        </div>
                    </div>
                    <div class="invoice-table-wrap">
                        <table class="invoice-table invoice-product-table">
                            <thead>
                            <tr>
                                <th>Ảnh</th>
                                <th>Sản phẩm</th>
                                <th>Đơn giá</th>
                                <th>Số lượng</th>
                                <th>Thành tiền</th>
                            </tr>
                            </thead>
                            <tbody>
                            <% for (ChiTietHoaDonView detail : chiTietList) { %>
                            <tr>
                                <td><span class="invoice-thumb"><i class="fas fa-glasses"></i></span></td>
                                <td><strong><%= text(detail.getTenSanPham()) %></strong></td>
                                <td class="invoice-money"><%= moneyFormat.format(detail.getDonGia()) %> đ</td>
                                <td><%= detail.getSoLuong() %></td>
                                <td class="invoice-money"><%= moneyFormat.format(detail.getTongTien()) %> đ</td>
                            </tr>
                            <% } %>
                            <% if (chiTietList.isEmpty()) { %>
                            <tr><td colspan="5">Hóa đơn này chưa có sản phẩm chi tiết.</td></tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="invoice-video-card">
                    <h3><i class="fas fa-truck-fast"></i> Thông tin giao hàng</h3>
                    <div class="invoice-shipping-grid">
                        <label class="invoice-field">
                            <span>Họ tên</span>
                            <input type="text" value="<%= text(hoaDon.getTenNguoiNhan()) %>" readonly>
                        </label>
                        <label class="invoice-field">
                            <span>Số điện thoại</span>
                            <input type="text" value="<%= text(hoaDon.getSoDienThoai()) %>" readonly>
                        </label>
                        <label class="invoice-field invoice-field--full">
                            <span>Địa chỉ</span>
                            <textarea readonly placeholder="Chưa có địa chỉ giao hàng trong bảng hoa_don"></textarea>
                        </label>
                    </div>
                </div>

                <div class="invoice-video-card">
                    <h3><i class="far fa-user"></i> Thông tin nhận hàng</h3>
                    <div class="invoice-shipping-grid">
                        <label class="invoice-field">
                            <span>Tên người nhận</span>
                            <input type="text" value="<%= text(hoaDon.getTenNguoiNhan()) %>" readonly>
                        </label>
                        <label class="invoice-field">
                            <span>SĐT người nhận</span>
                            <input type="text" value="<%= text(hoaDon.getSoDienThoai()) %>" readonly>
                        </label>
                        <label class="invoice-field invoice-field--full">
                            <span>Ghi chú</span>
                            <textarea readonly><%= text(hoaDon.getGhiChu()).equals("-") ? "" : text(hoaDon.getGhiChu()) %></textarea>
                        </label>
                    </div>
                </div>
            </div>

            <aside class="invoice-checkout-panel">
                <h3><i class="fas fa-wallet"></i> Thanh toán</h3>
                <div class="invoice-payment-option">
                    <span>Hình thức</span>
                    <strong><%= paymentList.isEmpty() ? "Chưa thanh toán" : text(paymentList.get(0).getPhuongThuc()) %></strong>
                </div>
                <label class="invoice-field invoice-field--full">
                    <span>Mã giảm giá</span>
                    <input type="text" placeholder="Mã giảm giá" readonly>
                </label>
                <div class="invoice-discount-box">
                    Gợi ý mã giảm giá sẽ hiển thị ở đây khi nối thêm bảng phiếu giảm giá.
                </div>
                <div class="invoice-total-lines">
                    <div class="invoice-total-line">
                        <span>Tạm tính</span>
                        <strong><%= moneyFormat.format(hoaDon.getTongTienThanhToan()) %> đ</strong>
                    </div>
                    <div class="invoice-total-line">
                        <span>Phí vận chuyển</span>
                        <strong>0 đ</strong>
                    </div>
                    <div class="invoice-total-line">
                        <span>Giảm giá</span>
                        <strong>0 đ</strong>
                    </div>
                    <div class="invoice-total-line invoice-total-line--final">
                        <span>Tổng phải trả</span>
                        <strong><%= moneyFormat.format(hoaDon.getTongTienThanhToan()) %> đ</strong>
                    </div>
                    <div class="invoice-total-line">
                        <span>Khách thanh toán</span>
                        <strong><%= moneyFormat.format(paidTotal) %> đ</strong>
                    </div>
                    <div class="invoice-total-line">
                        <span>Còn lại</span>
                        <strong><%= moneyFormat.format(hoaDon.getTongTienThanhToan().subtract(paidTotal)) %> đ</strong>
                    </div>
                </div>
                <form method="post" action="<%= request.getContextPath() %>/admin/hoa-don">
                    <input type="hidden" name="action" value="changeStatus">
                    <input type="hidden" name="id" value="<%= hoaDon.getId() %>">
                    <input type="hidden" name="trangThai" value="3">
                    <input type="hidden" name="ghiChu" value="Xác nhận thanh toán từ màn hình hóa đơn">
                    <button class="invoice-pay-btn" type="submit" <%= hoaDon.getTrangThai() != null && hoaDon.getTrangThai() == 5 ? "disabled" : "" %>>
                        Thanh toán tiền mặt
                    </button>
                </form>
            </aside>
        </section>

        <section class="invoice-timeline-card">
            <div class="invoice-timeline">
                <div class="timeline-step<%= stepClass(hoaDon.getTrangThai(), 1) %>">
                    <div class="timeline-step__name">Chờ thanh toán</div>
                    <div class="timeline-step__date"><%= hoaDon.getNgayTao() == null ? "" : hoaDon.getNgayTao().format(dateFormat) %></div>
                    <div class="timeline-step__dot"><i class="fas fa-check"></i></div>
                </div>
                <div class="timeline-step<%= stepClass(hoaDon.getTrangThai(), 3) %>">
                    <div class="timeline-step__name">Đã thanh toán</div>
                    <div class="timeline-step__date"></div>
                    <div class="timeline-step__dot"><i class="fas fa-check"></i></div>
                </div>
                <div class="timeline-step<%= stepClass(hoaDon.getTrangThai(), 5) %>">
                    <div class="timeline-step__name">Đã hủy</div>
                    <div class="timeline-step__date"></div>
                    <div class="timeline-step__dot"><i class="fas fa-xmark"></i></div>
                </div>
            </div>
        </section>

        <section class="invoice-detail-actions">
            <div class="invoice-action-left">
                <button class="invoice-btn invoice-btn--primary" type="button" data-open-modal="statusModal" <%= hoaDon.getTrangThai() != null && hoaDon.getTrangThai() == 5 ? "disabled" : "" %>>
                    <i class="fas fa-arrows-rotate"></i>
                    <span>Đổi trạng thái</span>
                </button>
                <form method="post" action="<%= request.getContextPath() %>/admin/hoa-don">
                    <input type="hidden" name="action" value="changeStatus">
                    <input type="hidden" name="id" value="<%= hoaDon.getId() %>">
                    <input type="hidden" name="trangThai" value="5">
                    <input type="hidden" name="ghiChu" value="Hủy hóa đơn từ màn hình chi tiết">
                    <button class="invoice-btn invoice-btn--danger" type="submit" <%= hoaDon.getTrangThai() != null && hoaDon.getTrangThai() == 5 ? "disabled" : "" %>>
                        <i class="fas fa-ban"></i>
                        Hủy đơn
                    </button>
                </form>
            </div>
            <div class="invoice-action-right">
                <button class="invoice-btn invoice-btn--outline" type="button" data-open-modal="historyModal">
                    <i class="fas fa-clock-rotate-left"></i>
                    Chi tiết lịch sử
                </button>
                <button class="invoice-btn invoice-btn--outline" id="btnPrintDetail" type="button">
                    <i class="fas fa-print"></i>
                    Xuất hóa đơn
                </button>
            </div>
        </section>

        <section class="invoice-detail-summary">
            <div>
                <div class="invoice-summary-title">
                    <h2>Thông tin đơn hàng</h2>
                    <span class="invoice-status <%= statusClass(hoaDon.getTrangThai()) %>"><%= statusText(hoaDon.getTrangThai()) %></span>
                    <span class="invoice-pill"><%= text(hoaDon.getTenNhanVien()).equals("-") ? "Online" : "Tại quầy" %></span>
                </div>
                <p>
                    Mã: <strong><%= hoaDon.getMaHoaDon() %></strong>
                    <span>•</span>
                    Tạo lúc: <strong><%= hoaDon.getNgayTao() == null ? "-" : hoaDon.getNgayTao().format(dateFormat) %></strong>
                    <span>•</span>
                    NV xử lý: <strong><%= text(hoaDon.getTenNhanVien()) %></strong>
                </p>
            </div>
            <div class="invoice-total-box">
                <span>Tổng thanh toán</span>
                <strong><%= moneyFormat.format(hoaDon.getTongTienThanhToan()) %> đ</strong>
                <small>Đã ghi nhận: <b><%= moneyFormat.format(paidTotal) %> đ</b></small>
            </div>
        </section>

        <section class="invoice-info-grid">
            <article class="invoice-info-panel">
                <h3><i class="far fa-user"></i> Khách hàng</h3>
                <dl>
                    <dt>Họ tên</dt>
                    <dd><%= text(hoaDon.getTenNguoiNhan()) %></dd>
                    <dt>SĐT</dt>
                    <dd><%= text(hoaDon.getSoDienThoai()) %></dd>
                    <dt>Email</dt>
                    <dd>-</dd>
                    <dt>Địa chỉ</dt>
                    <dd>-</dd>
                </dl>
            </article>

            <article class="invoice-info-panel">
                <h3><i class="fas fa-truck-fast"></i> Giao nhận</h3>
                <dl>
                    <dt>Người nhận</dt>
                    <dd><%= text(hoaDon.getTenNguoiNhan()) %></dd>
                    <dt>SĐT nhận</dt>
                    <dd><%= text(hoaDon.getSoDienThoai()) %></dd>
                    <dt>Địa chỉ nhận</dt>
                    <dd>-</dd>
                    <dt>Ghi chú</dt>
                    <dd><%= text(hoaDon.getGhiChu()) %></dd>
                </dl>
            </article>

            <article class="invoice-info-panel">
                <h3><i class="fas fa-coins"></i> Giá trị đơn</h3>
                <dl>
                    <dt>Tổng tiền</dt>
                    <dd><%= moneyFormat.format(hoaDon.getTongTienThanhToan()) %> đ</dd>
                    <dt>Đã thanh toán</dt>
                    <dd><%= moneyFormat.format(paidTotal) %> đ</dd>
                    <dt>Còn lại</dt>
                    <dd class="invoice-money"><%= moneyFormat.format(hoaDon.getTongTienThanhToan().subtract(paidTotal)) %> đ</dd>
                </dl>
            </article>
        </section>

        <section class="invoice-mini-grid">
            <div class="invoice-mini-card">
                <span>Trạng thái hiện tại</span>
                <strong><%= statusText(hoaDon.getTrangThai()) %></strong>
            </div>
            <div class="invoice-mini-card">
                <span>Số SP</span>
                <strong><%= productCount %></strong>
            </div>
            <div class="invoice-mini-card">
                <span>Đơn giá trị</span>
                <strong><%= moneyFormat.format(hoaDon.getTongTienThanhToan()) %> đ</strong>
            </div>
            <div class="invoice-mini-card">
                <span>Còn lại</span>
                <strong><%= moneyFormat.format(hoaDon.getTongTienThanhToan().subtract(paidTotal)) %> đ</strong>
            </div>
        </section>

        <section class="invoice-list-card invoice-detail-section">
            <div class="invoice-card-heading invoice-card-heading--compact">
                <div>
                    <h2>Lịch sử thanh toán</h2>
                    <p>Giao dịch ghi nhận cho hóa đơn này</p>
                </div>
            </div>
            <div class="invoice-table-wrap">
                <table class="invoice-table">
                    <thead>
                    <tr>
                        <th>Số tiền</th>
                        <th>Thời gian</th>
                        <th>Mã giao dịch</th>
                        <th>Phương thức</th>
                        <th>Ghi chú</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (ThanhToanHoaDonView payment : paymentList) { %>
                    <tr>
                        <td class="invoice-money"><%= moneyFormat.format(payment.getSoTien()) %> đ</td>
                        <td><%= payment.getThoiGian() == null ? "-" : payment.getThoiGian().format(dateFormat) %></td>
                        <td><%= text(payment.getMaGiaoDich()) %></td>
                        <td><%= text(payment.getPhuongThuc()) %></td>
                        <td><%= text(payment.getGhiChu()) %></td>
                    </tr>
                    <% } %>
                    <% if (paymentList.isEmpty()) { %>
                    <tr><td colspan="5">Chưa có thanh toán.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </section>

        <section class="invoice-list-card invoice-detail-section">
            <div class="invoice-card-heading invoice-card-heading--compact">
                <div>
                    <h2>Sản phẩm</h2>
                    <p>Gọng kính, tròng kính và phụ kiện trong đơn</p>
                </div>
            </div>
            <div class="invoice-table-wrap">
                <table class="invoice-table invoice-product-table">
                    <thead>
                    <tr>
                        <th>STT</th>
                        <th>Ảnh</th>
                        <th>Sản phẩm</th>
                        <th>Số lượng</th>
                        <th>Đơn giá</th>
                        <th>Thành tiền</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (int i = 0; i < chiTietList.size(); i++) {
                        ChiTietHoaDonView detail = chiTietList.get(i);
                    %>
                    <tr>
                        <td><%= i + 1 %></td>
                        <td><span class="invoice-thumb"><i class="fas fa-glasses"></i></span></td>
                        <td><strong><%= text(detail.getTenSanPham()) %></strong></td>
                        <td><%= detail.getSoLuong() %></td>
                        <td class="invoice-money"><%= moneyFormat.format(detail.getDonGia()) %> đ</td>
                        <td class="invoice-money"><%= moneyFormat.format(detail.getTongTien()) %> đ</td>
                    </tr>
                    <% } %>
                    <% if (chiTietList.isEmpty()) { %>
                    <tr><td colspan="6">Hóa đơn này chưa có sản phẩm chi tiết.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</div>

<div class="invoice-modal" id="statusModal" aria-hidden="true">
    <div class="invoice-modal__backdrop" data-close-modal></div>
    <section class="invoice-modal__dialog" role="dialog" aria-modal="true">
        <header class="invoice-modal__header">
            <h2><i class="fas fa-triangle-exclamation"></i> Xác nhận đổi trạng thái</h2>
            <button class="invoice-modal__close" type="button" data-close-modal aria-label="Đóng">
                <i class="fas fa-xmark"></i>
            </button>
        </header>
        <form method="post" action="<%= request.getContextPath() %>/admin/hoa-don">
            <div class="invoice-modal__body">
                <input type="hidden" name="action" value="changeStatus">
                <input type="hidden" name="id" value="<%= hoaDon.getId() %>">
                <p>Hóa đơn: <strong><%= hoaDon.getMaHoaDon() %></strong></p>
                <label class="invoice-field invoice-field--full">
                    <span>Trạng thái mới</span>
                    <select name="trangThai">
                        <option value="1" <%= hoaDon.getTrangThai() == null || hoaDon.getTrangThai() == 1 ? "selected" : "" %>>Chờ thanh toán</option>
                        <option value="3" <%= hoaDon.getTrangThai() != null && hoaDon.getTrangThai() == 3 ? "selected" : "" %>>Đã thanh toán</option>
                        <option value="5" <%= hoaDon.getTrangThai() != null && hoaDon.getTrangThai() == 5 ? "selected" : "" %>>Đã hủy</option>
                    </select>
                </label>
                <label class="invoice-field invoice-field--full">
                    <span>Ghi chú</span>
                    <textarea name="ghiChu" rows="4" placeholder="Nhập ghi chú xử lý hóa đơn"><%= text(hoaDon.getGhiChu()).equals("-") ? "" : text(hoaDon.getGhiChu()) %></textarea>
                </label>
                <small>Hành động này sẽ cập nhật trạng thái và ghi lịch sử hóa đơn.</small>
            </div>
            <footer class="invoice-modal__footer">
                <button class="invoice-btn invoice-btn--ghost" type="button" data-close-modal>Hủy</button>
                <button class="invoice-btn invoice-btn--primary" type="submit">Xác nhận</button>
            </footer>
        </form>
    </section>
</div>

<div class="invoice-modal" id="historyModal" aria-hidden="true">
    <div class="invoice-modal__backdrop" data-close-modal></div>
    <section class="invoice-modal__dialog invoice-modal__dialog--wide" role="dialog" aria-modal="true">
        <header class="invoice-modal__header">
            <h2>Chi tiết lịch sử</h2>
            <button class="invoice-modal__close" type="button" data-close-modal aria-label="Đóng">
                <i class="fas fa-xmark"></i>
            </button>
        </header>
        <div class="invoice-modal__body">
            <div class="invoice-table-wrap">
                <table class="invoice-table">
                    <thead>
                    <tr>
                        <th>Hành động</th>
                        <th>Thời gian</th>
                        <th>Mô tả</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (LichSuHoaDonView history : historyList) { %>
                    <tr>
                        <td><%= text(history.getHanhDong()) %></td>
                        <td><%= history.getNgayTao() == null ? "-" : history.getNgayTao().format(dateFormat) %></td>
                        <td><%= text(history.getGhiChu()) %></td>
                    </tr>
                    <% } %>
                    <% if (historyList.isEmpty()) { %>
                    <tr><td colspan="3">Chưa có lịch sử.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
        <footer class="invoice-modal__footer">
            <button class="invoice-btn invoice-btn--primary" type="button" data-close-modal>Đóng</button>
        </footer>
    </section>
</div>

<div class="invoice-toast" id="invoiceToast" role="status" aria-live="polite">
    <i class="fas fa-circle-check"></i>
    <div>
        <strong>Thành công</strong>
        <span id="toastMessage">Đã cập nhật thao tác</span>
    </div>
</div>

<script src="<%= request.getContextPath() %>/Admin/QuanLyHoaDon/hoa_don.js"></script>
</body>
</html>
