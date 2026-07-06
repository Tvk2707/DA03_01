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
</head>
<body>
<%@include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@include file="../layout/header.jsp" %>

    <div class="category-section">
        <div class="category-header">
            <h2 class="category-title">Sản Phẩm</h2>
            <button class="add-new-btn" onclick="openAddModal()">
                <i class="fas fa-plus"></i> Thêm mới
            </button>
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
                            <button type="button" class="action-btn edit-btn" title="Sửa"
                                    onclick="openEditModal(
                                            '${temp.id}',
                                            '${temp.maSanPham}',
                                            '${fn:escapeXml(temp.tenSanPham)}',
                                            '${fn:escapeXml(temp.moTaChiTiet)}',
                                            '${temp.danhMuc != null ? temp.danhMuc.id : ""}',
                                            '${temp.thuongHieu != null ? temp.thuongHieu.id : ""}',
                                            '${temp.chatLieu != null ? temp.chatLieu.id : ""}',
                                            '${temp.kieuDang != null ? temp.kieuDang.id : ""}',
                                            '${temp.gongKinh != null ? temp.gongKinh.id : ""}',
                                            '${temp.trongKinh != null ? temp.trongKinh.id : ""}',
                                            '${temp.trangThai}'
                                            )">
                                <i class="fas fa-edit"></i>
                            </button>
                            <form action="${pageContext.request.contextPath}/SanPham/delete" method="get"
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

<!-- ================= MODAL THÊM MỚI ================= -->
<div id="addProductModal" class="modal-overlay">
    <div class="modal-content" style="max-width:600px;">
        <div class="modal-header">
            <h3>Thêm Mới Sản Phẩm</h3>
            <span class="close-btn" onclick="closeAddModal()">&times;</span>
        </div>
        <form action="${pageContext.request.contextPath}/SanPham/insert" method="post">
            <div class="modal-body" style="max-height:65vh;overflow-y:auto;">
                <div class="form-group">
                    <label for="maSanPham">Mã sản phẩm</label>
                    <input type="text" id="maSanPham" name="maSanPham" placeholder="Nhập mã sản phẩm" required>
                </div>

                <div class="form-group">
                    <label for="tenSanPham">Tên sản phẩm</label>
                    <input type="text" id="tenSanPham" name="tenSanPham" placeholder="Nhập tên sản phẩm" required>
                </div>

                <div class="form-group">
                    <label for="moTaChiTiet">Mô tả chi tiết</label>
                    <textarea id="moTaChiTiet" name="moTaChiTiet" rows="3" placeholder="Nhập mô tả sản phẩm"></textarea>
                </div>

                <div class="form-group">
                    <label for="danhMucId">Danh mục</label>
                    <select id="danhMucId" name="danhMucId">
                        <option value="">-- Chọn danh mục --</option>
                        <c:forEach var="dm" items="${danhMucList}">
                            <option value="${dm.id}">${dm.tenDanhMuc}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="thuongHieuId">Thương hiệu</label>
                    <select id="thuongHieuId" name="thuongHieuId">
                        <option value="">-- Chọn thương hiệu --</option>
                        <c:forEach var="th" items="${thuongHieuList}">
                            <option value="${th.id}">${th.tenThuongHieu}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="chatLieuId">Chất liệu</label>
                    <select id="chatLieuId" name="chatLieuId">
                        <option value="">-- Chọn chất liệu --</option>
                        <c:forEach var="cl" items="${chatLieuList}">
                            <option value="${cl.id}">${cl.tenChatLieu}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="kieuDangId">Kiểu dáng</label>
                    <select id="kieuDangId" name="kieuDangId">
                        <option value="">-- Chọn kiểu dáng --</option>
                        <c:forEach var="kd" items="${kieuDangList}">
                            <option value="${kd.id}">${kd.tenKieuDang}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="gongKinhId">Gọng kính</label>
                    <select id="gongKinhId" name="gongKinhId">
                        <option value="">-- Chọn gọng kính --</option>
                        <c:forEach var="gk" items="${gongKinhList}">
                            <option value="${gk.id}">${gk.hinhDangGong.hinhDang} - ${gk.kieuQuaiKinh.kieuQuai}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="trongKinhId">Tròng kính</label>
                    <select id="trongKinhId" name="trongKinhId">
                        <option value="">-- Chọn tròng kính --</option>
                        <c:forEach var="tk" items="${trongKinhList}">
                            <option value="${tk.id}">${tk.loaiTrong}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="trangThai">Trạng thái</label>
                    <select id="trangThai" name="trangThai">
                        <option value="1">Hoạt động</option>
                        <option value="0">Ngừng bán</option>
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

