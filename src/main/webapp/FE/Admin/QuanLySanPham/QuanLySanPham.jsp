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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/sanpham.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<%@include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@include file="../layout/header.jsp" %>

    <div class="category-section">
        <div class="category-header">
            <h2 class="category-title">Sản Phẩm</h2>
            <!-- SỬA ĐỔI: Chuyển sang thẻ a và gọi đúng đường dẫn /SanPham/new của Servlet cũ -->
            <a href="${pageContext.request.contextPath}/SanPham/new" class="add-new-btn" style="text-decoration: none; display: inline-flex; align-items: center; gap: 6px;">
                <i class="fas fa-plus"></i> Thêm mới
            </a>
        </div>

        <c:if test="${not empty error}">
            <div style="background:#fdecea;color:#b3261e;padding:12px 16px;border-radius:8px;margin-bottom:16px;">
                <i class="fas fa-circle-exclamation"></i> ${error}
            </div>
        </c:if>

        <!-- Filter Section -->
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
                <tr>
                    <td><span class="category-id">${status.index + 1 + (currentPage - 1) * 10}</span></td>
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
                            <!-- SỬA ĐỔI: Chuyển sang thẻ a và gọi đúng đường dẫn /SanPham/edit kèm tham số id cũ của Servlet -->
                            <a href="${pageContext.request.contextPath}/SanPham/edit?id=${temp.id}"
                               class="action-btn edit-btn" title="Sửa"
                               style="text-decoration: none; display: inline-flex; align-items: center; justify-content: center;">
                                <i class="fas fa-edit"></i>
                            </a>

                            <form action="${pageContext.request.contextPath}/SanPham/delete" method="post"
                                  style="display:inline;">
                                <input type="hidden" name="id" value="${temp.id}">
                                <button type="submit" class="action-btn delete-btn" title="Xóa"
                                        onclick="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này?');">
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

        <c:if test="${totalPages > 1}">
            <div class="pagination" style="display:flex;gap:6px;justify-content:center;margin-top:20px;">
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <a href="${pageContext.request.contextPath}/SanPham?page=${p}"
                       style="padding:6px 12px;border-radius:6px;border:1px solid #ddd;text-decoration:none;
                               color:${p == currentPage ? '#fff' : '#333'};
                               background:${p == currentPage ? '#4a6cf7' : '#fff'};">
                            ${p}
                    </a>
                </c:forEach>
            </div>
        </c:if>
    </div>
</div>

<script>
    // --- RESET FILTERS ---
    function resetFilters() {
        document.getElementById('filterForm').reset();
        window.location.href = '${pageContext.request.contextPath}/SanPham';
    }
</script>
</body>
</html>
