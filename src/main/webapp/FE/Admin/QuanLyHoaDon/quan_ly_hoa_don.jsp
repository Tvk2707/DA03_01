<%@ page import="BE.Model.HoaDonView" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    request.setAttribute("pageTitle", "Quản lý hóa đơn");
    request.setAttribute("activeMenu", "hoadon");

    List<HoaDonView> hoaDonList = (List<HoaDonView>) request.getAttribute("hoaDonList");
    if (hoaDonList == null && request.getAttribute("errorMessage") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/hoa-don");
        return;
    }
    if (hoaDonList == null) {
        hoaDonList = Collections.emptyList();
    }

    DecimalFormat moneyFormat = new DecimalFormat("#,###");
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    Integer tongHoaDon = (Integer) request.getAttribute("tongHoaDon");
    BigDecimal doanhThu = (BigDecimal) request.getAttribute("doanhThu");
    Integer dangXuLy = (Integer) request.getAttribute("dangXuLy");
    Integer tongSanPham = (Integer) request.getAttribute("tongSanPham");
    String errorMessage = (String) request.getAttribute("errorMessage");

    if (tongHoaDon == null) tongHoaDon = hoaDonList.size();
    if (doanhThu == null) doanhThu = BigDecimal.ZERO;
    if (dangXuLy == null) dangXuLy = 0;
    if (tongSanPham == null) tongSanPham = 0;
