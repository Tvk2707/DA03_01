<%
    request.setAttribute("pageTitle", "Sản phẩm");
    request.setAttribute("activeSubMenu", "product");
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sản phẩm</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sanpham.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
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

        .modal {
            display: none !important;
            position: fixed !important;
            top: 0 !important;
            left: 0 !important;
            width: 100vw !important;
            height: 100vh !important;
            z-index: 99999 !important;
        }

        .modal.fade.show {
            display: flex !important;
            align-items: center !important;
            justify-content: center !important;
            background: rgba(15, 23, 42, 0.6) !important;
        }

        .modal-dialog {
            margin: auto !important;
            width: 100% !important;
            max-width: 500px !important;
            padding: 16px;
            box-sizing: border-box;
        }

        .modal-dialog-centered {
            display: flex !important;
            align-items: center !important;
            justify-content: center !important;
        }
    </style>
</head>
<body>
<%@include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@include file="../layout/header.jsp" %>

    <div class="category-section">
        <div class="category-header">
            <h2 class="category-title">Sản Phẩm</h2>
            <a href="${pageContext.request.contextPath}/SanPham/new" class="add-new-btn" style="text-decoration: none; display: inline-flex; align-items: center; gap: 6px;">
                <i class="fas fa-plus"></i> Thêm mới
            </a>
        </div>

        <c:if test="${not empty error}">
            <div style="background:#fdecea;color:#b3261e;padding:12px 16px;border-radius:8px;margin-bottom:16px;">
                <i class="fas fa-circle-exclamation"></i> ${error}
            </div>
        </c:if>

        <c:if test="${not empty param.success}">
            <div style="background:#e8f5e9;color:#2e7d32;padding:12px 16px;border-radius:8px;margin-bottom:16px;">
                <i class="fas fa-check-circle"></i> ${param.success}
            </div>
        </c:if>

        <div class="filter-section">
            <div class="filter-header">
                <div class="filter-title">
                    <i class="fas fa-filter"></i>
                    Bộ lọc tìm kiếm
                </div>
                <div class="filter-actions">
                    <a href="${pageContext.request.contextPath}/SanPham/export" class="btn-export">
                        <i class="fas fa-file-excel"></i> Xuất Excel
                    </a>
                    <button type="button" class="btn-reset" onclick="resetFilters()">
                        <i class="fas fa-undo"></i> Đặt lại
                    </button>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/SanPham/search" method="post" id="filterForm">
                <div class="filter-grid">
                    <div class="filter-group">
                        <label class="filter-label">Tìm kiếm</label>
                        <input type="text"
                               name="tenSanPham"
                               value="${searchTenSanPham}"
                               class="filter-input"
                               placeholder="Nhập mã hóa đơn / tên khách / SĐT...">
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Danh mục</label>
                        <select name="danhMucId" class="filter-select">
                            <option value="">Tất cả</option>
                            <c:forEach var="dm" items="${danhMucList}">
                                <option value="${dm.id}" ${dm.id == searchDanhMucId ? 'selected' : ''}>
                                        ${dm.tenDanhMuc}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Thương hiệu</label>
                        <select name="thuongHieuId" class="filter-select">
                            <option value="">Tất cả</option>
                            <c:forEach var="th" items="${thuongHieuList}">
                                <option value="${th.id}" ${th.id == searchThuongHieuId ? 'selected' : ''}>
                                        ${th.tenThuongHieu}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Trạng thái</label>
                        <select name="trangThai" class="filter-select">
                            <option value="">Tất cả</option>
                            <option value="1" ${'1' == searchTrangThai ? 'selected' : ''}>Hoạt động</option>
                            <option value="0" ${'0' == searchTrangThai ? 'selected' : ''}>Ngừng bán</option>
                        </select>
                    </div>
                </div>

                <div style="display: flex; gap: 16px; margin-top: 16px;">
                    <button type="submit" class="add-new-btn" style="padding: 10px 24px;">
                        <i class="fas fa-search"></i> Tìm kiếm
                    </button>
                    <c:if test="${not empty searchTenSanPham || not empty searchDanhMucId || not empty searchThuongHieuId}">
                        <a href="${pageContext.request.contextPath}/SanPham"
                           style="padding: 10px 24px; background: #f3f4f6; color: #6b7280; border-radius: 8px; text-decoration: none; display: inline-flex; align-items: center; gap: 6px;">
                            <i class="fas fa-times"></i> Xóa bộ lọc
                        </a>
                    </c:if>
                </div>
            </form>
        </div>

        <table class="category-table">
            <thead>
            <tr>
                <th>STT</th>
                <th>Mã SP</th>
                <th>Tên sản phẩm</th>
                <th>Danh mục</th>
                <th>Thương hiệu</th>
                <th>Chất liệu</th>
                <th>Kiểu dáng</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="temp" items="${items}" varStatus="status">
                <tr class="product-row">
                    <td><span class="category-id">${status.count + (currentPage - 1) * 10}</span></td>
                    <td><span class="category-code">${temp.maSanPham}</span></td>
                    <td><span class="category-name">${temp.tenSanPham}</span></td>
                    <td><span class="category-name">${temp.danhMuc.tenDanhMuc}</span></td>
                    <td><span class="category-name">${temp.thuongHieu.tenThuongHieu}</span></td>
                    <td><span class="category-name">${temp.chatLieu.tenChatLieu}</span></td>
                    <td><span class="category-name">${temp.kieuDang.tenKieuDang}</span></td>
                    <td>
                        <span class="category-status ${temp.trangThai == 1 ? 'status-active' : 'status-inactive'}">
                                ${temp.trangThai == 1 ? "Hoạt động" : "Ngừng bán"}
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons">
                            <a href="${pageContext.request.contextPath}/SanPhamChiTiet?sanPhamId=${temp.id}" class="btn-icon-circle btn-view" title="Xem biến thể">
                                <i class="fas fa-eye"></i>
                            </a>
                            <form action="${pageContext.request.contextPath}/SanPham/delete" method="post" style="display:inline;">
                                <input type="hidden" name="id" value="${temp.id}">
                                <button type="button" class="btn-icon-circle btn-delete" title="Xóa" onclick="confirmDelete(this)">
                                    <i class="fas fa-trash-alt"></i>
                                </button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty items}">
                <tr>
                    <td colspan="9" style="text-align: center; padding: 30px; color: #888;">
                        <i class="fas fa-inbox" style="font-size: 24px; margin-bottom: 10px; display: block;"></i>
                        Không tìm thấy dữ liệu nào.
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

        <div class="sp-pagination">
            <c:if test="${totalPages > 1}">
                <%-- Nut Trang truoc --%>
                <c:choose>
                    <c:when test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/SanPham?page=${currentPage - 1}" class="sp-page-btn">
                            <i class="fas fa-chevron-left"></i>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <button class="sp-page-btn" disabled><i class="fas fa-chevron-left"></i></button>
                    </c:otherwise>
                </c:choose>

                <%-- Cac so trang --%>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="${pageContext.request.contextPath}/SanPham?page=${i}"
                       class="sp-page-btn ${currentPage == i ? 'active' : ''}">
                            ${i}
                    </a>
                </c:forEach>

                <%-- Nut Trang sau --%>
                <c:choose>
                    <c:when test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/SanPham?page=${currentPage + 1}" class="sp-page-btn">
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

