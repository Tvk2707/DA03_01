<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String contextPath = request.getContextPath();
    String activeMenu = (String) request.getAttribute("activeMenu");
    String activeSubMenu = (String) request.getAttribute("activeSubMenu");
    if (activeMenu == null) activeMenu = "";
    if (activeSubMenu == null) activeSubMenu = "";
    boolean attributeActive = "attribute".equals(activeMenu)
            || "category".equals(activeSubMenu)
            || "brand".equals(activeSubMenu)
            || "material".equals(activeSubMenu)
            || "style".equals(activeSubMenu)
            || "color".equals(activeSubMenu)
            || "size".equals(activeSubMenu)
            || "lens".equals(activeSubMenu)
            || "frame".equals(activeSubMenu)
            || "frame-shape".equals(activeSubMenu)
            || "temple".equals(activeSubMenu);
%>

<aside class="sidebar" id="sidebar">
    <div class="sidebar-logo">
        <div class="logo-icon"><i class="fas fa-glasses"></i></div>
        <div class="logo-text">
            <h1>RIOR</h1>
            <span>Fine Eyewear &amp; Optics</span>
        </div>
    </div>

    <nav class="sidebar-menu">
        <div class="menu-section">
            <div class="menu-title">Trang chủ</div>
            <ul class="menu-list">
                <li class="menu-item">
                    <a href="<%= contextPath %>/admin/thong-ke"
                       class="menu-link <%= "dashboard".equals(activeMenu) ? "active" : "" %>">
                        <i class="fas fa-chart-bar"></i>
                        <span>Thống kê</span>
                    </a>
                </li>
            </ul>
        </div>

        <div class="menu-section">
            <div class="menu-title">Kênh bán hàng</div>
            <ul class="menu-list">
                <li class="menu-item">
                    <a href="#" class="menu-link">
                        <i class="fas fa-cash-register"></i>
                        <span>Bán tại quầy (POS)</span>
                    </a>
                </li>
            </ul>
        </div>

        <div class="menu-section">
            <div class="menu-title">Quản trị</div>
            <ul class="menu-list">
                <li class="menu-item">
                    <a href="<%= contextPath %>/admin/hoa-don"
                       class="menu-link <%= "hoadon".equals(activeMenu) || "invoice".equals(activeMenu) ? "active" : "" %>">
                        <i class="fas fa-file-invoice"></i>
                        <span>Quản lý hoá đơn</span>
                    </a>
                </li>
                <li class="menu-item <%= "product".equals(activeMenu) && !attributeActive ? "open" : "" %>">
                    <a href="#" class="menu-link <%= "product".equals(activeMenu) && !attributeActive ? "active" : "" %>">
                        <div class="menu-link-wrapper">
                            <i class="fas fa-box"></i>
                            <span>Quản lý sản phẩm</span>
                        </div>
                        <i class="fas fa-chevron-down toggle-icon"></i>
                    </a>
                    <ul class="submenu">
                        <li><a href="<%= contextPath %>/SanPham"
                               class="<%= "product".equals(activeSubMenu) ? "active" : "" %>">Sản phẩm</a></li>
                        <li><a href="<%= contextPath %>/SanPhamChiTiet"
                               class="<%= "product-detail".equals(activeSubMenu) ? "active" : "" %>">Sản phẩm chi tiết</a></li>
                    </ul>
                </li>
                <li class="menu-item <%= attributeActive ? "open" : "" %>">
                    <a href="#" class="menu-link <%= attributeActive ? "active" : "" %>">
                        <div class="menu-link-wrapper">
                            <i class="fas fa-cubes"></i>
                            <span>Quản lý thuộc tính</span>
                        </div>
                        <i class="fas fa-chevron-down toggle-icon"></i>
                    </a>
                    <ul class="submenu">
                        <li><a href="<%= contextPath %>/DanhMuc" class="<%= "category".equals(activeSubMenu) ? "active" : "" %>">Danh mục</a></li>
                        <li><a href="<%= contextPath %>/ThuongHieu" class="<%= "brand".equals(activeSubMenu) ? "active" : "" %>">Thương hiệu</a></li>
                        <li><a href="<%= contextPath %>/ChatLieu" class="<%= "material".equals(activeSubMenu) ? "active" : "" %>">Chất liệu</a></li>
                        <li><a href="<%= contextPath %>/KieuDang" class="<%= "style".equals(activeSubMenu) ? "active" : "" %>">Kiểu dáng</a></li>
                        <li><a href="<%= contextPath %>/MauSac" class="<%= "color".equals(activeSubMenu) ? "active" : "" %>">Màu sắc</a></li>
                        <li><a href="<%= contextPath %>/KichCo" class="<%= "size".equals(activeSubMenu) ? "active" : "" %>">Kích cỡ</a></li>
                        <li><a href="<%= contextPath %>/TrongKinh" class="<%= "lens".equals(activeSubMenu) ? "active" : "" %>">Tròng kính</a></li>
                        <li><a href="<%= contextPath %>/GongKinh" class="<%= "frame".equals(activeSubMenu) ? "active" : "" %>">Gọng kính</a></li>
                        <li><a href="<%= contextPath %>/HinhDangGong" class="<%= "frame-shape".equals(activeSubMenu) ? "active" : "" %>">Hình dạng gọng</a></li>
                        <li><a href="<%= contextPath %>/KieuQuaiKinh" class="<%= "temple".equals(activeSubMenu) ? "active" : "" %>">Kiểu quai kính</a></li>
                    </ul>
                </li>
                <li class="menu-item">
                    <a href="<%= contextPath %>/FE/Admin/QuanLyMaGiamGia/quan_ly_giam_gia.jsp" class="menu-link <%= "giamgia".equals(activeMenu) || "discount".equals(activeMenu) ? "active" : "" %>">
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

    <div class="sidebar-bottom">
        <ul class="bottom-menu">
            <li class="bottom-item">
                <a href="#" class="bottom-link"><i class="fas fa-cog"></i><span>Cài đặt</span></a>
            </li>
            <li class="bottom-item">
                <a href="#" class="bottom-link logout"><i class="fas fa-sign-out-alt"></i><span>Đăng xuất</span></a>
            </li>
        </ul>
    </div>
