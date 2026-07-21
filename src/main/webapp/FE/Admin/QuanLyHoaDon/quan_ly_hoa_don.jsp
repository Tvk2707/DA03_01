<%@ page import="QuanLyHoaDon.Model.HoaDonView" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Nếu người dùng mở trực tiếp JSP thì chuyển về controller để nạp dữ liệu trước.
    if (request.getAttribute("hoaDonList") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/hoa-don");
        return;
    }

    request.setAttribute("pageTitle", "Quản lý hóa đơn");
    request.setAttribute("activeMenu", "hoadon");

    List<HoaDonView> hoaDonList = (List<HoaDonView>) request.getAttribute("hoaDonList");
    DecimalFormat moneyFormat = new DecimalFormat("#,###");
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
%>
<%!
    private String text(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String attr(String value) {
        return text(value)
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String statusText(Integer status) {
        if (status == null) {
            return "Chờ thanh toán";
        }

        switch (status) {
            case 3:
                return "Đã thanh toán";
            case 5:
                return "Đã hủy";
            default:
                return "Chờ thanh toán";
        }
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
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý hóa đơn</title>

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

    <main id="page-content" class="invoice-page">
        <%-- Tiêu đề trang và các nút thao tác nhanh. --%>
        <section class="invoice-page-header">
            <div>
                <h1 class="invoice-title">Quản lý hóa đơn</h1>
            </div>
            <div class="invoice-header-actions">
                <button class="invoice-btn invoice-btn--outline" id="btnResetFilters" type="button">
                    <i class="fas fa-rotate-right"></i>
                    Đặt lại
                </button>
                <button class="invoice-btn invoice-btn--outline" id="btnExportOrders" type="button">
                    <i class="fas fa-file-excel"></i>
                    Xuất Excel trang HĐ
                </button>
            </div>
        </section>

        <%-- Bộ lọc tìm kiếm hóa đơn trên giao diện, xử lý bằng JavaScript. --%>
        <section class="invoice-filter-card">
            <button class="invoice-section-toggle" type="button" id="filterToggle">
                <span>
                    <i class="fas fa-filter"></i>
                    Bộ lọc tìm kiếm
                </span>
                <span class="toggle-note">Nhấn để thu gọn/mở rộng</span>
            </button>
            <div class="invoice-filter-body" id="filterBody">
                <label class="invoice-field">
                    <span>Tìm kiếm</span>
                    <input id="orderSearch" type="search" placeholder="Nhập mã hóa đơn, khách hàng hoặc SĐT">
                </label>
                <label class="invoice-field">
                    <span>Loại hóa đơn</span>
                    <select id="orderTypeFilter">
                        <option value="all">Tất cả</option>
                        <option value="Tại quầy">Tại quầy</option>
                        <option value="Bán hàng online">Bán hàng online</option>
                    </select>
                </label>
                <label class="invoice-field">
                    <span>Trạng thái</span>
                    <select id="statusFilter">
                        <option value="all">Tất cả</option>
                        <option value="Chờ thanh toán">Chờ thanh toán</option>
                        <option value="Đã thanh toán">Đã thanh toán</option>
                        <option value="Đã hủy">Đã hủy</option>
                    </select>
                </label>
                <label class="invoice-field">
                    <span>Từ ngày</span>
                    <input id="fromDateFilter" type="date">
                </label>
                <label class="invoice-field">
                    <span>Đến ngày</span>
                    <input id="toDateFilter" type="date">
                </label>
            </div>
        </section>

        <%-- Bảng danh sách hóa đơn lấy từ request attribute hoaDonList. --%>
        <section class="invoice-list-card">
            <div class="invoice-card-heading">
                <div class="invoice-heading-icon">
                    <i class="fas fa-file-invoice-dollar"></i>
                </div>
                <div>
                    <h2>Danh sách hóa đơn</h2>
                    <p>Lọc nhanh theo trạng thái xử lý đơn</p>
                </div>
            </div>

            <div class="invoice-status-tabs" id="statusTabs" role="tablist" aria-label="Lọc trạng thái hóa đơn">
                <button class="invoice-tab is-active" type="button" data-status="all">Tất cả</button>
                <button class="invoice-tab" type="button" data-status="Chờ thanh toán">Chờ thanh toán</button>
                <button class="invoice-tab" type="button" data-status="Đã thanh toán">Đã thanh toán</button>
                <button class="invoice-tab" type="button" data-status="Đã hủy">Đã hủy</button>
            </div>

            <div class="invoice-table-wrap">
                <table class="invoice-table" id="ordersTable">
                    <thead>
                    <tr>
                        <th>STT</th>
                        <th>Mã hóa đơn</th>
                        <th>Nhân viên</th>
                        <th>Khách hàng</th>
                        <th>Số điện thoại</th>
                        <th>Loại hóa đơn</th>
                        <th>Tổng tiền</th>
                        <th>Ngày tạo</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (int i = 0; i < hoaDonList.size(); i++) {
                        HoaDonView hoaDon = hoaDonList.get(i);
                        String statusLabel = statusText(hoaDon.getTrangThai());
                        String invoiceType = hoaDon.getTenNhanVien() == null || hoaDon.getTenNhanVien().trim().isEmpty()
                                ? "Bán hàng online" : "Tại quầy";
                        String dateValue = hoaDon.getNgayTao() == null ? "" : hoaDon.getNgayTao().toLocalDate().toString();
                        String customerName = hoaDon.getTenNguoiNhan() == null || hoaDon.getTenNguoiNhan().trim().isEmpty()
                                ? hoaDon.getTenKhachHang()
                                : hoaDon.getTenNguoiNhan();
                        String searchText = (hoaDon.getMaHoaDon() + " "
                                + text(customerName) + " "
                                + text(hoaDon.getTenNguoiNhan()) + " "
                                + text(hoaDon.getTenKhachHang()) + " "
                                + text(hoaDon.getSoDienThoai())).toLowerCase();
                    %>
                    <%-- Mỗi dòng table tương ứng với 1 hóa đơn trong database. --%>
                    <tr data-search="<%= searchText %>" data-status="<%= statusLabel %>" data-type="<%= invoiceType %>" data-date="<%= dateValue %>">
                        <td><%= i + 1 %></td>
                        <td><strong><%= hoaDon.getMaHoaDon() %></strong></td>
                        <td><%= text(hoaDon.getMaNhanVien()) %></td>
                        <td><%= text(customerName) %></td>
                        <td><%= text(hoaDon.getSoDienThoai()) %></td>
                        <td><span class="invoice-pill"><%= invoiceType %></span></td>
                        <td class="invoice-money"><%= moneyFormat.format(hoaDon.getTongTienThanhToan()) %> đ</td>
                        <td><%= hoaDon.getNgayTao() == null ? "-" : hoaDon.getNgayTao().format(dateFormat) %></td>
                        <td><span class="invoice-status <%= statusClass(hoaDon.getTrangThai()) %>"><%= statusLabel %></span></td>
                        <td>
                            <div class="invoice-actions">
                                <a class="invoice-icon-btn" href="<%= request.getContextPath() %>/admin/hoa-don/chi-tiet?id=<%= hoaDon.getId() %>" title="Xem chi tiết">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <button class="invoice-icon-btn" type="button"
                                        data-print="<%= attr(hoaDon.getMaHoaDon()) %>"
                                        data-print-url="<%= request.getContextPath() %>/admin/hoa-don/chi-tiet?id=<%= hoaDon.getId() %>&print=1"
                                        title="In hóa đơn">
                                    <i class="fas fa-print"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
                <div class="invoice-empty" id="emptyState">
                    <i class="fas fa-receipt"></i>
                    Không có hóa đơn phù hợp.
                </div>
            </div>

            <div class="invoice-table-footer">
                <span id="orderCount">Hiển thị <%= hoaDonList.size() %> / tổng <%= hoaDonList.size() %> hóa đơn</span>
                <div class="invoice-pagination">
                    <button type="button" disabled><i class="fas fa-chevron-left"></i></button>
                    <span>Trang <strong>1</strong></span>
                    <button type="button" disabled><i class="fas fa-chevron-right"></i></button>
                </div>
                <select aria-label="Số bản ghi mỗi trang">
                    <option>10 bản ghi / trang</option>
                    <option>20 bản ghi / trang</option>
                    <option>50 bản ghi / trang</option>
                </select>
            </div>
        </section>
    </main>
</div>

<%-- Toast hiện thông báo nhanh, nội dung được thay đổi trong hoa_don.js. --%>
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
