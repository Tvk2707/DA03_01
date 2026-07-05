<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Lấy activeMenu một lần, xử lý null để tránh NullPointerException
    String activeMenu = (String) request.getAttribute("activeMenu");
    if (activeMenu == null) activeMenu = "";

    // Lấy activeSubMenu để highlight các mục con trong Quản lý sản phẩm
    String activeSubMenu = (String) request.getAttribute("activeSubMenu");
    if (activeSubMenu == null) activeSubMenu = "";
%>

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
                    <a href="#" class="menu-link <%= "pos".equals(activeMenu) ? "active" : "" %>">
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
                    <a href="../Thongke.jsp" class="menu-link <%= "dashboard".equals(activeMenu) ? "active" : "" %>">
                        <i class="fas fa-chart-bar"></i>
                        <span>Thống kê</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="#" class="menu-link <%= "invoice".equals(activeMenu) ? "active" : "" %>">
                        <i class="fas fa-file-invoice"></i>
                        <span>Quản lý hoá đơn</span>
                    </a>
                </li>

                <!-- Quản lý sản phẩm (Đã thêm class 'open' nếu activeMenu là product) -->
                <li class="menu-item <%= "product".equals(activeMenu) ? "open" : "" %>">
                    <a href="#" class="menu-link <%= "product".equals(activeMenu) ? "active" : "" %>">
                        <div class="menu-link-wrapper">
                            <i class="fas fa-box"></i>
                            <span>Quản lý sản phẩm</span>
                        </div>
                        <i class="fas fa-chevron-down toggle-icon"></i>
                    </a>
                    <ul class="submenu">
                        <li><a href="../DanhMuc" class="<%= "category".equals(activeSubMenu) ? "active" : "" %>">Danh mục</a></li>
                        <li><a href="#" class="<%= "brand".equals(activeSubMenu) ? "active" : "" %>">Thương hiệu</a></li>
                        <li><a href="#" class="<%= "material".equals(activeSubMenu) ? "active" : "" %>">Chất liệu</a></li>
                        <li><a href="#" class="<%= "style".equals(activeSubMenu) ? "active" : "" %>">Kiểu dáng</a></li>
                        <li><a href="#" class="<%= "color".equals(activeSubMenu) ? "active" : "" %>">Màu sắc</a></li>
                        <li><a href="#" class="<%= "size".equals(activeSubMenu) ? "active" : "" %>">Kích Cỡ</a></li>
                        <li><a href="#" class="<%= "lens".equals(activeSubMenu) ? "active" : "" %>">Tròng Kính</a></li>
                        <li><a href="#" class="<%= "frame".equals(activeSubMenu) ? "active" : "" %>">Gọng Kính</a></li>
                    </ul>
                </li>

                <li class="menu-item">
                    <a href="../QuanLyMaGiamGia/quan_ly_giam_gia.jsp" class="menu-link <%= "discount".equals(activeMenu) ? "active" : "" %>">
                        <i class="fas fa-tags"></i>
                        <span>Quản lý giảm giá</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="#" class="menu-link <%= "customer".equals(activeMenu) ? "active" : "" %>">
                        <i class="fas fa-users"></i>
                        <span>Quản lý khách hàng</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="#" class="menu-link <%= "employee".equals(activeMenu) ? "active" : "" %>">
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
<button class="sidebar-toggle" id="sidebarToggle" type="button">
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
                // Không đóng sidebar nếu click vào menu cha có chứa submenu
                if (link.getAttribute('href') !== '#') {
                    toggleSidebar();
                }
            }
        });
    });

    // Xử lý đóng mở submenu
    document.querySelectorAll('.menu-item').forEach(item => {
        const link = item.querySelector('.menu-link');
        const submenu = item.querySelector('.submenu');

        if (link && submenu) {
            link.addEventListener('click', (e) => {
                if (link.getAttribute('href') === '#') {
                    e.preventDefault();
                    item.classList.toggle('open'); // Class này sẽ xoay icon và mở submenu
                }
            });
        }
    });
</script>