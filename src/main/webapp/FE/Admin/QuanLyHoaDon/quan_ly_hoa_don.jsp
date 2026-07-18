<%@ page import="BE.Model.HoaDonView" %>
<<<<<<< HEAD
<%@ page import="BE.Model.NhanVienView" %>
<%@ page import="BE.Model.SanPhamHoaDonView" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Nếu người dùng mở trực tiếp JSP thì chuyển về controller để nạp dữ liệu trước.
    if (request.getAttribute("hoaDonList") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/hoa-don");
        return;
    }

=======
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
>>>>>>> THONG_KE
    request.setAttribute("pageTitle", "Quản lý hóa đơn");
    request.setAttribute("activeMenu", "hoadon");

    List<HoaDonView> hoaDonList = (List<HoaDonView>) request.getAttribute("hoaDonList");
<<<<<<< HEAD
    List<NhanVienView> nhanVienList = (List<NhanVienView>) request.getAttribute("nhanVienList");
    List<SanPhamHoaDonView> sanPhamList = (List<SanPhamHoaDonView>) request.getAttribute("sanPhamList");
    if (nhanVienList == null) {
        nhanVienList = Collections.emptyList();
    }
    if (sanPhamList == null) {
        sanPhamList = Collections.emptyList();
    }
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
=======
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
>>>>>>> THONG_KE
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
<<<<<<< HEAD
    <title>Quản lý hóa đơn</title>
=======
    <title>Quản lý hóa đơn - RIOR Admin</title>
>>>>>>> THONG_KE

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/layout.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/sidebar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/header.css">
<<<<<<< HEAD
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/hoa_don.css">
=======
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/hoa_don.css?v=202607161130">
>>>>>>> THONG_KE
</head>
<body>
<%@ include file="../layout/sidebar.jsp" %>

<div class="main-content">
    <%@ include file="../layout/header.jsp" %>

    <main id="page-content" class="invoice-page">
<<<<<<< HEAD
        <%-- Tiêu đề trang và các nút thao tác nhanh. --%>
        <section class="invoice-page-header">
            <div>
                <h1 class="invoice-title">Quản lý hóa đơn</h1>
=======
        <section class="invoice-page-header">
            <div>
                <h1 class="invoice-title">Quản lý hóa đơn</h1>
                <p class="invoice-subtitle">Dữ liệu được lấy trực tiếp từ SQL Server</p>
>>>>>>> THONG_KE
            </div>
            <div class="invoice-header-actions">
                <button class="invoice-btn invoice-btn--outline" id="btnResetFilters" type="button">
                    <i class="fas fa-rotate-right"></i>
                    Đặt lại
                </button>
<<<<<<< HEAD
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

        <%-- Bộ lọc tìm kiếm hóa đơn trên giao diện, xử lý bằng JavaScript. --%>
=======
                <button class="invoice-btn invoice-btn--primary" id="btnExportOrders" type="button">
                    <i class="fas fa-file-excel"></i>
                    Xuất Excel trang HĐ
                </button>
            </div>
        </section>

>>>>>>> THONG_KE
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
<<<<<<< HEAD
                    <input id="orderSearch" type="search" placeholder="Nhập mã hóa đơn, khách hàng hoặc SĐT">
=======
                    <input id="orderSearch" type="search" placeholder="Nhập mã hóa đơn / tên khách / SĐT...">
>>>>>>> THONG_KE
                </label>
                <label class="invoice-field">
                    <span>Loại hóa đơn</span>
                    <select id="orderTypeFilter">
                        <option value="all">Tất cả</option>
                        <option value="Tại quầy">Tại quầy</option>
<<<<<<< HEAD
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
=======
>>>>>>> THONG_KE
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

