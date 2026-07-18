<<<<<<< HEAD
<%
  request.setAttribute("pageTitle", "Thống kê");
  request.setAttribute("activeMenu", "dashboard");
%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dashboard</title>
  <!-- Nhúng file CSS (Vẫn tách riêng CSS) -->
  <link rel="stylesheet" href="css/thongke.css">
  <link rel="stylesheet" href="css/layout.css">
  <link rel="stylesheet" href="css/sidebar.css">
  <link rel="stylesheet" href="css/header.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

  <!-- Font Awesome cho icon -->
</head>
<body>
<%@include file="layout/sidebar.jsp"%>
<div class="dashboard-container">
  <%@include file="layout/header.jsp"%>
  <!-- Top Row: Chart + Stats -->
  <div class="dashboard-top">
    <!-- Revenue Chart -->
    <div class="chart-card">
      <div class="chart-header">
        <h2 class="chart-title">Doanh thu theo tháng</h2>
        <div class="chart-filters">
          <button class="chart-filter-btn active">2025</button>
          <button class="chart-filter-btn">2024</button>
        </div>
      </div>
      <div class="chart-wrapper">
        <canvas id="revenueChart"></canvas>
      </div>
    </div>

    <!-- Category & Summary Stats -->
    <div class="stats-card">
      <!-- Category Breakdown -->
      <div class="category-stats">
        <div class="category-item">
          <div class="category-header">
            <span class="category-name">Tròng kính</span>
            <span class="category-percent">27%</span>
          </div>
          <div class="category-bar">
            <div class="category-bar-fill trong-kinh" style="width: 27%"></div>
          </div>
        </div>
        <div class="category-item">
          <div class="category-header">
            <span class="category-name">Kính râm</span>
            <span class="category-percent">22%</span>
          </div>
          <div class="category-bar">
            <div class="category-bar-fill kinh-ram" style="width: 22%"></div>
          </div>
        </div>
        <div class="category-item">
          <div class="category-header">
            <span class="category-name">Phụ kiện</span>
            <span class="category-percent">13%</span>
          </div>
          <div class="category-bar">
            <div class="category-bar-fill phu-kien" style="width: 13%"></div>
          </div>
        </div>
      </div>

      <!-- Summary Cards -->
      <div class="summary-cards">
        <div class="summary-card">
          <div class="summary-value">247</div>
          <div class="summary-label">Sản phẩm</div>
        </div>
        <div class="summary-card">
          <div class="summary-value">18</div>
          <div class="summary-label">Thương hiệu</div>
        </div>
      </div>
    </div>
  </div>

  <!-- Recent Orders -->
  <div class="orders-section">
    <div class="orders-header">
      <h2 class="orders-title">Đơn hàng gần đây</h2>
      <a href="#" class="view-all-link">
        Xem tất cả <i class="fas fa-arrow-right"></i>
      </a>
    </div>
    <table class="orders-table">
      <thead>
      <tr>
        <th>Mã HĐ</th>
        <th>Khách hàng</th>
        <th>Sản phẩm</th>
        <th>Giá trị</th>
        <th>Trạng thái</th>
        <th>Ngày</th>
      </tr>
      </thead>
      <tbody>
      <!-- Dữ liệu đã được xóa hết, bạn có thể dùng vòng lặp JSTL hoặc Java để render dữ liệu động vào đây -->
      </tbody>
    </table>
  </div>
</div>

