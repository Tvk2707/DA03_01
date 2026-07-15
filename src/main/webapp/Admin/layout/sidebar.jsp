<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Lấy activeMenu một lần, xử lý null để tránh NullPointerException
    String activeMenu = (String) request.getAttribute("activeMenu");
    if (activeMenu == null) activeMenu = "";

    // Lấy activeSubMenu để highlight các mục con trong Quản lý sản phẩm
    String activeSubMenu = (String) request.getAttribute("activeSubMenu");
    if (activeSubMenu == null) activeSubMenu = "";
%>

<aside class="sidebar" id="sidebar">
    <div class="sidebar-logo">
        <div class="logo-icon">
            <i class="fas fa-glasses"></i>
        </div>
        <div class="logo-text">
            <h1>RIOR</h1>
            <span>Fine Eyewear & Optics</span>
        </div>
    </div>

    <nav class="sidebar-menu">


        <div class="menu-section">
            <div class="menu-title">Trang chủ</div>
            <ul class="menu-list">
                <li class="menu-item">
                    <a href="../Thongke.jsp" class="menu-link <%= "dashboard".equals(activeMenu) ? "active" : "" %>">
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
                    <a href="#" class="menu-link <%= "pos".equals(activeMenu) ? "active" : "" %>">
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
                    <a href="#" class="menu-link <%= "invoice".equals(activeMenu) ? "active" : "" %>">
                        <i class="fas fa-file-invoice"></i>
                        <span>Quản lý hoá đơn</span>
                    </a>
                </li>

                <li class="menu-item <%= "product".equals(activeMenu) ? "open" : "" %>">
                    <a href="#" class="menu-link <%= "product".equals(activeMenu) ? "active" : "" %>">
                        <div class="menu-link-wrapper">
                            <i class="fas fa-box"></i>
                            <span>Quản lý sản phẩm</span>
                        </div>
                        <i class="fas fa-chevron-down toggle-icon"></i>
                    </a>
                    <ul class="submenu">
                        <li><a href="${pageContext.request.contextPath}/SanPham"
                               class="<%= "product".equals(activeSubMenu) ? "active" : "" %>">Sản phẩm </a></li>
                        <li><a href="${pageContext.request.contextPath}/SanPhamChiTiet"
                               class="<%= "pruducthh".equals(activeSubMenu) ? "active" : "" %>">Sản phẩm chi tiết </a></li>
                    </ul>
                </li>

                <li class="menu-item <%= "attribute".equals(activeMenu) ? "open" : "" %>">
                    <a href="#" class="menu-link <%= "attribute".equals(activeMenu) ? "active" : "" %>">
                        <div class="menu-link-wrapper">
                            <i class="fas fa-cubes"></i>
                            <span>Quản lý thuộc tính</span>
                        </div>
                        <i class="fas fa-chevron-down toggle-icon"></i>
                    </a>
                    <ul class="submenu">
                        <li><a href="${pageContext.request.contextPath}/DanhMuc"
                               class="<%= "category".equals(activeSubMenu) ? "active" : "" %>">Danh mục</a></li>
                        <li><a href="${pageContext.request.contextPath}/ThuongHieu"
                               class="<%= "brand".equals(activeSubMenu) ? "active" : "" %>">Thương hiệu</a></li>
                        <li><a href="${pageContext.request.contextPath}/ChatLieu"
                               class="<%= "material".equals(activeSubMenu) ? "active" : "" %>">Chất liệu</a></li>
                        <li><a href="${pageContext.request.contextPath}/KieuDang"
                               class="<%= "style".equals(activeSubMenu) ? "active" : "" %>">Kiểu dáng</a></li>
                        <li><a href="${pageContext.request.contextPath}/MauSac"
                               class="<%= "color".equals(activeSubMenu) ? "active" : "" %>">Màu sắc</a></li>
                        <li><a href="${pageContext.request.contextPath}/KichCo"
                               class="<%= "size".equals(activeSubMenu) ? "active" : "" %>">Kích Cỡ</a></li>
                        <li><a href="${pageContext.request.contextPath}/TrongKinh"
                               class="<%= "lens".equals(activeSubMenu) ? "active" : "" %>">Tròng Kính</a></li>
                        <li><a href="${pageContext.request.contextPath}/GongKinh"
                               class="<%= "frame".equals(activeSubMenu) ? "active" : "" %>">Gọng Kính</a></li>
                    </ul>
                </li>

                <li class="menu-item">
                    <a href="../QuanLyMaGiamGia/quan_ly_giam_gia.jsp"
                       class="menu-link <%= "discount".equals(activeMenu) ? "active" : "" %>">
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

