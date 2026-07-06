<%
    request.setAttribute("pageTitle", "Gọng kính");
    request.setAttribute("activeSubMenu", "frame");
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gọng kính</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<div>
    <%@include file="../layout/sidebar.jsp"%>
    <div class="dashboard-container">
        <%@include file="../layout/header.jsp"%>

        <div class="category-section">
            <div class="category-header">
                <h2 class="category-title">Gọng kính</h2>
                <button class="add-new-btn" onclick="openAddModal()">
                    <i class="fas fa-plus"></i> Thêm mới
                </button>
            </div>

            <div class="search-card">
                <form action="${pageContext.request.contextPath}/GongKinh" method="get" class="search-form">
                    <div class="search-input-wrapper">
                        <i class="fas fa-search search-icon"></i>
                        <input type="text" name="keyword" value="${keyword}" placeholder="Tìm kiếm gọng kính...">
                    </div>
                    <button type="submit" class="search-btn">
                        <i class="fas fa-search"></i> Tìm kiếm
                    </button>
                    <c:if test="${not empty keyword}">
                        <a href="${pageContext.request.contextPath}/GongKinh" class="clear-btn">
                            <i class="fas fa-times"></i> Xóa bộ lọc
                        </a>
                    </c:if>
                </form>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="error-message" style="color:#c0392b; background:#fdecea; border:1px solid #f5c6cb; padding:10px 15px; border-radius:6px; margin-bottom:15px;">
                    <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                </div>
            </c:if>

            <table class="category-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Hình dáng gọng</th>
                    <th>Kiểu quai kính</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="temp" items="${items}">
                    <tr>
                        <td><span class="category-id">#${temp.id}</span></td>
                        <td><span class="category-code">${temp.hinhDangGong.hinhDang}</span></td>
                        <td><span class="category-name">${temp.kieuQuaiKinh.kieuQuai}</span></td>
                        <td><span class="category-status ${temp.trangThai == 1 ? 'status-active' : 'status-inactive'}">${temp.trangThai == 1 ? "Hoạt động" : "Không hoạt động"}</span></td>
                        <td>
                            <div class="action-buttons">
                                <button type="button" class="action-btn edit-btn" title="Sửa"
                                        onclick="openEditModal('${temp.id}', '${temp.hinhDangGong.id}', '${temp.kieuQuaiKinh.id}', '${temp.trangThai}')">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <form action="${pageContext.request.contextPath}/GongKinh/delete?id=${temp.id}" method="post" style="display:inline;">
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
                <h3>Thêm Mới Gọng Kính</h3>
                <span class="close-btn" onclick="closeAddModal()">&times;</span>
            </div>
            <form action="${pageContext.request.contextPath}/GongKinh/insert" method="post">
                <div class="modal-body">
                    <div class="form-group">
                        <label for="hinhDangGong">Hình dáng gọng</label>
                        <select id="hinhDangGong" name="hinhDangGong" required>
                            <option value="">-- Chọn hình dáng --</option>
                            <c:forEach var="hd" items="${hinhDangGong}">
                                <option value="${hd.id}">${hd.hinhDang}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="kieuQuaiKinh">Kiểu quai kính</label>
                        <select id="kieuQuaiKinh" name="kieuQuaiKinh" required>
                            <option value="">-- Chọn kiểu quai --</option>
                            <c:forEach var="kq" items="${kieuQuaiKinh}">
                                <option value="${kq.id}">${kq.kieuQuai}</option>
                            </c:forEach>
                        </select>
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
                <h3>Chỉnh Sửa Gọng Kính</h3>
                <span class="close-btn" onclick="closeEditModal()">&times;</span>
            </div>
            <form action="${pageContext.request.contextPath}/GongKinh/update" method="post">
                <div class="modal-body">
                    <input type="hidden" id="editId" name="id">

                    <div class="form-group">
                        <label for="editHinhDangGong">Hình dáng gọng</label>
                        <select id="editHinhDangGong" name="hinhDangGong" required>
                            <option value="">-- Chọn hình dáng --</option>
                            <c:forEach var="hd" items="${hinhDangGong}">
                                <option value="${hd.id}">${hd.hinhDang}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="editKieuQuaiKinh">Kiểu quai kính</label>
                        <select id="editKieuQuaiKinh" name="kieuQuaiKinh" required>
                            <option value="">-- Chọn kiểu quai --</option>
                            <c:forEach var="kq" items="${kieuQuaiKinh}">
                                <option value="${kq.id}">${kq.kieuQuai}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="editTrangThai">Trạng thái</label>
                        <select id="editTrangThai" name="trangThai">
                            <option value="1">Hoạt động</option>
                            <option value="0">Không hoạt động</option>
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
    // Lưu ý: idHinhDang và idKieuQuai giờ là ID (số), dùng để set giá trị cho <select>
    function openEditModal(id, idHinhDang, idKieuQuai, trangThai) {
        document.getElementById("editId").value = id;
        document.getElementById("editHinhDangGong").value = idHinhDang;
        document.getElementById("editKieuQuaiKinh").value = idKieuQuai;
        document.getElementById("editTrangThai").value = trangThai;

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