<div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" style="border-radius: 12px; border: none; box-shadow: 0 10px 25px rgba(0,0,0,0.25); width: 100%;">
            <div class="modal-header" style="background: #fff5f5; border-bottom: 1px solid #fee2e2; padding: 16px 20px;">
                <h5 class="modal-title text-danger" id="deleteModalLabel" style="font-weight: 700; font-size: 16px; margin: 0; display: flex; align-items: center; gap: 8px;">
                    <i class="fas fa-exclamation-triangle"></i> Xác Nhận Xóa
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Dong" style="background: none; border: none; font-size: 22px; cursor: pointer; color: #94a3b8; line-height: 1;">&times;</button>
            </div>
            <div class="modal-body" style="padding: 24px; font-size: 15px; color: #4b5563; line-height: 1.6;">
                Bạn có chắc chắn muốn xóa sản phẩm này không?<br>Hành động này sẽ không thể hoàn tác và dữ liệu sẽ bị mất vĩnh viễn.
            </div>
            <div class="modal-footer" style="background: #f9fafb; border-top: 1px solid #f3f4f6; padding: 14px 20px; display: flex; justify-content: flex-end; gap: 12px;">
                <button type="button" class="btn-reset" data-bs-dismiss="modal" style="padding: 8px 20px; font-weight: 600; cursor: pointer; border-radius: 6px;">Hủy Bỏ</button>
                <button type="button" class="add-new-btn" id="confirmDeleteBtn" style="background: #dc2626; padding: 8px 20px; font-weight: 600; cursor: pointer; border-radius: 6px;">Đồng Ý Xóa</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    function resetFilters() {
        document.getElementById('filterForm').reset();
        window.location.href = '${pageContext.request.contextPath}/SanPham';
    }

    let activeFormToDelete = null;

    function confirmDelete(button) {
        activeFormToDelete = button.closest('form');

        const modalEl = document.getElementById('deleteConfirmModal');
        if (typeof bootstrap !== 'undefined') {
            const bsModal = new bootstrap.Modal(modalEl);
            bsModal.show();
        } else {
            modalEl.classList.add('show');
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        const modalEl = document.getElementById('deleteConfirmModal');

        document.getElementById('confirmDeleteBtn').addEventListener('click', function () {
            if (activeFormToDelete) {
                activeFormToDelete.submit();
            }
        });

        modalEl.querySelectorAll('[data-bs-dismiss="modal"], .btn-close').forEach(btn => {
            btn.addEventListener('click', function() {
                if (typeof bootstrap !== 'undefined') {
                    const bsModal = bootstrap.Modal.getInstance(modalEl);
                    if (bsModal) bsModal.hide();
                }
                modalEl.classList.remove('show');
            });
        });
    });
</script>
</body>
</html>