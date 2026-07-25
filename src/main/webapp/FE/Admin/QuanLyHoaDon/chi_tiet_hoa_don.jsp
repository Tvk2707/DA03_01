<%@ page import="QuanLyHoaDon.Model.ChiTietHoaDonView" %>
<%@ page import="QuanLyHoaDon.Model.HoaDonView" %>
<%@ page import="QuanLyHoaDon.Model.LichSuHoaDonView" %>
<%@ page import="QuanLyHoaDon.Model.LichSuThanhToanView" %>
<%@ page import="QuanLyHoaDon.Model.ThanhToanHoaDonView" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Nếu mở trực tiếp JSP không qua controller thì quay về danh sách hóa đơn.
    if (request.getAttribute("hoaDon") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/hoa-don");
        return;
    }

    request.setAttribute("pageTitle", "Chi tiết hóa đơn");
    request.setAttribute("activeMenu", "hoadon");

    HoaDonView hoaDon = (HoaDonView) request.getAttribute("hoaDon");
    List<ChiTietHoaDonView> chiTietList = (List<ChiTietHoaDonView>) request.getAttribute("chiTietList");
    List<ThanhToanHoaDonView> paymentList = (List<ThanhToanHoaDonView>) request.getAttribute("paymentList");
    List<LichSuThanhToanView> paymentHistoryList = (List<LichSuThanhToanView>) request.getAttribute("paymentHistoryList");
    List<LichSuHoaDonView> historyList = (List<LichSuHoaDonView>) request.getAttribute("historyList");

    if (chiTietList == null) {
        chiTietList = Collections.emptyList();
    }

    if (paymentList == null) {
        paymentList = Collections.emptyList();
    }

    if (paymentHistoryList == null) {
        paymentHistoryList = Collections.emptyList();
    }

    if (historyList == null) {
        historyList = Collections.emptyList();
    }
    DecimalFormat moneyFormat = new DecimalFormat("#,###");
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    int productCount = 0;
    BigDecimal paidTotal = BigDecimal.ZERO;
    BigDecimal invoiceSubtotal = BigDecimal.ZERO;

    // Tính tổng số sản phẩm trong hóa đơn.
    for (ChiTietHoaDonView detail : chiTietList) {
        int quantity = detail.getSoLuong() == null ? 0 : detail.getSoLuong();
        BigDecimal unitPrice = detail.getDonGia() == null ? BigDecimal.ZERO : detail.getDonGia();
        productCount += quantity;
        invoiceSubtotal = invoiceSubtotal.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }

    // Tính tổng tiền đã thanh toán.
    for (ThanhToanHoaDonView payment : paymentList) {
        paidTotal = paidTotal.add(payment.getSoTien() == null ? BigDecimal.ZERO : payment.getSoTien());
    }
    BigDecimal invoiceTotal = hoaDon.getTongTienThanhToan() == null
            ? BigDecimal.ZERO : hoaDon.getTongTienThanhToan();
    BigDecimal invoiceDiscount = invoiceSubtotal.subtract(invoiceTotal);
    if (invoiceDiscount.signum() < 0) {
        invoiceDiscount = BigDecimal.ZERO;
    }
    BigDecimal amountDue = invoiceTotal.subtract(paidTotal);
    if (amountDue.signum() < 0) {
        amountDue = BigDecimal.ZERO;
    }
    boolean isPaid = hoaDon.getTrangThai() != null && hoaDon.getTrangThai() == 3;
    boolean isCancelled = hoaDon.getTrangThai() != null && hoaDon.getTrangThai() == 5;
    String customerName = text(hoaDon.getTenKhachHang());
    if ("-".equals(customerName)) {
        customerName = text(hoaDon.getTenNguoiNhan());
    }
    if ("-".equals(customerName)) {
        customerName = "Khách lẻ";
    }
    String customerPhone = text(hoaDon.getSoDienThoaiKhachHang());
    if ("-".equals(customerPhone)) {
        customerPhone = text(hoaDon.getSoDienThoai());
    }
    String employeeDisplay = text(hoaDon.getTenNhanVien());
    if (!"-".equals(employeeDisplay) && !"-".equals(text(hoaDon.getMaNhanVien()))) {
        employeeDisplay += " (" + hoaDon.getMaNhanVien() + ")";
    } else if ("-".equals(employeeDisplay)) {
        employeeDisplay = text(hoaDon.getMaNhanVien());
    }
    String voucherDisplay = text(hoaDon.getMaVoucher());
    if (!"-".equals(voucherDisplay) && !"-".equals(text(hoaDon.getTenVoucher()))) {
        voucherDisplay += " - " + hoaDon.getTenVoucher();
    }
    String qrContent = "Hóa đơn: " + text(hoaDon.getMaHoaDon())
            + " | Tổng tiền: " + moneyFormat.format(hoaDon.getTongTienThanhToan()) + " đ"
            + " | Ngày tạo: " + (hoaDon.getNgayTao() == null ? "" : hoaDon.getNgayTao().format(dateFormat));
    String qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=180x180&data="
            + URLEncoder.encode(qrContent, StandardCharsets.UTF_8);