<<<<<<< HEAD
        <%-- Form thêm hóa đơn. action=save sẽ được HoaDonController.doPost xử lý. --%>
        <section class="invoice-form-card" id="invoiceFormCard">
            <div class="invoice-card-heading">
                <div class="invoice-heading-icon">
                    <i class="fas fa-file-circle-plus"></i>
                </div>
                <div>
                    <h2 id="formTitle">Thêm hóa đơn</h2>
                    <p>Nhập thông tin hóa đơn.</p>
                </div>
            </div>

            <form class="invoice-simple-form" method="post" action="<%= request.getContextPath() %>/admin/hoa-don">
                <input type="hidden" name="action" value="save">

                <label class="invoice-field">
                    <span>Nhân viên</span>
                    <select id="idNhanVien" name="idNhanVien">
                        <option value="">Chọn nhân viên</option>
                        <% for (NhanVienView nhanVien : nhanVienList) { %>
                        <option value="<%= nhanVien.getId() %>"><%= text(nhanVien.getHoTen()) %> - <%= text(nhanVien.getMaNhanVien()) %></option>
                        <% } %>
                    </select>
                </label>

                <div class="invoice-product-picker invoice-field--full">
                    <div class="invoice-product-picker__heading">
                        <span>Sản phẩm trong hóa đơn</span>
                        <button class="invoice-btn invoice-btn--outline invoice-product-add" type="button" id="addInvoiceProduct">
                            <i class="fas fa-plus"></i>
                            Thêm sản phẩm
                        </button>
                    </div>
                    <div id="invoiceProductLines">
                        <div class="invoice-product-line">
                            <select class="invoice-product-select" name="productId">
                                <option value="">Chọn sản phẩm</option>
                                <% for (SanPhamHoaDonView product : sanPhamList) { %>
                                <option value="<%= product.getId() %>"
                                        data-price="<%= product.getGiaBan() %>"
                                        data-stock="<%= product.getSoLuongTon() %>">
                                    <%= text(product.getTenSanPham()) %> - <%= text(product.getMa()) %> - <%= moneyFormat.format(product.getGiaBan()) %> đ
                                </option>
                                <% } %>
                            </select>
                            <input class="invoice-product-quantity" name="productQuantity" type="number" min="1" value="1" aria-label="Số lượng sản phẩm">
                            <button class="invoice-icon-btn invoice-product-remove" type="button" title="Bỏ sản phẩm">
                                <i class="fas fa-xmark"></i>
                            </button>
                        </div>
                    </div>
                </div>

                <label class="invoice-field">
                    <span>Mã hóa đơn</span>
                    <input id="maHoaDon" name="maHoaDon" type="text" placeholder="HD001" required>
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
                    <input id="tongTienThanhToan" name="tongTienThanhToan" type="number" min="0" step="1000" placeholder="Tự tính từ sản phẩm" readonly>
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

        <%-- Bảng danh sách hóa đơn lấy từ request attribute hoaDonList. --%>
=======
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

>>>>>>> THONG_KE
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
<<<<<<< HEAD
                <button class="invoice-tab" type="button" data-status="Chờ thanh toán">Chờ thanh toán</button>
                <button class="invoice-tab" type="button" data-status="Đã thanh toán">Đã thanh toán</button>
=======
                <button class="invoice-tab" type="button" data-status="Chờ xác nhận">Chờ xác nhận</button>
                <button class="invoice-tab" type="button" data-status="Đã xác nhận">Đã xác nhận</button>
                <button class="invoice-tab" type="button" data-status="Đang xử lý">Đang xử lý</button>
                <button class="invoice-tab" type="button" data-status="Hoàn thành">Hoàn thành</button>
>>>>>>> THONG_KE
                <button class="invoice-tab" type="button" data-status="Đã hủy">Đã hủy</button>
            </div>

            <div class="invoice-table-wrap">
                <table class="invoice-table" id="ordersTable">
                    <thead>
                    <tr>
                        <th>STT</th>
                        <th>Mã hóa đơn</th>
<<<<<<< HEAD
                        <th>Nhân viên</th>
=======
                        <th>Tên nhân viên</th>
>>>>>>> THONG_KE
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
<<<<<<< HEAD
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
                        <td><%= text(hoaDon.getTenNhanVien()) %></td>
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
=======
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
>>>>>>> THONG_KE
                            </div>
                        </td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
                <div class="invoice-empty" id="emptyState">
                    <i class="fas fa-receipt"></i>
<<<<<<< HEAD
                    Không có hóa đơn phù hợp.
=======
                    Không có dữ liệu phù hợp
>>>>>>> THONG_KE
                </div>
            </div>

            <div class="invoice-table-footer">
<<<<<<< HEAD
                <span id="orderCount">Hiển thị <%= hoaDonList.size() %> / tổng <%= hoaDonList.size() %> hóa đơn</span>
=======
                <span id="orderCount">Hiển thị <%= hoaDonList.size() %> / tổng <%= hoaDonList.size() %> bản ghi</span>
>>>>>>> THONG_KE
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

<<<<<<< HEAD
<%-- Toast hiện thông báo nhanh, nội dung được thay đổi trong hoa_don.js. --%>
<div class="invoice-toast" id="invoiceToast" role="status" aria-live="polite">
    <i class="fas fa-circle-check"></i>
    <div>
        <strong>Thành công</strong>
=======
<div class="invoice-toast" id="invoiceToast" role="status" aria-live="polite">
    <i class="fas fa-circle-check"></i>
    <div>
        <strong>Thông báo</strong>
>>>>>>> THONG_KE
        <span id="toastMessage">Đã cập nhật thao tác</span>
    </div>
</div>

<script src="<%= request.getContextPath() %>/FE/Admin/QuanLyHoaDon/hoa_don.js"></script>
</body>
</html>