<div class="overlay" id="overlay"></div>

<button class="sidebar-toggle" id="sidebarToggle" type="button">
    <i class="fas fa-bars"></i>
</button>
<script>
    // --- 1. SIDEBAR TOGGLE TRÊN MOBILE ---
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('overlay');
    const sidebarToggle = document.getElementById('sidebarToggle');

    function toggleSidebar() {
        sidebar.classList.toggle('active');
        overlay.classList.toggle('active');
    }

    if (sidebarToggle) sidebarToggle.addEventListener('click', toggleSidebar);
    if (overlay) overlay.addEventListener('click', toggleSidebar);

    document.querySelectorAll('.menu-link, .bottom-link').forEach(link => {
        link.addEventListener('click', () => {
            if (window.innerWidth <= 1024 && link.getAttribute('href') !== '#') {
                toggleSidebar();
            }
        });
    });

    // --- 2. XỬ LÝ ĐÓNG MỞ SUBMENU ĐỘC QUYỀN KHI CLICK TẠI CHỐ ---
    const menuItemsWithSub = document.querySelectorAll('.menu-item');

    menuItemsWithSub.forEach(item => {
        const link = item.querySelector('.menu-link');
        const submenu = item.querySelector('.submenu');

        if (link && submenu) {
            link.addEventListener('click', (e) => {
                if (link.getAttribute('href') === '#') {
                    e.preventDefault();

                    if (!item.classList.contains('open')) {
                        menuItemsWithSub.forEach(otherItem => {
                            otherItem.classList.remove('open');
                        });
                        item.classList.add('open');
                    } else {
                        item.classList.remove('open');
                    }
                }
            });
        }
    });

    // --- 3. ĐỒNG BỘ TRẠNG THÁI ACTIVE VÀ XỬ LÝ THANH CUỘN (SCROLL) LINH ĐỘNG ---
    document.addEventListener("DOMContentLoaded", function () {
        const currentPath = window.location.pathname;
        const allLinks = document.querySelectorAll('.sidebar-menu a');
        let matched = false;

        // Xác định chính xác vùng chứa thanh cuộn (Ưu tiên vùng chứa menu .sidebar-menu)
        const scrollContainer = document.querySelector('.sidebar-menu') || document.getElementById('sidebar');

        // Reset triệt để class active/open mặc định sai lệch từ Backend render
        document.querySelectorAll('.menu-link, .submenu a').forEach(el => el.classList.remove('active'));
        document.querySelectorAll('.menu-item').forEach(el => el.classList.remove('open'));

        // thuật toán tìm liên kết trùng khớp chính xác nhất (Tránh lỗi active cả Sản phẩm + Sản phẩm chi tiết)
        let bestMatchLink = null;
        let maxLength = 0;

        allLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (!href || href === '#' || href.startsWith('javascript:')) return;

            // Kiểm tra xem đường dẫn URL hiện tại có khớp hoàn toàn hoặc kết thúc bằng href không
            if (currentPath.endsWith(href) || currentPath === href) {
                // Ưu tiên chuỗi href có độ dài lớn nhất (/SanPhamChiTiet thắng /SanPham)
                if (href.length > maxLength) {
                    maxLength = href.length;
                    bestMatchLink = link;
                }
            }
        });

        // Tiến hành gán class active duy nhất cho mục đúng nhất tìm được
        if (bestMatchLink) {
            matched = true;
            bestMatchLink.classList.add('active');

            const parentSubmenu = bestMatchLink.closest('.submenu');
            if (parentSubmenu) {
                const parentMenuItem = parentSubmenu.closest('.menu-item');
                if (parentMenuItem) {
                    parentMenuItem.classList.add('open');
                    const parentLink = parentMenuItem.querySelector('.menu-link');
                    if (parentLink) parentLink.classList.add('active');

                    localStorage.setItem('activeHref', bestMatchLink.getAttribute('href'));
                    localStorage.setItem('openMenuText', parentMenuItem.querySelector('.menu-link span').innerText.trim());
                }
            } else {
                const parentMenuItem = bestMatchLink.closest('.menu-item');
                if (parentMenuItem) parentMenuItem.classList.add('active');

                localStorage.removeItem('activeHref');
                localStorage.removeItem('openMenuText');
            }
        }

        // Tầng dự phòng khôi phục trạng thái active dựa trên bộ nhớ tạm nếu URL chứa tham số truy vấn ẩn
        if (!matched) {
            const savedHref = localStorage.getItem('activeHref');
            const savedOpenMenuText = localStorage.getItem('openMenuText');

            if (savedHref) {
                const activeLink = document.querySelector(`.submenu a[href="${savedHref}"]`);
                if (activeLink) {
                    document.querySelectorAll('.submenu a').forEach(el => el.classList.remove('active'));
                    activeLink.classList.add('active');
                }
            }

            if (savedOpenMenuText) {
                menuItemsWithSub.forEach(item => {
                    const titleSpan = item.querySelector('.menu-link span');
                    if (titleSpan && titleSpan.innerText.trim() === savedOpenMenuText) {
                        item.classList.add('open');
                        const parentLink = item.querySelector('.menu-link');
                        if (parentLink) parentLink.classList.add('active');
                    }
                });
            }
        }

        // ==========================================
        // KHU VỰC GIỮ VÀ DI CHUYỂN CUỘN THEO PHÂN MỤC ACTIVE TỰ NHIÊN
        // ==========================================
        if (scrollContainer) {
            const savedScrollTop = sessionStorage.getItem("sidebarScrollTop");
            const activeElement = document.querySelector('.submenu a.active, .menu-item.active > a, .menu-link.active');

            if (savedScrollTop !== null) {
                // Khôi phục chính xác vị trí cuộn cũ mà người dùng đang đứng trước khi reload trang
                scrollContainer.scrollTop = parseInt(savedScrollTop, 10);
                sessionStorage.removeItem("sidebarScrollTop");
            } else if (activeElement) {
                // Tự động cuộn menu mượt mà để hiển thị mục active nếu nó nằm khuất màn hình
                setTimeout(() => {
                    const containerRect = scrollContainer.getBoundingClientRect();
                    const activeRect = activeElement.getBoundingClientRect();

                    if (activeRect.top < containerRect.top || activeRect.bottom > containerRect.bottom) {
                        scrollContainer.scrollTop = (activeElement.offsetTop - scrollContainer.offsetTop) - (containerRect.height / 3);
                    }
                }, 100);
            }
        }

        // Đón đầu hành vi click chuột vào link bất kỳ để lưu vị trí cuộn hiện tại trước khi load trang
        document.querySelectorAll('.sidebar-menu a').forEach(link => {
            link.addEventListener('click', function (e) {
                if (this.getAttribute('href') === '#') {
                    return;
                }

                if (scrollContainer) {
                    sessionStorage.setItem("sidebarScrollTop", scrollContainer.scrollTop);
                }

                // Lưu vết routing cho menu con
                if (this.closest('.submenu')) {
                    const href = this.getAttribute('href');
                    const parentMenuItem = this.closest('.menu-item');
                    if (href && href !== '#') localStorage.setItem('activeHref', href);
                    if (parentMenuItem) {
                        const text = parentMenuItem.querySelector('.menu-link span').innerText.trim();
                        localStorage.setItem('openMenuText', text);
                    }
                } else {
                    localStorage.removeItem('activeHref');
                    localStorage.removeItem('openMenuText');
                }
            });
        });
    });
</script>