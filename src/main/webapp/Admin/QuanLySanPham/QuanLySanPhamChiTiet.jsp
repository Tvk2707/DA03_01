<%
    request.setAttribute("pageTitle", "Biến thể sản phẩm");
    request.setAttribute("activeSubMenu", "productt");
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
    <style>
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            overflow: auto;
            background-color: rgba(0, 0, 0, 0.6);
        }

        .modal-content {
            background-color: #fefefe;
            margin: 5% auto;
            padding: 24px;
            border: 1px solid #888;
            width: 80%;
            max-width: 700px;
            border-radius: 10px;
            position: relative;
        }

        .close-btn {
            color: #aaa;
            position: absolute;
            top: 10px;
            right: 20px;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
        }

        .close-btn:hover,
        .close-btn:focus {
            color: black;
        }

        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-group label {
            margin-bottom: 6px;
            font-weight: 600;
        }

        .form-group input,
        .form-group select {
            padding: 10px;
            border-radius: 5px;
            border: 1px solid #ccc;
        }

        .form-actions {
            margin-top: 24px;
            display: flex;
            justify-content: flex-end;
            gap: 12px;
        }

        .preview-image {
            width: 100px;
            height: 100px;
            object-fit: cover;
            border: 1px solid #ddd;
            border-radius: 6px;
        }

        /* Class tạo hiệu ứng highlight nhấp nháy cho dòng vừa sửa */
        .highlight-row {
            animation: flashBackground 2.5s ease-out;
            border: 2px solid #4a6cf7 !important;
        }

        @keyframes flashBackground {
            0% { background-color: #fff3cd; }
            50% { background-color: #fff3cd; }
            100% { background-color: transparent; }
        }
    </style>
</head>
<body>
<div class="admin-layout">
    <jsp:include page="../layout/sidebar.jsp"/>
    <div class="main-content">
        <jsp:include page="../layout/header.jsp"/>
        <div id="page-content">
            <div class="category-section">
                <div class="category-header">
                    <h2 class="category-title">Biến Thể Sản Phẩm</h2>
                </div>

                <c:if test="${not empty sessionScope.success}">
                    <div style="background:#e6f4ea;color:#34a853;padding:12px 16px;border-radius:8px;margin-bottom:16px;">
                        <i class="fas fa-check-circle"></i> ${sessionScope.success}
                    </div>
                    <c:remove var="success" scope="session"/>
                </c:if>

                <c:if test="${not empty sessionScope.error}">
                    <div style="background:#fdecea;color:#b3261e;padding:12px 16px;border-radius:8px;margin-bottom:16px;">
                        <i class="fas fa-circle-exclamation"></i> ${sessionScope.error}
                    </div>
                    <c:remove var="error" scope="session"/>
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
                        <tr id="product-row-${temp.id}"
                            data-id="${temp.id}"
                            data-ma="${temp.ma}"
                            data-mau-sac-id="${temp.mauSac.id}"
                            data-kich-co-id="${temp.kichCo.id}"
                            data-gia-nhap="${temp.giaNhap}"
                            data-gia-ban="${temp.giaBan}"
                            data-so-luong-ton="${temp.soLuongTon}"
                            data-trong-luong="${temp.trongLuong}"
                            data-trang-thai="${temp.trangThai}"
                            data-hinh-anh="${temp.hinhAnh}"
                            data-san-pham-id="${temp.sanPham.id}">
                            <td><span class="category-id">${status.count}</span></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty temp.hinhAnh}">
                                        <img src="${pageContext.request.contextPath}/File_Anh/images/${temp.hinhAnh}"
                                             alt="Ảnh biến thể"
                                             style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; border: 1px solid #ddd;"
                                             onerror="this.onerror=null;this.src='https://images.placeholders.dev/?width=50&height=50&text=Error&bgColor=%23fdecea&textColor=%23b3261e';"/>
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
                                    <button type="button" class="action-btn edit-btn" title="Sửa">
                                        <i class="fas fa-edit"></i>
                                    </button>
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
    </div>
</div>

<div id="updateModal" class="modal">
    <div class="modal-content">
        <span class="close-btn">&times;</span>
        <h2>Cập nhật biến thể sản phẩm</h2>
        <form action="${pageContext.request.contextPath}/SanPhamChiTiet/update" method="post" enctype="multipart/form-data" id="updateForm">
            <input type="hidden" name="id" id="updateId">
            <input type="hidden" name="sanPhamId" id="updateSanPhamId">
            <input type="hidden" name="hinhAnhCu" id="updateHinhAnhCu">

            <div class="form-grid">
                <div class="form-group">
                    <label for="updateMa">Mã biến thể</label>
                    <input type="text" id="updateMa" name="ma" readonly style="background-color: #eee;">
                </div>
                <div class="form-group">
                    <label for="updateMauSac">Màu sắc</label>
                    <select id="updateMauSac" name="mauSacId" required>
                        <c:forEach var="ms" items="${mauSacList}">
                            <option value="${ms.id}">${ms.tenMau}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="updateKichCo">Kích cỡ</label>
                    <select id="updateKichCo" name="kichCoId" required>
                        <c:forEach var="kc" items="${kichCoList}">
                            <option value="${kc.id}">${kc.tenKichCo}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="updateGiaNhap">Giá nhập</label>
                    <input type="number" id="updateGiaNhap" name="giaNhap" required min="0">
                </div>
                <div class="form-group">
                    <label for="updateGiaBan">Giá bán</label>
                    <input type="number" id="updateGiaBan" name="giaBan" required min="0">
                </div>
                <div class="form-group">
                    <label for="updateSoLuongTon">Số lượng tồn</label>
                    <input type="number" id="updateSoLuongTon" name="soLuongTon" required min="0">
                </div>
                <div class="form-group">
                    <label for="updateTrongLuong">Trọng lượng (gram)</label>
                    <input type="number" id="updateTrongLuong" name="trongLuong" required min="0">
                </div>
                <div class="form-group">
                    <label for="updateTrangThai">Trạng thái</label>
                    <select id="updateTrangThai" name="trangThai" required>
                        <option value="1">Hoạt động</option>
                        <option value="0">Ngừng bán</option>
                    </select>
                </div>
                <div class="form-group" style="grid-column: 1 / -1;">
                    <label for="updateHinhAnh">Hình ảnh</label>
                    <input type="file" id="updateHinhAnh" name="hinhAnh" accept="image/*" onchange="previewImage(event)">
                    <img id="imagePreview" src="" alt="Image Preview" class="preview-image" style="margin-top: 10px; display: none;"/>
                </div>
            </div>

            <div class="form-actions">
                <button type="button" class="cancel-btn add-new-btn" style="background-color: #6c757d;">Hủy</button>
                <button type="submit" class="add-new-btn">Cập nhật</button>
            </div>
        </form>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const modal = document.getElementById('updateModal');
        const closeBtn = modal.querySelector('.close-btn');
        const cancelBtn = modal.querySelector('.cancel-btn');
        const editBtns = document.querySelectorAll('.edit-btn');
        const form = document.getElementById('updateForm');
        const imagePreview = document.getElementById('imagePreview');
        const defaultImgSrc = "https://images.placeholders.dev/?width=100&height=100&text=No+Image&bgColor=%23e9ecef&textColor=%236c757d";

        // --- MỞ MODAL VÀ ĐIỀN DỮ LIỆU ---
        editBtns.forEach(button => {
            button.addEventListener('click', function () {
                const row = this.closest('tr');
                const dataset = row.dataset;

                form.querySelector('#updateId').value = dataset.id;
                form.querySelector('#updateSanPhamId').value = dataset.sanPhamId;
                form.querySelector('#updateMa').value = dataset.ma;
                form.querySelector('#updateMauSac').value = dataset.mauSacId;
                form.querySelector('#updateKichCo').value = dataset.kichCoId;
                form.querySelector('#updateGiaNhap').value = dataset.giaNhap;
                form.querySelector('#updateGiaBan').value = dataset.giaBan;
                form.querySelector('#updateSoLuongTon').value = dataset.soLuongTon;
                form.querySelector('#updateTrongLuong').value = dataset.trongLuong;
                form.querySelector('#updateTrangThai').value = dataset.trangThai;
                form.querySelector('#updateHinhAnhCu').value = dataset.hinhAnh;

                // Hiển thị ảnh cũ
                const hinhAnh = dataset.hinhAnh;
                if (hinhAnh && hinhAnh !== 'null' && hinhAnh.trim() !== '') {
                    imagePreview.src = '${pageContext.request.contextPath}/File_Anh/images/' + hinhAnh;
                } else {
                    imagePreview.src = defaultImgSrc;
                }
                imagePreview.style.display = 'block';

                // Reset the file input to ensure onchange fires even if the same file is selected again
                document.getElementById('updateHinhAnh').value = '';

                modal.style.display = 'block';
            });
        });

        // --- ĐÓNG MODAL ---
        function closeModal() {
            modal.style.display = 'none';
        }
        closeBtn.addEventListener('click', closeModal);
        cancelBtn.addEventListener('click', closeModal);
        // Đóng modal khi click bên ngoài
        window.addEventListener('click', function(event) {
            if (event.target === modal) {
                closeModal();
            }
        });
    });

    // --- PREVIEW ẢNH KHI CHỌN FILE ---
    // Đặt bên ngoài DOMContentLoaded để hàm này là global và có thể được gọi từ onchange attribute
    function previewImage(event) {
        const file = event.target.files[0];
        const imagePreview = document.getElementById('imagePreview');
        const updateHinhAnhCu = document.getElementById('updateHinhAnhCu').value;

        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                imagePreview.src = e.target.result;
            }
            reader.readAsDataURL(file);
        } else {
            // Nếu người dùng hủy chọn file, quay về hiển thị ảnh cũ ban đầu
            if (updateHinhAnhCu && updateHinhAnhCu !== 'null' && updateHinhAnhCu.trim() !== '') {
                imagePreview.src = '${pageContext.request.contextPath}/File_Anh/images/' + updateHinhAnhCu;
            } else {
                imagePreview.src = "https://images.placeholders.dev/?width=100&height=100&text=No+Image&bgColor=%23e9ecef&textColor=%236c757d";
            }
        }
    }
</script>

</body>
</html>
