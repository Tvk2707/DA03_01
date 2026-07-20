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

    <!-- Thư viện bổ sung phục vụ tính năng QR và Download ZIP -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
    <script src="https://unpkg.com/html5-qrcode"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/FileSaver.js/2.0.5/FileSaver.min.js"></script>

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

        .close-btn:hover { color: black; }

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

        .form-group input, .form-group select {
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
        }

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
        }
        .sp-page-btn.active {
            background-color: #b4975a;
            color: #fff;
            border-color: #b4975a;
        }

        .status-switch {
            position: relative;
            display: inline-block;
            width: 44px;
            height: 22px;
        }
        .status-switch input { opacity: 0; width: 0; height: 0; }
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
            content: ""; height: 16px; width: 16px; left: 3px; bottom: 3px;
            background-color: white; transition: .3s; border-radius: 50%;
        }
        .status-switch input:checked + .status-slider { background-color: #b4975a; }
        .status-switch input:checked + .status-slider:before { transform: translateX(22px); }

        .btn-secondary-outline {
            background: #ffffff !important;
            color: #4b5563 !important;
            border: 1px solid #d1d5db !important;
            padding: 10px 20px;
            border-radius: 8px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            text-decoration: none;
            transition: all 0.2s;
        }
        .btn-secondary-outline:hover:not(:disabled) { background: #f9fafb !important; }

        /* Trạng thái disabled cho nút tải file zip */
        .btn-secondary-outline:disabled {
            background: #f3f4f6 !important;
            color: #9ca3af !important;
            border-color: #e5e7eb !important;
            cursor: not-allowed !important;
            opacity: 0.7;
        }

        .vertical-divider {
            width: 1px;
            background-color: #e5e7eb;
            align-self: stretch;
            margin: 0 4px;
        }

        .modal-qr-section {
            background: #f8fafc;
            border: 1px dashed #cbd5e1;
            border-radius: 8px;
            padding: 12px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 8px;
        }
        #modalQrcode img, #modalQrcode canvas { width: 110px !important; height: 110px !important; display: block; }

        .qr-detail-layout {
            display: flex;
            gap: 24px;
            align-items: center;
            margin-top: 16px;
        }
        .qr-info-list {
            display: flex;
            flex-direction: column;
            gap: 10px;
            font-size: 15px;
        }
    </style>
</head>
<body>
<div class="admin-layout">
    <jsp:include page="../layout/sidebar.jsp"/>
    <div class="main-content">
        <jsp:include page="../layout/header.jsp"/>
            <div class="category-section">
                <div class="category-header" style="display: flex; justify-content: space-between; align-items: center;">
                    <h2 class="category-title">Biến Thể Sản Phẩm</h2>

                    <button type="button" class="add-new-btn" id="btnOpenScanner" style="background-color: #b4975a;">
                        <i class="fas fa-camera"></i> Quét QR Sản Phẩm
                    </button>
                </div>

                <div class="filter-section">
                    <form action="${pageContext.request.contextPath}/SanPhamChiTiet" method="get" id="filterForm">

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
                        <input type="hidden" name="sanPhamId" value="${sanPhamId}">
                        <div class="filter-grid">
                            <div class="filter-group">
                                <label class="filter-label">Tìm kiếm</label>
                                <input type="text" name="ma" value="${searchMa}" class="filter-input" placeholder="Nhập mã biến thể...">
                            </div>
                            <div class="filter-group">
                                <label class="filter-label">Màu sắc</label>
                                <select name="mauSacId" class="filter-select">
                                    <option value="">Tất cả</option>
                                    <c:forEach var="ms" items="${mauSacList}">
                                        <option value="${ms.id}" ${ms.id == searchMauSacId ? 'selected' : ''}>${ms.tenMau}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="filter-group">
                                <label class="filter-label">Kích cỡ</label>
                                <select name="kichCoId" class="filter-select">
                                    <option value="">Tất cả</option>
                                    <c:forEach var="kc" items="${kichCoList}">
                                        <option value="${kc.id}" ${kc.id == searchKichCoId ? 'selected' : ''}>${kc.tenKichCo}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="filter-group">
                                <label class="filter-label">Trạng thái</label>
                                <select name="trangThai" class="filter-select">
                                    <option value="">Tất cả</option>
                                    <option value="1" ${'1' == searchTrangThai ? 'selected' : ''}>Đang kinh doanh</option>
                                    <option value="0" ${'0' == searchTrangThai ? 'selected' : ''}>Ngừng bán</option>
                                </select>
                            </div>
                        </div>

                        <!-- Khu vực nút chức năng được tối ưu lại bố cục -->
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 16px; height: 42px;">
                            <!-- Nhóm bên trái: Hành động lọc -->
                            <div style="display: flex; gap: 12px; height: 100%;">
                                <button type="submit" class="add-new-btn"><i class="fas fa-search"></i> Tìm kiếm</button>
                                <a href="${pageContext.request.contextPath}/SanPhamChiTiet" class="btn-secondary-outline">
                                    <i class="fas fa-sync-alt"></i> Đặt lại
                                </a>
                            </div>

                            <!-- Đường phân cách dọc mỏng ở giữa -->
                            <div class="vertical-divider"></div>

                            <!-- Nhóm bên phải: Hành động xuất dữ liệu -->
                            <div style="display: flex; gap: 12px; height: 100%;">
                                <button type="button" class="btn-secondary-outline" id="btnDownloadSelectedQR" style="color: #2e7d32 !important; border-color: #a5d6a7 !important;" disabled>
                                    <i class="fas fa-file-archive"></i> Tải QR đã chọn (.ZIP)
                                </button>
                                <a href="${pageContext.request.contextPath}/SanPhamChiTiet/export?sanPhamId=${sanPhamId}&ma=${searchMa}&mauSacId=${searchMauSacId}&kichCoId=${searchKichCoId}&trangThai=${searchTrangThai}" class="btn-secondary-outline">
                                    <i class="fas fa-file-export"></i> Xuất Excel
                                </a>
                            </div>
                        </div>
                    </form>
                </div>

                <table class="category-table">
                    <thead>
                    <tr>
                        <th style="width: 40px;"><input type="checkbox" id="selectAllVariants"></th>
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
                            data-gia-ban="<fmt:formatNumber value='${temp.giaBan}' pattern='#,##0'/> đ"
                            data-so-luong-ton="${temp.soLuongTon}"
                            data-trong-luong="${temp.trongLuong}"
                            data-trang-thai="${temp.trangThai}"
                            data-hinh-anh="${temp.hinhAnh}"
                            data-san-pham-id="${temp.sanPham.id}">
                            <td><input type="checkbox" class="variant-checkbox" value="${temp.id}"></td>
                            <td><span class="category-id js-stt-cell">1</span></td>
                            <td>
                                <img src="${pageContext.request.contextPath}/File_Anh/images/${temp.hinhAnh}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;" onerror="this.src='https://images.placeholders.dev/?width=50&height=50&text=No+Image';"/>
                            </td>
                            <td><span class="category-code">${temp.ma}</span></td>
                            <td><span class="category-name">${temp.mauSac.tenMau}</span></td>
                            <td><span class="category-name">${temp.kichCo.tenKichCo}</span></td>
                            <td><span class="category-name"><fmt:formatNumber value="${temp.giaNhap}" pattern="#,##0"/> đ</span></td>
                            <td><span class="category-name"><fmt:formatNumber value="${temp.giaBan}" pattern="#,##0"/> đ</span></td>
                            <td><span class="category-name">${temp.soLuongTon}</span></td>
                            <td>
                                <span class="category-status JS-variant-status-text ${temp.trangThai == 1 ? 'status-active' : 'status-inactive'}">
                                        ${temp.trangThai == 1 ? "Đang kinh doanh" : "Ngừng bán"}
                                </span>
                            </td>
                            <td>
                                <div class="action-buttons" style="align-items: center; gap: 12px;">
                                    <button type="button" class="action-btn edit-btn" title="Sửa"><i class="fas fa-edit"></i></button>
                                    <label class="status-switch">
                                        <input type="checkbox" class="JS-variant-status-toggle" data-id="${temp.id}" ${temp.trangThai == 1 ? 'checked' : ''}>
                                        <span class="status-slider"></span>
                                    </label>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
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
            <input type="hidden" name="id" id="updateId"><input type="hidden" name="sanPhamId" id="updateSanPhamId"><input type="hidden" name="hinhAnhCu" id="updateHinhAnhCu">
            <div class="form-grid">
                <div class="form-group">
                    <label for="updateMa">Mã biến thể</label>
                    <input type="text" id="updateMa" name="ma" readonly style="background-color: #eee;">
                </div>
                <div class="modal-qr-section">
                    <label style="font-size: 13px; color: #4b5563; margin-bottom: 2px;">Mã QR Sản phẩm</label>
                    <div id="modalQrcode"></div>
                </div>
                <div class="form-group">
                    <label for="updateMauSac">Màu sắc</label>
                    <select id="updateMauSac" name="mauSacId" required>
                        <c:forEach var="ms" items="${mauSacList}"><option value="${ms.id}">${ms.tenMau}</option></c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="updateKichCo">Kích cỡ</label>
                    <select id="updateKichCo" name="kichCoId" required>
                        <c:forEach var="kc" items="${kichCoList}"><option value="${kc.id}">${kc.tenKichCo}</option></c:forEach>
                    </select>
                </div>
                <div class="form-group"><label for="updateGiaNhap">Giá nhập</label><input type="number" id="updateGiaNhap" name="giaNhap" required min="0"></div>
                <div class="form-group"><label for="updateGiaBan">Giá bán</label><input type="number" id="updateGiaBan" name="giaBan" required min="0"></div>
                <div class="form-group"><label for="updateSoLuongTon">Số lượng tồn</label><input type="number" id="updateSoLuongTon" name="soLuongTon" required min="0"></div>
                <div class="form-group"><label for="updateTrongLuong">Trọng lượng (gram)</label><input type="number" id="updateTrongLuong" name="trongLuong" required min="0"></div>
                <div class="form-group">
                    <label for="updateTrangThai">Trạng thái</label>
                    <select id="updateTrangThai" name="trangThai" required><option value="1">Đang kinh doanh</option><option value="0">Ngừng bán</option></select>
                </div>
                <div class="form-group" style="grid-column: 1 / -1;">
                    <label for="updateHinhAnh">Hình ảnh</label><input type="file" id="updateHinhAnh" name="hinhAnh" accept="image/*" onchange="previewImage(event)">
                    <img id="imagePreview" src="" class="preview-image" style="margin-top: 10px; display: none;"/>
                </div>
            </div>
            <div class="form-actions">
                <button type="button" class="cancel-btn add-new-btn" style="background-color: #6c757d;">Hủy</button>
                <button type="submit" class="add-new-btn">Cập nhật</button>
            </div>
        </form>
    </div>
</div>

<div id="scannerModal" class="modal">
    <div class="modal-content" style="max-width: 500px;">
        <span class="close-btn" id="btnCloseScanner">&times;</span>
        <h2>Quét Mã QR Biến Thể</h2>
        <div id="reader" style="width: 100%; max-width: 450px; margin-top: 16px;"></div>
    </div>
</div>

<div id="scannedInfoModal" class="modal">
    <div class="modal-content" style="max-width: 550px;">
        <span class="close-btn" id="btnCloseInfoModal">&times;</span>
        <h2><i class="fas fa-info-circle" style="color: #b4975a;"></i> Chi Tiết Biến Thể Quét Được</h2>
        <div class="qr-detail-layout">
            <img id="scanInfoImg" src="" style="width: 130px; height: 130px; object-fit: cover; border-radius: 8px; border: 1px solid #ddd;">
            <div class="qr-info-list">
                <div>Mã biến thể: <strong id="scanInfoMa" style="color: #b4975a;"></strong></div>
                <div>Màu sắc: <strong id="scanInfoMau"></strong></div>
                <div>Kích cỡ: <strong id="scanInfoKichCo"></strong></div>
                <div>Giá bán: <strong id="scanInfoGiaBan" style="color: #dc2626;"></strong></div>
                <div>Tồn kho: <strong id="scanInfoTonKho"></strong></div>
                <div>Trạng thái: <span id="scanInfoTrangThai" class="category-status"></span></div>
            </div>
        </div>
        <div class="form-actions" style="margin-top: 20px;">
            <button type="button" class="add-new-btn" id="btnEditFromScan"><i class="fas fa-edit"></i> Mở Form Sửa</button>
            <button type="button" class="cancel-btn add-new-btn" id="btnOkCloseInfo" style="background-color: #6c757d;">Xong</button>
        </div>
    </div>
</div>

<div id="hiddenQrContainer" style="display: none; visibility: hidden;"></div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const modal = document.getElementById('updateModal');
        const closeBtn = modal.querySelector('.close-btn');
        const cancelBtn = modal.querySelector('.cancel-btn');
        const editBtns = document.querySelectorAll('.edit-btn');
        const form = document.getElementById('updateForm');
        const imagePreview = document.getElementById('imagePreview');
        const defaultImgSrc = "https://images.placeholders.dev/?width=100&height=100&text=No+Image";
        const modalQrContainer = document.getElementById('modalQrcode');

        document.querySelectorAll('.JS-variant-status-toggle').forEach(toggle => {
            toggle.addEventListener('change', function() {
                const variantId = this.getAttribute('data-id');
                const isChecked = this.checked;
                const statusValue = isChecked ? 1 : 0;
                const statusText = isChecked ? "Đang kinh doanh" : "Ngừng bán";
                const row = this.closest('tr');
                const textLabel = row.querySelector('.JS-variant-status-text');

                row.dataset.trangThai = statusValue;

                fetch('${pageContext.request.contextPath}/SanPhamChiTiet/update-status', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `id=\${variantId}&trangThai=\${statusValue}`
                })
                    .then(response => {
                        if (response.ok) {
                            if (textLabel) {
                                textLabel.textContent = statusText;
                                textLabel.className = `category-status JS-variant-status-text \${isChecked ? 'status-active' : 'status-inactive'}`;
                            }
                        } else {
                            alert("Cập nhật thất bại!");
                            this.checked = !isChecked;
                        }
                    }).catch(() => { alert("Lỗi kết nối mạng!"); this.checked = !isChecked; });
            });
        });

        editBtns.forEach(button => {
            button.addEventListener('click', function () {
                const row = this.closest('tr');
                openEditModalForForm(row.dataset);
            });
        });

        function openEditModalForForm(dataset) {
            form.querySelector('#updateId').value = dataset.id;
            form.querySelector('#updateSanPhamId').value = dataset.sanPhamId;
            form.querySelector('#updateMa').value = dataset.ma;
            form.querySelector('#updateMauSac').value = dataset.mauSacId;
            form.querySelector('#updateKichCo').value = dataset.kichCoId;
            form.querySelector('#updateGiaNhap').value = dataset.giaNhap;
            form.querySelector('#updateGiaBan').value = parseInt(dataset.giaBan.replace(/[^0-9]/g, ''));
            form.querySelector('#updateSoLuongTon').value = dataset.soLuongTon;
            form.querySelector('#updateTrongLuong').value = dataset.trongLuong;
            form.querySelector('#updateTrangThai').value = dataset.trangThai;
            form.querySelector('#updateHinhAnhCu').value = dataset.hinhAnh;

            modalQrContainer.innerHTML = "";
            if(dataset.ma) {
                new QRCode(modalQrContainer, { text: dataset.ma, width: 110, height: 110 });
            }

            if (dataset.hinhAnh && dataset.hinhAnh !== 'null' && dataset.hinhAnh.trim() !== '') {
                imagePreview.src = '${pageContext.request.contextPath}/File_Anh/images/' + dataset.hinhAnh;
            } else { imagePreview.src = defaultImgSrc; }
            imagePreview.style.display = 'block';
            modal.style.display = 'block';
        }

        closeBtn.addEventListener('click', () => modal.style.display = 'none');
        cancelBtn.addEventListener('click', () => modal.style.display = 'none');

        // =========================================================================
        // 🔄 CAMERA SCANNER QR CODE
        // =========================================================================
        const scannerModal = document.getElementById('scannerModal');
        const scannedInfoModal = document.getElementById('scannedInfoModal');
        let html5QrcodeScanner = null;
        let scannedDatasetTarget = null;

        document.getElementById('btnOpenScanner').addEventListener('click', () => {
            scannerModal.style.display = 'block';

            const config = {
                fps: 25,
                qrbox: function(viewfinderWidth, viewfinderHeight) {
                    const minEdge = Math.min(viewfinderWidth, viewfinderHeight);
                    const boxSize = Math.floor(minEdge * 0.65);
                    return { width: boxSize, height: boxSize };
                },
                aspectRatio: 1.0,
                experimentalFeatures: {
                    useBarCodeDetectorIfSupported: true
                }
            };

            html5QrcodeScanner = new Html5QrcodeScanner("reader", config, false);
            html5QrcodeScanner.render((decodedText) => {
                html5QrcodeScanner.clear();
                scannerModal.style.display = 'none';

                const allRows = Array.from(document.querySelectorAll('.product-row'));
                const matchedRow = allRows.find(row => row.dataset.ma.trim() === decodedText.trim());

                if (matchedRow) {
                    scannedDatasetTarget = matchedRow.dataset;

                    document.getElementById('scanInfoMa').innerText = scannedDatasetTarget.ma;
                    document.getElementById('scanInfoMau').innerText = matchedRow.querySelector('td:nth-child(5)').innerText;
                    document.getElementById('scanInfoKichCo').innerText = matchedRow.querySelector('td:nth-child(6)').innerText;
                    document.getElementById('scanInfoGiaBan').innerText = scannedDatasetTarget.giaBan;
                    document.getElementById('scanInfoTonKho').innerText = scannedDatasetTarget.soLuongTon;

                    const statusText = parseInt(scannedDatasetTarget.trangThai) === 1 ? "Đang kinh doanh" : "Ngừng bán";
                    const statusClass = parseInt(scannedDatasetTarget.trangThai) === 1 ? "status-active" : "status-inactive";
                    document.getElementById('scanInfoTrangThai').innerText = statusText;
                    document.getElementById('scanInfoTrangThai').className = `category-status \${statusClass}`;

                    if (scannedDatasetTarget.hinhAnh && scannedDatasetTarget.hinhAnh !== 'null') {
                        document.getElementById('scanInfoImg').src = '${pageContext.request.contextPath}/File_Anh/images/' + scannedDatasetTarget.hinhAnh;
                    } else { document.getElementById('scanInfoImg').src = defaultImgSrc; }

                    scannedInfoModal.style.display = 'block';
                } else {
                    alert(`Không tìm thấy biến thể nào có mã khớp với mã QR hệ thống: \${decodedText}`);
                }
            });
        });

        document.getElementById('btnCloseScanner').addEventListener('click', () => {
            if(html5QrcodeScanner) html5QrcodeScanner.clear();
            scannerModal.style.display = 'none';
        });

        document.getElementById('btnCloseInfoModal').addEventListener('click', () => scannedInfoModal.style.display = 'none');
        document.getElementById('btnOkCloseInfo').addEventListener('click', () => scannedInfoModal.style.display = 'none');

        document.getElementById('btnEditFromScan').addEventListener('click', () => {
            scannedInfoModal.style.display = 'none';
            if(scannedDatasetTarget) openEditModalForForm(scannedDatasetTarget);
        });

        // =========================================================================
        // 📦 LOGIC CHECKBOX & KHỞI TẠO TRẠNG THÁI NÚT TẢI QR (.ZIP)
        // =========================================================================
        const selectAllCb = document.getElementById('selectAllVariants');
        const btnDownloadSelectedQR = document.getElementById('btnDownloadSelectedQR');

        function toggleDownloadQrButtonState() {
            const checkedBoxes = document.querySelectorAll('.variant-checkbox:checked');
            if (checkedBoxes.length > 0) {
                btnDownloadSelectedQR.removeAttribute('disabled');
            } else {
                btnDownloadSelectedQR.setAttribute('disabled', 'true');
            }
        }

        if (selectAllCb) {
            selectAllCb.addEventListener('change', function() {
                const isChecked = this.checked;
                document.querySelectorAll('.product-row').forEach(row => {
                    if (row.style.display !== 'none') {
                        const cb = row.querySelector('.variant-checkbox');
                        if (cb) cb.checked = isChecked;
                    }
                });
                toggleDownloadQrButtonState();
            });
        }

        // Lắng nghe sự thay đổi của từng checkbox biến thể trong bảng
        document.querySelector('.category-table tbody').addEventListener('change', function(e) {
            if (e.target && e.target.classList.contains('variant-checkbox')) {
                toggleDownloadQrButtonState();

                // Đồng bộ trạng thái của checkbox "Chọn tất cả" nếu bấm bỏ chọn thủ công
                if (!e.target.checked && selectAllCb) {
                    selectAllCb.checked = false;
                }
            }
        });

        btnDownloadSelectedQR.addEventListener('click', function() {
            const checkedBoxes = document.querySelectorAll('.variant-checkbox:checked');
            if (checkedBoxes.length === 0) return;

            const zip = new JSZip();
            const folder = zip.folder("QR_Codes_San_Pham");
            const hiddenContainer = document.getElementById('hiddenQrContainer');
            let processedCount = 0;

            checkedBoxes.forEach(cb => {
                const row = cb.closest('tr');
                const codeMa = row.dataset.ma;

                const tempDiv = document.createElement('div');
                hiddenContainer.appendChild(tempDiv);

                new QRCode(tempDiv, {
                    text: codeMa, width: 250, height: 250,
                    correctLevel: QRCode.CorrectLevel.H
                });

                setTimeout(() => {
                    const canvas = tempDiv.querySelector('canvas');
                    if (canvas) {
                        const dataUrl = canvas.toDataURL("image/png");
                        const base64Data = dataUrl.replace(/^data:image\/(png|jpg);base64,/, "");
                        folder.file(`QR_\${codeMa}.png`, base64Data, { base64: true });
                    }

                    tempDiv.remove();
                    processedCount++;

                    if (processedCount === checkedBoxes.length) {
                        zip.generateAsync({ type: "blob" }).then(function(content) {
                            saveAs(content, "Danh_Sach_Ma_QR_BienThe.zip");
                        });
                    }
                }, 150);
            });
        });

        // =========================================================================
        // ⚡ PHÂN TRANG JAVASCRIPT CLIENT
        // =========================================================================
        const tableBody = document.querySelector(".category-table tbody");
        if (tableBody) {
            const rows = Array.from(tableBody.querySelectorAll(".product-row"));
            if (rows.length > 0) {
                const rowsPerPage = 5;
                let currentPage = 1;
                const savedPage = sessionStorage.getItem('vbt_currentPage');
                if (savedPage) {
                    const totalPages = Math.ceil(rows.length / rowsPerPage);
                    if (parseInt(savedPage) <= totalPages) currentPage = parseInt(savedPage);
                }

                function displayTable(page) {
                    const start = (page - 1) * rowsPerPage;
                    const end = start + rowsPerPage;
                    rows.forEach((row, index) => {
                        if (index >= start && index < end) {
                            row.style.display = "";
                            const sttCell = row.querySelector(".js-stt-cell");
                            if (sttCell) sttCell.innerText = index + 1;
                        } else { row.style.display = "none"; }
                    });
                    if(selectAllCb) selectAllCb.checked = false;
                    document.querySelectorAll('.variant-checkbox').forEach(c => c.checked = false);
                    toggleDownloadQrButtonState(); // Đặt lại nút download khi đổi trang
                }

                function setupPagination() {
                    const pageCount = Math.ceil(rows.length / rowsPerPage);
                    const paginationContainer = document.getElementById("paginationContainer");
                    paginationContainer.innerHTML = "";
                    if (pageCount <= 1) return;

                    const prevBtn = document.createElement("button");
                    prevBtn.className = "sp-page-btn"; prevBtn.innerHTML = '<i class="fas fa-chevron-left"></i>';
                    prevBtn.type = "button"; prevBtn.disabled = currentPage === 1;
                    prevBtn.addEventListener("click", () => { if (currentPage > 1) { currentPage--; updatePagination(); } });
                    paginationContainer.appendChild(prevBtn);

                    for (let i = 1; i <= pageCount; i++) {
                        const btn = document.createElement("button");
                        btn.className = "sp-page-btn " + (i === currentPage ? "active" : "");
                        btn.innerText = i; btn.type = "button";
                        btn.addEventListener("click", () => { currentPage = i; updatePagination(); });
                        paginationContainer.appendChild(btn);
                    }

                    const nextBtn = document.createElement("button");
                    nextBtn.className = "sp-page-btn"; nextBtn.innerHTML = '<i class="fas fa-chevron-right"></i>';
                    nextBtn.type = "button"; nextBtn.disabled = currentPage === pageCount;
                    nextBtn.addEventListener("click", () => { if (currentPage < pageCount) { currentPage++; updatePagination(); } });
                    paginationContainer.appendChild(nextBtn);
                }

                function updatePagination() { displayTable(currentPage); setupPagination(); sessionStorage.setItem('vbt_currentPage', currentPage); }
                updatePagination();
            }
        }
    });

    function previewImage(event) {
        const file = event.target.files[0];
        const imagePreview = document.getElementById('imagePreview');
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) { imagePreview.src = e.target.result; }
            reader.readAsDataURL(file);
        }
    }
</script>
</body>
</html>