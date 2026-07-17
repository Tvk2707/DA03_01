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

    <!-- ✅ TÍNH NĂNG MỚI: NHÚNG CDN THƯ VIỆN QRCODE JS ĐỂ TỰ ĐỘNG GENERATE QR PHÍA CLIENT -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>

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

        .form-grid.form-group {
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

        /* CSS cho nut Lam moi */
        .reset-btn {
            background-color: #6c757d;
            color: white;
            padding: 10px 24px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 500;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            transition: background-color 0.3s ease;
        }

        .reset-btn:hover {
            background-color: #5a6268;
            color: white;
        }

        .reset-btn i {
            font-size: 14px;
        }

        /* --- CSS MỚI: DÀNH CHO PHÂN TRANG BẰNG JAVASCRIPT --- */
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
        }
        .sp-page-btn:hover:not(:disabled) {
            background-color: #f3f4f6;
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

        /* ======================================================= */
        /* ✅ TÍNH NĂNG MỚI: CSS PHẲNG MẪU CHUẨN ĐỒNG BỘ 100% THEO HÌNH */
        /* ======================================================= */
        .qr-modal {
            display: none;
            position: fixed;
            z-index: 10000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.4);
            align-items: center;
            justify-content: center;
        }

        .qr-modal-content {
            background-color: #ffffff;
            border-radius: 12px;
            width: 100%;
            max-width: 680px;
            padding: 45px 50px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
            position: relative;
            box-sizing: border-box;
        }

        .qr-main-layout {
            display: grid;
            grid-template-columns: 210px 1fr;
            gap: 40px;
            align-items: center;
        }

        .qr-left-column {
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        #qrcodeTable canvas, #qrcodeTable img {
            width: 195px !important;
            height: 195px !important;
            display: block;
        }

        .qr-right-column {
            display: flex;
            flex-direction: column;
            gap: 14px;
        }

        .qr-info-row {
            display: grid;
            grid-template-columns: 115px 1fr;
            font-size: 16px;
            line-height: 1.4;
            color: #1f2937;
        }

        .qr-info-label {
            color: #4b5563;
            font-weight: 400;
        }

        .qr-info-value {
            font-weight: 700;
            color: #000000;
        }

        .qr-status-active {
            color: #16a34a !important;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .qr-bottom-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 35px;
        }

        .qr-btn-group-right {
            display: flex;
            gap: 10px;
        }

        .qr-action-btn {
            padding: 10px 22px;
            font-size: 14px;
            font-weight: 600;
            border-radius: 6px;
            border: none;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: background-color 0.2s ease;
            text-decoration: none;
        }

        .btn-qr-blue {
            background-color: #2b6cb0;
            color: #ffffff;
        }
        .btn-qr-blue:hover {
            background-color: #23558a;
        }

        .btn-qr-gray {
            background-color: #4a5568;
            color: #ffffff;
            min-width: 85px;
            justify-content: center;
        }
        .btn-qr-gray:hover {
            background-color: #363f4d;
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
                <c:if test="${not empty sanPhamDuocLoc}">
                    <div style="background-color: #fdfaf6; color: #5c4d3c; padding: 14px 20px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #e2d8c9; display: flex; justify-content: space-between; align-items: center;">
                        <div style="font-size: 15px;">
                            <i class="fas fa-filter" style="margin-right: 6px; color: #b4975a;"></i>
                            Đang lọc biến thể thuộc sản phẩm: <strong style="font-size: 16px; color: #1f2937;">${sanPhamDuocLoc.maSanPham} - ${sanPhamDuocLoc.tenSanPham}</strong>
                        </div>

                        <a href="${pageContext.request.contextPath}/SanPhamChiTiet" style="background-color: #b4975a; color: white; padding: 8px 16px; border-radius: 6px; text-decoration: none; font-weight: 600; font-size: 14px; box-shadow: 0 1px 2px rgba(0,0,0,0.05); transition: opacity 0.2s;" onmouseover="this.style.opacity='0.85'" onmouseout="this.style.opacity='1'">
                            <i class="fas fa-th-list"></i> Hiện đầy đủ danh sách
                        </a>
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
                                    <option value="1" ${'1' == searchTrangThai ? 'selected' : ''}>Dang kinh doanh</option>
                                    <option value="0" ${'0' == searchTrangThai ? 'selected' : ''}>Ngừng bán</option>
                                </select>
                            </div>
                        </div>

                        <div style="display: flex; gap: 16px; margin-top: 16px;">
                            <button type="submit" class="add-new-btn" style="padding: 10px 24px;">
                                <i class="fas fa-search"></i> Tìm kiếm
                            </button>

                            <a href="${pageContext.request.contextPath}/SanPhamChiTiet" class="reset-btn">
                                <i class="fas fa-sync-alt"> </i>Hiển thị đầy đủ
                            </a>
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
                        <tr class="product-row" id="product-row-${temp.id}"
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
                            <td><span class="category-id js-stt-cell">1</span></td>

                            <!-- ✅ ĐÃ THÊM CLASS js-trigger-qr VÀ THUỘC TÍNH CON TRỎ ĐỂ CLICK ẢNH BẬT QR -->
                            <td>
                                <c:choose>
                                    <c:when test="${not empty temp.hinhAnh}">
                                        <img src="${pageContext.request.contextPath}/File_Anh/images/${temp.hinhAnh}"
                                             alt="Ảnh biến thể"
                                             class="js-trigger-qr"
                                             style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; border: 1px solid #ddd; cursor: pointer;"
                                             onerror="this.onerror=null;this.src='https://images.placeholders.dev/?width=50&height=50&text=Error&bgColor=%23fdecea&textColor=%23b3261e';"/>
                                    </c:when>
                                    <c:otherwise>
                                        <img src="https://images.placeholders.dev/?width=50&height=50&text=No+Image&bgColor=%23e9ecef&textColor=%236c757d"
                                             alt="No image"
                                             class="js-trigger-qr"
                                             style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; opacity: 0.6; cursor: pointer;"/>
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
                                        ${temp.trangThai == 1 ? "Đang kinh doanh" : "Ngừng bán"}
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

                <div class="sp-pagination" id="paginationContainer"></div>

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
                        <option value="1">Đang kinh doanh</option>
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

<!-- ✅ TÍNH NĂNG MỚI: HTML CARD OVERLAY HIỂN THỊ CHI TIẾT BIẾN THỂ & QR CODE THEO HÌNH MẪU -->
<div id="qrDetailModal" class="qr-modal">
    <div class="qr-modal-content">

        <div class="qr-main-layout">
            <!-- Cột trái: Vùng hiển thị QR Code hình vuông chính giữa -->
            <div class="qr-left-column">
                <div id="qrcodeTable"></div>
            </div>

            <!-- Cột phải: Bảng liệt kê thông tin chi tiết thẳng hàng dấu hai chấm -->
            <div class="qr-right-column">
                <div class="qr-info-row">
                    <span class="qr-info-label">Mã biến thể:</span>
                    <span class="qr-info-value" id="qrDetailMa"></span>
                </div>
                <div class="qr-info-row">
                    <span class="qr-info-label">Màu sắc:</span>
                    <span class="qr-info-value" id="qrDetailMau"></span>
                </div>
                <div class="qr-info-row">
                    <span class="qr-info-label">Kích cỡ:</span>
                    <span class="qr-info-value" id="qrDetailKichCo"></span>
                </div>
                <div class="qr-info-row">
                    <span class="qr-info-label">Giá bán:</span>
                    <span class="qr-info-value" id="qrDetailGiaBan"></span>
                </div>
                <div class="qr-info-row">
                    <span class="qr-info-label">Tồn kho:</span>
                    <span class="qr-info-value" id="qrDetailTonKho"></span>
                </div>
                <div class="qr-info-row">
                    <span class="qr-info-label">Trạng thái:</span>
                    <span class="qr-info-value" id="qrDetailTrangThai"></span>
                </div>
            </div>
        </div>

        <!-- Hàng nút bấm chức năng chân trang -->
        <div class="qr-bottom-actions">
            <!-- Nút tải ảnh định vị chính xác dưới trục QR -->
            <div style="width: 210px; display: flex; justify-content: center;">
                <button type="button" class="qr-action-btn btn-qr-blue" id="btnDownloadQR">
                    <i class="fas fa-download"></i> Tải ảnh QR
                </button>
            </div>

            <!-- Bộ đôi nút Sửa nhanh và Đóng góc phải -->
            <div class="qr-btn-group-right">
                <button type="button" class="qr-action-btn btn-qr-blue" id="btnQuickEditQR">
                    <i class="fas fa-wrench"></i> Sửa biến thể
                </button>
                <button type="button" class="qr-action-btn btn-qr-gray" id="btnCancelQR">Đóng</button>
            </div>
        </div>

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
        window.addEventListener('click', function(event) {
            if (event.target === modal) {
                closeModal();
            }
        });

        // ==========================================
        // --- THÊM MỚI: LOGIC PHÂN TRANG BẰNG JS ---
        // ==========================================
        const tableBody = document.querySelector(".category-table tbody");
        if (tableBody) {
            const rows = Array.from(tableBody.querySelectorAll(".product-row"));

            if (rows.length > 0) {
                const rowsPerPage = 5;
                let currentPage = 1;

                function displayTable(page) {
                    const start = (page - 1) * rowsPerPage;
                    const end = start + rowsPerPage;

                    rows.forEach((row, index) => {
                        if (index >= start && index < end) {
                            row.style.display = ""; // Hiển thị

                            const sttCell = row.querySelector(".js-stt-cell");
                            if (sttCell) {
                                sttCell.innerText = index + 1;
                            }
                        } else {
                            row.style.display = "none"; // Ẩn đi
                        }
                    });
                }

                function setupPagination() {
                    const pageCount = Math.ceil(rows.length / rowsPerPage);
                    const paginationContainer = document.getElementById("paginationContainer");
                    paginationContainer.innerHTML = "";

                    if (pageCount <= 1) return;

                    const prevBtn = document.createElement("button");
                    prevBtn.className = "sp-page-btn";
                    prevBtn.innerHTML = '<i class="fas fa-chevron-left"></i>';
                    prevBtn.type = "button";
                    prevBtn.disabled = currentPage === 1;
                    prevBtn.addEventListener("click", () => {
                        if (currentPage > 1) {
                            currentPage--;
                            updatePagination();
                        }
                    });
                    paginationContainer.appendChild(prevBtn);

                    for (let i = 1; i <= pageCount; i++) {
                        const btn = document.createElement("button");
                        btn.className = "sp-page-btn " + (i === currentPage ? "active" : "");
                        btn.innerText = i;
                        btn.type = "button";
                        btn.addEventListener("click", () => {
                            currentPage = i;
                            updatePagination();
                        });
                        paginationContainer.appendChild(btn);
                    }

                    const nextBtn = document.createElement("button");
                    nextBtn.className = "sp-page-btn";
                    nextBtn.innerHTML = '<i class="fas fa-chevron-right"></i>';
                    nextBtn.type = "button";
                    nextBtn.disabled = currentPage === pageCount;
                    nextBtn.addEventListener("click", () => {
                        if (currentPage < pageCount) {
                            currentPage++;
                            updatePagination();
                        }
                    });
                    paginationContainer.appendChild(nextBtn);
                }

                function updatePagination() {
                    displayTable(currentPage);
                    setupPagination();
                }

                updatePagination();
            }
        }

        // =========================================================================
        // ✅ TÍNH NĂNG MỚI: SCRIPT ĐIỀU HƯỚNG TỰ SINH MÃ QR VÀ DOWNLOAD CHUẨN MẪU
        // =========================================================================
        const qrModal = document.getElementById('qrDetailModal');
        const btnCancelQR = document.getElementById('btnCancelQR');
        const qrContainer = document.getElementById('qrcodeTable');
        const btnDownloadQR = document.getElementById('btnDownloadQR');
        const btnQuickEditQR = document.getElementById('btnQuickEditQR');
        let activeRowForEdit = null;

        document.querySelectorAll('.js-trigger-qr').forEach(imgElement => {
            imgElement.addEventListener('click', function () {
                const row = this.closest('tr');
                activeRowForEdit = row;
                const dataset = row.dataset;

                // Gán dữ liệu sang bảng Card thông tin
                document.getElementById('qrDetailMa').innerText = dataset.ma;
                document.getElementById('qrDetailMau').innerText = row.querySelector('td:nth-child(4)').innerText;
                document.getElementById('qrDetailKichCo').innerText = row.querySelector('td:nth-child(5)').innerText;
                document.getElementById('qrDetailGiaBan').innerText = row.querySelector('td:nth-child(7)').innerText;
                document.getElementById('qrDetailTonKho').innerText = dataset.soLuongTon;

                const statusValue = parseInt(dataset.trangThai);
                const statusContainer = document.getElementById('qrDetailTrangThai');
                if (statusValue === 1) {
                    statusContainer.innerHTML = `<span class="qr-status-active"><i class="fas fa-check-circle"></i> Đang kinh doanh</span>`;
                } else {
                    statusContainer.innerHTML = `<span style="color: #9ca3af;"><i class="fas fa-minus-circle"></i> Ngừng bán</span>`;
                }

                // Thực hiện tạo mới vẽ ảnh mã QR Code từ Thư viện
                qrContainer.innerHTML = "";
                new QRCode(qrContainer, {
                    text: dataset.ma,
                    width: 195,
                    height: 195,
                    colorDark: "#000000",
                    colorLight: "#ffffff",
                    correctLevel: QRCode.CorrectLevel.H
                });

                qrModal.style.display = 'flex';
            });
        });

        // Trigger tải ảnh QR dạng File PNG
        btnDownloadQR.addEventListener('click', function () {
            const canvas = qrContainer.querySelector('canvas');
            if (canvas) {
                const imageURI = canvas.toDataURL("image/png");
                const downloadLink = document.createElement('a');
                const maSP = document.getElementById('qrDetailMa').innerText;

                downloadLink.href = imageURI;
                downloadLink.download = 'QR_' + maSP + '.png';
                document.body.appendChild(downloadLink);
                downloadLink.click();
                document.body.removeChild(downloadLink);
            }
        });

        // Liên kết ngầm nút Sửa của hệ thống gốc
        btnQuickEditQR.addEventListener('click', function () {
            if (activeRowForEdit) {
                qrModal.style.display = 'none';
                const systemEditBtn = activeRowForEdit.querySelector('.edit-btn');
                if (systemEditBtn) {
                    systemEditBtn.click();
                }
            }
        });

        btnCancelQR.addEventListener('click', () => qrModal.style.display = 'none');
        window.addEventListener('click', (e) => {
            if (e.target === qrModal) {
                qrModal.style.display = 'none';
            }
        });
    });

    // --- PREVIEW ẢNH KHI CHỌN FILE ---
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