<!-- ================= MODAL CHỈNH SỬA ================= -->
<div id="editProductModal" class="modal-overlay">
    <div class="modal-content" style="max-width:600px;">
        <div class="modal-header">
            <h3>Chỉnh Sửa Sản Phẩm</h3>
            <span class="close-btn" onclick="closeEditModal()">&times;</span>
        </div>
        <form action="${pageContext.request.contextPath}/SanPham/update" method="post">
            <div class="modal-body" style="max-height:65vh;overflow-y:auto;">
                <input type="hidden" id="editId" name="id">

                <div class="form-group">
                    <label for="editMaSanPham">Mã sản phẩm</label>
                    <input type="text" id="editMaSanPham" name="maSanPham" readonly
                           style="background-color:#f5f5f5;cursor:not-allowed;">
                </div>

                <div class="form-group">
                    <label for="editTenSanPham">Tên sản phẩm</label>
                    <input type="text" id="editTenSanPham" name="tenSanPham" placeholder="Nhập tên sản phẩm" required>
                </div>

                <div class="form-group">
                    <label for="editMoTaChiTiet">Mô tả chi tiết</label>
                    <textarea id="editMoTaChiTiet" name="moTaChiTiet" rows="3"></textarea>
                </div>

                <div class="form-group">
                    <label for="editDanhMucId">Danh mục</label>
                    <select id="editDanhMucId" name="danhMucId">
                        <option value="">-- Chọn danh mục --</option>
                        <c:forEach var="dm" items="${danhMucList}">
                            <option value="${dm.id}">${dm.tenDanhMuc}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="editThuongHieuId">Thương hiệu</label>
                    <select id="editThuongHieuId" name="thuongHieuId">
                        <option value="">-- Chọn thương hiệu --</option>
                        <c:forEach var="th" items="${thuongHieuList}">
                            <option value="${th.id}">${th.tenThuongHieu}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="editChatLieuId">Chất liệu</label>
                    <select id="editChatLieuId" name="chatLieuId">
                        <option value="">-- Chọn chất liệu --</option>
                        <c:forEach var="cl" items="${chatLieuList}">
                            <option value="${cl.id}">${cl.tenChatLieu}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="editKieuDangId">Kiểu dáng</label>
                    <select id="editKieuDangId" name="kieuDangId">
                        <option value="">-- Chọn kiểu dáng --</option>
                        <c:forEach var="kd" items="${kieuDangList}">
                            <option value="${kd.id}">${kd.tenKieuDang}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="editGongKinhId">Gọng kính</label>
                    <select id="editGongKinhId" name="gongKinhId">
                        <option value="">-- Chọn gọng kính --</option>
                        <c:forEach var="gk" items="${gongKinhList}">
                            <option value="${gk.id}">${gk.hinhDangGong.hinhDang} - ${gk.kieuQuaiKinh.kieuQuai}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="editTrongKinhId">Tròng kính</label>
                    <select id="editTrongKinhId" name="trongKinhId">
                        <option value="">-- Chọn tròng kính --</option>
                        <c:forEach var="tk" items="${trongKinhList}">
                            <option value="${tk.id}">${tk.loaiTrong}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="editTrangThai">Trạng thái</label>
                    <select id="editTrangThai" name="trangThai">
                        <option value="1">Hoạt động</option>
                        <option value="0">Ngừng bán</option>
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
    // --- MODAL THÊM MỚI ---
    function openAddModal() {
        document.getElementById("addProductModal").style.display = "flex";
    }

    function closeAddModal() {
        document.getElementById("addProductModal").style.display = "none";
    }

    // --- MODAL SỬA ---
    function openEditModal(id, maSanPham, tenSanPham, moTaChiTiet, danhMucId, thuongHieuId, chatLieuId, kieuDangId, gongKinhId, trongKinhId, trangThai) {
        document.getElementById("editId").value = id;
        document.getElementById("editMaSanPham").value = maSanPham;
        document.getElementById("editTenSanPham").value = tenSanPham;
        document.getElementById("editMoTaChiTiet").value = moTaChiTiet;
        document.getElementById("editDanhMucId").value = danhMucId;
        document.getElementById("editThuongHieuId").value = thuongHieuId;
        document.getElementById("editChatLieuId").value = chatLieuId;
        document.getElementById("editKieuDangId").value = kieuDangId;
        document.getElementById("editGongKinhId").value = gongKinhId;
        document.getElementById("editTrongKinhId").value = trongKinhId;
        document.getElementById("editTrangThai").value = trangThai;

        document.getElementById("editProductModal").style.display = "flex";
    }

    function closeEditModal() {
        document.getElementById("editProductModal").style.display = "none";
    }

    // --- ĐÓNG MODAL KHI CLICK RA NGOÀI ---
    window.onclick = function (event) {
        let addModal = document.getElementById("addProductModal");
        let editModal = document.getElementById("editProductModal");
        if (event.target == addModal) addModal.style.display = "none";
        if (event.target == editModal) editModal.style.display = "none";
    }

    // --- RESET FILTERS ---
    function resetFilters() {
        document.getElementById('filterForm').reset();
        window.location.href = '${pageContext.request.contextPath}/SanPham';
    }
</script>
</body>
</html>