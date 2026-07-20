<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String contextPath = request.getContextPath();
    String activeMenu = (String) request.getAttribute("activeMenu");
    String activeSubMenu = (String) request.getAttribute("activeSubMenu");
    if (activeMenu == null) activeMenu = "";
    if (activeSubMenu == null) activeSubMenu = "";
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
            <div class="menu-title">Trung tâm quản trị</div>
            <ul class="menu-list">
                <li class="menu-item">
                    <a href="<%= contextPath %>/admin/thong-ke"
                       class="menu-link <%= "dashboard".equals(activeMenu) ? "active" : "" %>">
                        <i class="fas fa-layer-group"></i>
                        <span>Tổng hợp bán hàng</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="<%= contextPath %>/admin/hoa-don"
                       class="menu-link <%= "hoadon".equals(activeMenu) ? "active" : "" %>">
                        <i class="fas fa-file-invoice"></i>
                        <span>Quản lý hóa đơn</span>
                    </a>
                </li>
                <li class="menu-item <%= "product".equals(activeMenu) ? "open" : "" %>">
                    <a href="#" class="menu-link <%= "product".equals(activeMenu) ? "active" : "" %>"
                       aria-expanded="<%= "product".equals(activeMenu) ? "true" : "false" %>"
                       aria-controls="productSubmenu">
                        <span class="menu-link-wrapper">
                            <i class="fas fa-box"></i>
                            <span>Quản lý sản phẩm</span>
                        </span>
                        <i class="fas fa-chevron-down toggle-icon"></i>
                    </a>
                    <ul class="submenu" id="productSubmenu">
                        <li>
                            <a href="<%= contextPath %>/SanPham"
                               class="<%= "product".equals(activeSubMenu) ? "active" : "" %>">
                                Sản phẩm
                            </a>
                        </li>
                        <li>
                            <a href="<%= contextPath %>/SanPhamChiTiet"
                               class="<%= "product-detail".equals(activeSubMenu) ? "active" : "" %>">
                                Sản phẩm chi tiết
                            </a>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>

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

        <div class="menu-section">
            <div class="menu-title">Khác</div>
            <ul class="menu-list">
                <li class="menu-item">
                    <a href="<%= contextPath %>/FE/Admin/QuanLyMaGiamGia/quan_ly_giam_gia.jsp" class="menu-link">
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

        document.querySelectorAll('.menu-item').forEach(function (item) {
            const link = item.querySelector(':scope > .menu-link');
            const submenu = item.querySelector(':scope > .submenu');
            if (!link || !submenu) return;

            link.addEventListener('click', function (event) {
                event.preventDefault();
                item.classList.toggle('open');
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