<!-- Chart.js Library -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<!-- Code JavaScript nằm trực tiếp trong HTML -->
<script>
  document.addEventListener("DOMContentLoaded", function() {
    // 1. Khởi tạo Revenue Chart
    const canvas = document.getElementById('revenueChart');
    if (canvas) {
      const ctx = canvas.getContext('2d');

      // Gradient fill cho biểu đồ
      const gradient = ctx.createLinearGradient(0, 0, 0, 300);
      gradient.addColorStop(0, 'rgba(184, 149, 106, 0.2)');
      gradient.addColorStop(1, 'rgba(184, 149, 106, 0)');

      new Chart(ctx, {
        type: 'line',
        data: {
          labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12'],
          datasets: [{
            label: 'Doanh thu',
            data: [40, 55, 50, 65, 70, 60, 85, 90, 80, 95, 100, 105],
            borderColor: '#b8956a',
            backgroundColor: gradient,
            borderWidth: 2.5,
            fill: true,
            tension: 0.4,
            pointRadius: 0,
            pointHoverRadius: 6,
            pointHoverBackgroundColor: '#b8956a',
            pointHoverBorderColor: '#fff',
            pointHoverBorderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              backgroundColor: '#4a4a4a',
              titleFont: { size: 13 },
              bodyFont: { size: 14, weight: 'bold' },
              padding: 12,
              cornerRadius: 8,
              callbacks: {
                label: function(context) {
                  return context.parsed.y + 'M';
                }
              }
            }
          },
          scales: {
            x: {
              grid: { display: false },
              ticks: { color: '#888', font: { size: 12 } },
              border: { display: false }
            },
            y: {
              beginAtZero: true,
              max: 110,
              ticks: {
                color: '#888',
                font: { size: 12 },
                stepSize: 35,
                callback: function(value) {
                  return value + 'M';
                }
              },
              grid: {
                color: '#f0ebe3',
                drawBorder: false
              },
              border: { display: false }
            }
          },
          interaction: {
            intersect: false,
            mode: 'index'
          }
        }
      });
    }

    // 2. Xử lý sự kiện nút bấm (Chart filter buttons)
    document.querySelectorAll('.chart-filter-btn').forEach(btn => {
      btn.addEventListener('click', function() {
        document.querySelectorAll('.chart-filter-btn').forEach(b => b.classList.remove('active'));
        this.classList.add('active');
      });
    });
  });
</script>

</body>
</html>
=======
<%@ page import="BE.Model.ThongKeCustomer" %>
<%@ page import="BE.Model.ThongKeOverview" %>
<%@ page import="BE.Model.ThongKeProduct" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    request.setAttribute("pageTitle", "Thống kê");
    request.setAttribute("activeMenu", "dashboard");

    if (request.getAttribute("todayOverview") == null && request.getAttribute("errorMessage") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/thong-ke");
        return;
    }

    DecimalFormat moneyFormat = new DecimalFormat("#,###");
    ThongKeOverview todayOverview = overview(request.getAttribute("todayOverview"));
    ThongKeOverview weekOverview = overview(request.getAttribute("weekOverview"));
    ThongKeOverview monthOverview = overview(request.getAttribute("monthOverview"));
    ThongKeOverview yearOverview = overview(request.getAttribute("yearOverview"));
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
    <link rel="stylesheet" href="<%= request.getContextPath() %>/FE/Admin/css/thongke.css?v=202607161120">
</head>
<body class="statistics-screen">
<%@ include file="layout/sidebar.jsp" %>