</aside>

<div class="overlay" id="overlay"></div>
<button class="sidebar-toggle" id="sidebarToggle" type="button" aria-label="Mở menu">
    <i class="fas fa-bars"></i>
</button>

<script>
    (function () {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('overlay');
        const toggle = document.getElementById('sidebarToggle');

        function toggleSidebar() {
            if (sidebar) sidebar.classList.toggle('active');
            if (overlay) overlay.classList.toggle('active');
        }

        if (toggle) toggle.addEventListener('click', toggleSidebar);
        if (overlay) overlay.addEventListener('click', toggleSidebar);

        const menuItemsWithSub = document.querySelectorAll('.menu-item');

        menuItemsWithSub.forEach(function (item) {
            const link = item.querySelector(':scope > .menu-link');
            const submenu = item.querySelector(':scope > .submenu');
            if (!link || !submenu) return;

            link.addEventListener('click', function (event) {
                event.preventDefault();
                const shouldOpen = !item.classList.contains('open');
                menuItemsWithSub.forEach(function (otherItem) {
                    if (otherItem !== item && otherItem.querySelector(':scope > .submenu')) {
                        otherItem.classList.remove('open');
                        const otherLink = otherItem.querySelector(':scope > .menu-link');
                        if (otherLink) otherLink.setAttribute('aria-expanded', 'false');
                    }
                });
                item.classList.toggle('open', shouldOpen);
                link.setAttribute('aria-expanded', item.classList.contains('open') ? 'true' : 'false');
            });
        });

        document.querySelectorAll('.menu-link, .bottom-link').forEach(function (link) {
            link.addEventListener('click', function () {
                if (window.innerWidth <= 1024 && link.getAttribute('href') !== '#') {
                    toggleSidebar();
                }
            });
        });
    }());
</script>

