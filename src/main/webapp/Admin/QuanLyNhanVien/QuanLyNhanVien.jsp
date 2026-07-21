<%
    request.setAttribute("pageTitle", "Quản lý nhân viên");
    request.setAttribute("activeMenu", "employee");
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý nhân viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        /* ========================================================== */
        /* 🛠️ SỬA LỖI CHE LAYOUT & ĐỒNG BỘ GIAO DIỆN HỆ THỐNG       */
        /* ========================================================== */
        body {
            background-color: #f8fafc;
            margin: 0;
            padding: 0;
        }

        .dashboard-container {
            margin-left: 260px !important; /* Đẩy nội dung tránh bị Sidebar che */
            padding: 24px 32px !important;
            min-height: 100vh;
            box-sizing: border-box;
            transition: all 0.3s ease;
        }

        /* 🎨 CSS CHO KHUNG BỘ LỌC TÌM KIẾM */
        .filter-section {
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 24px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        }

        .filter-header {
            margin-bottom: 16px;
            padding-bottom: 12px;
            border-bottom: 1px solid #f3f4f6;
        }

        .filter-title {
            font-size: 16px;
            font-weight: 700;
            color: #1f2937;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .filter-title i {
            color: #b4975a;
        }

        /* Lưới lọc 3 cột cho NV */
        .filter-grid-nv {
            display: grid;
            grid-template-columns: 2fr 1fr 1fr;
            gap: 16px;
            align-items: flex-start;
        }

        .filter-group {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .filter-label {
            font-size: 13px;
            font-weight: 600;
            color: #4b5563;
        }

        .filter-input,
        .filter-select {
            width: 100%;
            height: 40px;
            padding: 8px 12px;
            font-size: 14px;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            background-color: #ffffff;
            color: #1f2937;
            box-sizing: border-box;
            transition: all 0.2s ease;
        }

        .filter-input:focus,
        .filter-select:focus {
            border-color: #b4975a !important;
            outline: none;
            box-shadow: 0 0 0 3px rgba(180, 151, 90, 0.15);
        }

        /* Radio group giới tính */
        .gender-radio-group {
            display: flex;
            align-items: center;
            gap: 16px;
            height: 40px;
        }

        .gender-radio-group label {
            font-size: 14px;
            color: #374151;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .gender-radio-group input[type="radio"] {
            accent-color: #b4975a;
            cursor: pointer;
        }

        /* Hàng chứa các nút bấm hành động trong bộ lọc */
        .filter-action-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 20px;
            padding-top: 16px;
            border-top: 1px dashed #e5e7eb;
        }

        .filter-action-left {
            display: flex;
            gap: 12px;
            align-items: center;
        }

        .filter-action-right {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        /* Nút bấm chuẩn */
        .add-new-btn {
            background-color: #b4975a !important;
            color: #ffffff !important;
            border: none !important;
            border-radius: 8px;
            padding: 10px 20px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: background-color 0.2s ease;
            text-decoration: none;
        }

        .add-new-btn:hover {
            background-color: #9a8048 !important;
        }

        .btn-secondary-outline {
            background: #ffffff !important;
            color: #4b5563 !important;
            border: 1px solid #d1d5db !important;
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .btn-secondary-outline:hover {
            background: #f9fafb !important;
            border-color: #9ca3af !important;
            color: #1f2937 !important;
        }

        /* Toolbar Sticky Header */
        .table-toolbar {
            position: sticky;
            top: 70px;
            background: #ffffff;
            z-index: 90;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 14px 16px;
            margin-top: 24px;
            border: 1px solid #e5e7eb;
            border-bottom: none;
            border-radius: 8px 8px 0 0;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
        }

        .toolbar-left-results {
            font-size: 14px;
            font-weight: 500;
            color: #4b5563;
        }

        .toolbar-left-results span {
            font-weight: 700;
            color: #1f2937;
        }

        .category-table {
            margin-top: 0 !important;
            border-top-left-radius: 0 !important;
            border-top-right-radius: 0 !important;
            width: 100%;
        }

        /* Custom Toggle Switch */
        .status-switch {
            position: relative;
            display: inline-block;
            width: 44px;
            height: 22px;
        }

        .status-switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .status-slider {
            position: absolute;
            cursor: pointer;
            top: 0; left: 0; right: 0; bottom: 0;
            background-color: #94a3b8;
            transition: .3s;
            border-radius: 22px;
        }

        .status-slider:before {
            position: absolute;
            content: "";
            height: 16px;
            width: 16px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .3s;
            border-radius: 50%;
            box-shadow: 0 1px 3px rgba(0,0,0,0.2);
        }

        .status-switch input:checked + .status-slider {
            background-color: #b4975a;
        }

        .status-switch input:checked + .status-slider:before {
            transform: translateX(22px);
        }

        /* Toast thông báo */
        .toast-custom {
            position: fixed;
            top: 25px;
            right: 25px;
            padding: 16px 24px;
            border-radius: 8px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
            z-index: 1000000;
            display: flex;
            align-items: center;
            gap: 12px;
            font-weight: 600;
            font-size: 15px;
        }

        .toast-success-style {
            background-color: #e8f5e9;
            color: #2e7d32;
            border: 1px solid #c8e6c9;
        }

        .toast-error-style {
            background-color: #fdecea;
            color: #b3261e;
            border: 1px solid #fad2cf;
        }

        /* Responsive Sidebar */
        @media (max-width: 992px) {
            .dashboard-container {
                margin-left: 0 !important;
                padding: 16px !important;
            }
            .filter-grid-nv {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<%@ include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@ include file="../layout/header.jsp" %>

    <div class="category-section">
        <div class="category-header">
            <h2 class="category-title">Quản lý nhân viên</h2>
        </div>

        <!-- THÔNG BÁO -->
        <c:if test="${not empty success || param.success == '1'}">
            <div id="toast-msg" class="toast-custom toast-success-style">
                <i class="fas fa-circle-check"></i>
                <span><c:choose><c:when test="${not empty success}">${success}</c:when><c:otherwise>Thao tác thành công!</c:otherwise></c:choose></span>
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div id="toast-msg" class="toast-custom toast-error-style">
                <i class="fas fa-circle-exclamation"></i>
                <span>${error}</span>
            </div>
        </c:if>

        <!-- BỘ LỌC TÌM KIẾM ĐÃ ĐƯỢC BỌC LẠI BẰNG ĐÚNG CLASS CSS -->
        <div class="filter-section">
            <div class="filter-header">
                <div class="filter-title">
                    <i class="fas fa-filter"></i>
                    Bộ lọc tìm kiếm
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/NhanVien/search" method="post" id="filterForm">
                <div class="filter-grid-nv">
                    <div class="filter-group">
                        <label class="filter-label">Tìm kiếm</label>
                        <input type="text" name="hoTen" value="${param.hoTen}" class="filter-input" placeholder="Tên, mã, email...">
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Trạng thái</label>
                        <select class="filter-select" name="trangThai">
                            <option value="">Tất cả</option>
                            <option value="1" ${param.trangThai == '1' ? 'selected' : ''}>Đang hoạt động</option>
                            <option value="0" ${param.trangThai == '0' ? 'selected' : ''}>Ngừng hoạt động</option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Giới tính</label>
                        <div class="gender-radio-group">
                            <label><input type="radio" name="gioiTinh" value="" ${empty param.gioiTinh ? 'checked' : ''}> Tất cả</label>
                            <label><input type="radio" name="gioiTinh" value="1" ${param.gioiTinh == '1' ? 'checked' : ''}> Nam</label>
                            <label><input type="radio" name="gioiTinh" value="0" ${param.gioiTinh == '0' ? 'checked' : ''}> Nữ</label>
                        </div>
                    </div>
                </div>

                <div class="filter-action-row">
                    <div class="filter-action-left">
                        <c:if test="${not empty param.hoTen || not empty param.trangThai || not empty param.gioiTinh}">
                            <a href="${pageContext.request.contextPath}/NhanVien"
                               style="padding: 10px 20px; background: #fee2e2; color: #dc2626; border-radius: 8px; text-decoration: none; display: inline-flex; align-items: center; gap: 6px; font-weight: 600; font-size: 14px;">
                                <i class="fas fa-xmark"></i> Xóa bộ lọc
                            </a>
                        </c:if>
                    </div>

                    <!-- Nút tìm kiếm & Đặt lại bên phải -->
                    <div class="filter-action-right">
                        <button type="submit" class="add-new-btn" style="padding: 10px 24px;">
                            <i class="fas fa-search"></i> Tìm kiếm
                        </button>
                        <button type="button" class="btn-secondary-outline" onclick="document.getElementById('filterForm').reset(); window.location='${pageContext.request.contextPath}/NhanVien';">
                            <i class="fas fa-rotate-left"></i> Đặt lại
                        </button>
                    </div>
                </div>
            </form>
        </div>

        <!-- TOOLBAR BẢNG -->
        <div class="table-toolbar">
            <div class="toolbar-left-results">
                Hiển thị <span>${items.size()}</span> nhân viên
            </div>
            <div style="display: flex; gap: 12px; align-items: center;">
                <a href="#" class="btn-secondary-outline" title="In danh sách" onclick="window.print(); return false;">
                    <i class="fas fa-print"></i> In
                </a>
                <a href="#" class="btn-secondary-outline" title="Xuất Excel">
                    <i class="fas fa-file-export"></i> Xuất Excel
                </a>
                <a href="${pageContext.request.contextPath}/NhanVien/new" class="add-new-btn" style="text-decoration: none; display: inline-flex; align-items: center; gap: 6px; padding: 10px 20px;">
                    <i class="fas fa-plus"></i> Thêm mới
                </a>
            </div>
        </div>

        <!-- BẢNG DỮ LIỆU -->
        <table class="category-table">
            <thead>
            <tr>
                <th>STT</th>
                <th>MÃ NV</th>
                <th>TÊN NHÂN VIÊN</th>
                <th>CHỨC VỤ</th>
                <th>GIỚI TÍNH</th>
                <th>SĐT</th>
                <th>EMAIL</th>
                <th>ĐỊA CHỈ</th>
                <th>TRẠNG THÁI</th>
                <th>THAO TÁC</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="nv" items="${items}" varStatus="status">
                <tr>
                    <td><span class="category-id">${status.index + 1 + (currentPage - 1) * 10}</span></td>
                    <td><strong style="color: #1f2937;">${nv.maNhanVien}</strong></td>
                    <td title="${nv.hoTen}"><strong>${nv.hoTen}</strong></td>
                    <td title="${nv.chucVu}">${nv.chucVu}</td>
                    <td>${nv.gioiTinh == 1 ? 'Nam' : 'Nữ'}</td>
                    <td>
                        <c:if test="${not empty nv.soDienThoai}">
                            <i class="fas fa-phone" style="color:#9ca3af;margin-right:4px;font-size:12px;"></i>${nv.soDienThoai}
                        </c:if>
                    </td>
                    <td title="${nv.email}">
                        <c:if test="${not empty nv.email}">
                            <i class="fas fa-envelope" style="color:#9ca3af;margin-right:4px;font-size:12px;"></i>${nv.email}
                        </c:if>
                    </td>
                    <td title="${nv.diaChi}">${nv.diaChi}</td>
                    <td>
                        <span class="category-status ${nv.trangThai == 1 ? 'status-active' : 'status-inactive'}">
                                ${nv.trangThai == 1 ? 'Đang hoạt động' : 'Ngừng hoạt động'}
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons" style="align-items: center; gap: 10px;">
                            <a href="${pageContext.request.contextPath}/NhanVien/edit?id=${nv.id}" class="btn-icon-circle btn-view" title="Chỉnh sửa">
                                <i class="fas fa-pen"></i>
                            </a>

                            <form action="${pageContext.request.contextPath}/NhanVien/delete" method="post" style="display:inline;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa nhân viên này?');">
                                <input type="hidden" name="id" value="${nv.id}">
                                <button type="submit" class="btn-icon-circle" title="Xóa" style="border:none; cursor:pointer;">
                                    <i class="fas fa-trash-alt" style="color:#dc2626;"></i>
                                </button>
                            </form>

                            <label class="status-switch" title="Trạng thái">
                                <input type="checkbox" ${nv.trangThai == 1 ? 'checked' : ''} disabled>
                                <span class="status-slider"></span>
                            </label>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty items}">
                <tr>
                    <td colspan="10" style="text-align: center; padding: 30px; color: #888;">
                        <i class="fas fa-inbox" style="font-size: 24px; margin-bottom: 10px; display: block;"></i>
                        Không tìm thấy dữ liệu nào.
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

        <!-- PHÂN TRANG -->
        <c:if test="${totalPages > 1}">
            <div class="sp-pagination">
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <a href="${pageContext.request.contextPath}/NhanVien?page=${p}" class="sp-page-btn ${p == currentPage ? 'active' : ''}">
                            ${p}
                    </a>
                </c:forEach>
            </div>
        </c:if>

    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        const toast = document.getElementById("toast-msg");
        if (toast) {
            setTimeout(function () {
                toast.style.opacity = "0";
                toast.style.transform = "translateY(-10px)";
                setTimeout(function () { toast.remove(); }, 500);
            }, 3000);
        }
    });
</script>
</body>
</html>