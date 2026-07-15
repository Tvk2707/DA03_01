<%
    request.setAttribute("pageTitle", "Thêm sản phẩm");
%>
<%
    String actionAttr = (String) request.getAttribute("action");
    if (actionAttr == null) {
        actionAttr = request.getParameter("action");
    }
    boolean isEdit = "edit".equals(actionAttr);

    request.setAttribute("formActionUrl", isEdit ? "update" : "insert");
    request.setAttribute("pageTitleForm", isEdit ? "Cập nhật sản phẩm" : "Thêm mới sản phẩm");
    request.setAttribute("buttonTextForm", isEdit ? "Cập nhật" : "Thêm mới");
    request.setAttribute("isEdit", isEdit);
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitleForm}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sanpham.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<%@include file="../layout/sidebar.jsp" %>
<div class="dashboard-container sp-wide-page">
    <%@include file="../layout/header.jsp" %>

    <div class="category-section sp-product-builder">
        <div class="category-header">
            <div>
                <h2 class="category-title">${pageTitleForm}</h2>
                <div class="sp-page-subtitle">Quản lý thông tin chung và tạo nhanh nhiều biến thể (màu sắc, kích cỡ) cho sản phẩm.</div>
            </div>
            <a href="${pageContext.request.contextPath}/SanPham" class="btn-reset" style="text-decoration:none;">
                <i class="fas fa-arrow-left"></i> Quay lại danh sách
            </a>
        </div>

        <c:if test="${not empty error}">
            <div class="sp-alert sp-alert-error">
                <i class="fas fa-circle-exclamation"></i> ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/SanPham/${formActionUrl}"
              method="post" enctype="multipart/form-data" id="sanPhamForm">

            <c:if test="${isEdit}">
                <input type="hidden" name="id" value="${sanPham.id}">
            </c:if>

            <!-- SECTION THÔNG TIN CHUNG SẢN PHẨM -->
            <section class="filter-section sp-card">
                <div class="filter-header">
                    <div class="filter-title">
                        <i class="fas fa-box"></i> Thông tin sản phẩm
                    </div>
                </div>

                <div class="sp-form-grid sp-form-grid-2">
                    <div class="filter-group">
                        <label class="filter-label">Mã sản phẩm <span class="required">*</span></label>
                        <input type="text" class="filter-input" id="maSanPham" name="maSanPham"
                               value="${sanPham.maSanPham}" ${isEdit ? 'readonly' : ''} required
                               placeholder="Ví dụ: SP001" maxlength="60">
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Tên sản phẩm <span class="required">*</span></label>
                        <input type="text" class="filter-input" name="tenSanPham"
                               value="${sanPham.tenSanPham}" required maxlength="200"
                               placeholder="Nhập tên sản phẩm">
                    </div>
                </div>

                <div class="sp-form-grid sp-form-grid-2">
                    <div class="filter-group">
                        <label class="filter-label">Danh mục <span class="required">*</span></label>
                        <select class="filter-select" name="danhMucId" required>
                            <option value="">-- Chọn danh mục --</option>
                            <c:forEach var="dm" items="${danhMucList}">
                                <option value="${dm.id}" ${dm.id == sanPham.danhMuc.id ? 'selected' : ''}>
                                        ${dm.tenDanhMuc}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Thương hiệu <span class="required">*</span></label>
                        <select class="filter-select" name="thuongHieuId" required>
                            <option value="">-- Chọn thương hiệu --</option>
                            <c:forEach var="th" items="${thuongHieuList}">
                                <option value="${th.id}" ${th.id == sanPham.thuongHieu.id ? 'selected' : ''}>
                                        ${th.tenThuongHieu}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="sp-form-grid sp-form-grid-2">
                    <div class="filter-group">
                        <label class="filter-label">Chất liệu <span class="required">*</span></label>
                        <select class="filter-select" name="chatLieuId" required>
                            <option value="">-- Chọn chất liệu --</option>
                            <c:forEach var="cl" items="${chatLieuList}">
                                <option value="${cl.id}" ${cl.id == sanPham.chatLieu.id ? 'selected' : ''}>
                                        ${cl.tenChatLieu}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Kiểu dáng <span class="required">*</span></label>
                        <select class="filter-select" name="kieuDangId" required>
                            <option value="">-- Chọn kiểu dáng --</option>
                            <c:forEach var="kd" items="${kieuDangList}">
                                <option value="${kd.id}" ${kd.id == sanPham.kieuDang.id ? 'selected' : ''}>
                                        ${kd.tenKieuDang}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="sp-form-grid sp-form-grid-2">
                    <div class="filter-group">
                        <label class="filter-label">Gọng kính <span class="required">*</span></label>
                        <select class="filter-select" name="gongKinhId" required>
                            <option value="">-- Chọn gọng kính --</option>
                            <c:forEach var="gk" items="${gongKinhList}">
                                <option value="${gk.id}" ${gk.id == sanPham.gongKinh.id ? 'selected' : ''}>
                                        ${gk.hinhDangGong.hinhDang} - ${gk.kieuQuaiKinh.kieuQuai}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Tròng kính <span class="required">*</span></label>
                        <select class="filter-select" name="trongKinhId" required>
                            <option value="">-- Chọn tròng kính --</option>
                            <c:forEach var="tk" items="${trongKinhList}">
                                <option value="${tk.id}" ${tk.id == sanPham.trongKinh.id ? 'selected' : ''}>
                                        ${tk.loaiTrong}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="sp-form-grid sp-form-grid-2">
                    <div class="filter-group">
                        <label class="filter-label">Mô tả chi tiết</label>
                        <textarea class="filter-input" name="moTaChiTiet" rows="3"
                                  maxlength="1000" placeholder="Nhập mô tả chi tiết">${sanPham.moTaChiTiet}</textarea>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Trạng thái sản phẩm</label>
                        <select class="filter-select" name="trangThai">
                            <option value="1" ${sanPham.trangThai == 1 ? 'selected' : ''}>Hoạt động</option>
                            <option value="0" ${sanPham.trangThai == 0 ? 'selected' : ''}>Ngừng bán</option>
                        </select>
                    </div>
                </div>
            </section>

            <!-- SECTION BỘ CHỌN THUỘC TÍNH ĐỂ TẠO BIẾN THỂ -->
            <section class="filter-section sp-card">
                <div class="filter-header">
                    <div class="filter-title">
                        <i class="fas fa-layer-group"></i> Chọn thuộc tính biến thể
                    </div>
                </div>

                <select id="mauSacSource" class="sp-source-select" multiple aria-hidden="true">
                    <c:forEach var="ms" items="${mauSacList}">
                        <option value="${ms.id}" data-code="${ms.maMau}">${ms.tenMau}</option>
                    </c:forEach>
                </select>
                <select id="kichCoSource" class="sp-source-select" multiple aria-hidden="true">
                    <c:forEach var="kc" items="${kichCoList}">
                        <option value="${kc.id}">${kc.tenKichCo}</option>
                    </c:forEach>
                </select>

                <div class="sp-variant-builder-grid">
                    <div class="filter-group sp-relative">
                        <label class="filter-label">Màu sắc <span class="required">*</span></label>
                        <div class="sp-tag-input-box" id="colorInputBox">
                            <div class="sp-tag-list" id="colorSelectedTags"></div>
                            <input type="text" class="sp-ghost-input" id="colorSearch" placeholder="Chọn màu sắc..." readonly>
                            <i class="fas fa-chevron-down sp-select-icon"></i>
                        </div>
                        <div class="sp-dropdown-menu sp-hidden" id="colorDropdown">
                            <div class="sp-dropdown-list" id="colorOptions"></div>
                        </div>
                    </div>

                    <div class="filter-group sp-relative">
                        <label class="filter-label">Kích cỡ <span class="required">*</span></label>
                        <div class="sp-tag-input-box" id="sizeInputBox">
                            <div class="sp-tag-list" id="sizeSelectedTags"></div>
                            <input type="text" class="sp-ghost-input" id="sizeSearch" placeholder="Chọn kích cỡ..." readonly>
                            <i class="fas fa-chevron-down sp-select-icon"></i>
                        </div>
                        <div class="sp-dropdown-menu sp-hidden" id="sizeDropdown">
                            <div class="sp-dropdown-list" id="sizeOptions"></div>
                        </div>
                    </div>
                </div>

                <div class="sp-variant-summary">
                    <div>
                        <span>Đã chọn</span>
                        <strong id="selectedColorCount">0 màu</strong>
                        <strong id="selectedSizeCount">0 kích cỡ</strong>
                    </div>
                    <div class="sp-create-count">Sẽ tạo <strong id="variantCount">0</strong> biến thể</div>
                    <button type="button" class="sp-generate-btn" id="generateVariantsBtn">
                        <i class="fas fa-bolt"></i> Tạo biến thể tự động
                    </button>
                </div>
            </section>

            <!-- SECTION DANH SÁCH BIẾN THỂ (CHIA BOX THEO TỪNG MÀU SẮC) -->
            <section class="sp-hidden" id="variantListCard">
                <div class="variant-list-head-tools">
                    <h3 class="variant-list-card-title"><i class="fas fa-table-list"></i> Danh sách biến thể theo màu sắc</h3>
                    <button type="button" class="sp-danger-btn" id="clearVariantsBtn">
                        <i class="fas fa-trash-alt"></i> Xóa tất cả biến thể
                    </button>
                </div>

                <!-- Các card nhóm màu sẽ được render tự động bằng JS vào đây -->
                <div id="colorVariantCardsContainer"></div>
            </section>

            <!-- SECTION ẢNH THEO MÀU SẮC DẠNG GRID GỌN GÀNG PHÍA DƯỚI -->
            <section class="filter-section sp-card sp-hidden" id="colorImageCard">
                <div class="filter-header">
                    <div class="filter-title" style="font-size: 18px; font-weight: bold;">
                        📸 Ảnh theo màu sắc
                    </div>
                </div>
                <div class="sp-image-grid" id="colorImageGrid"></div>
            </section>

            <!-- BOTTOM BAR ACTIONS -->
            <div class="sp-bottom-actions">
                <a href="${pageContext.request.contextPath}/SanPham" class="btn-reset" style="text-decoration:none;">
                    <i class="fas fa-times"></i> Hủy
                </a>
                <button type="submit" class="add-new-btn" id="saveBtn">
                    <i class="fas fa-check"></i> ${buttonTextForm}
                </button>
            </div>

        </form>
    </div>
