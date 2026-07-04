<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<header class="admin-header">
    <!-- Left: Breadcrumb -->
    <div class="header-left">
        <button class="back-button">
            <i class="fas fa-chevron-left"></i>
        </button>
        <nav class="breadcrumb">
            <div class="breadcrumb-item">
                <a href="#" class="breadcrumb-link">Admin</a>
            </div>
            <div class="breadcrumb-item active">
                <span><%= request.getAttribute("pageTitle") %></span>            </div>
        </nav>
    </div>

    <!-- Right: User Menu -->
    <div class="header-right">
        <!-- Notification -->
        <button class="notification-btn">
            <i class="fas fa-bell"></i>
            <span class="notification-badge"></span>
        </button>

        <!-- User Profile -->
        <div class="user-profile" id="userProfile">
            <div class="user-info">
                <div class="user-name">Admin Tuấn</div>
                <div class="user-role">Quản trị viên</div>
            </div>
            <div class="user-avatar">AT</div>

            <!-- Dropdown Menu -->
            <div class="user-dropdown" id="userDropdown">
                <div class="dropdown-header">
                    <div class="user-name">Admin Tuấn</div>
                    <div class="user-role">Quản trị viên</div>
                </div>
                <ul class="dropdown-menu">
                    <li class="dropdown-item">
                        <a href="#" class="dropdown-link">
                            <i class="fas fa-user"></i>
                            <span>Hồ sơ cá nhân</span>
                        </a>
                    </li>
                    <li class="dropdown-item">
                        <a href="#" class="dropdown-link">
                            <i class="fas fa-cog"></i>
                            <span>Cài đặt</span>
                        </a>
                    </li>
                    <li class="dropdown-item">
                        <a href="#" class="dropdown-link">
                            <i class="fas fa-question-circle"></i>
                            <span>Trợ giúp</span>
                        </a>
                    </li>
                    <li class="dropdown-divider"></li>
                    <li class="dropdown-item">
                        <a href="#" class="dropdown-link" style="color: #dc3545;">
                            <i class="fas fa-sign-out-alt"></i>
                            <span>Đăng xuất</span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</header>

<script>
    // User dropdown toggle
    const userProfile = document.getElementById('userProfile');
    const userDropdown = document.getElementById('userDropdown');

    if (userProfile && userDropdown) {
        userProfile.addEventListener('click', (e) => {
            e.stopPropagation();
            userDropdown.classList.toggle('show');
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', (e) => {
            if (!userProfile.contains(e.target)) {
                userDropdown.classList.remove('show');
            }
        });
    }
</script>