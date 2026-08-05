<%@ page import="QuanLyHoaDon.Model.ThongKeCustomer" %>
<%@ page import="QuanLyHoaDon.Model.ThongKeOverview" %>
<%@ page import="QuanLyHoaDon.Model.ThongKeProduct" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    request.setAttribute("pageTitle", "Thống kê");
    request.setAttribute("activeMenu", "dashboard");

    if (request.getAttribute("reportOverview") == null && request.getAttribute("errorMessage") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/thong-ke");
        return;
    }

    DecimalFormat moneyFormat = new DecimalFormat("#,###");
    ThongKeOverview reportOverview = overview(request.getAttribute("reportOverview"));

    // Đã sửa: Xóa .withDayOfMonth(1) để mặc định lấy ngày hôm nay
    LocalDate filterFrom = request.getAttribute("filterFrom") instanceof LocalDate
            ? (LocalDate) request.getAttribute("filterFrom") : LocalDate.now();

    LocalDate filterTo = request.getAttribute("filterTo") instanceof LocalDate
            ? (LocalDate) request.getAttribute("filterTo") : LocalDate.now();
    LocalDate currentDate = request.getAttribute("currentDate") instanceof LocalDate
            ? (LocalDate) request.getAttribute("currentDate") : LocalDate.now();
    Integer completionRate = (Integer) request.getAttribute("completionRate");
    if (completionRate == null) completionRate = 0;

    List<ThongKeProduct> bestSellers = (List<ThongKeProduct>) request.getAttribute("bestSellers");
    List<ThongKeCustomer> topCustomers = (List<ThongKeCustomer>) request.getAttribute("topCustomers");
    List<ThongKeProduct> slowStockProducts = (List<ThongKeProduct>) request.getAttribute("slowStockProducts");
    if (bestSellers == null) bestSellers = Collections.emptyList();
    if (topCustomers == null) topCustomers = Collections.emptyList();
    if (slowStockProducts == null) slowStockProducts = Collections.emptyList();
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<%!
    private ThongKeOverview overview(Object value) {
        return value instanceof ThongKeOverview ? (ThongKeOverview) value : new ThongKeOverview();
    }

    private String money(DecimalFormat formatter, BigDecimal value) {
        if (value == null) {
            return "0 đ";
        }
        return formatter.format(value) + " đ";
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thống kê - RIOR Admin</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/layout.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/sidebar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/thongke.css?v=202607251245">
    <style>
        /* Inline fallback de khung overview khong bi CSS grid cu ghi de khi WAR dang chay chua cap nhat. */
        .statistics-screen #overviewCards.stat-overview {
            display: block !important;
            width: 100%;
            margin: 0 0 14px;
        }

        .statistics-screen #overviewCards .stat-overview-card--combined {
            display: block;
            width: 100%;
            box-sizing: border-box;
            min-height: 0;
            padding: 16px 16px 0;
            border: 1px solid #e2e0da;
            border-radius: 8px;
            background: #fff;
            box-shadow: 0 5px 14px rgba(91, 72, 48, .07);
        }

        .statistics-screen #overviewCards .stat-overview-filter-head,
        .statistics-screen #overviewCards .stat-overview-filter-bar,
        .statistics-screen #overviewCards .stat-overview-filter-title,
        .statistics-screen #overviewCards .stat-overview-quick-filters,
        .statistics-screen #overviewCards .stat-filter-actions {
            display: flex;
        }

        .statistics-screen #overviewCards .stat-overview-filter-head {
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            min-height: 42px;
        }

        .statistics-screen #overviewCards .stat-overview-filter-title {
            align-items: center;
            gap: 10px;
        }

        .statistics-screen #overviewCards .stat-overview-filter-title .stat-card-icon {
            width: 34px;
            height: 34px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 7px;
            background: #f2eadc;
            color: #6b5438;
        }

        .statistics-screen #overviewCards .stat-overview-filter-title .stat-card-label {
            display: block;
            color: #242a35;
            font-size: 14px;
            font-weight: 700;
        }

        .statistics-screen #overviewCards .stat-overview-filter-title small {
            display: block;
            margin-top: 2px;
            color: #767b83;
            font-size: 10px;
        }

        .statistics-screen #overviewCards .stat-overview-quick-filters {
            align-items: center;
            gap: 5px;
            flex-wrap: wrap;
        }

        .statistics-screen #overviewCards .stat-quick-filter {
            min-height: 29px;
            border: 1px solid #e0ded8;
            border-radius: 999px;
            background: #fff;
            color: #4e535b;
            padding: 5px 11px;
            font: inherit;
            font-size: 10.5px;
            cursor: pointer;
        }

        .statistics-screen #overviewCards .stat-quick-filter:hover,
        .statistics-screen #overviewCards .stat-quick-filter.is-active {
            border-color: #5b4427;
            background: #5b4427;
            color: #fff;
        }

        .statistics-screen #overviewCards .stat-overview-filter-bar {
            align-items: flex-end;
            gap: 9px;
            margin-top: 12px;
            padding: 9px 0;
            border-top: 1px solid #efede8;
            border-bottom: 1px solid #efede8;
        }

        .statistics-screen #overviewCards .stat-overview-filter-icon {
            align-self: center;
            min-width: 70px;
            color: #747982;
            font-size: 10.5px;
        }

        .statistics-screen #overviewCards .stat-date-field {
            display: flex;
            flex-direction: column;
            gap: 4px;
            width: 155px;
        }

        .statistics-screen #overviewCards .stat-date-field > span {
            color: #41464f;
            font-size: 10px;
            font-weight: 600;
        }

        .statistics-screen #overviewCards .stat-date-field input {
            width: 100%;
            min-height: 31px;
            box-sizing: border-box;
            border: 1px solid #d9d8d4;
            border-radius: 4px;
            background: #fff;
            padding: 5px 8px;
            color: #353a43;
            font-size: 11px;
        }

        .statistics-screen #overviewCards .stat-date-arrow {
            align-self: center;
            color: #85888d;
            font-size: 10px;
        }

        .statistics-screen #overviewCards .stat-filter-actions {
            align-items: center;
            gap: 6px;
            margin-left: auto;
        }

        .statistics-screen #overviewCards .stat-filter-actions .stat-btn {
            min-height: 31px;
            border-radius: 4px;
            padding: 6px 10px;
            font: inherit;
            font-size: 10.5px;
            white-space: nowrap;
        }

        .statistics-screen #overviewCards .stat-btn--primary {
            border: 1px solid #9b7a4e;
            background: #9b7a4e;
            color: #fff;
        }

        .statistics-screen #overviewCards .stat-btn--ghost {
            border: 1px solid #c4c0b8;
            background: #fff;
            color: #5e6268;
        }

        .statistics-screen #overviewCards .stat-overview-metrics {
            display: grid;
            grid-template-columns: 1.2fr 1fr 1fr 1fr;
            margin-top: 0;
        }

        .statistics-screen #overviewCards .stat-overview-metric {
            min-width: 0;
            padding: 13px 17px 15px;
            border-right: 1px solid #e5e3de;
            text-align: center;
        }

        .statistics-screen #overviewCards .stat-overview-metric:first-child { text-align: left; }
        .statistics-screen #overviewCards .stat-overview-metric:last-child { border-right: 0; }
        .statistics-screen #overviewCards .stat-overview-metric > span { display: block; color: #3f444c; font-size: 10.5px; }
        .statistics-screen #overviewCards .stat-overview-metric > strong { display: block; margin-top: 3px; color: #675035; font-size: 20px; line-height: 1.15; }
        .statistics-screen #overviewCards .stat-overview-metric > small { display: block; min-height: 15px; margin-top: 4px; color: #81858a; font-size: 9.5px; line-height: 1.35; }
        .statistics-screen #overviewCards .stat-overview-metric > small .is-done { color: #408063; }
        .statistics-screen #overviewCards .stat-overview-metric > small .is-cancel { color: #ac4e69; }
        .statistics-screen #overviewCards .stat-overview-metric > small .is-process { color: #a17639; }
        .statistics-screen #overviewCards .stat-overview-progress { height: 4px; max-width: 130px; margin: 8px auto 0; overflow: hidden; border-radius: 999px; background: #e4e4df; }
        .statistics-screen #overviewCards .stat-overview-progress span { display: block; height: 100%; border-radius: inherit; background: #6f9d78; }

        @media (max-width: 900px) {
            .statistics-screen #overviewCards .stat-overview-filter-head { align-items: flex-start; flex-direction: column; }
            .statistics-screen #overviewCards .stat-overview-quick-filters { width: 100%; }
            .statistics-screen #overviewCards .stat-overview-filter-bar { align-items: stretch; flex-wrap: wrap; }
            .statistics-screen #overviewCards .stat-date-field { flex: 1 1 150px; }
            .statistics-screen #overviewCards .stat-filter-actions { width: 100%; margin-left: 0; }
            .statistics-screen #overviewCards .stat-filter-actions .stat-btn { flex: 1; }
        }

        @media (max-width: 640px) {
            .statistics-screen #overviewCards .stat-overview-card--combined { padding: 14px 11px 0; }
            .statistics-screen #overviewCards .stat-date-arrow { display: none; }
            .statistics-screen #overviewCards .stat-date-field { flex-basis: 100%; }
            .statistics-screen #overviewCards .stat-overview-metrics { grid-template-columns: repeat(2, minmax(0, 1fr)); }
            .statistics-screen #overviewCards .stat-overview-metric,
            .statistics-screen #overviewCards .stat-overview-metric:first-child { padding: 12px 7px; border-right: 1px solid #e5e3de; text-align: center; }
            .statistics-screen #overviewCards .stat-overview-metric:nth-child(2) { border-right: 0; }
            .statistics-screen #overviewCards .stat-overview-metric:nth-child(-n+2) { border-bottom: 1px solid #e5e3de; }
        }
    </style>
