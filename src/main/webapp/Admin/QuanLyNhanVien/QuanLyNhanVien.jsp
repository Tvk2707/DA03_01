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
        /* 🛠️ LAYOUT & GIAO DIỆN QUẢN LÝ NHÂN VIÊN                   */
        /* ========================================================== */
        body {
            background-color: #f8fafc;
            margin: 0;
            padding: 0;
        }

        .dashboard-container {
            margin-left: 280px !important;
            padding: 0 !important;
            width: calc(100% - 280px) !important;
            max-width: none !important;
            min-height: 100vh;
            box-sizing: border-box;
            transition: all 0.3s ease;
        }

        /* 🎨 BỘ LỌC TÌM KIẾM */
        .filter-section {
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            padding: 20px 24px;
            margin-bottom: 24px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        }

        .filter-header {
            margin-bottom: 14px;
            padding-bottom: 12px;
            border-bottom: 1px solid #f3f4f6;
        }

        .filter-title {
            font-size: 15px;
            font-weight: 700;
            color: #1f2937;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .filter-title i {
            color: #b4975a;
        }

        /* Bộ lọc 1 cột full-width */
        .filter-search-row {
            display: flex;
            gap: 12px;
            align-items: center;
        }

        .filter-input-wrap {
            flex: 1;
            position: relative;
        }

        .filter-input-wrap i {
            position: absolute;
            left: 14px;
            top: 50%;
            transform: translateY(-50%);
            color: #9ca3af;
            font-size: 14px;
            pointer-events: none;
        }

        .filter-input {
            width: 100%;
            height: 42px;
            padding: 8px 14px 8px 40px;
            font-size: 14px;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            background-color: #ffffff;
            color: #1f2937;
            box-sizing: border-box;
            transition: all 0.2s ease;
        }

        .filter-input:focus {
            border-color: #b4975a !important;
            outline: none;
            box-shadow: 0 0 0 3px rgba(180, 151, 90, 0.15);
        }

        .filter-input::placeholder {
            color: #9ca3af;
        }

        /* Nút bấm chuẩn */
        .btn-primary {
            background-color: #b4975a;
            color: #ffffff;
            border: none;
            border-radius: 8px;
            padding: 0 22px;
            height: 42px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 7px;
            transition: background-color 0.2s ease;
            white-space: nowrap;
        }

        .btn-primary:hover {
            background-color: #9a8048;
        }

        .btn-outline {
            background: #ffffff;
            color: #4b5563;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            padding: 0 18px;
            height: 42px;
            display: inline-flex;
            align-items: center;
            gap: 7px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s ease;
            white-space: nowrap;
        }

        .btn-outline:hover {
            background: #f9fafb;
            border-color: #9ca3af;
            color: #1f2937;
        }

        .btn-clear {
            background: #fee2e2;
            color: #dc2626;
            border: none;
            border-radius: 8px;
            padding: 0 16px;
            height: 42px;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s ease;
        }

        .btn-clear:hover {
            background: #fecaca;
        }

        /* Toolbar Sticky */
        .table-toolbar {
            position: sticky;
            top: 70px;
            background: #ffffff;
            z-index: 90;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 14px 18px;
            margin-top: 24px;
            border: 1px solid #e5e7eb;
            border-bottom: none;
            border-radius: 8px 8px 0 0;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
        }

        .toolbar-count {
            font-size: 14px;
            font-weight: 500;
            color: #4b5563;
        }

        .toolbar-count span {
            font-weight: 700;
            color: #1f2937;
        }

        .toolbar-actions {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .category-table {
            margin-top: 0 !important;
            border-top-left-radius: 0 !important;
            border-top-right-radius: 0 !important;
            width: 100%;
        }

        /* Toast thông báo */
        .toast-custom {
            position: fixed;
            top: 24px;
            right: 24px;
            padding: 14px 22px;
            border-radius: 10px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
            z-index: 999999;
            display: flex;
            align-items: center;
            gap: 12px;
            font-weight: 600;
            font-size: 14px;
            min-width: 280px;
            animation: slideInToast 0.35s ease;
        }

        @keyframes slideInToast {
            from { transform: translateX(100px); opacity: 0; }
            to   { transform: translateX(0);    opacity: 1; }
        }

        .toast-success {
            background-color: #f0fdf4;
            color: #15803d;
            border: 1px solid #bbf7d0;
        }

        .toast-error {
            background-color: #fef2f2;
            color: #b91c1c;
            border: 1px solid #fecaca;
        }

        /* Modal xác nhận xóa */
        .modal-overlay {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.45);
            z-index: 99999;
            align-items: center;
            justify-content: center;
        }

        .modal-overlay.show {
            display: flex;
        }

        .modal-box {
            background: #fff;
            border-radius: 14px;
            padding: 32px 28px 24px;
            max-width: 400px;
            width: 90%;
            box-shadow: 0 20px 60px rgba(0,0,0,0.2);
            text-align: center;
            animation: modalPop 0.25s ease;
        }

        @keyframes modalPop {
            from { transform: scale(0.88); opacity: 0; }
            to   { transform: scale(1);    opacity: 1; }
        }

        .modal-icon {
            width: 58px;
            height: 58px;
            border-radius: 50%;
            background: #fef2f2;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 16px;
        }

        .modal-icon i {
            font-size: 26px;
            color: #dc2626;
        }

        .modal-title {
            font-size: 18px;
            font-weight: 700;
            color: #1f2937;
            margin-bottom: 8px;
        }

        .modal-desc {
            font-size: 14px;
            color: #6b7280;
            margin-bottom: 24px;
            line-height: 1.6;
        }

        .modal-actions {
            display: flex;
            gap: 10px;
            justify-content: center;
        }

        .btn-modal-cancel {
            flex: 1;
            padding: 11px 0;
            border-radius: 8px;
            font-weight: 600;
            font-size: 14px;
            border: 1px solid #d1d5db;
            background: #fff;
            color: #4b5563;
            cursor: pointer;
            transition: all 0.2s;
        }

        .btn-modal-cancel:hover {
            background: #f9fafb;
        }

        .btn-modal-confirm {
            flex: 1;
            padding: 11px 0;
            border-radius: 8px;
            font-weight: 600;
            font-size: 14px;
            border: none;
            background: #dc2626;
            color: #fff;
            cursor: pointer;
            transition: all 0.2s;
        }

        .btn-modal-confirm:hover {
            background: #b91c1c;
        }

        /* Row fade out khi xóa */
        tr.fade-out {
            transition: opacity 0.4s ease, transform 0.4s ease;
            opacity: 0;
            transform: translateX(20px);
        }

        /* Responsive */
        @media (max-width: 992px) {
            .dashboard-container {
                margin-left: 0 !important;
                padding: 16px !important;
            }
        }

        /* ========================================================== */
        /* 🖨️ CSS DÀNH CHO IN ẤN                                      */
        /* ========================================================== */
        .print-only { display: none; }

        @media print {
            /* Ẩn toàn bộ phần không cần in */
            .sidebar,
            nav,
            header,
            .filter-section,
            .table-toolbar,
            .sp-pagination,
            .modal-overlay,
            .toast-custom,
            .no-print { display: none !important; }

            /* Hiện phần chỉ dành cho in */
            .print-only { display: block !important; }

            /* Reset layout — bỏ margin sidebar */
            body {
                background: #fff !important;
                margin: 0 !important;
                padding: 0 !important;
            }

            .dashboard-container {
                margin-left: 0 !important;
                padding: 0 !important;
                width: 100% !important;
            }

            .category-section {
                padding: 0 !important;
                margin: 0 !important;
            }

            /* Bảng full-width, font rõ ràng */
            .category-table {
                width: 100% !important;
                font-size: 10.5pt !important;
                border-collapse: collapse !important;
            }

            .category-table th,
            .category-table td {
                border: 1px solid #333 !important;
                padding: 6px 8px !important;
                color: #000 !important;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }

            .category-table thead tr {
                background-color: #b4975a !important;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }

            .category-table thead th {
                color: #fff !important;
                font-weight: bold !important;
            }

            /* Mã nhân viên màu đen thay vì vàng */
            .category-table strong[style*="b4975a"] {
                color: #000 !important;
            }

            /* Không tô màu xen kẽ */
            .category-table tbody tr:nth-child(even) {
                background-color: #f5f5f5 !important;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }

            /* Tránh ngắt dòng giữa chừng một hàng */
            .category-table tbody tr {
                page-break-inside: avoid;
            }

            /* Badge trạng thái */
            .status-active {
                background-color: #d1fae5 !important;
                color: #065f46 !important;
                padding: 2px 8px;
                border-radius: 4px;
                font-size: 9pt;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }

            /* Tiêu đề in */
            .print-header {
                display: block !important;
                text-align: center;
                margin-bottom: 16px;
                padding-bottom: 12px;
                border-bottom: 2px solid #b4975a;
            }

            .print-header h1 {
                font-size: 16pt;
                font-weight: bold;
                color: #1f2937;
                margin: 0 0 4px 0;
            }

            .print-header p {
                font-size: 10pt;
                color: #555;
                margin: 2px 0;
            }

            /* Footer in */
            .print-footer {
                display: block !important;
                text-align: center;
                margin-top: 16px;
                padding-top: 8px;
                border-top: 1px solid #ccc;
                font-size: 9pt;
                color: #666;
            }

            /* In theo chiều ngang A4 */
            @page {
                size: A4 landscape;
                margin: 15mm 10mm;
            }
        }
    </style>
</head>
<body>
<%@ include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@ include file="../layout/header.jsp" %>

    <div class="category-section">

        <!-- TIÊU ĐỀ CHỈ HIỆN KHI IN -->
        <div class="print-header print-only">
            <h1>DANH SÁCH NHÂN VIÊN</h1>
            <p>Ngày in: <strong id="printDate"></strong> &nbsp;|&nbsp; Tổng số: <strong id="printCount">${items.size()}</strong> nhân viên</p>
        </div>

        <div class="category-header">
            <h2 class="category-title">Quản lý nhân viên</h2>
        </div>

        <!-- BỘ LỌC TÌM KIẾM (1 ô, full-width) -->
        <div class="filter-section">
            <div class="filter-header">
                <div class="filter-title">
                    <i class="fas fa-search"></i>
                    Tìm kiếm nhân viên
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/NhanVien/search" method="post" id="filterForm">
                <div class="filter-search-row">
                    <div class="filter-input-wrap">
                        <i class="fas fa-user-tie"></i>
                        <input type="text" name="tuKhoa" value="${tuKhoa}" class="filter-input"
                               placeholder="Tìm theo tên, mã nhân viên, email...">
                    </div>

                    <button type="submit" class="btn-primary">
                        <i class="fas fa-search"></i> Tìm kiếm
                    </button>

                    <c:if test="${not empty tuKhoa}">
                        <a href="${pageContext.request.contextPath}/NhanVien" class="btn-clear">
                            <i class="fas fa-xmark"></i> Xóa bộ lọc
                        </a>
                    </c:if>
                </div>
            </form>
        </div>

        <!-- TOOLBAR BẢNG -->
        <div class="table-toolbar">
            <div class="toolbar-count" id="countLabel">
                Hiển thị <span id="countNum">${items.size()}</span> nhân viên
            </div>
            <div class="toolbar-actions">
                <a href="#" class="btn-outline" title="In danh sách" onclick="window.print(); return false;">
                    <i class="fas fa-print"></i> In
                </a>
                <a href="${pageContext.request.contextPath}/NhanVien/export"
                   class="btn-outline" title="Xuất Excel" target="_blank">
                    <i class="fas fa-file-excel" style="color:#217346;"></i> Xuất Excel
                </a>
                <a href="${pageContext.request.contextPath}/NhanVien/new" class="btn-primary"
                   style="text-decoration: none;">
                    <i class="fas fa-plus"></i> Thêm mới
                </a>
            </div>
        </div>

        <!-- BẢNG DỮ LIỆU -->
        <table class="category-table" id="nhanVienTable">
            <thead>
            <tr>
                <th>STT</th>
                <th>NHÂN VIÊN</th>
                <th>CHỨC VỤ</th>
                <th>GIỚI TÍNH</th>
                <th>LIÊN HỆ</th>
                <th>ĐỊA CHỈ</th>
                <th>TRẠNG THÁI</th>
                <th class="no-print">THAO TÁC</th>
            </tr>
            </thead>
            <tbody id="nhanVienBody">
            <c:forEach var="nv" items="${items}" varStatus="status">
                <tr id="row-${nv.id}">
                    <td><span class="category-id">${status.index + 1 + (currentPage - 1) * 10}</span></td>

                    <%-- Nhân viên: Mã + Tên --%>
                    <td>
                        <strong style="color: #b4975a; display: block;">${nv.maNhanVien}</strong>
                        <strong style="color: #1f2937;">${nv.hoTen}</strong>
                    </td>
                    <td title="${nv.chucVu}">${nv.chucVu}</td>
                    <td>${nv.gioiTinh == 1 ? 'Nam' : 'Nữ'}</td>

                    <%-- Liên hệ: SĐT + Email --%>
                    <td>
                        <c:if test="${not empty nv.soDienThoai}">
                            <div style="display:flex; align-items:center; gap:4px; white-space:nowrap;">
                                <i class="fas fa-phone" style="color:#9ca3af; font-size:12px; flex-shrink:0;"></i>
                                <span>${nv.soDienThoai}</span>
                            </div>
                        </c:if>
                        <c:if test="${not empty nv.email}">
                            <div style="display:flex; align-items:center; gap:4px; margin-top:4px;">
                                <i class="fas fa-envelope" style="color:#9ca3af; font-size:12px; flex-shrink:0;"></i>
                                <span>${nv.email}</span>
                            </div>
                        </c:if>
                    </td>
                    <td title="${nv.diaChi}">${nv.diaChi}</td>
                    <td>
                        <span class="category-status ${nv.trangThai == 1 ? 'status-active' : 'status-inactive'}">
                            ${nv.trangThai == 1 ? 'Đang hoạt động' : 'Ngừng hoạt động'}
                        </span>
                    </td>
                    <td class="no-print">
                        <div class="action-buttons" style="align-items: center; gap: 10px;">
                            <a href="${pageContext.request.contextPath}/NhanVien/edit?id=${nv.id}"
                               class="btn-icon-circle btn-view" title="Chỉnh sửa">
                                <i class="fas fa-pen"></i>
                            </a>
                            <button type="button"
                                    class="btn-icon-circle"
                                    title="Xóa"
                                    style="border:none; cursor:pointer; background:transparent;"
                                    onclick="openDeleteModal(${nv.id}, '${nv.hoTen}')">
                                <i class="fas fa-trash-alt" style="color:#dc2626;"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty items}">
                <tr id="emptyRow">
                    <td colspan="8" style="text-align: center; padding: 40px; color: #9ca3af;">
                        <i class="fas fa-users-slash" style="font-size: 28px; margin-bottom: 12px; display: block;"></i>
                        Không tìm thấy nhân viên nào.
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

        <!-- PHÂN TRANG -->
        <c:if test="${totalPages > 1}">
            <div class="sp-pagination">
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <a href="${pageContext.request.contextPath}/NhanVien?page=${p}"
                       class="sp-page-btn ${p == currentPage ? 'active' : ''}">
                        ${p}
                    </a>
                </c:forEach>
            </div>
        </c:if>

        <!-- FOOTER CHỈ HIỆN KHI IN -->
        <div class="print-footer print-only">
            ――――――――――――――――――――――――――――――――――――――――
            <p>Tài liệu này được xuất tự động từ Hệ thống Quản lý &nbsp;|&nbsp; Ngày in: <span id="printDateFooter"></span></p>
        </div>

    </div>
</div>

<!-- ============================================================ -->
<!-- MODAL XÁC NHẬN XÓA                                          -->
<!-- ============================================================ -->
<div class="modal-overlay" id="deleteModal">
    <div class="modal-box">
        <div class="modal-icon">
            <i class="fas fa-trash-alt"></i>
        </div>
        <div class="modal-title">Xác nhận xóa</div>
        <div class="modal-desc" id="modalDesc">
            Bạn có chắc muốn xóa nhân viên này khỏi danh sách?<br>
            Hành động này chỉ ẩn nhân viên, dữ liệu vẫn được bảo toàn.
        </div>
        <div class="modal-actions">
            <button class="btn-modal-cancel" onclick="closeDeleteModal()">
                <i class="fas fa-times"></i> Hủy bỏ
            </button>
            <button class="btn-modal-confirm" id="confirmDeleteBtn">
                <i class="fas fa-trash-alt"></i> Xóa
            </button>
        </div>
    </div>
</div>

<script>
    const CTX = '${pageContext.request.contextPath}';
    let pendingDeleteId = null;
    let pendingDeleteName = '';

    /* Mở modal xác nhận xóa */
    function openDeleteModal(id, hoTen) {
        pendingDeleteId = id;
        pendingDeleteName = hoTen;
        document.getElementById('modalDesc').innerHTML =
            'Bạn có chắc muốn xóa nhân viên <strong>' + hoTen + '</strong> khỏi danh sách?<br>' +
            '<span style="font-size:13px;color:#9ca3af;">Dữ liệu vẫn được bảo toàn trong hệ thống.</span>';
        document.getElementById('deleteModal').classList.add('show');
    }

    /* Đóng modal */
    function closeDeleteModal() {
        document.getElementById('deleteModal').classList.remove('show');
        pendingDeleteId = null;
    }

    /* Đóng modal khi click ngoài */
    document.getElementById('deleteModal').addEventListener('click', function(e) {
        if (e.target === this) closeDeleteModal();
    });

    /* Xác nhận xóa — AJAX */
    document.getElementById('confirmDeleteBtn').addEventListener('click', function () {
        if (!pendingDeleteId) return;

        const id = pendingDeleteId;
        const name = pendingDeleteName;
        closeDeleteModal();

        fetch(CTX + '/NhanVien/delete', {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'id=' + encodeURIComponent(id)
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                // Ẩn dòng trong bảng
                const row = document.getElementById('row-' + id);
                if (row) {
                    row.classList.add('fade-out');
                    setTimeout(() => {
                        row.remove();
                        updateCount();
                    }, 420);
                }
                showToast('success', '✓ Đã xóa nhân viên <strong>' + name + '</strong> khỏi danh sách');
            } else {
                showToast('error', '✕ Lỗi: ' + (data.message || 'Không thể xóa'));
            }
        })
        .catch(() => {
            showToast('error', '✕ Có lỗi xảy ra, vui lòng thử lại');
        });
    });

    /* Cập nhật số đếm + đánh lại STT sau khi ẩn dòng */
    function updateCount() {
        const rows = document.querySelectorAll('#nhanVienBody tr[id^="row-"]');
        document.getElementById('countNum').textContent = rows.length;

        // Đánh lại số thứ tự liên tục
        rows.forEach(function(row, index) {
            const sttCell = row.querySelector('.category-id');
            if (sttCell) {
                sttCell.textContent = index + 1;
            }
        });

        if (rows.length === 0) {
            const tbody = document.getElementById('nhanVienBody');
            tbody.innerHTML = '<tr id="emptyRow"><td colspan="8" style="text-align:center;padding:40px;color:#9ca3af;">' +
                '<i class="fas fa-users-slash" style="font-size:28px;margin-bottom:12px;display:block;"></i>' +
                'Không tìm thấy nhân viên nào.</td></tr>';
        }
    }

    /* Hiện Toast thông báo */
    function showToast(type, msg) {
        const existing = document.querySelectorAll('.toast-custom');
        existing.forEach(t => t.remove());

        const toast = document.createElement('div');
        toast.className = 'toast-custom toast-' + type;
        toast.innerHTML = '<i class="fas ' + (type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation') + '"></i>' +
                          '<span>' + msg + '</span>';
        document.body.appendChild(toast);

        setTimeout(() => {
            toast.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(60px)';
            setTimeout(() => toast.remove(), 420);
        }, 3500);
    }

    /* Điền ngày in vào print-header và print-footer trước khi in */
    window.addEventListener('beforeprint', function () {
        const now = new Date();
        const formatted = now.toLocaleDateString('vi-VN', {
            day: '2-digit', month: '2-digit', year: 'numeric'
        });
        const el1 = document.getElementById('printDate');
        const el2 = document.getElementById('printDateFooter');
        const rows = document.querySelectorAll('#nhanVienBody tr[id^="row-"]');
        const countEl = document.getElementById('printCount');
        if (el1) el1.textContent = formatted;
        if (el2) el2.textContent = formatted;
        if (countEl) countEl.textContent = rows.length;
    });
</script>
</body>
</html>
