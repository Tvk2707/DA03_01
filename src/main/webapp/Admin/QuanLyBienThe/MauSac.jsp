<%
    request.setAttribute("pageTitle", "Màu Sắc");
    request.setAttribute("activeSubMenu", "color");
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh mục</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<%@include file="../layout/sidebar.jsp"%>
<div class="dashboard-container">
    <%@include file="../layout/header.jsp"%>

    <div class="category-section">
        <div class="category-header">
            <h2 class="category-title">Màu Sắc</h2>
            <button class="add-new-btn" onclick="openAddModal()">
                <i class="fas fa-plus"></i> Thêm mới
            </button>
        </div>

        <div class="search-card">
            <form action="${pageContext.request.contextPath}/MauSac" method="get" class="search-form">
                <div class="search-input-wrapper">
                    <i class="fas fa-search search-icon"></i>
                    <input type="text" name="keyword" value="${keyword}" placeholder="Tìm kiếm danh mục...">
                </div>
                <button type="submit" class="search-btn">
                    <i class="fas fa-search"></i> Tìm kiếm
                </button>
                <c:if test="${not empty keyword}">
                    <a href="${pageContext.request.contextPath}/MauSac" class="clear-btn">
                        <i class="fas fa-times"></i> Xóa bộ lọc
                    </a>
                </c:if>
            </form>
        </div>

        <table class="category-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>Mã màu</th>
                <th>Tên màu</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="temp" items="${items}">
                <tr>
                    <td><span class="category-id">#${temp.id}</span></td>
                    <td><span class="category-code">${temp.maMau}</span></td>
                    <td><span class="category-name">${temp.tenMau}</span></td>
                    <td><span class="category-status ${temp.trangThai == 1 ? 'status-active' : 'status-inactive'}">${temp.trangThai == 1 ? "Hoạt động" : "Không hoạt động"}</span></td>
                    <td>
                        <div class="action-buttons">
                            <button type="button" class="action-btn edit-btn" title="Sửa"
                                    onclick="openEditModal('${temp.id}', '${temp.maMau}', '${temp.tenMau}', '${temp.trangThai}')">
                                <i class="fas fa-edit"></i>
                            </button>
                            <form action="${pageContext.request.contextPath}/MauSac/delete?id=${temp.id}" method="post" style="display:inline;">
                                <input type="hidden" name="id" value="${temp.id}">
                                <button type="submit" class="action-btn delete-btn" title="Xóa" onclick="return confirm('Bạn có chắc chắn muốn xóa mục này?');">
                                    <i class="fas fa-trash-alt"></i>
                                </button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty items}">
                <tr>
                    <td colspan="5" style="text-align: center; padding: 30px; color: #888;">
                        <i class="fas fa-inbox" style="font-size: 24px; margin-bottom: 10px; display: block;"></i>
                        Không tìm thấy dữ liệu nào.
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<div id="addCategoryModal" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Thêm Mới Danh Mục</h3>
            <span class="close-btn" onclick="closeAddModal()">&times;</span>
        </div>
        <form action="${pageContext.request.contextPath}/MauSac/insert" method="post">
            <div class="modal-body">
                <div class="form-group">
                    <label for="maMau">Mã màu</label>
                    <input type="text" id="maMau" name="maMau" placeholder="Nhập mã màu (ví dụ: MS006)" required>
                </div>

                <div class="form-group">
                    <label for="tenMau">Tên màu</label>
                    <input type="text" id="tenMau" name="tenMau" placeholder="Nhập tên màu" required>
                </div>

                <div class="form-group">
                    <label for="trangthai">Trạng thái</label>
                    <select id="trangthai" name="trangThai">
                        <option value="1">Hoạt động</option>
                        <option value="0">Không hoạt động</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-cancel" onclick="closeAddModal()">Hủy bỏ</button>
                <button type="submit" class="btn-submit">Lưu lại</button>
            </div>
        </form>
    </div>
</div>

<div id="editCategoryModal" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Chỉnh Sửa Danh Mục</h3>
            <span class="close-btn" onclick="closeEditModal()">&times;</span>
        </div>
        <form action="${pageContext.request.contextPath}/MauSac/update" method="post">
            <div class="modal-body">
                <input type="hidden" id="editId" name="id">

                <div class="form-group">
                    <label for="editMaMau">Mã màu</label>
                    <input type="text" id="editMaMau" name="maMau" value="${mauSac.maMau}"  placeholder="Nhập mã màu" required>
                </div>

                <div class="form-group">
                    <label for="editTenMau">Tên màu</label>
                    <input type="text" id="editTenMau" name="tenMau" value="${mauSac.tenMau}" placeholder="Nhập tên màu" required>
                </div>

                <div class="form-group">
                    <label for="editTrangThai">Trạng thái</label>
                    <select id="editTrangThai" name="trangThai">
                        <option value="1" ${mauSac.trangThai == 1 ? 'selected' : ''}>Hoạt động</option>
                        <option value="0" ${mauSac.trangThai == 0 ? 'selected' : ''}>Không hoạt động</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-cancel" onclick="closeEditModal()">Hủy bỏ</button>
                <button type="submit" class="btn-submit">Cập nhật</button>
            </div>
        </form>
    </div>
</div>

<script>
    // --- XỬ LÝ MODAL THÊM MỚI ---
    function openAddModal() {
        document.getElementById("addCategoryModal").style.display = "flex";
    }

    function closeAddModal() {
        document.getElementById("addCategoryModal").style.display = "none";
    }

    // --- XỬ LÝ MODAL SỬA ---
    function openEditModal(id, maMau, tenMau, trangThai) {
        // Đổ dữ liệu từ hàng được click vào các ô input trong Modal Sửa
        document.getElementById("editId").value = id;
        document.getElementById("editMaMau").value = maMau;
        document.getElementById("editTenMau").value = tenMau;
        document.getElementById("editTrangThai").value = trangThai;

        // Hiển thị modal sửa lên màn hình
        document.getElementById("editCategoryModal").style.display = "flex";
    }

    function closeEditModal() {
        document.getElementById("editCategoryModal").style.display = "none";
    }

    // --- ĐÓNG MODAL KHI CLICK RA NGOÀI VÙNG NỀN TỐI ---
    window.onclick = function(event) {
        let addModal = document.getElementById("addCategoryModal");
        let editModal = document.getElementById("editCategoryModal");

        if (event.target == addModal) {
            addModal.style.display = "none";
        }
        if (event.target == editModal) {
            editModal.style.display = "none";
        }
    }
</script>
</body>
</html>