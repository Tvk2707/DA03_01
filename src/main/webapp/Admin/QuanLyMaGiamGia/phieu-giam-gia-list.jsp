<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%
    if (request.getAttribute("pageTitle") == null) {
        request.setAttribute("pageTitle", "Quản lý phiếu giảm giá");
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý phiếu giảm giá</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/phieu-giam-gia.css">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        /* ========================================================== */
        /* 🛠️ SỬA LỖI LAYOUT BỊ SIDEBAR / HEADER CHE MẤT NỘI DUNG    */
        /* ========================================================== */
        body {
            background-color: #f8fafc;
            margin: 0;
            padding: 0;
        }

        /* Đồng bộ khoảng cách dashboard-container như bảng Danh mục */
        .dashboard-container {
            margin-left: 260px !important; /* Dẩy nội dung sang phải tránh Sidebar */
            padding: 24px 32px !important;
            min-height: 100vh;
            box-sizing: border-box;
            transition: all 0.3s ease;
        }

        /* Phân trang */
        .sp-pagination {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            gap: 6px;
            margin-top: 16px;
            padding: 10px 0;
        }
        .sp-page-btn {
            padding: 6px 12px;
            border: 1px solid #d1d5db;
            background-color: #fff;
            color: #374151;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            transition: all 0.2s ease;
            text-decoration: none;
        }
        .sp-page-btn:hover:not(:disabled) {
            background-color: #f3f4f6;
            color: #374151;
        }
        .sp-page-btn.active {
            background-color: #b4975a;
            color: #fff;
            border-color: #b4975a;
        }
        .sp-page-btn:disabled {
            opacity: 0.5;
            cursor: not-allowed;
            background-color: #f9fafb;
        }

        /* Toast Notifications */
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
            opacity: 1;
            transition: opacity 0.5s ease, transform 0.5s ease;
            animation: slideInToast 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
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
        @keyframes slideInToast {
            from { transform: translateX(120%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }

        /* Filter Grid 4 Cột */
        .filter-grid-4 {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 16px;
            align-items: flex-start;
        }
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
        }

        /* Toggle Switch Trạng Thái */
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

        /* Badges */
        .badge-type {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 600;
            background-color: #f3f4f6;
            color: #374151;
        }

        /* Responsive Khi Thu Gọn Sidebar Hoặc Màn Hình Nhỏ */
        @media (max-width: 992px) {
            .dashboard-container {
                margin-left: 0 !important;
                padding: 16px !important;
            }
            .filter-grid-4 {
                grid-template-columns: repeat(2, 1fr);
            }
        }
        @media (max-width: 640px) {
            .filter-grid-4 {
                grid-template-columns: 1fr;
            }
            .filter-action-row {
                flex-direction: column;
                gap: 12px;
                align-items: stretch;
            }
            .filter-action-right {
                flex-direction: column;
                gap: 8px;
            }
            .filter-action-right button, .filter-action-right a {
                width: 100%;
                justify-content: center;
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
            <h2 class="category-title">Quản lý phiếu giảm giá</h2>
        </div>

        <!-- THÔNG BÁO TOAST -->
        <div id="toast-container"></div>
        <c:if test="${not empty errorMessage}">
            <div id="toast-msg" class="toast-custom toast-error-style">
                <i class="fas fa-circle-exclamation"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div id="toast-msg" class="toast-custom toast-success-style">
                <i class="fas fa-check-circle"></i>
                <span>${successMessage}</span>
            </div>
        </c:if>

        <!-- BỘ LỌC TÌM KIẾM -->
        <div class="filter-section">
            <div class="filter-header">
                <div class="filter-title">
                    <i class="fas fa-filter"></i>
                    Bộ lọc tìm kiếm
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/PhieuGiamGia" method="get" id="filterForm">
                <div class="filter-grid-4">
                    <div class="filter-group">
                        <label class="filter-label">Tìm theo mã hoặc tên</label>
                        <input type="text" name="keyword" value="${keyword}" class="filter-input" placeholder="Nhập mã hoặc tên phiếu...">
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Mã giảm giá</label>
                        <input type="text" name="maVoucher" value="${maVoucher}" class="filter-input" placeholder="VD: VC001">
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Tên giảm giá</label>
                        <input type="text" name="tenVoucher" value="${tenVoucher}" class="filter-input" placeholder="Tên phiếu">
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Loại giảm</label>
                        <select name="loaiGiamGia" class="filter-select">
                            <option value="">Tất cả</option>
                            <option value="percent" <c:if test="${loaiGiamGia == 'percent'}">selected</c:if>>Giảm phần trăm</option>
                            <option value="amount" <c:if test="${loaiGiamGia == 'amount'}">selected</c:if>>Giảm tiền</option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Trạng thái</label>
                        <select name="trangThai" class="filter-select">
                            <option value="">Tất cả</option>
                            <option value="active" <c:if test="${trangThai == 'active'}">selected</c:if>>Đang áp dụng</option>
                            <option value="upcoming" <c:if test="${trangThai == 'upcoming'}">selected</c:if>>Chưa bắt đầu</option>
                            <option value="expired" <c:if test="${trangThai == 'expired'}">selected</c:if>>Kết thúc</option>
                            <option value="inactive" <c:if test="${trangThai == 'inactive'}">selected</c:if>>Ngừng áp dụng</option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Từ ngày</label>
                        <input type="date" name="tuNgay" value="${tuNgay}" class="filter-input">
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Đến ngày</label>
                        <input type="date" name="denNgay" value="${denNgay}" class="filter-input">
                    </div>
                </div>

                <div class="filter-action-row">
                    <div class="filter-action-left">
                        <c:if test="${not empty keyword || not empty maVoucher || not empty tenVoucher || not empty loaiGiamGia || not empty trangThai || not empty tuNgay || not empty denNgay}">
                            <a href="${pageContext.request.contextPath}/PhieuGiamGia"
                               style="padding: 10px 20px; background: #fee2e2; color: #dc2626; border-radius: 8px; text-decoration: none; display: inline-flex; align-items: center; gap: 6px; font-weight: 600; font-size: 14px;">
                                <i class="fas fa-xmark"></i> Xóa bộ lọc nâng cao
                            </a>
                        </c:if>
                    </div>

                    <div class="filter-action-right">
                        <button type="submit" class="add-new-btn" style="padding: 10px 24px;">
                            <i class="fas fa-search"></i> Tìm kiếm
                        </button>
                        <a href="${pageContext.request.contextPath}/PhieuGiamGia" class="btn-secondary-outline">
                            <i class="fas fa-rotate-left"></i> Đặt lại
                        </a>
                    </div>
                </div>
            </form>
        </div>

        <!-- TOOLBAR BẢNG -->
        <div class="table-toolbar">
            <div class="toolbar-left-results">
                Hiển thị <span>${fn:length(items)}</span> / tổng <span>${totalRecords}</span> phiếu giảm giá
            </div>
            <div style="display: flex; gap: 12px; align-items: center;">
                <a href="${exportUrl}" class="btn-secondary-outline">
                    <i class="fas fa-file-export"></i> Xuất Excel
                </a>
                <a href="${pageContext.request.contextPath}/PhieuGiamGia/new" class="add-new-btn" style="text-decoration: none; display: inline-flex; align-items: center; gap: 6px; padding: 10px 20px;">
                    <i class="fas fa-plus"></i> Thêm mới
                </a>
            </div>
        </div>

        <!-- BẢNG DỮ LIỆU -->
        <table class="category-table">
            <thead>
            <tr>
                <th>STT</th>
                <th>Mã giảm giá</th>
                <th>Tên giảm giá</th>
                <th>Loại phiếu</th>
                <th>Giá trị giảm</th>
                <th>Đơn tối thiểu</th>
                <th>Số lượng</th>
                <th>Ngày bắt đầu</th>
                <th>Ngày kết thúc</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="coupon" items="${items}" varStatus="loop">
                <tr>
                    <td>
                        <span class="category-id">${startIndex + loop.count}</span>
                    </td>
                    <td><strong style="color: #1f2937;">${coupon.maVoucher}</strong></td>
                    <td>${coupon.tenVoucher}</td>
                    <td>
                        <span class="badge-type">${coupon.loaiPhieu == 1 ? 'Cá nhân' : 'Công khai'}</span>
                    </td>
                    <td>
                        <strong style="color: #dc2626;">
                            <c:choose>
                                <c:when test="${coupon.loaiGiamGiaFilterValue == 'percent'}">
                                    <fmt:formatNumber value="${coupon.giaTriGiam}" maxFractionDigits="0"/>%
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber value="${coupon.giaTriGiam}" type="number" maxFractionDigits="0"/> đ
                                </c:otherwise>
                            </c:choose>
                        </strong>
                        <div style="font-size: 0.8em; color: #6b7280;">${coupon.loaiGiamGiaText}</div>
                    </td>
                    <td><fmt:formatNumber value="${coupon.donToiThieu}" type="number" maxFractionDigits="0"/> đ</td>
                    <td><span style="font-weight: 600;">${coupon.soLuongDaDung == null ? 0 : coupon.soLuongDaDung}/${coupon.soLuong}</span></td>
                    <td>${coupon.ngayBatDauText}</td>
                    <td>${coupon.ngayKetThucText}</td>
                    <td>
                        <span class="category-status JS-status-text ${coupon.trangThaiCssClass == 'status-active' ? 'status-active' : 'status-inactive'}">
                            <c:choose>
                                <c:when test="${coupon.trangThaiCssClass == 'status-upcoming'}">Chưa diễn ra</c:when>
                                <c:when test="${coupon.trangThaiCssClass == 'status-active'}">Đang diễn ra</c:when>
                                <c:when test="${coupon.trangThaiCssClass == 'status-expired'}">Đã kết thúc</c:when>
                                <c:otherwise>Ngừng hoạt động</c:otherwise>
                            </c:choose>
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons" style="align-items: center; gap: 12px;">
                            <a href="${pageContext.request.contextPath}/PhieuGiamGia/edit?id=${coupon.id}" class="btn-icon-circle btn-view" title="Chỉnh sửa">
                                <i class="fas fa-pen"></i>
                            </a>

                            <label class="status-switch" title="Chuyển trạng thái">
                                <input type="checkbox"
                                       class="js-coupon-status-toggle"
                                       data-id="${coupon.id}"
                                    ${coupon.trangThai == 1 ? 'checked' : ''}>
                                <span class="status-slider"></span>
                            </label>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty items}">
                <tr>
                    <td colspan="11" style="text-align: center; padding: 30px; color: #888;">
                        <i class="fas fa-inbox" style="font-size: 24px; margin-bottom: 10px; display: block;"></i>
                        Không có dữ liệu phù hợp.
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

        <!-- PHÂN TRANG -->
        <div class="sp-pagination">
            <c:if test="${totalPages > 1}">
                <c:choose>
                    <c:when test="${currentPage > 1}">
                        <a href="${pageUrlPrefix}${currentPage - 1}" class="sp-page-btn">
                            <i class="fas fa-chevron-left"></i>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <button class="sp-page-btn" disabled><i class="fas fa-chevron-left"></i></button>
                    </c:otherwise>
                </c:choose>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="${pageUrlPrefix}${i}" class="sp-page-btn ${currentPage == i ? 'active' : ''}">
                            ${i}
                    </a>
                </c:forEach>

                <c:choose>
                    <c:when test="${currentPage < totalPages}">
                        <a href="${pageUrlPrefix}${currentPage + 1}" class="sp-page-btn">
                            <i class="fas fa-chevron-right"></i>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <button class="sp-page-btn" disabled><i class="fas fa-chevron-right"></i></button>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function showLiveToast(message, isSuccess) {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast-custom ${isSuccess ? 'toast-success-style' : 'toast-error-style'}`;
        toast.innerHTML = `<i class="fas ${isSuccess ? 'fa-check-circle' : 'fa-circle-exclamation'}"></i><span>\${message}</span>`;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = "0";
            toast.style.transform = "translateY(-10px)";
            setTimeout(() => toast.remove(), 500);
        }, 2500);
    }

    document.addEventListener('DOMContentLoaded', () => {
        const toast = document.getElementById("toast-msg");
        if (toast) {
            setTimeout(function () {
                toast.style.opacity = "0";
                toast.style.transform = "translateY(-10px)";
                setTimeout(function () { toast.remove(); }, 500);
            }, 3000);
        }

        const couponSwitches = document.querySelectorAll('.js-coupon-status-toggle');
        couponSwitches.forEach(switchInput => {
            switchInput.addEventListener('change', function () {
                const couponId = this.dataset.id;
                const isChecked = this.checked;
                const newStatus = isChecked ? 1 : 0;
                const currentSwitch = this;

                currentSwitch.disabled = true;

                const params = new URLSearchParams();
                params.append('id', couponId);
                params.append('trangThai', newStatus);

                fetch('${pageContext.request.contextPath}/PhieuGiamGia/change-status', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    },
                    body: params
                })
                    .then(response => {
                        if (!response.ok) throw new Error('Cập nhật thất bại');

                        const row = currentSwitch.closest('tr');
                        const statusBadge = row.querySelector('.JS-status-text');

                        if (statusBadge) {
                            if (newStatus === 1) {
                                statusBadge.className = 'category-status JS-status-text status-active';
                                statusBadge.textContent = 'Đang diễn ra';
                            } else {
                                statusBadge.className = 'category-status JS-status-text status-inactive';
                                statusBadge.textContent = 'Ngừng hoạt động';
                            }
                        }
                        showLiveToast('Cập nhật trạng thái thành công!', true);
                    })
                    .catch(error => {
                        showLiveToast('Lỗi cập nhật trạng thái phiếu giảm giá!', false);
                        currentSwitch.checked = !isChecked;
                    })
                    .finally(() => {
                        currentSwitch.disabled = false;
                    });
            });
        });
    });
</script>
</body>
</html>