%>
<%!
    private String text(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String productMeta(ChiTietHoaDonView detail) {
        StringBuilder builder = new StringBuilder();
        appendMeta(builder, "Ma", detail.getMaSanPhamChiTiet());
        appendMeta(builder, "DM", detail.getDanhMuc());
        appendMeta(builder, "TH", detail.getThuongHieu());
        appendMeta(builder, "Mau", detail.getMauSac());
        appendMeta(builder, "KC", detail.getKichCo());
        appendMeta(builder, "CL", detail.getChatLieu());
        appendMeta(builder, "Dang", detail.getKieuDang());
        appendMeta(builder, "Gong", detail.getHinhDangGong());
        appendMeta(builder, "Quai", detail.getKieuQuaiKinh());
        appendMeta(builder, "Trong", detail.getLoaiTrong());
        return builder.length() == 0 ? "-" : builder.toString();
    }

    private String imageUrl(ChiTietHoaDonView detail, String contextPath) {
        String image = detail.getHinhAnhSanPham();

        if (image == null || image.trim().isEmpty()) {
            return "";
        }

        String normalizedImage = image.trim().replace("\\", "/");

        if (normalizedImage.startsWith("http://")
                || normalizedImage.startsWith("https://")
                || normalizedImage.startsWith("/")) {
            return normalizedImage;
        }

        if (normalizedImage.startsWith("FE/") || normalizedImage.startsWith("File_Anh/")) {
            return contextPath + "/" + normalizedImage;
        }

        if (normalizedImage.startsWith("hinh_anh_san_pham/")) {
            return contextPath + "/FE/Admin/" + normalizedImage;
        }

        // Database lưu tên file, còn ảnh được đặt trong thư mục hinh_anh_san_pham.
        return contextPath + "/FE/Admin/hinh_anh_san_pham/" + normalizedImage;
    }

    private void appendMeta(StringBuilder builder, String label, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        if (builder.length() > 0) {
            builder.append(" | ");
        }

        builder.append(label).append(": ").append(value);
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

    private String receiptText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String vietnameseNumber(BigDecimal value) {
        if (value == null) {
            return "Không đồng";
        }

        long amount = value.setScale(0, RoundingMode.HALF_UP).longValue();
        if (amount == 0) {
            return "Không đồng";
        }

        if (amount < 0) {
            return "Âm " + vietnameseNumber(BigDecimal.valueOf(-amount));
        }

        long[] groups = {1_000_000_000L, 1_000_000L, 1_000L, 1L};
        String[] names = {"tỷ", "triệu", "nghìn", ""};
        StringBuilder result = new StringBuilder();
        long remaining = amount;

        for (int i = 0; i < groups.length; i++) {
            int group = (int) (remaining / groups[i]);
            remaining %= groups[i];
            if (group == 0) {
                continue;
            }

            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(readReceiptGroup(group, result.length() == 0)).append(" ").append(names[i]);
        }

        String words = result.toString().trim();
        return words.substring(0, 1).toUpperCase() + words.substring(1) + " đồng chẵn";
    }

    private String readReceiptGroup(int number, boolean firstGroup) {
        String[] digits = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        int hundreds = number / 100;
        int tens = (number / 10) % 10;
        int ones = number % 10;
        StringBuilder result = new StringBuilder();

        if (hundreds > 0) {
            result.append(digits[hundreds]).append(" trăm");
        } else if (!firstGroup && number > 0) {
            result.append("không trăm");
        }

        if (tens > 1) {
            result.append(" ").append(digits[tens]).append(" mươi");
            if (ones == 1) {
                result.append(" mốt");
            } else if (ones == 5) {
                result.append(" lăm");
            } else if (ones > 0) {
                result.append(" ").append(digits[ones]);
            }
        } else if (tens == 1) {
            result.append(" mười");
            if (ones == 5) {
                result.append(" lăm");
            } else if (ones > 0) {
                result.append(" ").append(digits[ones]);
            }
        } else if (ones > 0) {
            if (result.length() > 0) {
                result.append(" lẻ");
            }
            result.append(" ").append(digits[ones]);
        }

        return result.toString().trim();
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
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/layout.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/sidebar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/hoa_don.css">
</head>
<body>
<%@ include file="/Admin/layout/sidebar.jsp" %>

<div class="main-content">
    <%@ include file="/Admin/layout/header.jsp" %>

    <main id="page-content" class="invoice-page invoice-detail-page">
        <section class="invoice-detail-page-heading">
            <div>
                <h1 class="invoice-title">Chi tiết hóa đơn</h1>
            </div>
            <div class="invoice-header-actions">
                <button class="invoice-btn invoice-btn--outline" id="btnPrintDetail" type="button">
                    <i class="fas fa-print"></i>
                    In hóa đơn
                </button>
                <a class="invoice-btn invoice-btn--outline" href="<%= request.getContextPath() %>/admin/hoa-don">
                    <i class="fas fa-arrow-left"></i>
                    Quay lại
                </a>
            </div>
        </section>

        <%-- Thanh tiến trình trạng thái được đặt đầu tiên để dễ theo dõi. --%>
        <section class="invoice-timeline-card invoice-timeline-card--top">
            <div class="invoice-timeline">
                <div class="timeline-step<%= stepClass(hoaDon.getTrangThai(), 1) %>">
                    <div class="timeline-step__name">Chờ thanh toán</div>
                    <div class="timeline-step__dot"><i class="fas fa-check"></i></div>
                </div>
                <div class="timeline-step<%= stepClass(hoaDon.getTrangThai(), 3) %>">
                    <div class="timeline-step__name">Đã thanh toán</div>
                    <div class="timeline-step__dot"><i class="fas fa-check"></i></div>
                </div>
                <div class="timeline-step<%= stepClass(hoaDon.getTrangThai(), 5) %>">
                    <div class="timeline-step__name">Đã hủy</div>
                    <div class="timeline-step__dot"><i class="fas fa-xmark"></i></div>
                </div>
            </div>
        </section>

        <%-- Khu vực làm việc chính: sản phẩm, thông tin giao hàng và thanh toán. --%>
        <section class="invoice-workspace">
            <div class="invoice-workspace__left">
                <div class="invoice-list-card invoice-detail-section">
                    <div class="invoice-card-heading invoice-card-heading--compact">
                        <div>
                        <h2>Sản phẩm trong hóa đơn</h2>
                        <p>Danh sách sản phẩm khách mua tại quầy</p>
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
                                <td>
                                    <% String firstImageUrl = imageUrl(detail, request.getContextPath()); %>
                                    <% if (firstImageUrl.isEmpty()) { %>
                                    <span class="invoice-thumb"><i class="fas fa-glasses"></i></span>
                                    <% } else { %>
                                    <img class="invoice-thumb" src="<%= firstImageUrl %>" alt="<%= text(detail.getTenSanPham()) %>"
                                         onerror="this.style.display='none';this.nextElementSibling.style.display='inline-flex'">
                                    <span class="invoice-thumb" style="display:none"><i class="fas fa-glasses"></i></span>
                                    <% } %>
                                </td>
                                <td>
                                    <strong><%= text(detail.getTenSanPham()) %></strong>
                                    <small class="invoice-product-meta"><%= productMeta(detail) %></small>
                                </td>
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
                    <h3><i class="far fa-user"></i> Thông tin nhận hàng</h3>
                    <div class="invoice-shipping-grid">
                        <label class="invoice-field">
                            <span>Tên người nhận</span>
                            <input type="text" value="<%= customerName %>" readonly>
                        </label>
                        <label class="invoice-field">
                            <span>SĐT người nhận</span>
                            <input type="text" value="<%= customerPhone %>" readonly>
                        </label>
                        <label class="invoice-field">
                            <span>Nhân viên bán hàng</span>
                            <input type="text" value="<%= employeeDisplay %>" readonly>
                        </label>
                        <label class="invoice-field">
                            <span>Loại hóa đơn</span>
                            <input type="text" value="Tại quầy" readonly>
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
                <div class="invoice-total-lines">
                    <div class="invoice-total-line">
                        <span>Tạm tính</span>
                        <strong><%= moneyFormat.format(invoiceSubtotal) %> đ</strong>
                    </div>
                    <div class="invoice-total-line">
                        <span>Phí vận chuyển</span>
                        <strong>0 đ</strong>
                    </div>
                    <% if (!"-".equals(voucherDisplay)) { %>
                    <div class="invoice-total-line">
                        <span>Voucher</span>
                        <strong><%= voucherDisplay %></strong>
                    </div>
                    <% } %>
                    <div class="invoice-total-line">
                        <span>Giảm giá</span>
                        <strong><%= moneyFormat.format(invoiceDiscount) %> đ</strong>
                    </div>
                    <div class="invoice-total-line invoice-total-line--final">
                        <span>Tổng phải trả</span>
                        <strong><%= moneyFormat.format(invoiceTotal) %> đ</strong>
                    </div>
                    <div class="invoice-total-line">
                        <span>Khách thanh toán</span>
                        <strong><%= moneyFormat.format(paidTotal) %> đ</strong>
                    </div>
                    <div class="invoice-total-line">
                        <span>Phải thu</span>
                        <strong><%= moneyFormat.format(amountDue) %> đ</strong>
                    </div>
                </div>
                <% if (isPaid) { %>
                <div class="invoice-payment-option">
                    <span>Trạng thái</span>
                    <strong>Đã thanh toán đủ</strong>
                </div>
                <% } else { %>
                <form method="post" action="<%= request.getContextPath() %>/admin/hoa-don">
                    <input type="hidden" name="action" value="changeStatus">
                    <input type="hidden" name="id" value="<%= hoaDon.getId() %>">
                    <input type="hidden" name="trangThai" value="3">
                    <input type="hidden" name="ghiChu" value="Thanh toán tiền mặt từ màn hình hóa đơn">
                    <button class="invoice-pay-btn" type="submit" <%= isCancelled ? "disabled" : "" %>>
                        <i class="fas fa-money-bill-wave"></i>
                        Thanh toán tiền mặt
                    </button>
                </form>
                <button class="invoice-pay-btn invoice-pay-btn--qr" type="button" data-open-modal="qrPaymentModal" <%= isCancelled ? "disabled" : "" %>>
                    <i class="fas fa-qrcode"></i>
                    Thanh toán QR
                </button>
                <% } %>
            </aside>
        </section>

        <%-- Các nút xử lý: cập nhật trạng thái, hủy hóa đơn, in, xem lịch sử. --%>
        <section class="invoice-detail-actions">
            <div class="invoice-action-left">
                <button class="invoice-btn invoice-btn--primary" type="button" data-open-modal="statusModal" <%= isPaid || isCancelled ? "disabled" : "" %>>
                    <i class="fas fa-arrows-rotate"></i>
                    <span>Đổi trạng thái</span>
                </button>
                <form method="post" action="<%= request.getContextPath() %>/admin/hoa-don">
                    <input type="hidden" name="action" value="changeStatus">
                    <input type="hidden" name="id" value="<%= hoaDon.getId() %>">
                    <input type="hidden" name="trangThai" value="5">
                    <input type="hidden" name="ghiChu" value="Hủy hóa đơn từ màn hình chi tiết">
                    <button class="invoice-btn invoice-btn--danger" type="submit" <%= isPaid || isCancelled ? "disabled" : "" %>>
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
            </div>
        </section>

        <section class="invoice-history-layout">
            <%-- Thông tin chi tiết được đặt cạnh lịch sử thanh toán. --%>
            <section class="invoice-page-header invoice-detail-summary-card">
                <div class="invoice-detail-header-copy">
                    <h1 class="invoice-title">Thông tin khách hàng</h1>
                    <div class="invoice-customer-summary">
                        <div>
                            <span>Họ tên</span>
                            <strong><%= customerName %></strong>
                        </div>
                        <div>
                            <span>Số điện thoại</span>
                            <strong><%= customerPhone %></strong>
                        </div>
                        <div>
                            <span>Email</span>
                            <strong><%= text(hoaDon.getEmailKhachHang()) %></strong>
                        </div>
                        <div>
                            <span>Mã khách hàng</span>
                            <strong><%= "-".equals(text(hoaDon.getMaKhachHang())) ? "Khách lẻ" : text(hoaDon.getMaKhachHang()) %></strong>
                        </div>
                        <div>
                            <span>Ghi chú</span>
                            <strong><%= text(hoaDon.getGhiChu()) %></strong>
                        </div>
                    </div>
                </div>
            </section>

            <%-- Bảng lịch sử thanh toán của hóa đơn. --%>
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
        </section>

        <%-- Bảng lịch sử thanh toán đọc trực tiếp từ bảng lich_su_thanh_toan. --%>
        <section class="invoice-list-card invoice-detail-section">
            <div class="invoice-card-heading invoice-card-heading--compact">
                <div>
                    <h2>Lịch sử thanh toán hệ thống</h2>
                    <p>Giao dịch thanh toán của hóa đơn</p>
                </div>
            </div>
            <div class="invoice-table-wrap">
                <table class="invoice-table">
                    <thead>
                    <tr>
                        <th>Số tiền</th>
                        <th>Phương thức</th>
                        <th>Trạng thái</th>
                        <th>Ngày thanh toán</th>
                        <th>Ghi chú</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (LichSuThanhToanView paymentHistory : paymentHistoryList) { %>
                    <tr>
                        <td class="invoice-money"><%= moneyFormat.format(paymentHistory.getSoTien()) %> đ</td>
                        <td><%= text(paymentHistory.getPhuongThucThanhToan()) %></td>
                        <td><%= paymentHistory.getTrangThaiThanhToan() == null ? "-" : paymentHistory.getTrangThaiThanhToan() %></td>
                        <td><%= paymentHistory.getNgayThanhToan() == null ? "-" : paymentHistory.getNgayThanhToan().format(dateFormat) %></td>
                        <td><%= text(paymentHistory.getGhiChu()) %></td>
                    </tr>
                    <% } %>
                    <% if (paymentHistoryList.isEmpty()) { %>
                    <tr><td colspan="5">Chưa có lịch sử thanh toán.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</div>

<%
    BigDecimal receiptSubtotal = invoiceSubtotal;
    int receiptQuantity = 0;
    for (ChiTietHoaDonView detail : chiTietList) {
        receiptQuantity += detail.getSoLuong() == null ? 0 : detail.getSoLuong();
    }
    BigDecimal receiptTotal = hoaDon.getTongTienThanhToan() == null
            ? receiptSubtotal : hoaDon.getTongTienThanhToan();
    BigDecimal receiptPaid = paidTotal == null ? BigDecimal.ZERO : paidTotal;
    BigDecimal receiptChange = receiptPaid.subtract(receiptTotal);
    if (receiptChange.signum() < 0) {
        receiptChange = BigDecimal.ZERO;
    }
    BigDecimal receiptDiscount = receiptSubtotal.subtract(receiptTotal);
    if (receiptDiscount.signum() < 0) {
        receiptDiscount = BigDecimal.ZERO;
    }
    String receiptCustomer = customerName;
%>

<section class="invoice-receipt-print" aria-label="Hóa đơn bán hàng">
    <header class="receipt-header">
        <strong>RIOR</strong>
        <span>Fine Eyewear &amp; Optics</span>
        <span>82 VT7 - Hà Nội</span>
        <span>Hotline: 0437373076 - 091250165</span>
        <h1>HÓA ĐƠN BÁN HÀNG</h1>
    </header>

    <div class="receipt-divider"></div>
    <div class="receipt-info">
        <div class="receipt-meta-row">
            <span>Ngày: <%= hoaDon.getNgayTao() == null ? "-" : hoaDon.getNgayTao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) %></span>
            <span>Số HĐ: <%= receiptText(hoaDon.getMaHoaDon()) %></span>
        </div>
        <div class="receipt-meta-row">
            <span>Người bán: <%= receiptText(employeeDisplay) %></span>
            <span>Loại đơn: Tại quầy</span>
        </div>
        <div>Khách: <%= receiptText(receiptCustomer) %></div>
        <div>SĐT: <%= receiptText(customerPhone) %></div>
        <div>Địa chỉ: <%= receiptText(hoaDon.getGhiChu()) %></div>
    </div>
    <div class="receipt-divider"></div>
    <table class="receipt-table">
        <thead>
        <tr>
            <th>Tên hàng</th>
            <th>SL</th>
            <th>Đơn giá</th>
            <th>Thành tiền</th>
        </tr>
        </thead>
        <tbody>
        <% for (ChiTietHoaDonView detail : chiTietList) { %>
        <tr>
            <td><%= receiptText(text(detail.getTenSanPham())) %></td>
            <td><%= detail.getSoLuong() == null ? 0 : detail.getSoLuong() %></td>
            <td><%= moneyFormat.format(detail.getDonGia() == null ? BigDecimal.ZERO : detail.getDonGia()) %> đ</td>
            <td><%= moneyFormat.format(detail.getTongTien() == null ? BigDecimal.ZERO : detail.getTongTien()) %> đ</td>
        </tr>
        <% } %>
        <% if (chiTietList.isEmpty()) { %>
        <tr><td colspan="4">Chưa có sản phẩm</td></tr>
        <% } %>
        </tbody>
        <tfoot>
        <tr>
            <th colspan="2">Tổng tiền</th>
            <th><%= receiptQuantity %></th>
            <th><%= moneyFormat.format(receiptSubtotal) %> đ</th>
        </tr>
        </tfoot>
    </table>

    <div class="receipt-summary">
        <div><span>Tổng tiền</span><strong><%= moneyFormat.format(receiptSubtotal) %> đ</strong></div>
        <div><span>Giảm giá</span><strong><%= moneyFormat.format(receiptDiscount) %> đ</strong></div>
        <% if (!"-".equals(voucherDisplay)) { %>
        <div><span>Voucher</span><strong><%= receiptText(voucherDisplay) %></strong></div>
        <% } %>
        <div><span>Phí vận chuyển</span><strong>0 đ</strong></div>
        <div class="receipt-summary-total"><span>TỔNG THANH TOÁN</span><strong><%= moneyFormat.format(receiptTotal) %> đ</strong></div>
    </div>

    <p class="receipt-words"><em>Bằng chữ: <%= vietnameseNumber(receiptTotal) %></em></p>
    <div class="receipt-divider"></div>
    <div class="receipt-qr">
        <img src="<%= qrImageUrl %>" alt="Mã QR hóa đơn <%= text(hoaDon.getMaHoaDon()) %>">
        <span>Quét để tra cứu: <%= receiptText(hoaDon.getMaHoaDon()) %></span>
    </div>
    <p class="receipt-thanks">Cảm ơn quý khách!</p>
</section>

<%-- Modal thanh toán QR theo giao diện chung của website. --%>
<div class="invoice-modal qr-payment-modal" id="qrPaymentModal" aria-hidden="true">
    <div class="invoice-modal__backdrop" data-close-modal></div>
    <section class="invoice-modal__dialog qr-payment-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="qrPaymentTitle">
        <header class="invoice-modal__header qr-payment-modal__header">
            <div>
                <h2 id="qrPaymentTitle"><i class="fas fa-qrcode"></i> Thanh toán QR</h2>
                <p>Quét mã để hoàn tất thanh toán hóa đơn</p>
            </div>
            <button class="invoice-modal__close" type="button" data-close-modal aria-label="Đóng">
                <i class="fas fa-xmark"></i>
            </button>
        </header>
        <form method="post" action="<%= request.getContextPath() %>/admin/hoa-don">
            <div class="invoice-modal__body qr-payment-modal__body">
                <input type="hidden" name="action" value="changeStatus">
                <input type="hidden" name="id" value="<%= hoaDon.getId() %>">
                <input type="hidden" name="trangThai" value="3">

                <div class="qr-payment-summary">
                    <div class="qr-payment-summary__item">
                        <span>Mã hóa đơn</span>
                        <strong><%= text(hoaDon.getMaHoaDon()) %></strong>
                    </div>
                    <div class="qr-payment-summary__item qr-payment-summary__item--amount">
                        <span>Tổng thanh toán</span>
                        <strong><%= moneyFormat.format(hoaDon.getTongTienThanhToan()) %> đ</strong>
                    </div>
                </div>

                <div class="qr-payment-code">
                    <img src="<%= qrImageUrl %>" alt="Mã QR thanh toán hóa đơn <%= text(hoaDon.getMaHoaDon()) %>">
                    <span>Quét mã để thanh toán hóa đơn</span>
                </div>

                <div class="qr-payment-order">
                    <span>Mã đơn</span>
                    <strong><%= text(hoaDon.getMaHoaDon()) %></strong>
                </div>

                <label class="invoice-field invoice-field--full qr-payment-note">
                    <span>Ghi chú</span>
                    <input type="text" name="ghiChu" value="Khách đã thanh toán QR - <%= text(hoaDon.getMaHoaDon()) %>">
                </label>
            </div>
            <footer class="invoice-modal__footer qr-payment-modal__footer">
                <button class="invoice-btn invoice-btn--primary qr-payment-confirm" type="submit">
                    <i class="fas fa-check"></i>
                    Xác nhận thanh toán
                </button>
                <button class="invoice-btn invoice-btn--ghost" type="button" data-close-modal>Đóng</button>
            </footer>
        </form>
    </section>
</div>

<%-- Modal cập nhật trạng thái hóa đơn. Form này gửi action=changeStatus về controller. --%>
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

<%-- Modal xem lịch sử thao tác hóa đơn. --%>
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

<%-- Toast hiện thông báo nhanh, điều khiển bởi hoa_don.js. --%>
<div class="invoice-toast" id="invoiceToast" role="status" aria-live="polite">
    <i class="fas fa-circle-check"></i>
    <div>
        <strong>Thành công</strong>
        <span id="toastMessage">Đã cập nhật thao tác</span>
    </div>
</div>

<script src="<%= request.getContextPath() %>/FE/Admin/QuanLyHoaDon/hoa_don.js"></script>
</body>
</html>
