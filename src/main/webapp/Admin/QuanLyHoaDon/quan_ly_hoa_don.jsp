<%@ page import="BE.Model.HoaDonView" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    if (request.getAttribute("hoaDonList") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/hoa-don");
        return;
    }

    request.setAttribute("pageTitle", "Quản lý hóa đơn");
    request.setAttribute("activeMenu", "hoadon");

    List<HoaDonView> hoaDonList = (List<HoaDonView>) request.getAttribute("hoaDonList");
    DecimalFormat moneyFormat = new DecimalFormat("#,###");
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    BigDecimal doanhThu = BigDecimal.ZERO;
    int dangXuLy = 0;

    for (HoaDonView item : hoaDonList) {
        if (item.getTrangThai() != null && item.getTrangThai() == 3) {
            doanhThu = doanhThu.add(item.getTongTienThanhToan());
        }

        if (item.getTrangThai() == null || item.getTrangThai() == 1) {
            dangXuLy++;
        }
    }
%>
<%!
    private String text(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
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
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/layout.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/sidebar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/hoa_don.css">
</head>
<body>
<%@ include file="../layout/sidebar.jsp" %>

<div class="main-content">
    <%@ include file="../layout/header.jsp" %>

    <main id="page-content" class="invoice-page">
        <section class="invoice-page-header">
            <div>
                <h1 class="invoice-title">Quản lý hóa đơn</h1>
                <p class="invoice-subtitle">Theo dõi đơn kính mắt từ dữ liệu SQL Server.</p>
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
                <button class="invoice-btn invoice-btn--primary" id="btnShowForm" type="button">
                    <i class="fas fa-plus"></i>
                    Thêm hóa đơn
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
                    <input id="orderSearch" type="search" placeholder="Nhập mã hóa đơn, khách hàng hoặc SĐT">
                </label>
                <label class="invoice-field">
                    <span>Loại hóa đơn</span>
                    <select id="orderTypeFilter">
                        <option value="all">Tất cả</option>
                        <option value="Tại quầy">Tại quầy</option>
                        <option value="Online">Online</option>
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

        <section class="invoice-stats">
            <div class="invoice-stat">
                <span class="invoice-stat__label">Tổng hóa đơn</span>
                <strong><%= hoaDonList.size() %></strong>
                <small>Dữ liệu từ bảng hoa_don</small>
            </div>
            <div class="invoice-stat">
                <span class="invoice-stat__label">Doanh thu</span>
                <strong><%= moneyFormat.format(doanhThu) %> đ</strong>
                <small>Hóa đơn đã thanh toán</small>
            </div>
            <div class="invoice-stat">
                <span class="invoice-stat__label">Đang xử lý</span>
                <strong><%= dangXuLy %></strong>
                <small>Chờ thanh toán/xác nhận</small>
            </div>
            <div class="invoice-stat">
                <span class="invoice-stat__label">Đã hủy</span>
                <strong><%= hoaDonList.stream().filter(item -> item.getTrangThai() != null && item.getTrangThai() == 5).count() %></strong>
                <small>Hóa đơn ngừng xử lý</small>
            </div>
        </section>

        <section class="invoice-form-card" id="invoiceFormCard">
            <div class="invoice-card-heading">
                <div class="invoice-heading-icon">
                    <i class="fas fa-file-circle-plus"></i>
                </div>
                <div>
                    <h2 id="formTitle">Thêm hóa đơn</h2>
                    <p>Form này lưu dữ liệu vào bảng hoa_don.</p>
                </div>
            </div>

            <form class="invoice-simple-form" method="post" action="<%= request.getContextPath() %>/admin/hoa-don">
                <input type="hidden" name="action" value="save">
                <input type="hidden" name="id" id="invoiceId">

                <label class="invoice-field">
                    <span>Mã hóa đơn</span>
                    <input id="maHoaDon" name="maHoaDon" type="text" placeholder="Ví dụ: HD006" required>
                </label>

                <label class="invoice-field">
                    <span>Tên người nhận</span>
                    <input id="tenNguoiNhan" name="tenNguoiNhan" type="text" placeholder="Ví dụ: Nguyễn Văn A">
                </label>

                <label class="invoice-field">
                    <span>Số điện thoại</span>
                    <input id="soDienThoai" name="soDienThoai" type="text" placeholder="Ví dụ: 0901234567">
                </label>

                <label class="invoice-field">
                    <span>Tổng tiền</span>
                    <input id="tongTienThanhToan" name="tongTienThanhToan" type="number" min="0" step="1000" placeholder="Ví dụ: 1500000">
                </label>

                <label class="invoice-field">
                    <span>Trạng thái</span>
                    <select id="trangThai" name="trangThai">
                        <option value="1">Chờ thanh toán</option>
                        <option value="3">Đã thanh toán</option>
                        <option value="5">Đã hủy</option>
                    </select>
                </label>

                <label class="invoice-field invoice-field--full">
                    <span>Ghi chú</span>
                    <textarea id="ghiChu" name="ghiChu" placeholder="Nhập ghi chú nếu có"></textarea>
                </label>

                <div class="invoice-form-actions">
                    <button class="invoice-btn invoice-btn--primary" type="submit">
                        <i class="fas fa-floppy-disk"></i>
                        Lưu hóa đơn
                    </button>
                    <button class="invoice-btn invoice-btn--outline" id="btnCancelForm" type="button">Hủy</button>
                </div>
            </form>
        </section>

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
                        String invoiceType = hoaDon.getTenNhanVien() == null || hoaDon.getTenNhanVien().trim().isEmpty() ? "Online" : "Tại quầy";
                        String dateValue = hoaDon.getNgayTao() == null ? "" : hoaDon.getNgayTao().toLocalDate().toString();
                        String searchText = (hoaDon.getMaHoaDon() + " "
                                + text(hoaDon.getTenNguoiNhan()) + " "
                                + text(hoaDon.getSoDienThoai())).toLowerCase();
                    %>
                    <tr data-search="<%= searchText %>" data-status="<%= statusLabel %>" data-type="<%= invoiceType %>" data-date="<%= dateValue %>">
                        <td><%= i + 1 %></td>
                        <td><strong><%= hoaDon.getMaHoaDon() %></strong></td>
                        <td><%= text(hoaDon.getTenNhanVien()) %></td>
                        <td><%= text(hoaDon.getTenNguoiNhan()) %></td>
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
                                <button class="invoice-icon-btn" type="button" data-print="<%= hoaDon.getMaHoaDon() %>" title="In hóa đơn">
                                    <i class="fas fa-print"></i>
                                </button>
                                <button class="invoice-icon-btn" type="button"
                                        data-edit
                                        data-id="<%= hoaDon.getId() %>"
                                        data-code="<%= hoaDon.getMaHoaDon() %>"
                                        data-name="<%= text(hoaDon.getTenNguoiNhan()) %>"
                                        data-phone="<%= text(hoaDon.getSoDienThoai()) %>"
                                        data-total="<%= hoaDon.getTongTienThanhToan() %>"
                                        data-status-value="<%= hoaDon.getTrangThai() %>"
                                        data-note="<%= text(hoaDon.getGhiChu()) %>"
                                        title="Sửa">
                                    <i class="fas fa-pen"></i>
                                </button>
                                <form method="post" action="<%= request.getContextPath() %>/admin/hoa-don" class="invoice-delete-form">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="<%= hoaDon.getId() %>">
                                    <button class="invoice-icon-btn" type="submit" title="Hủy hóa đơn">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </form>
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
