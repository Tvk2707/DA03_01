<%
    request.setAttribute("pageTitle", "Biến thể sản phẩm");
    request.setAttribute("activeSubMenu", "product");
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Biến thể sản phẩm</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sanpham.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<%@include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@include file="../layout/header.jsp" %>

    <div class="category-section">
        <div class="category-header">
            <h2 class="category-title">Biến Thể Sản Phẩm</h2>
        </div>

        <c:if test="${not empty error}">
            <div style="background:#fdecea;color:#b3261e;padding:12px 16px;border-radius:8px;margin-bottom:16px;">
                <i class="fas fa-circle-exclamation"></i> ${error}
            </div>
        </c:if>

        <div class="filter-section">
            <div class="filter-header">
                <div class="filter-title">
                    <i class="fas fa-filter"></i>
                    Bộ lọc tìm kiếm
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/SanPhamChiTiet" method="get" id="filterForm">
                <input type="hidden" name="sanPhamId" value="${sanPhamId}">

                <div class="filter-grid">
                    <div class="filter-group">
                        <label class="filter-label">Tìm kiếm</label>
                        <input type="text"
                               name="ma"
                               value="${searchMa}"
                               class="filter-input"
                               placeholder="Nhập mã biến thể...">
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Màu sắc</label>
                        <select name="mauSacId" class="filter-select">
                            <option value="">Tất cả</option>
                            <c:forEach var="ms" items="${mauSacList}">
                                <option value="${ms.id}" ${ms.id == searchMauSacId ? 'selected' : ''}>
                                        ${ms.tenMau}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Kích cỡ</label>
                        <select name="kichCoId" class="filter-select">
                            <option value="">Tất cả</option>
                            <c:forEach var="kc" items="${kichCoList}">
                                <option value="${kc.id}" ${kc.id == searchKichCoId ? 'selected' : ''}>
                                        ${kc.tenKichCo}
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
                </div>
            </form>
        </div>

        <table class="category-table">
            <thead>
            <tr>
                <th>STT</th>
                <th>Ảnh</th>
                <th>Mã biến thể</th>
                <th>Màu sắc</th>
                <th>Kích cỡ</th>
                <th>Giá nhập</th>
                <th>Giá bán</th>
                <th>Tồn kho</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="temp" items="${items}" varStatus="status">
                <tr>
                    <td><span class="category-id">${status.count}</span></td>

                    <!-- THẺ HIỂN THỊ ẢNH ĐƯỢC CẬP NHẬT CHỐNG GIẬT MÀN HÌNH -->
                    <td>
                        <c:choose>
                            <c:when test="${not empty temp.hinhAnh}">
                                <img src="${pageContext.request.contextPath}/File_Anh/images/${temp.hinhAnh}"
                                     alt="Ảnh biến thể"
                                     style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; border: 1px solid #ddd;"
                                     onerror="loadCachedImage('${temp.hinhAnh}', this)"/>
                            </c:when>
                            <c:otherwise>
                                <img src="https://images.placeholders.dev/?width=50&height=50&text=No+Image&bgColor=%23e9ecef&textColor=%236c757d"
                                     alt="No image"
                                     style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; opacity: 0.6;"/>
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <td><span class="category-code">${temp.ma}</span></td>
                    <td><span class="category-name">${temp.mauSac.tenMau}</span></td>
                    <td><span class="category-name">${temp.kichCo.tenKichCo}</span></td>
                    <td><span class="category-name"><fmt:formatNumber value="${temp.giaNhap}" pattern="#,##0"/> đ</span></td>
                    <td><span class="category-name"><fmt:formatNumber value="${temp.giaBan}" pattern="#,##0"/> đ</span></td>
                    <td><span class="category-name">${temp.soLuongTon}</span></td>
                    <td>
                        <span class="category-status ${temp.trangThai == 1 ? 'status-active' : 'status-inactive'}">
                                ${temp.trangThai == 1 ? "Hoạt động" : "Ngừng bán"}
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons">
                            <form action="${pageContext.request.contextPath}/SanPhamChiTiet/delete" method="post"
                                  style="display:inline;">
                                <input type="hidden" name="id" value="${temp.id}">
                                <input type="hidden" name="sanPhamId" value="${temp.sanPham.id}">
                                <button type="submit" class="action-btn delete-btn" title="Xóa"
                                        onclick="return confirm('Bạn có chắc chắn muốn xóa biến thể này?');">
                                    <i class="fas fa-trash-alt"></i>
                                </button>
                            </form>
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

        <c:if test="${totalPages > 1}">
            <div class="pagination" style="display:flex;gap:6px;justify-content:center;margin-top:20px;">
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <a href="${pageContext.request.contextPath}/SanPhamChiTiet?sanPhamId=${sanPhamId}&page=${p}"
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
    // --- FIX LỖI VÒNG LẶP VÔ HẠN (CHỐNG GIẬT HÌNH) ---
    function loadCachedImage(fileName, imgElement) {
        imgElement.onerror = null; // Huỷ bỏ bắt lỗi ngay lập tức để chặn đứng vòng lặp vô hạn gây nhấp nháy

        if (fileName) {
            const cachedData = localStorage.getItem('img_' + fileName);
            if (cachedData) {
                imgElement.src = cachedData;
                return;
            }
        }
        // Link ảnh CDN dự phòng an toàn tuyệt đối
        imgElement.src = "https://images.placeholders.dev/?width=50&height=50&text=No+Image&bgColor=%23e9ecef&textColor=%236c757d";
    }
</script>
</body>
</html>