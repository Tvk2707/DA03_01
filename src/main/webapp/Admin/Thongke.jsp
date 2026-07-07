<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    request.setAttribute("pageTitle", "Thống kê");
    request.setAttribute("activeMenu", "dashboard");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thống kê - RIOR Admin</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/layout.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/sidebar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/thongke.css?v=202607071015">
</head>
<body class="statistics-screen">
<%@ include file="layout/sidebar.jsp" %>

<div class="main-content">
    <%@ include file="layout/header.jsp" %>

    <main id="page-content" class="stat-page">
        <section class="stat-page-header">
            <h1 class="stat-page-title"><i class="fas fa-chart-column"></i> Thống kê</h1>
            <button class="stat-export-link" id="exportReport" type="button">
                <i class="fas fa-download"></i> Xuất báo cáo
            </button>
        </section>

        <section class="stat-overview" id="overviewCards" aria-label="Số liệu tổng quan">
            <article class="stat-overview-card" data-range="today">
                <div class="stat-card-topline">
                    <span class="stat-card-label">Hôm nay</span>
                    <span class="stat-card-icon"><i class="far fa-calendar"></i></span>
                </div>
                <strong class="stat-card-value" data-field="revenue">0 đ</strong>
                <p class="stat-card-meta">Sản phẩm đã bán <b data-field="products">0</b> <i></i> Đơn hàng <b data-field="orders">0</b></p>
                <div class="stat-card-statuses">
                    <span class="is-done">Hoàn thành <b data-field="done">0</b></span>
                    <span class="is-cancel">Hủy <b data-field="cancelled">0</b></span>
                    <span class="is-process">Xử lý <b data-field="processing">0</b></span>
                </div>
            </article>

            <article class="stat-overview-card" data-range="week">
                <div class="stat-card-topline">
                    <span class="stat-card-label">Tuần này</span>
                    <span class="stat-card-icon"><i class="far fa-calendar-check"></i></span>
                </div>
                <strong class="stat-card-value" data-field="revenue">0 đ</strong>
                <p class="stat-card-meta">Sản phẩm đã bán <b data-field="products">0</b> <i></i> Đơn hàng <b data-field="orders">0</b></p>
                <div class="stat-card-statuses">
                    <span class="is-done">Hoàn thành <b data-field="done">0</b></span>
                    <span class="is-cancel">Hủy <b data-field="cancelled">0</b></span>
                    <span class="is-process">Xử lý <b data-field="processing">0</b></span>
                </div>
            </article>

            <article class="stat-overview-card" data-range="month">
                <div class="stat-card-topline">
                    <span class="stat-card-label">Tháng này</span>
                    <span class="stat-card-icon"><i class="fas fa-chart-line"></i></span>
                </div>
                <strong class="stat-card-value" data-field="revenue">0 đ</strong>
                <p class="stat-card-meta">Sản phẩm đã bán <b data-field="products">0</b> <i></i> Đơn hàng <b data-field="orders">0</b></p>
                <div class="stat-card-statuses">
                    <span class="is-done">Hoàn thành <b data-field="done">0</b></span>
                    <span class="is-cancel">Hủy <b data-field="cancelled">0</b></span>
                    <span class="is-process">Xử lý <b data-field="processing">0</b></span>
                </div>
            </article>

            <article class="stat-overview-card" data-range="year">
                <div class="stat-card-topline">
                    <span class="stat-card-label">Năm nay</span>
                    <span class="stat-card-icon"><i class="fas fa-calendar-days"></i></span>
                </div>
                <strong class="stat-card-value" data-field="revenue">0 đ</strong>
                <p class="stat-card-meta">Sản phẩm đã bán <b data-field="products">0</b> <i></i> Đơn hàng <b data-field="orders">0</b></p>
                <div class="stat-card-statuses">
                    <span class="is-done">Hoàn thành <b data-field="done">0</b></span>
                    <span class="is-cancel">Hủy <b data-field="cancelled">0</b></span>
                    <span class="is-process">Xử lý <b data-field="processing">0</b></span>
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
                            <p id="chartDescription">Doanh thu theo ngày trong tháng 04/2026</p>
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
                <button class="stat-btn stat-btn--soft" id="openCompare" type="button">
                    <i class="fas fa-code-compare"></i> So sánh
                </button>
            </div>

            <div class="stat-chart-legend" id="chartLegend"></div>
            <div class="stat-chart" id="revenueChart" role="img" aria-label="Biểu đồ doanh thu"></div>
            <div class="stat-chart-footer">
                <p>Tổng doanh thu: <strong id="chartTotal">0 đ</strong></p>
                <span><i class="fas fa-circle-info"></i> Đơn vị: VNĐ</span>
            </div>
        </section>

        <section class="stat-panel stat-filter-panel">
            <div class="stat-filter-title">
                <span class="stat-heading-icon"><i class="fas fa-filter"></i></span>
                <div>
                    <h2>Bộ lọc báo cáo</h2>
                    <p>Lọc dữ liệu chi tiết theo khoảng ngày</p>
                </div>
            </div>
            <div class="stat-filter-fields">
                <label class="stat-date-field">
                    <span>Từ ngày</span>
                    <input id="reportFrom" type="date" value="2026-04-01">
                </label>
                <span class="stat-date-arrow"><i class="fas fa-arrow-right"></i></span>
                <label class="stat-date-field">
                    <span>Đến ngày</span>
                    <input id="reportTo" type="date" value="2026-04-30">
                </label>
            </div>
            <div class="stat-filter-actions">
                <button class="stat-btn stat-btn--primary" id="applyReportFilter" type="button">
                    <i class="fas fa-filter"></i> Lọc dữ liệu
                </button>
                <button class="stat-btn stat-btn--ghost" id="resetReportFilter" type="button">
                    <i class="fas fa-rotate-left"></i> Đặt lại
                </button>
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
                        <tbody id="bestSellerBody"></tbody>
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
                    <span class="stat-panel-badge" id="totalOrderBadge">0 đơn</span>
                </div>
                <div class="stat-completion">
                    <div><span>Tỷ lệ hoàn thành</span><strong id="completionRate">0%</strong></div>
                    <div class="stat-progress"><span id="completionBar"></span></div>
                </div>
                <div class="stat-status-list" id="orderStatusList"></div>
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
                        <tbody id="customerBody"></tbody>
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
                        <tbody id="slowStockBody"></tbody>
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

<script src="<%= request.getContextPath() %>/Admin/thongke.js?v=202607071015"></script>
</body>
</html>