</head>
<body class="statistics-screen"
      data-statistics-url="<%= request.getContextPath() %>/admin/thong-ke"
      data-current-date="<%= currentDate %>"
      data-current-year="<%= currentDate.getYear() %>"
      data-current-month="<%= currentDate.getMonthValue() %>">
<%@ include file="/Admin/layout/sidebar.jsp" %>

<div class="main-content">
    <%@ include file="/Admin/layout/header.jsp" %>

    <main id="page-content" class="stat-page">
        <% if (errorMessage != null) { %>
        <div class="empty-state"><%= errorMessage %></div>
        <% } %>

        <section class="stat-page-header">
            <h1 class="stat-page-title"><i class="fas fa-chart-column"></i> Thống kê</h1>
            <button class="stat-export-link" id="exportReport" type="button">
                <i class="fas fa-download"></i> Xuất báo cáo
            </button>
        </section>

        <section class="stat-overview" id="overviewCards" aria-label="Bộ lọc và số liệu tổng quan">
            <article class="stat-overview-card stat-overview-card--combined">
                <div class="stat-overview-filter-head">
                    <div class="stat-overview-filter-title">
                        <span class="stat-card-icon"><i class="fas fa-chart-column"></i></span>
                        <div>
                            <span class="stat-card-label">Bộ lọc thống kê</span>
                            <small>Dữ liệu bên dưới được cập nhật theo bộ lọc này</small>
                        </div>
                    </div>
                    <!-- Đã sửa: Chuyển class is-active sang nút Hôm nay -->
                    <div class="stat-overview-quick-filters" role="group" aria-label="Khoảng thời gian nhanh">
                        <button type="button" class="stat-quick-filter is-active" data-filter-preset="today">Hôm nay</button>
                        <button type="button" class="stat-quick-filter" data-filter-preset="week">Tuần này</button>
                        <button type="button" class="stat-quick-filter" data-filter-preset="month">Tháng này</button>
                        <button type="button" class="stat-quick-filter" data-filter-preset="year">Năm nay</button>
                    </div>
                </div>

                <div class="stat-overview-filter-bar">
                    <span class="stat-overview-filter-icon"><i class="fas fa-filter"></i> Bộ lọc</span>
                    <label class="stat-date-field">
                        <span>Từ ngày</span>
                        <input id="reportFrom" type="date" value="<%= filterFrom %>">
                    </label>
                    <span class="stat-date-arrow"><i class="fas fa-arrow-right"></i></span>
                    <label class="stat-date-field">
                        <span>Đến ngày</span>
                        <input id="reportTo" type="date" value="<%= filterTo %>">
                    </label>
                    <div class="stat-filter-actions">
                        <button class="stat-btn stat-btn--primary" id="applyReportFilter" type="button">
                            <i class="fas fa-filter"></i> Lọc dữ liệu
                        </button>
                        <button class="stat-btn stat-btn--ghost" id="resetReportFilter" type="button">
                            <i class="fas fa-rotate-left"></i> Đặt lại
                        </button>
                    </div>
                </div>

                <div class="stat-overview-metrics" aria-label="Tổng quan theo khoảng ngày đã chọn">
                    <div class="stat-overview-metric">
                        <span>Doanh thu</span>
                        <strong data-field="revenue"><%= money(moneyFormat, reportOverview.getRevenue()) %></strong>
                        <small>Đã ghi nhận trong kỳ</small>
                    </div>
                    <div class="stat-overview-metric">
                        <span>Đơn hàng</span>
                        <strong data-field="orders"><%= reportOverview.getOrders() %></strong>
                        <small>
                            <span class="is-done">Đã thanh toán <b data-field="done"><%= reportOverview.getDone() %></b></span> ·
                            <span class="is-cancel">Hủy <b data-field="cancelled"><%= reportOverview.getCancelled() %></b></span> ·
                            <span class="is-process">Chờ <b data-field="processing"><%= reportOverview.getProcessing() %></b></span>
                        </small>
                    </div>
                    <div class="stat-overview-metric">
                        <span>Sản phẩm đã bán</span>
                        <strong data-field="products"><%= reportOverview.getProducts() %></strong>
                        <small>Items sold</small>
                    </div>
                    <div class="stat-overview-metric stat-overview-metric--completion">
                        <span>Tỷ lệ hoàn thành</span>
                        <strong data-field="completion"><%= completionRate %>%</strong>
                        <div class="stat-overview-progress"><span data-field="completion-bar" style="width: <%= completionRate %>%;"></span></div>
                    </div>
                </div>
            </article>
        </section>

        <section class="stat-panel stat-revenue-panel">
            <div class="stat-panel-heading stat-revenue-heading">
                <div class="stat-chart-left">
                    <div class="stat-heading-title">
                        <i class="fas fa-money-bill-trend-up"></i>
                        <div>
                            <h2>Doanh thu</h2>
                            <p id="chartDescription">Doanh thu theo ngày trong tháng <%= String.format("%02d", currentDate.getMonthValue()) %>/<%= currentDate.getYear() %></p>
                        </div>
                    </div>
                    <div class="stat-chart-actions">
                        <label class="stat-select-field">
                            <span class="sr-only">Kiểu thời gian</span>
                            <select id="periodMode">
                                <option value="month">Theo tháng</option>
                                <option value="quarter">Theo quý</option>
                                <option value="year">Theo năm</option>
                            </select>
                        </label>
                        <div class="stat-period-fields" id="periodFields"></div>
                    </div>
                </div>
            </div>

            <div class="stat-chart-legend" id="chartLegend"></div>
            <div class="stat-chart" id="revenueChart" role="img" aria-label="Biểu đồ doanh thu"></div>
            <div class="stat-chart-footer">
                <p>Tổng doanh thu: <strong id="chartTotal">0 đ</strong></p>
                <span><i class="fas fa-circle-info"></i> Đơn vị: VNĐ</span>
            </div>
        </section>

        <section class="stat-detail-grid">
            <article class="stat-panel stat-table-panel">
                <div class="stat-panel-heading">
                    <div class="stat-heading-title">
                        <span class="stat-heading-icon"><i class="fas fa-trophy"></i></span>
                        <div>
                            <h2>Top sản phẩm bán chạy</h2>
                            <p>Xếp hạng theo số lượng bán</p>
                        </div>
                    </div>
                    <span class="stat-panel-badge">Top 5</span>
                </div>
                <div class="stat-table-wrap">
                    <table class="stat-table">
                        <thead><tr><th>Sản phẩm</th><th>Đã bán</th><th>Tồn</th></tr></thead>
                        <tbody id="bestSellerBody">
                        <% if (bestSellers.isEmpty()) { %>
                        <tr><td colspan="3">Chưa có dữ liệu sản phẩm.</td></tr>
                        <% } else { %>
                        <% for (ThongKeProduct item : bestSellers) { %>
                        <tr>
                            <td><%= safe(item.getTenSanPham()) %></td>
                            <td><%= item.getDaBan() %></td>
                            <td><%= item.getTonKho() %></td>
                        </tr>
                        <% } %>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            </article>

            <article class="stat-panel stat-order-panel">
                <div class="stat-panel-heading">
                    <div class="stat-heading-title">
                        <span class="stat-heading-icon"><i class="fas fa-file-invoice"></i></span>
                        <div>
                            <h2>Trạng thái đơn hàng</h2>
                            <p>Tiến độ xử lý trong kỳ đã lọc</p>
                        </div>
                    </div>
                    <span class="stat-panel-badge" id="totalOrderBadge"><%= reportOverview.getOrders() %> đơn</span>
                </div>
                <div class="stat-completion">
                    <div><span>Tỷ lệ hoàn thành</span><strong id="completionRate"><%= completionRate %>%</strong></div>
                    <div class="stat-progress"><span id="completionBar" style="width: <%= completionRate %>%;"></span></div>
                </div>
                <div class="stat-status-list" id="orderStatusList">
                    <div class="stat-status-item"><span>Đã thanh toán</span><strong data-field="done"><%= reportOverview.getDone() %></strong></div>
                    <div class="stat-status-item"><span>Chờ xử lý</span><strong data-field="processing"><%= reportOverview.getProcessing() %></strong></div>
                    <div class="stat-status-item"><span>Đã hủy</span><strong data-field="cancelled"><%= reportOverview.getCancelled() %></strong></div>
                </div>
            </article>

            <article class="stat-panel stat-table-panel">
                <div class="stat-panel-heading">
                    <div class="stat-heading-title">
                        <span class="stat-heading-icon"><i class="fas fa-users"></i></span>
                        <div>
                            <h2>Khách hàng tiềm năng</h2>
                            <p>Khách có tổng chi tiêu cao nhất</p>
                        </div>
                    </div>
                    <span class="stat-panel-badge">Top chi tiêu</span>
                </div>
                <div class="stat-table-wrap">
                    <table class="stat-table">
                        <thead><tr><th>Khách hàng</th><th>Số đơn</th><th>Tổng chi tiêu</th></tr></thead>
                        <tbody id="customerBody">
                        <% if (topCustomers.isEmpty()) { %>
                        <tr><td colspan="3">Chưa có dữ liệu khách hàng.</td></tr>
                        <% } else { %>
                        <% for (ThongKeCustomer item : topCustomers) { %>
                        <tr>
                            <td><%= safe(item.getTenKhachHang()) %></td>
                            <td><%= item.getSoDon() %></td>
                            <td><%= money(moneyFormat, item.getTongChiTieu()) %></td>
                        </tr>
                        <% } %>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            </article>

            <article class="stat-panel stat-table-panel">
                <div class="stat-panel-heading">
                    <div class="stat-heading-title">
                        <span class="stat-heading-icon"><i class="fas fa-boxes-stacked"></i></span>
                        <div>
                            <h2>Bán chậm &amp; tồn kho</h2>
                            <p>Sản phẩm cần chú ý trong kỳ</p>
                        </div>
                    </div>
                    <span class="stat-panel-badge is-warning">Cần theo dõi</span>
                </div>
                <div class="stat-table-wrap">
                    <table class="stat-table">
                        <thead><tr><th>Sản phẩm</th><th>Đã bán</th><th>Tồn</th></tr></thead>
                        <tbody id="slowStockBody">
                        <% if (slowStockProducts.isEmpty()) { %>
                        <tr><td colspan="3">Chưa có dữ liệu tồn kho.</td></tr>
                        <% } else { %>
                        <% for (ThongKeProduct item : slowStockProducts) { %>
                        <tr>
                            <td><%= safe(item.getTenSanPham()) %></td>
                            <td><%= item.getDaBan() %></td>
                            <td><%= item.getTonKho() %></td>
                        </tr>
                        <% } %>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            </article>
        </section>
    </main>
</div>

<div class="stat-modal" id="compareModal" aria-hidden="true">
    <div class="stat-modal-backdrop" data-close-modal></div>
    <div class="stat-modal-dialog" role="dialog" aria-modal="true" aria-labelledby="compareTitle">
        <div class="stat-modal-header">
            <div>
                <h2 id="compareTitle"><i class="fas fa-code-compare"></i> So sánh doanh thu</h2>
                <p>Chọn hai giai đoạn để hiển thị đồng thời trên biểu đồ</p>
            </div>
            <button class="stat-modal-close" type="button" data-close-modal aria-label="Đóng"><i class="fas fa-xmark"></i></button>
        </div>
        <div class="stat-modal-body">
            <label class="stat-date-field">
                <span>So sánh theo</span>
                <select id="compareMode">
                    <option value="month">Theo tháng</option>
                    <option value="quarter">Theo quý</option>
                    <option value="year">Theo năm</option>
                </select>
            </label>
            <div class="stat-compare-fields" id="compareFields"></div>
            <div class="stat-modal-note"><i class="far fa-lightbulb"></i> Dữ liệu của hai giai đoạn sẽ dùng cùng một thang đo.</div>
        </div>
        <div class="stat-modal-footer">
            <button class="stat-btn stat-btn--ghost" type="button" data-close-modal>Hủy</button>
            <button class="stat-btn stat-btn--primary" id="applyCompare" type="button">So sánh</button>
        </div>
    </div>
</div>

<div class="stat-toast" id="statToast" role="status" aria-live="polite">
    <i class="fas fa-circle-check"></i>
    <div><strong>Thành công</strong><span id="statToastMessage"></span></div>
</div>

<script src="<%= request.getContextPath() %>/FE/Admin/thongke.js?v=202607301"></script>
</body>
</html>
