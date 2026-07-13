<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<aside class="sidebar" id="sidebar">
    <!-- Logo -->
    <div class="sidebar-logo">
        <div class="logo-icon">
            <i class="fas fa-glasses"></i>
        </div>
        <div class="logo-text">
            <h1>RIOR</h1>
            <span>Fine Eyewear & Optics</span>
        </div>
    </div>

    <!-- Menu -->
    <nav class="sidebar-menu">
        <!-- Sales Channels -->
        <div class="menu-section">
            <div class="menu-title">Kênh bán hàng</div>
            <ul class="menu-list">
                <li class="menu-item">
                    <a href="#" class="menu-link">
                        <i class="fas fa-shopping-bag"></i>
                        <span>Bán hàng online</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="#" class="menu-link">
                        <i class="fas fa-cash-register"></i>
                        <span>Bán tại quầy (POS)</span>
                    </a>
                </li>
            </ul>
        </div>

        <!-- Administration -->
        <div class="menu-section">
            <div class="menu-title">Quản trị</div>
            <ul class="menu-list">
                <li class="menu-item">
                    <a href="<%= request.getContextPath() %>/Admin/Thongke.jsp" class="menu-link <%= "dashboard".equals(request.getAttribute("activeMenu"))?"active":""%>">
                        <i class="fas fa-chart-bar"></i>
                        <span>Thống kê</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="<%= request.getContextPath() %>/admin/hoa-don" class="menu-link <%= "hoadon".equals(request.getAttribute("activeMenu"))?"active":""%>">
                        <i class="fas fa-file-invoice"></i>
                        <span>Quản lý hoá đơn</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="#" class="menu-link">
                        <i class="fas fa-box"></i>
                        <span>Quản lý sản phẩm</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="<%= request.getContextPath() %>/Admin/QuanLyMaGiamGia/quan_ly_giam_gia.jsp" class="menu-link <%= "giamgia".equals(request.getAttribute("activeMenu"))?"active":""%>">
                        <i class="fas fa-tags"></i>
                        <span>Quản lý giảm giá</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="#" class="menu-link">
                        <i class="fas fa-users"></i>
                        <span>Quản lý khách hàng</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="#" class="menu-link">
                        <i class="fas fa-user-tie"></i>
                        <span>Quản lý nhân viên</span>
                    </a>
                </li>
            </ul>
        </div>
    </nav>

    <!-- Bottom Menu -->
    <div class="sidebar-bottom">
        <ul class="bottom-menu">
            <li class="bottom-item">
                <a href="#" class="bottom-link">
                    <i class="fas fa-cog"></i>
                    <span>Cài đặt</span>
                </a>
            </li>
            <li class="bottom-item">
                <a href="#" class="bottom-link logout">
                    <i class="fas fa-sign-out-alt"></i>
                    <span>Đăng xuất</span>
                </a>
            </li>
        </ul>
    </div>
</aside>

<!-- Overlay for mobile -->
<div class="overlay" id="overlay"></div>

<!-- Mobile Toggle Button -->
<button class="sidebar-toggle" id="sidebarToggle">
    <i class="fas fa-bars"></i>
</button>

<script>
    // Sidebar toggle functionality
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('overlay');
    const sidebarToggle = document.getElementById('sidebarToggle');

    function toggleSidebar() {
        sidebar.classList.toggle('active');
        overlay.classList.toggle('active');
    }

    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', toggleSidebar);
    }

    if (overlay) {
        overlay.addEventListener('click', toggleSidebar);
    }

    // Close sidebar when clicking on a menu link (mobile)
    document.querySelectorAll('.menu-link, .bottom-link').forEach(link => {
        link.addEventListener('click', () => {
            if (window.innerWidth <= 1024) {
                toggleSidebar();
            }
        })
    })
</script>