<div class="main-content">
    <%@ include file="layout/header.jsp" %>

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

        <section class="stat-overview" id="overviewCards" aria-label="Số liệu tổng quan">
            <article class="stat-overview-card" data-range="today">
                <div class="stat-card-topline">
                    <span class="stat-card-label">Hôm nay</span>
                    <span class="stat-card-icon"><i class="far fa-calendar"></i></span>
                </div>
                <strong class="stat-card-value" data-field="revenue"><%= money(moneyFormat, todayOverview.getRevenue()) %></strong>
                <p class="stat-card-meta">Sản phẩm đã bán <b data-field="products"><%= todayOverview.getProducts() %></b> <i></i> Đơn hàng <b data-field="orders"><%= todayOverview.getOrders() %></b></p>
                <div class="stat-card-statuses">
                    <span class="is-done">Hoàn thành <b data-field="done"><%= todayOverview.getDone() %></b></span>
                    <span class="is-cancel">Hủy <b data-field="cancelled"><%= todayOverview.getCancelled() %></b></span>
                    <span class="is-process">Xử lý <b data-field="processing"><%= todayOverview.getProcessing() %></b></span>
                </div>
            </article>

            <article class="stat-overview-card" data-range="week">
                <div class="stat-card-topline">
                    <span class="stat-card-label">Tuần này</span>
                    <span class="stat-card-icon"><i class="far fa-calendar-check"></i></span>
                </div>
                <strong class="stat-card-value" data-field="revenue"><%= money(moneyFormat, weekOverview.getRevenue()) %></strong>
                <p class="stat-card-meta">Sản phẩm đã bán <b data-field="products"><%= weekOverview.getProducts() %></b> <i></i> Đơn hàng <b data-field="orders"><%= weekOverview.getOrders() %></b></p>
                <div class="stat-card-statuses">
                    <span class="is-done">Hoàn thành <b data-field="done"><%= weekOverview.getDone() %></b></span>
                    <span class="is-cancel">Hủy <b data-field="cancelled"><%= weekOverview.getCancelled() %></b></span>
                    <span class="is-process">Xử lý <b data-field="processing"><%= weekOverview.getProcessing() %></b></span>
                </div>
            </article>

            <article class="stat-overview-card" data-range="month">
                <div class="stat-card-topline">
                    <span class="stat-card-label">Tháng này</span>
                    <span class="stat-card-icon"><i class="fas fa-chart-line"></i></span>
                </div>
                <strong class="stat-card-value" data-field="revenue"><%= money(moneyFormat, monthOverview.getRevenue()) %></strong>
                <p class="stat-card-meta">Sản phẩm đã bán <b data-field="products"><%= monthOverview.getProducts() %></b> <i></i> Đơn hàng <b data-field="orders"><%= monthOverview.getOrders() %></b></p>
                <div class="stat-card-statuses">
                    <span class="is-done">Hoàn thành <b data-field="done"><%= monthOverview.getDone() %></b></span>
                    <span class="is-cancel">Hủy <b data-field="cancelled"><%= monthOverview.getCancelled() %></b></span>
                    <span class="is-process">Xử lý <b data-field="processing"><%= monthOverview.getProcessing() %></b></span>
                </div>
            </article>

            <article class="stat-overview-card" data-range="year">
                <div class="stat-card-topline">
                    <span class="stat-card-label">Năm nay</span>
                    <span class="stat-card-icon"><i class="fas fa-calendar-days"></i></span>
                </div>
                <strong class="stat-card-value" data-field="revenue"><%= money(moneyFormat, yearOverview.getRevenue()) %></strong>
                <p class="stat-card-meta">Sản phẩm đã bán <b data-field="products"><%= yearOverview.getProducts() %></b> <i></i> Đơn hàng <b data-field="orders"><%= yearOverview.getOrders() %></b></p>
                <div class="stat-card-statuses">
                    <span class="is-done">Hoàn thành <b data-field="done"><%= yearOverview.getDone() %></b></span>
                    <span class="is-cancel">Hủy <b data-field="cancelled"><%= yearOverview.getCancelled() %></b></span>
                    <span class="is-process">Xử lý <b data-field="processing"><%= yearOverview.getProcessing() %></b></span>
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
                    <span class="stat-panel-badge" id="totalOrderBadge"><%= yearOverview.getOrders() %> đơn</span>
                </div>
                <div class="stat-completion">
                    <div><span>Tỷ lệ hoàn thành</span><strong id="completionRate"><%= completionRate %>%</strong></div>
                    <div class="stat-progress"><span id="completionBar" style="width: <%= completionRate %>%;"></span></div>
                </div>
                <div class="stat-status-list" id="orderStatusList">
                    <div class="stat-status-item"><span>Hoàn thành</span><strong><%= yearOverview.getDone() %></strong></div>
                    <div class="stat-status-item"><span>Đang xử lý</span><strong><%= yearOverview.getProcessing() %></strong></div>
                    <div class="stat-status-item"><span>Đã hủy</span><strong><%= yearOverview.getCancelled() %></strong></div>
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

<script src="<%= request.getContextPath() %>/FE/Admin/thongke.js?v=202607161120"></script>
</body>
</html>
>>>>>>> THONG_KE
