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