%>
<%!
    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String money(DecimalFormat formatter, BigDecimal value) {
        if (value == null) {
            return "0 VND";
        }
        return formatter.format(value) + " VND";
    }

    private String statusText(int status) {
        switch (status) {
            case 1:
                return "Chờ xác nhận";
            case 2:
                return "Đã xác nhận";
            case 3:
                return "Đang xử lý";
            case 4:
                return "Hoàn thành";
            case 5:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }

    private String statusClass(int status) {
        switch (status) {
            case 1:
                return "invoice-status--waiting";
            case 2:
                return "invoice-status--confirmed";
            case 3:
                return "invoice-status--processing";
            case 4:
                return "invoice-status--done";
            case 5:
                return "invoice-status--cancelled";
            default:
                return "";
        }
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý hóa đơn - RIOR Admin</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/layout.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/sidebar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/hoa_don.css?v=202607161130">
</head>
<body>
<%@ include file="../layout/sidebar.jsp" %>

<div class="main-content">
    <%@ include file="../layout/header.jsp" %>

    <main id="page-content" class="invoice-page">
        <section class="invoice-page-header">
            <div>
                <h1 class="invoice-title">Quản lý hóa đơn</h1>
                <p class="invoice-subtitle">Dữ liệu được lấy trực tiếp từ SQL Server</p>
            </div>
            <div class="invoice-header-actions">
                <button class="invoice-btn invoice-btn--outline" id="btnResetFilters" type="button">
                    <i class="fas fa-rotate-right"></i>
                    Đặt lại
                </button>
                <button class="invoice-btn invoice-btn--primary" id="btnExportOrders" type="button">
                    <i class="fas fa-file-excel"></i>
                    Xuất Excel trang HĐ
                </button>
            </div>
        </section>

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
                    <input id="orderSearch" type="search" placeholder="Nhập mã hóa đơn / tên khách / SĐT...">
                </label>
                <label class="invoice-field">
                    <span>Loại hóa đơn</span>
                    <select id="orderTypeFilter">
                        <option value="all">Tất cả</option>
                        <option value="Tại quầy">Tại quầy</option>
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

        <section class="invoice-stats">
            <div class="invoice-stat">
                <span class="invoice-stat__label">Tổng hóa đơn</span>
                <strong><%= tongHoaDon %></strong>
                <small>Dữ liệu từ SQL Server</small>
            </div>
            <div class="invoice-stat">
                <span class="invoice-stat__label">Doanh thu</span>
                <strong><%= money(moneyFormat, doanhThu) %></strong>
                <small>Đơn hoàn thành</small>
            </div>
            <div class="invoice-stat">
                <span class="invoice-stat__label">Đang xử lý</span>
                <strong><%= dangXuLy %></strong>
                <small>Chưa hoàn thành</small>
            </div>
            <div class="invoice-stat">
                <span class="invoice-stat__label">Sản phẩm kính</span>
                <strong><%= tongSanPham %></strong>
                <small>Tổng số lượng trong hóa đơn</small>
            </div>
        </section>

        <% if (errorMessage != null) { %>
        <div class="invoice-empty is-visible">
            <i class="fas fa-triangle-exclamation"></i>
            <%= errorMessage %>
        </div>
        <% } %>

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
                <button class="invoice-tab" type="button" data-status="Chờ xác nhận">Chờ xác nhận</button>
                <button class="invoice-tab" type="button" data-status="Đã xác nhận">Đã xác nhận</button>
                <button class="invoice-tab" type="button" data-status="Đang xử lý">Đang xử lý</button>
                <button class="invoice-tab" type="button" data-status="Hoàn thành">Hoàn thành</button>
                <button class="invoice-tab" type="button" data-status="Đã hủy">Đã hủy</button>
            </div>

            <div class="invoice-table-wrap">
                <table class="invoice-table" id="ordersTable">
                    <thead>
                    <tr>
                        <th>STT</th>
                        <th>Mã hóa đơn</th>
                        <th>Tên nhân viên</th>
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
                    <% for (HoaDonView hoaDon : hoaDonList) {
                        String ngayTao = hoaDon.getNgayTao() == null ? "-" : hoaDon.getNgayTao().format(dateFormat);
                        String trangThai = statusText(hoaDon.getTrangThai());
                        String searchText = (safe(hoaDon.getMaHoaDon()) + " "
                                + safe(hoaDon.getTenNhanVien()) + " "
                                + safe(hoaDon.getMaNhanVien()) + " "
                                + safe(hoaDon.getTenKhachHang()) + " "
                                + safe(hoaDon.getSoDienThoai())).toLowerCase();
                        String dateValue = hoaDon.getNgayTao() == null ? "" : hoaDon.getNgayTao().toLocalDate().toString();
                    %>
                    <tr data-id="<%= hoaDon.getId() %>"
                        data-search="<%= searchText %>"
                        data-type="<%= safe(hoaDon.getLoaiHoaDon()) %>"
                        data-status="<%= trangThai %>"
                        data-date="<%= dateValue %>">
                        <td><%= hoaDon.getStt() %></td>
                        <td><strong><%= safe(hoaDon.getMaHoaDon()) %></strong></td>
                        <td><%= safe(hoaDon.getTenNhanVien()) %> <small><%= safe(hoaDon.getMaNhanVien()) %></small></td>
                        <td><%= safe(hoaDon.getTenKhachHang()) %></td>
                        <td><%= safe(hoaDon.getSoDienThoai()) %></td>
                        <td><span class="invoice-pill"><%= safe(hoaDon.getLoaiHoaDon()) %></span></td>
                        <td class="invoice-money"><%= money(moneyFormat, hoaDon.getTongTien()) %></td>
                        <td><%= ngayTao %></td>
                        <td><span class="invoice-status <%= statusClass(hoaDon.getTrangThai()) %>"><%= trangThai %></span></td>
                        <td>
                            <div class="invoice-actions">
                                <a class="invoice-icon-btn" href="<%= request.getContextPath() %>/FE/Admin/QuanLyHoaDon/chi_tiet_hoa_don.jsp?id=<%= hoaDon.getId() %>" title="Xem chi tiết"><i class="fas fa-eye"></i></a>
                                <button class="invoice-icon-btn" type="button" data-print="<%= safe(hoaDon.getMaHoaDon()) %>" title="In hóa đơn"><i class="fas fa-print"></i></button>
                            </div>
                        </td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
                <div class="invoice-empty" id="emptyState">
                    <i class="fas fa-receipt"></i>
                    Không có dữ liệu phù hợp
                </div>
            </div>

            <div class="invoice-table-footer">
                <span id="orderCount">Hiển thị <%= hoaDonList.size() %> / tổng <%= hoaDonList.size() %> bản ghi</span>
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

<div class="invoice-toast" id="invoiceToast" role="status" aria-live="polite">
    <i class="fas fa-circle-check"></i>
    <div>
        <strong>Thông báo</strong>
        <span id="toastMessage">Đã cập nhật thao tác</span>
    </div>
</div>

<script src="<%= request.getContextPath() %>/FE/Admin/QuanLyHoaDon/hoa_don.js"></script>
</body>
</html>