</div>

<!-- HOP THOAI POPUP NHẬP NHANH GIÁ TRỊ THEO NHÓM -->
<div class="modal-backdrop sp-hidden" id="bulkApplyModal">
    <div class="modal-box">
        <div class="modal-header">
            <h4 class="modal-title" id="bulkModalTitle">Áp dụng giá trị cho nhóm</h4>
            <button type="button" class="modal-close-btn" id="closeModalBtn">&times;</button>
        </div>
        <div class="modal-body">
            <input type="hidden" id="bulkModalColorId">
            <div class="filter-group" style="margin-bottom: 12px;">
                <label class="filter-label">Giá nhập chung</label>
                <input type="number" min="0" class="filter-input" id="modalGiaNhap" placeholder="Nhập giá nhập">
            </div>
            <div class="filter-group" style="margin-bottom: 12px;">
                <label class="filter-label">Giá bán chung</label>
                <input type="number" min="0" class="filter-input" id="modalGiaBan" placeholder="Nhập giá bán">
            </div>
            <div class="filter-group" style="margin-bottom: 12px;">
                <label class="filter-label">Số lượng tồn kho chung</label>
                <input type="number" min="0" class="filter-input" id="modalTonKho" placeholder="Nhập tồn kho">
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn-reset" id="cancelModalBtn">Hủy</button>
            <button type="button" class="add-new-btn" id="confirmModalBtn">Đồng ý áp dụng</button>
        </div>
    </div>
