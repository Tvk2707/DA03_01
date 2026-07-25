<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="QuanLySanPham.Entity.NhanVien" %>
<%@ page import="java.util.Locale" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    Object nhanVienHeaderValue = request.getAttribute("nhanVienBanHang");
    if (!(nhanVienHeaderValue instanceof NhanVien)) {
        nhanVienHeaderValue = session.getAttribute("nhanVienDangNhap");
    }

    String tenNhanVienHeader = "Chưa xác định";
    String vaiTroNhanVienHeader = "Nhân viên bán hàng";
    String chuVietTatHeader = "NV";

    if (nhanVienHeaderValue instanceof NhanVien) {
        NhanVien nhanVienHeader = (NhanVien) nhanVienHeaderValue;
        String hoTen = nhanVienHeader.getHoTen() == null ? "" : nhanVienHeader.getHoTen().trim();
        if (!hoTen.isEmpty()) {
            tenNhanVienHeader = hoTen;
            String[] cacPhanTen = hoTen.split("\\s+");
            String chuDau = cacPhanTen[0].substring(0, 1);
            String chuCuoi = cacPhanTen.length > 1
                    ? cacPhanTen[cacPhanTen.length - 1].substring(0, 1)
                    : "";
            chuVietTatHeader = (chuDau + chuCuoi).toUpperCase(Locale.ROOT);
        }

        String chucVu = nhanVienHeader.getChucVu() == null ? "" : nhanVienHeader.getChucVu().trim();
        if (!chucVu.isEmpty()) {
            vaiTroNhanVienHeader = chucVu;
        } else if (nhanVienHeader.isQuanLy()) {
            vaiTroNhanVienHeader = "Quản trị viên";
        }
    }

    request.setAttribute("tenNhanVienHeader", tenNhanVienHeader);
    request.setAttribute("vaiTroNhanVienHeader", vaiTroNhanVienHeader);
    request.setAttribute("chuVietTatHeader", chuVietTatHeader);
%>
<header class="admin-header">
    <div class="header-left">
        <button class="back-button">
            <i class="fas fa-chevron-left"></i>
        </button>
        <nav class="breadcrumb">
            <div class="breadcrumb-item active">
                <span><%= request.getAttribute("pageTitle") %></span>
            </div>
        </nav>
    </div>

    <div class="header-right">
        <button class="notification-btn">
            <i class="fas fa-bell"></i>
            <span class="notification-badge"></span>
        </button>

        <div class="user-profile" id="userProfile">
            <div class="user-info">
                <div class="user-name"><c:out value="${tenNhanVienHeader}"/></div>
                <div class="user-role"><c:out value="${vaiTroNhanVienHeader}"/></div>
            </div>
            <div class="user-avatar"><c:out value="${chuVietTatHeader}"/></div>

            <div class="user-dropdown" id="userDropdown">
                <div class="dropdown-header">
                    <div class="user-name"><c:out value="${tenNhanVienHeader}"/></div>
                    <div class="user-role"><c:out value="${vaiTroNhanVienHeader}"/></div>
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