</div>

<style>
    /* --- STYLE CHUNG & LAYOUT --- */
    .sp-wide-page {
        max-width: 1280px;
        margin-left: 280px;
    }
    .sp-product-builder {
        background: #f7f8fb;
        border-radius: 14px;
        padding-bottom: 80px;
    }
    .sp-page-subtitle {
        margin-top: 6px;
        color: #6b7280;
        font-size: 14px;
    }
    .sp-card {
        border: 1px solid #e5e7eb;
        border-radius: 12px;
        box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
        margin-bottom: 18px;
        padding: 20px;
        background: #fff;
    }
    .sp-alert {
        padding: 12px 16px;
        border-radius: 8px;
        margin-bottom: 16px;
        display: flex;
        align-items: center;
        gap: 8px;
    }
    .sp-alert-error {
        background: #fdecea;
        color: #b3261e;
    }
    .required { color: #dc2626; }

    /* --- FORM GRID --- */
    .sp-form-grid {
        display: grid;
        gap: 16px;
        margin-bottom: 16px;
    }
    .sp-form-grid-2 { grid-template-columns: repeat(2, minmax(0, 1fr)); }

    .filter-group {
        display: flex;
        flex-direction: column;
        gap: 6px;
    }
    .sp-relative { position: relative; }
    .filter-label {
        font-size: 14px;
        font-weight: 600;
        color: #374151;
    }
    .filter-input, .filter-select {
        width: 100%;
        padding: 10px 12px;
        border: 1px solid #d1d5db;
        border-radius: 8px;
        font-size: 14px;
        color: #111827;
        background: #fff;
        transition: border-color 0.2s, box-shadow 0.2s;
        box-sizing: border-box;
    }
    .filter-input:focus, .filter-select:focus {
        outline: none;
        border-color:  #b4975a;
        box-shadow: 0 0 0 3px rgba(180, 151, 90, 0.1);
    }
    textarea.filter-input { resize: vertical; min-height: 80px; }

    /* --- TAG INPUT CHỌN BIẾN THỂ GỌN GÀNG --- */
    .sp-source-select { display: none; }

    .sp-tag-input-box {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        gap: 6px;
        min-height: 42px;
        padding: 6px 36px 6px 12px;
        border: 1px solid #d1d5db;
        border-radius: 8px;
        background: #fff;
        cursor: pointer;
        position: relative;
        transition: border-color 0.2s, box-shadow 0.2s;
    }
    .sp-tag-input-box:hover { border-color: #b4975a; }
    .sp-tag-input-box.focus {
        border-color: #b4975a;
        box-shadow: 0 0 0 3px rgba(180, 151, 90, 0.1);
    }

    .sp-select-icon {
        position: absolute;
        right: 12px;
        top: 50%;
        transform: translateY(-50%);
        color: #9ca3af;
        font-size: 12px;
        pointer-events: none;
        transition: transform 0.2s;
    }
    .sp-tag-input-box.focus .sp-select-icon { transform: translateY(-50%) rotate(180deg); }

    .sp-tag-list { display: flex; flex-wrap: wrap; gap: 6px; }

    .sp-pill {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 4px 10px;
        border-radius: 6px;
        background: #f0f4ff;
        color: #1e3a8a;
        font-size: 13px;
        font-weight: 500;
        border: 1px solid #dbeafe;
    }
    .sp-pill-remove {
        border: none;
        background: transparent;
        color: #93c5fd;
        cursor: pointer;
        padding: 0;
        font-size: 11px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .sp-pill-remove:hover { color: #1e3a8a; }

    .sp-ghost-input {
        border: none;
        outline: none;
        padding: 0;
        font-size: 14px;
        color: #111827;
        background: transparent;
        cursor: pointer;
        flex-grow: 1;
        min-width: 100px;
    }

    .sp-dropdown-menu {
        position: absolute;
        top: 100%;
        left: 0;
        right: 0;
        z-index: 100;
        margin-top: 4px;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        background: #fff;
        box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
        max-height: 200px;
        overflow-y: auto;
    }
    .sp-dropdown-list { padding: 6px; }
    .sp-dropdown-option {
        width: 100%;
        padding: 8px 12px;
        border: none;
        background: transparent;
        text-align: left;
        font-size: 14px;
        color: #374151;
        cursor: pointer;
        border-radius: 6px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    /* Custom style cho nút Xóa tất cả biến thể */
    .sp-danger-btn {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        padding: 8px 16px;
        background-color: #fff1f2; /* Nền hồng đỏ nhạt hiện đại */
        color: #e11d48;            /* Màu chữ đỏ đô nổi bật */
        border: 1px solid #fecdd3; /* Viền hồng nhạt */
        border-radius: 8px;        /* Bo góc mềm mại đồng bộ hệ thống */
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s ease;
        outline: none;
    }

    /* Hiệu ứng khi di chuột qua (Hover) */
    .sp-danger-btn:hover {
        background-color: #ffe4e6; /* Nền đậm hơn một chút */
        color: #be123c;            /* Màu đỏ đậm hơn */
        border-color: #fda4af;
        box-shadow: 0 2px 6px rgba(225, 29, 72, 0.08); /* Đổ bóng nhẹ */
    }

    /* Hiệu ứng khi click vào (Active) */
    .sp-danger-btn:active {
        transform: scale(0.97); /* Hiệu ứng lún nút nhẹ cực chuyên nghiệp */
    }

    /* Căn chỉnh icon thùng rác bên trong nút */
    .sp-danger-btn i {
        font-size: 14px;
    }
    .sp-dropdown-option:hover { background: #f3f4f6; }
    .sp-dropdown-option.selected {
        background: #f0f4ff;
        color: #4a6cf7;
        font-weight: 600;
    }
    .sp-dropdown-option i { font-size: 12px; display: none; }
    .sp-dropdown-option.selected i { display: block; }

    .sp-variant-builder-grid {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 18px;
    }

    .sp-variant-summary {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
        margin-top: 18px;
        padding: 14px;
        border-radius: 10px;
        background: #f9fafb;
        border: 1px solid #e5e7eb;
    }
    .sp-variant-summary strong { margin-left: 10px; color: #111827; }
    .sp-create-count { color: #4b5563; }

    .sp-generate-btn {
        border: none;
        border-radius: 10px;
        padding: 12px 18px;
        color: #fff;
        background: #b4975a;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 8px;
        font-weight: 700;
        white-space: nowrap;
        transition: background 0.2s, transform 0.1s;
    }
    .sp-generate-btn:active { transform: scale(0.98); }

    /* --- LAYOUT PHÂN CHIA NHÓM BIẾN THỂ THEO MÀU SẮC (DŨNG THEO ẢNH MẪU) --- */
    .variant-list-head-tools {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin: 24px 0 14px 0;
    }
    .variant-list-card-title {
        font-size: 18px;
        font-weight: bold;
        color: #1f2937;
        display: flex;
        align-items: center;
        gap: 8px;
    }
    .sp-color-group-card {
        background: #fff;
        border: 1px solid #e2e8f0;
        border-radius: 12px;
        margin-bottom: 20px;
        overflow: hidden;
        box-shadow: 0 4px 12px rgba(0,0,0,0.02);
    }
    .color-group-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 14px 20px;
        background: #f8fafc;
        border-bottom: 1px solid #e2e8f0;
    }
    .color-group-title {
        font-size: 15px;
        font-weight: bold;
        color: #1e3a8a;
        display: flex;
        align-items: center;
        gap: 8px;
    }
    .color-group-title span {
        font-size: 12px;
        color: #64748b;
        font-weight: normal;
    }
    .btn-apply-group {
        border: none;
        background: #b4975a;
        color: #fff;
        font-weight: 600;
        font-size: 13px;
        padding: 8px 14px;
        border-radius: 6px;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 6px;
        transition: background 0.2s;
    }
    .btn-apply-group:hover { background: #b4975a; }

    /* LAYOUT BẢNG BÊN TRONG NHÓM MÀU */
    .variant-group-table {
        width: 100%;
        border-collapse: collapse;
    }
    .variant-group-table th {
        background: #ffffff;
        color: #475569;
        font-weight: 600;
        font-size: 13px;
        padding: 12px 20px;
        text-align: left;
        border-bottom: 1px solid #f1f5f9;
    }
    .variant-group-table td {
        padding: 10px 20px;
        border-bottom: 1px solid #f1f5f9;
        vertical-align: middle;
    }
    .variant-group-table tr:last-child td {
        border-bottom: none;
    }
    .size-input-read {
        background: #f8fafc;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        padding: 10px 14px;
        font-weight: bold;
        color: #1e293b;
        width: 100%;
        box-sizing: border-box;
        text-align: center;
    }
    .sp-hidden { display: none !important; }

    /* NÚT XÓA BIẾN THỂ TRONG CARD */
    .btn-delete-variant {
        border: none;
        background: #fee2e2;
        color: #ef4444;
        width: 32px;
        height: 32px;
        border-radius: 6px;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: background 0.2s;
    }
    .btn-delete-variant:hover { background: #fca5a5; }

    /* --- GIAO DIỆN HIỂN THỊ ẢNH THEO MÀU SẮC DƯỚI CARD --- */
    .sp-image-grid {
        display: grid;
        grid-template-columns: repeat(4, minmax(0, 1fr));
        gap: 16px;
        margin-top: 10px;
    }
    .sp-upload-card {
        border: 1px solid #e2e8f0;
        border-radius: 12px;
        padding: 14px;
        background: #fff;
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        min-height: 180px;
        justify-content: space-between;
    }
    .sp-upload-title {
        font-weight: bold;
        color: #1e293b;
        font-size: 14px;
        margin-bottom: 10px;
        display: flex;
        align-items: center;
        gap: 6px;
    }
    .sp-upload-zone-box {
        border: 2px dashed #cbd5e1;
        border-radius: 8px;
        width: 100%;
        flex-grow: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        padding: 10px;
        background: #f8fafc;
        transition: background 0.2s, border-color 0.2s;
    }
    .sp-upload-zone-box:hover {
        border-color: #b4975a;
        background: #f1f5f9;
    }
    .sp-upload-zone-box img {
        width: 44px;
        height: 44px;
        object-fit: contain;
        margin-bottom: 6px;
    }
    .sp-upload-zone-box span {
        font-size: 12px;
        color: #94a3b8;
    }

    /* --- STYLE MODAL POPUP --- */
    .modal-backdrop {
        position: fixed;
        top: 0; left: 0; right: 0; bottom: 0;
        background: rgba(15, 23, 42, 0.4);
        z-index: 1000;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .modal-box {
        background: #fff;
        border-radius: 12px;
        width: 100%;
        max-width: 420px;
        box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        overflow: hidden;
    }
    .modal-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 20px;
        background: #f8fafc;
        border-bottom: 1px solid #e2e8f0;
    }
    .modal-title { font-size: 16px; font-weight: bold; color: #1e293b; margin: 0; }
    .modal-close-btn { background: none; border: none; font-size: 20px; cursor: pointer; color: #94a3b8; }
    .modal-body { padding: 20px; }
    .modal-footer {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
        padding: 14px 20px;
        background: #f8fafc;
        border-top: 1px solid #e2e8f0;
    }

    /* --- BOTTOM ACTIONS BAR --- */
    .sp-bottom-actions {
        position: sticky;
        bottom: 0;
        z-index: 10;
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        padding: 16px 20px;
        background: linear-gradient(180deg, rgba(247, 248, 251, 0), #f7f8fb 30%);
        border-top: 1px solid #e5e7eb;
        margin: 0 -20px -20px;
        border-radius: 0 0 12px 12px;
    }
    .btn-reset {
        padding: 10px 18px;
        border: 1px solid #d1d5db;
        border-radius: 8px;
        background: #fff;
        color: #374151;
        font-weight: 600;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 8px;
        transition: all 0.2s;
        text-decoration: none;
    }
    .btn-reset:hover { background: #f3f4f6; border-color: #9ca3af; }

    .add-new-btn {
        padding: 10px 24px;
        border: none;
        border-radius: 8px;
        background: #b4975a;
        color: #fff;
        font-weight: 600;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 8px;
        transition: background 0.2s, transform 0.1s;
    }
    .add-new-btn:hover { background: #a3864b; }
    .add-new-btn:active { transform: scale(0.98); }

    /* RESPONSIVE */
    @media (max-width: 1180px) {
        .sp-form-grid-2, .sp-image-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
    }
    @media (max-width: 900px) {
        .sp-wide-page { margin-left: 0; padding: 16px; }
        .sp-form-grid-2, .sp-variant-builder-grid, .sp-image-grid { grid-template-columns: 1fr; }
        .sp-variant-summary { align-items: stretch; flex-direction: column; }
        .sp-generate-btn { justify-content: center; }
        .sp-bottom-actions { margin: 0 -16px -16px; padding: 16px; }
    }
</style>

<script>
    const variantState = {
        colors: [],
        sizes: [],
        variants: [],
        imageFilesByColor: new Map()
    };

    const sourceOptions = {
        colors: readSourceOptions('mauSacSource'),
        sizes: readSourceOptions('kichCoSource')
    };

    document.addEventListener('DOMContentLoaded', function () {
        // Khởi tạo các ô Tag Input kết hợp Dropdown gọn gàng
        initCustomSelect('colors', 'colorInputBox', 'colorDropdown', 'colorOptions', 'colorSelectedTags', 'colorSearch');
        initCustomSelect('sizes', 'sizeInputBox', 'sizeDropdown', 'sizeOptions', 'sizeSelectedTags', 'sizeSearch');

        bindVariantActions();
        updateVariantSummary();
        initModalEvents();
    });

    function readSourceOptions(selectId) {
        const select = document.getElementById(selectId);
        return Array.from(select.options).map(function (option) {
            return {
                id: option.value,
                label: option.text.trim(),
                code: option.dataset.code || option.text.trim()
            };
        });
    }

    // --- HÀM XỬ LÝ DROPDOWN & TAGS GỌN GÀNG ---
    function initCustomSelect(type, inputBoxId, dropdownId, optionsListId, selectedTagsId, ghostInputId) {
        const inputBox = document.getElementById(inputBoxId);
        const dropdown = document.getElementById(dropdownId);
        const ghostInput = document.getElementById(ghostInputId);

        inputBox.addEventListener('click', function (e) {
            e.stopPropagation();
            closeAllDropdowns(dropdownId);
            dropdown.classList.toggle('sp-hidden');
            inputBox.classList.toggle('focus');
        });

        document.addEventListener('click', function () {
            dropdown.classList.add('sp-hidden');
            inputBox.classList.remove('focus');
        });

        renderDropdownOptions(type, optionsListId, selectedTagsId, ghostInput);
    }

    function closeAllDropdowns(exceptId) {
        const dropdowns = ['colorDropdown', 'sizeDropdown'];
        const boxes = ['colorInputBox', 'sizeInputBox'];
        dropdowns.forEach((id, idx) => {
            if (id !== exceptId) {
                document.getElementById(id).classList.add('sp-hidden');
                document.getElementById(boxes[idx]).classList.remove('focus');
            }
        });
    }

    function renderDropdownOptions(type, optionsListId, selectedTagsId, ghostInput) {
        const list = document.getElementById(optionsListId);
        list.innerHTML = '';

        sourceOptions[type].forEach(function (item) {
            const optionBtn = document.createElement('button');
            optionBtn.type = 'button';
            optionBtn.className = 'sp-dropdown-option';

            const isSelected = variantState[type].some(selected => selected.id === item.id);
            if (isSelected) optionBtn.classList.add('selected');

            optionBtn.innerHTML = '<span>' + escapeHtml(item.label) + '</span><i class="fas fa-check"></i>';

            optionBtn.addEventListener('click', function (e) {
                e.stopPropagation();
                toggleSelection(type, item);
                optionBtn.classList.toggle('selected');
                renderSelectedTags(type, selectedTagsId, optionsListId, ghostInput);
                updateVariantSummary();
            });

            list.appendChild(optionBtn);
        });
    }

    function renderSelectedTags(type, selectedTagsId, optionsListId, ghostInput) {
        const tagContainer = document.getElementById(selectedTagsId);
        tagContainer.innerHTML = '';

        if (variantState[type].length > 0) {
            ghostInput.placeholder = '';
        } else {
            ghostInput.placeholder = type === 'colors' ? 'Chọn màu sắc...' : 'Chọn kích cỡ...';
        }

        variantState[type].forEach(function (item) {
            const pill = document.createElement('span');
            pill.className = 'sp-pill';
            pill.innerHTML = '<span>' + escapeHtml(item.label) + '</span><button type="button" class="sp-pill-remove"><i class="fas fa-times"></i></button>';

            pill.querySelector('button').addEventListener('click', function (e) {
                e.stopPropagation();
                toggleSelection(type, item);
                renderSelectedTags(type, selectedTagsId, optionsListId, ghostInput);

                const dropdownList = document.getElementById(optionsListId);
                Array.from(dropdownList.children).forEach(optionNode => {
                    if (optionNode.textContent.trim() === item.label) {
                        optionNode.classList.remove('selected');
                    }
                });

                updateVariantSummary();
            });

            tagContainer.appendChild(pill);
        });
    }

    function toggleSelection(type, item) {
        const index = variantState[type].findIndex(function (selected) { return selected.id === item.id; });
        if (index >= 0) {
            variantState[type].splice(index, 1);
        } else {
            variantState[type].push(item);
        }
    }

    function updateVariantSummary() {
        const colorCount = variantState.colors.length;
        const sizeCount = variantState.sizes.length;
        document.getElementById('selectedColorCount').textContent = colorCount + ' màu';
        document.getElementById('selectedSizeCount').textContent = sizeCount + ' kích cỡ';
        document.getElementById('variantCount').textContent = colorCount * sizeCount;
    }

    function bindVariantActions() {
        document.getElementById('generateVariantsBtn').addEventListener('click', generateVariants);
        document.getElementById('clearVariantsBtn').addEventListener('click', clearAllVariants);
        document.getElementById('sanPhamForm').addEventListener('submit', validateBeforeSubmit);
        document.getElementById('maSanPham').addEventListener('input', refreshVariantCodes);
    }

    function generateVariants() {
        if (variantState.colors.length === 0 || variantState.sizes.length === 0) {
            alert('Vui lòng chọn ít nhất 1 màu sắc và 1 kích cỡ.');
            return;
        }

        variantState.variants = [];
        variantState.colors.forEach(function (color) {
            variantState.sizes.forEach(function (size) {
                variantState.variants.push({
                    color: color,
                    size: size,
                    ma: buildVariantCode(color, size),
                    giaNhap: '',
                    giaBan: '',
                    soLuongTon: '10', // mac dinh giong anh mau ban gui
                    trongLuong: '0',
                    trangThai: '1'
                });
            });
        });

        renderColorCards();
        renderColorUploadCards();
        document.getElementById('variantListCard').classList.remove('sp-hidden');
        document.getElementById('colorImageCard').classList.remove('sp-hidden');
    }

    /* --- RENDER CARD THEO TỪNG MÀU SẮC (DŨNG THEO LAYOUT ẢNH MẪU) --- */
    function renderColorCards() {
        const container = document.getElementById('colorVariantCardsContainer');
        container.innerHTML = '';

        if (variantState.variants.length === 0) return;

        // Group các biến thể theo color.id
        const grouped = {};
        variantState.variants.forEach(function (v) {
            if (!grouped[v.color.id]) {
                grouped[v.color.id] = {
                    color: v.color,
                    items: []
                };
            }
            grouped[v.color.id].items.push(v);
        });

        Object.values(grouped).forEach(function (group) {
            const card = document.createElement('div');
            card.className = 'sp-color-group-card';
            card.dataset.colorId = group.color.id;

            let cardHtml =
                '<div class="color-group-header">' +
                '<div class="color-group-title">⚫ ' + escapeHtml(group.color.label) + ' <span>(' + group.items.length + ' kích cỡ)</span></div>' +
                '<button type="button" class="btn-apply-group" onclick="openBulkApplyModal(\'' + escapeAttr(group.color.id) + '\', \'' + escapeAttr(group.color.label) + '\')">' +
                '⚡ Áp dụng nhóm' +
                '</button>' +
                '</div>' +
                '<div class="sp-table-scroll" style="border: none; border-radius: 0;">' +
                '<table class="variant-group-table">' +
                '<thead>' +
                '<tr>' +
                '<th style="width: 30%;">Kích cỡ</th>' +
                '<th style="width: 30%;">Số lượng tồn</th>' +
                '<th style="width: 30%;">Đơn giá (Bán / Nhập)</th>' +
                '<th style="width: 10%; text-align: center;"></th>' +
                '</tr>' +
                '</thead>' +
                '<tbody id="tbody_' + escapeAttr(group.color.id) + '">';

            cardHtml += '</tbody></table></div>';
            card.innerHTML = cardHtml;
            container.appendChild(card);

            // Render dòng biến thể cụ thể của màu đó
            const tbody = document.getElementById('tbody_' + group.color.id);
            group.items.forEach(function (variant) {
                const tr = document.createElement('tr');
                tr.innerHTML =
                    '<td>' +
                    '<input type="hidden" name="mauSacId[]" value="' + escapeAttr(variant.color.id) + '">' +
                    '<input type="hidden" name="kichCoId[]" value="' + escapeAttr(variant.size.id) + '">' +
                    '<input type="hidden" name="ma[]" value="' + escapeAttr(variant.ma) + '">' +
                    '<div class="size-input-read">' + escapeHtml(variant.size.label) + '</div>' +
                    '</td>' +
                    '<td>' +
                    '<input type="number" min="0" class="filter-input js-ton-kho" name="soLuongTon[]" value="' + escapeAttr(variant.soLuongTon) + '" required placeholder="Nhập tồn kho">' +
                    '</td>' +
                    '<td>' +
                    '<div style="display: flex; gap: 8px;">' +
                    '<input type="number" min="0" class="filter-input js-gia-ban" name="giaBan[]" value="' + escapeAttr(variant.giaBan) + '" required placeholder="Giá bán">' +
                    '<input type="number" min="0" class="filter-input js-gia-nhap" name="giaNhap[]" value="' + escapeAttr(variant.giaNhap) + '" required placeholder="Giá nhập">' +
                    '<input type="hidden" name="trongLuong[]" value="0">' +
                    '<input type="hidden" name="trangThaiChiTiet[]" value="1">' +
                    '</div>' +
                    '</td>' +
                    '<td>' +
                    '<button type="button" class="btn-delete-variant" title="Xóa dòng này"><i class="fas fa-times"></i></button>' +
                    '</td>';

                // Sự kiện xóa dòng đơn lẻ
                tr.querySelector('.btn-delete-variant').addEventListener('click', function() {
                    const idx = variantState.variants.indexOf(variant);
                    if (idx > -1) {
                        variantState.variants.splice(idx, 1);
                    }
                    renderColorCards();
                    toggleGeneratedCards();
                });

                tbody.appendChild(tr);
            });
        });
    }

    /* --- CHỨC NĂNG POPUP NHẬP NHANH GIÁ TRỊ THEO NHÓM MÀU SẮC --- */
    function initModalEvents() {
        document.getElementById('closeModalBtn').addEventListener('click', hideModal);
        document.getElementById('cancelModalBtn').addEventListener('click', hideModal);
        document.getElementById('confirmModalBtn').addEventListener('click', applyBulkGroupValues);
    }

    window.openBulkApplyModal = function(colorId, colorLabel) {
        document.getElementById('bulkModalColorId').value = colorId;
        document.getElementById('bulkModalTitle').textContent = 'Áp dụng nhanh cho nhóm màu: ' + colorLabel;
        document.getElementById('modalGiaNhap').value = '';
        document.getElementById('modalGiaBan').value = '';
        document.getElementById('modalTonKho').value = '';
        document.getElementById('bulkApplyModal').classList.remove('sp-hidden');
    };

    function hideModal() {
        document.getElementById('bulkApplyModal').classList.add('sp-hidden');
    }

    function applyBulkGroupValues() {
        const colorId = document.getElementById('bulkModalColorId').value;
        const giaNhapVal = document.getElementById('modalGiaNhap').value;
        const giaBanVal = document.getElementById('modalGiaBan').value;
        const tonKhoVal = document.getElementById('modalTonKho').value;

        // Cập nhật lại state chính
        variantState.variants.forEach(function (v) {
            if (v.color.id === colorId) {
                if (giaNhapVal !== '') v.giaNhap = giaNhapVal;
                if (giaBanVal !== '') v.giaBan = giaBanVal;
                if (tonKhoVal !== '') v.soLuongTon = tonKhoVal;
            }
        });

        // Vẽ lại giao diện card nhóm màu
        renderColorCards();
        hideModal();
    }

    function clearAllVariants() {
        if (!confirm('Xóa toàn bộ danh sách biến thể?')) return;
        variantState.variants = [];
        document.getElementById('colorVariantCardsContainer').innerHTML = '';
        toggleGeneratedCards();
    }

    function toggleGeneratedCards() {
        const hasVariants = variantState.variants.length > 0;
        document.getElementById('variantListCard').classList.toggle('sp-hidden', !hasVariants);
        document.getElementById('colorImageCard').classList.toggle('sp-hidden', !hasVariants);
    }

    function refreshVariantCodes() {
        if (variantState.variants.length === 0) return;
        variantState.variants.forEach(function (variant) {
            variant.ma = buildVariantCode(variant.color, variant.size);
        });
        document.querySelectorAll('input[name="ma[]"]').forEach(function (input, index) {
            if (variantState.variants[index]) {
                input.value = variantState.variants[index].ma;
            }
        });
    }

    function buildVariantCode(color, size) {
        const productCode = normalizeCode(document.getElementById('maSanPham').value || 'SP');
        return productCode + '-' + normalizeCode(color.code || color.label) + '-' + normalizeCode(size.label);
    }

    /* --- RENDERING PHẦN HIỂN THỊ HÌNH ẢNH THEO MÀU SẮC (BLOCK VUÔNG BÊN DƯỚI) --- */
    function renderColorUploadCards() {
        const grid = document.getElementById('colorImageGrid');
        grid.innerHTML = '';

        variantState.colors.forEach(function (color) {
            const hasVariant = variantState.variants.some(function (v) { return v.color.id === color.id; });
            if (!hasVariant) return;

            const card = document.createElement('div');
            card.className = 'sp-upload-card';
            card.innerHTML =
                '<div class="sp-upload-title">⚫ ' + escapeHtml(color.label) + '</div>' +
                '<label class="sp-upload-zone-box" id="zone_' + escapeAttr(color.id) + '">' +
                '<input type="file" name="fileAnh_' + escapeAttr(color.id) + '" accept="image/*" multiple style="display:none;">' +
                '<img src="data:image/svg+xml;utf8,<svg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 24 24\' width=\'48\' height=\'48\'><path fill=\'%2394a3b8\' d=\'M19 5v14H5V5h14m0-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-4.86 8.86l-3 3.87L9 13.14 6 17h12l-3.86-5.14z\'/></svg>" id="img_prev_' + escapeAttr(color.id) + '">' +
                '<span id="txt_prev_' + escapeAttr(color.id) + '">Chưa có ảnh</span>' +
                '</label>';

            const input = card.querySelector('input[type="file"]');
            const zone = card.querySelector('.sp-upload-zone-box');
            const previewImg = card.querySelector('#img_prev_' + color.id);
            const previewTxt = card.querySelector('#txt_prev_' + color.id);

            input.addEventListener('change', function () {
                if (input.files && input.files[0]) {
                    const file = input.files[0];
                    previewImg.src = URL.createObjectURL(file);
                    previewTxt.textContent = "Đã chọn: " + file.name;
                }
            });

            grid.appendChild(card);
        });
    }

    function validateBeforeSubmit(event) {
        if (variantState.variants.length === 0) {
            event.preventDefault();
            alert('Vui lòng tạo ít nhất một biến thể trước khi lưu sản phẩm.');
        }
    }

    function normalizeText(value) {
        return (value || '')
            .toString()
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '')
            .toLowerCase()
            .trim();
    }

    function normalizeCode(value) {
        return normalizeText(value)
            .toUpperCase()
            .replace(/Đ/g, 'D')
            .replace(/[^A-Z0-9]+/g, '-')
            .replace(/^-+|-+$/g, '') || 'SP';
    }

    function escapeHtml(value) {
        return String(value || '')
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    function escapeAttr(value) {
        return escapeHtml(value);
    }
</script>
</body>
</html>