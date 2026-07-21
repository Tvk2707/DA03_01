<%
    request.setAttribute("pageTitle", "Sản phẩm");
    request.setAttribute("activeMenu", "product");
    request.setAttribute("activeSubMenu", "product");
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/sanpham.css?v=20260718-fullscreen">
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

        /* ========================================================== */
        /* 🛠️ CSS ĐỒNG BỘ SPACING LƯỚI 5 CỘT & POPOVER KHOẢNG GIÁ     */
        /* ========================================================== */
        .filter-grid {
            display: grid;
            grid-template-columns: repeat(5, 1fr); /* Định hình 5 cột hoàn hảo */
            gap: 16px; /* Hệ thống spacing đồng bộ 16px */
            align-items: flex-start;
        }

        .price-popover-container {
            position: relative;
            display: flex;
            flex-direction: column;
        }

        .price-dropdown-trigger {
            background: #ffffff;
            border: 1px solid #ccc;
            border-radius: 5px;
            padding: 10px;
            font-size: 14px;
            color: #374151;
            text-align: left;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
            height: 40px;
            box-sizing: border-box;
            width: 100%;
            transition: all 0.2s ease;
        }

        .price-dropdown-trigger:focus, .price-dropdown-trigger.open-active {
            border-color: #b4975a !important;
            box-shadow: 0 0 0 3px rgba(180, 151, 90, 0.15);
        }

        /* Khối Panel Overlay của Khoảng giá */
        .price-popover-panel {
            display: none;
            position: absolute;
            top: 45px;
            right: 0;
            width: 320px;
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            padding: 18px;
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
            z-index: 100;
            overflow: visible;
        }

        .price-popover-panel.show {
            display: block;
        }

        .quick-filter-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
            margin-bottom: 14px;
        }

        .filter-tag-btn {
            padding: 4px 10px;
            background-color: #f3f4f6;
            color: #4b5563;
            border: 1px solid #e5e7eb;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .filter-tag-btn:hover {
            background-color: #e5e7eb;
            color: #1f2937;
        }

        .filter-tag-btn.active {
            background-color: #b4975a;
            color: #ffffff;
            border-color: #b4975a;
        }

        .slider-container {
            position: relative;
            height: 6px;
            background: #e5e7eb;
            border-radius: 999px;
            margin: 25px 10px 20px 10px;
        }

        .slider-track-bar {
            position: absolute;
            height: 100%;
            background: #b4975a; /* Brand Color hệ thống */
            border-radius: 999px;
        }

        .range-input-slider {
            position: relative;
        }

        .range-input-slider input[type="range"] {
            position: absolute;
            width: 100%;
            height: 6px;
            top: -26px;
            background: none;
            pointer-events: none;
            -webkit-appearance: none;
            -moz-appearance: none;
        }

        .range-input-slider input[type="range"]::-webkit-slider-thumb {
            height: 18px;
            width: 18px;
            border-radius: 50%;
            background: #ffffff;
            border: 4px solid #b4975a;
            box-shadow: 0 2px 4px rgba(0,0,0,0.15);
            cursor: pointer;
            pointer-events: auto;
            -webkit-appearance: none;
        }

        .slider-tooltip {
            position: absolute;
            background: #b4975a;
            color: #ffffff;
            padding: 3px 6px;
            border-radius: 5px;
            font-size: 11px;
            font-weight: 600;
            white-space: nowrap;
            transform: translateX(-50%);
            top: -30px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            opacity: 0;
            visibility: hidden;
            transition: opacity 0.15s ease, visibility 0.15s ease;
        }

        .slider-tooltip::after {
            content: '';
            position: absolute;
            bottom: -4px;
            left: 50%;
            transform: translateX(-50%);
            border-width: 4px 4px 0;
            border-style: solid;
            border-color: #b4975a transparent transparent;
        }

        /* Tooltip hiển thị mượt mà khi hover vùng Panel hoặc đang active thanh kéo */
        .price-popover-panel:hover .slider-tooltip,
        .range-input-slider input[type="range"]:active ~ .slider-tooltip {
            opacity: 1;
            visibility: visible;
        }

        .slider-limit-label {
            position: absolute;
            top: 12px;
            font-size: 10px;
            color: #9ca3af;
        }

        .input-currency-container {
            display: flex;
            gap: 8px;
            align-items: center;
            margin-top: 12px;
        }

        .currency-field {
            flex: 1;
        }

        .currency-field span {
            font-size: 11px;
            color: #6b7280;
            font-weight: 600;
        }

        .filter-input:focus {
            border-color: #b4975a !important;
            outline: none;
            box-shadow: 0 0 0 3px rgba(180, 151, 90, 0.15);
        }

        .popover-actions {
            display: flex;
            justify-content: flex-end;
            gap: 8px;
            margin-top: 14px;
            padding-top: 10px;
            border-top: 1px solid #f3f4f6;
        }

        .popover-btn {
            padding: 6px 12px;
            font-size: 12px;
            font-weight: 600;
            border-radius: 6px;
            cursor: pointer;
            border: none;
        }

        .popover-btn-clear {
            background: #f3f4f6;
            color: #4b5563;
        }

        .popover-btn-apply {
            background: #b4975a;
            color: #ffffff;
        }

        /* ========================================================== */
        /* ✨ CẤU TRÚC TOAST NOTIFICATION GÓC TRÊN BÊN PHẢI MỚI      */
        /* ========================================================== */
        .toast-custom {
            position: fixed;
            top: 25px;
            right: 25px;
            padding: 16px 24px;
            border-radius: 8px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
            z-index: 1000000; /* Đảm bảo luôn nổi trên mọi thành phần */
            display: flex;
            align-items: center;
            gap: 12px;
            font-weight: 600;
            font-size: 15px;
            opacity: 1;
            transition: opacity 0.5s ease, transform 0.5s ease;
            animation: slideInToast 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        }
        .toast-success-style {
            background-color: #e8f5e9;
            color: #2e7d32;
            border: 1px solid #c8e6c9;
        }
        .toast-error-style {
            background-color: #fdecea;
            color: #b3261e;
            border: 1px solid #fad2cf;
        }
        @keyframes slideInToast {
            from { transform: translateX(120%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }

        /* ========================================================== */
        /* 🆕 CSS BỔ SUNG: HÀNG HÀNH ĐỘNG BỘ LỌC & THANH TOOLBAR STICKY*/
        /* ========================================================== */
        .filter-action-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 20px;
            padding-top: 16px;
            border-top: 1px dashed #e5e7eb;
        }
        .filter-action-left {
            display: flex;
            gap: 12px;
            align-items: center;
        }
        .filter-action-right {
            display: flex;
            align-items: center;
        }
        .btn-secondary-outline {
            background: #ffffff !important;
            color: #4b5563 !important;
            border: 1px solid #d1d5db !important;
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        .btn-secondary-outline:hover {
            background: #f9fafb !important;
            border-color: #9ca3af !important;
            color: #1f2937 !important;
        }

        /* Toolbar dính phía trên bảng dữ liệu */
        .table-toolbar {
            position: sticky;
            top: 70px; /* Điều chỉnh lại theo chiều cao thực tế của Header dự án nếu bị đè */
            background: #ffffff;
            z-index: 90;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 14px 16px;
            margin-top: 24px;
            border: 1px solid #e5e7eb;
            border-bottom: none;
            border-radius: 8px 8px 0 0;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
        }
        .toolbar-left-results {
            font-size: 14px;
            font-weight: 500;
            color: #4b5563;
        }
        .toolbar-left-results span {
            font-weight: 700;
            color: #1f2937;
        }
        /* Thay đổi border-radius của bảng để khớp mượt với toolbar phía trên */
        .category-table {
            margin-top: 0 !important;
            border-top-left-radius: 0 !important;
            border-top-right-radius: 0 !important;
        }

        /* ========================================================== */
        /* 🎛️ CUSTOM TOGGLE SWITCH CHO TRẠNG THÁI SẢN PHẨM MỚI       */
        /* ========================================================== */
        .status-switch {
            position: relative;
            display: inline-block;
            width: 44px;
            height: 22px;
        }
        .status-switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }
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
            content: "";
            height: 16px;
            width: 16px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .3s;
            border-radius: 50%;
            box-shadow: 0 1px 3px rgba(0,0,0,0.2);
        }
        .status-switch input:checked + .status-slider {
            background-color: #b4975a; /* Khớp với màu thương hiệu hệ thống */
        }
        .status-switch input:checked + .status-slider:before {
            transform: translateX(22px);
        }

        /* Responsive Mobile */
        @media (max-width: 1024px) {
            .filter-grid {
                grid-template-columns: repeat(2, 1fr);
            }
            .price-popover-panel {
                left: 0;
                right: auto;
            }
        }
        @media (max-width: 640px) {
            .filter-grid {
                grid-template-columns: 1fr;
            }
            .input-currency-container {
                flex-direction: column;
                align-items: stretch;
            }
            .input-currency-container .input-separator {
                display: none;
            }
            .filter-action-row {
                flex-direction: column;
                gap: 12px;
                align-items: stretch;
            }
            .filter-action-left {
                flex-direction: column;
                align-items: stretch;
            }
            .filter-action-right {
                flex-direction: column;
                gap: 8px;
            }
            .filter-action-right button,
            .filter-action-right a {
                width: 100%;
                justify-content: center;
            }
        }
    </style>
</head>
<body>
<%@include file="/Admin/layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@include file="/Admin/layout/header.jsp" %>

    <div class="category-section">
        <div class="category-header">
            <h2 class="category-title">Sản Phẩm</h2>
        </div>

        <!-- THÔNG BÁO DẠNG TOAST GÓC TRÊN PHẢI -->
        <div id="toast-container"></div>
        <c:if test="${not empty error}">
            <div id="toast-msg" class="toast-custom toast-error-style">
                <i class="fas fa-circle-exclamation"></i>
                <span>${error}</span>
            </div>
        </c:if>

        <c:if test="${not empty param.success}">
            <div id="toast-msg" class="toast-custom toast-success-style">
                <i class="fas fa-check-circle"></i>
                <span>${param.success}</span>
            </div>
        </c:if>

        <div class="filter-section">
            <div class="filter-header">
                <div class="filter-title">
                    <i class="fas fa-filter"></i>
                    Bộ lọc tìm kiếm
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
                               placeholder="Tìm kiếm ...">
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

                    <div class="filter-group price-popover-container" id="pricePopoverContainer">
                        <label class="filter-label">Khoảng giá</label>
                        <button type="button" class="price-dropdown-trigger" id="priceDropdownBtn">
                            <span id="priceTriggerText">Khoảng giá</span>
                            <i class="fas fa-chevron-down" style="font-size: 12px; color: #9ca3af;"></i>
                        </button>

                        <div class="price-popover-panel" id="pricePopoverPanel">
                            <div class="quick-filter-tags">
                                <button type="button" class="filter-tag-btn" data-min="0" data-max="2000000">Dưới 2tr</button>
                                <button type="button" class="filter-tag-btn" data-min="2000000" data-max="5000000">2tr - 5tr</button>
                                <button type="button" class="filter-tag-btn" data-min="5000000" data-max="10000000">5tr - 10tr</button>
                                <button type="button" class="filter-tag-btn" id="quickTagMax" data-min="10000000" data-max="20000000">Trên 10tr</button>
                            </div>

                            <div class="slider-container">
                                <div class="slider-track-bar" id="sliderTrack"></div>
                                <div class="slider-tooltip" id="tooltipMin">0 đ</div>
                                <div class="slider-tooltip" id="tooltipMax">20.000.000 đ</div>
                                <span class="slider-limit-label" style="left: 0;">0 đ</span>
                                <span class="slider-limit-label" style="right: 0;" id="limitMaxLabel">20.000.000 đ</span>
                            </div>

                            <div class="range-input-slider">
                                <input type="range" id="rangeMin" min="0" max="20000000" step="50000" value="${not empty searchGiaTu ? searchGiaTu : 0}">
                                <input type="range" id="rangeMax" min="0" max="20000000" step="50000" value="${not empty searchGiaDen ? searchGiaDen : 20000000}">
                            </div>

                            <div class="input-currency-container">
                                <div class="currency-field">
                                    <span>Từ (đ)</span>
                                    <input type="text" id="displayMin" class="filter-input" placeholder="0" style="width: 100%; margin-top: 4px; height: 34px;">
                                    <input type="hidden" name="giaTu" id="inputMin" value="${not empty searchGiaTu ? searchGiaTu : 0}">
                                </div>
                                <span class="input-separator" style="margin-top: 18px; color: #9ca3af; font-weight: bold;">-</span>
                                <div class="currency-field">
                                    <span>Đến (đ)</span>
                                    <input type="text" id="displayMax" class="filter-input" placeholder="Không giới hạn" style="width: 100%; margin-top: 4px; height: 34px;">
                                    <input type="hidden" name="giaDen" id="inputMax" value="${not empty searchGiaDen ? searchGiaDen : 20000000}">
                                </div>
                            </div>

                            <div id="priceValidationError" style="color: #dc2626; font-size: 11px; font-weight: 500; margin-top: 6px; display: none;">
                                <i class="fas fa-circle-exclamation"></i> Giá từ lớn hơn giá đến!
                            </div>

                            <div class="popover-actions">
                                <button type="button" class="popover-btn popover-btn-clear" id="btnPopoverClear">Xóa</button>
                                <button type="button" class="popover-btn popover-btn-apply" id="btnPopoverApply">Áp dụng</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="filter-action-row">
                    <div class="filter-action-left">
                        <c:if test="${not empty searchTenSanPham || not empty searchDanhMucId || not empty searchThuongHieuId || not empty searchGiaTu || not empty searchGiaDen}">
                            <a href="${pageContext.request.contextPath}/SanPham"
                               style="padding: 10px 20px; background: #fee2e2; color: #dc2626; border-radius: 8px; text-decoration: none; display: inline-flex; align-items: center; gap: 6px; font-weight: 600; font-size: 14px;">
                                <i class="fas fa-xmark"></i> Xóa bộ lọc nâng cao
                            </a>
                        </c:if>
                    </div>

                    <div class="filter-action-right" style="display: flex; gap: 12px; align-items: center;">
                        <button type="submit" class="add-new-btn" style="padding: 10px 24px;" id="mainSubmitBtn">
                            <i class="fas fa-search"></i> Tìm kiếm
                        </button>

                        <button type="button" class="btn-secondary-outline" onclick="resetFilters()">
                            <i class="fas fa-rotate-left"></i> Đặt lại
                        </button>
                    </div>
                </div>
            </form>
        </div>

        <div class="table-toolbar">
            <div class="toolbar-left-results">
                Hiển thị <span>${fn:length(items)}</span> sản phẩm
            </div>
            <div style="display: flex; gap: 12px; align-items: center;">
                <a href="${pageContext.request.contextPath}/SanPham/export" class="btn-secondary-outline">
                    <i class="fas fa-file-export"></i> Xuất Excel
                </a>

                <a href="${pageContext.request.contextPath}/SanPham/new" class="add-new-btn" style="text-decoration: none; display: inline-flex; align-items: center; gap: 6px; padding: 10px 20px;">
                    <i class="fas fa-plus"></i> Thêm mới
                </a>
            </div>
        </div>

        <table class="category-table">
            <thead>
            <tr>
                <th>STT</th>
                <th>Mã/Sản phẩm</th>
                <th>Phân Loại/Thương Hiệu</th>
                <th>Chi Tiết</th>
                <th>Số lượng</th>
                <th>Đơn giá</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="temp" items="${items}" varStatus="status">
                <tr class="product-row" data-price="${temp.giaMax}">
                    <td>
                        <span class="category-id">
                                ${status.count + ((not empty currentPage ? currentPage : 1) - 1) * 10}
                        </span>
                    </td>
                    <td>
                        <span style="font-weight: 600; color: #1f2937; display: block; margin-bottom: 4px;">${temp.tenSanPham}</span>
                        <span style="font-size: 0.85em; color: #6b7280;">${temp.maSanPham}</span>
                    </td>
                    <td>
                        <span style="color: #374151; display: block; margin-bottom: 4px;">${temp.danhMuc.tenDanhMuc}</span>
                        <span style="font-weight: 600; color: #1f2937; font-size: 0.9em;">${temp.thuongHieu.tenThuongHieu}</span>
                    </td>
                    <td>
                        <span style="color: #374151; display: block; margin-bottom: 4px;">${temp.chatLieu.tenChatLieu}</span>
                        <span style="color: #6b7280; font-size: 0.9em;">${temp.kieuDang.tenKieuDang}</span>
                    </td>
                    <td>
                        <span style="font-weight: 600; color: #374151;">
                                ${temp.tongSoLuong != null ? temp.tongSoLuong : 0}
                        </span>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${temp.giaMin == temp.giaMax && temp.giaMin > 0}">
                                <strong style="color: #dc2626;">
                                    <fmt:formatNumber value="${temp.giaMin}" type="number"/> đ
                                </strong>
                            </c:when>
                            <c:when test="${temp.giaMin < temp.giaMax}">
                                <strong style="color: #dc2626;">
                                    <fmt:formatNumber value="${temp.giaMin}" type="number"/> - <fmt:formatNumber value="${temp.giaMax}" type="number"/> đ
                                </strong>
                            </c:when>
                            <c:otherwise>
                                <span style="color: #9ca3af; font-style: italic;">Chưa có giá</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <!-- Gắn thêm class JS-status-text để JS dễ tìm kiếm và đổi text/màu nhanh -->
                        <span class="category-status JS-status-text ${temp.trangThai == 1 ? 'status-active' : 'status-inactive'}">
                                ${temp.trangThai == 1 ? "Đang kinh doanh" : "Ngừng bán"}
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons" style="align-items: center; gap: 12px;">
                            <a href="${pageContext.request.contextPath}/SanPhamChiTiet?sanPhamId=${temp.id}" class="btn-icon-circle btn-view" title="Xem biến thể">
                                <i class="fas fa-eye"></i>
                            </a>

                            <!-- Đã đổi nút Xóa thành Switch thay đổi trạng thái bán hàng -->
                            <label class="status-switch" title="Gạt để thay đổi trạng thái bán">
                                <input type="checkbox"
                                       class="JS-status-toggle"
                                       data-id="${temp.id}"
                                    ${temp.trangThai == 1 ? 'checked' : ''}>
                                <span class="status-slider"></span>
                            </label>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty items}">
                <tr>
                    <td colspan="8" style="text-align: center; padding: 30px; color: #888;">
                        <i class="fas fa-inbox" style="font-size: 24px; margin-bottom: 10px; display: block;"></i>
                        Không tìm thấy dữ liệu nào.
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

        <div class="sp-pagination">
            <c:if test="${totalPages > 1}">
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

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="${pageContext.request.contextPath}/SanPham?page=${i}"
                       class="sp-page-btn ${currentPage == i ? 'active' : ''}">
                            ${i}
                    </a>
                </c:forEach>

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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    function resetFilters() {
        document.getElementById('filterForm').reset();
        window.location.href = '${pageContext.request.contextPath}/SanPham';
    }

    // Hàm tạo Toast thông báo nhanh góc màn hình cho việc cập nhật Switch thái
    function showLiveToast(message, isSuccess) {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast-custom ${isSuccess ? 'toast-success-style' : 'toast-error-style'}`;
        toast.innerHTML = `<i class="fas ${isSuccess ? 'fa-check-circle' : 'fa-circle-exclamation'}"></i><span>\${message}</span>`;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = "0";
            toast.style.transform = "translateY(-10px)";
            setTimeout(() => toast.remove(), 500);
        }, 2500);
    }

    document.addEventListener('DOMContentLoaded', function () {
        // =========================================================================
        // 🔄 XỬ LÝ SỰ KIỆN TOGGLE SWITCH ĐỔI TRẠNG THÁI (REAL-TIME AJAX)
        // =========================================================================
        document.querySelectorAll('.JS-status-toggle').forEach(toggle => {
            toggle.addEventListener('change', function() {
                const productId = this.getAttribute('data-id');
                const isChecked = this.checked;

                // Quy đổi trạng thái số tương ứng (1: Đang kinh doanh, 0: Ngừng bán)
                const statusValue = isChecked ? 1 : 0;
                const statusText = isChecked ? "Đang kinh doanh" : "Ngừng bán";

                const row = this.closest('tr');
                const textLabel = row.querySelector('.JS-status-text');

                // Gửi Request cập nhật trạng thái ngay lập tức về Backend bằng Fetch API
                fetch('${pageContext.request.contextPath}/SanPham/update-status', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `id=\${productId}&trangThai=\${statusValue}`
                })
                    .then(response => {
                        if (response.ok) {
                            // Cập nhật giao diện trực tiếp tại hàng đó mà không cần reload
                            if (textLabel) {
                                textLabel.textContent = statusText;
                                if (isChecked) {
                                    textLabel.classList.remove('status-inactive');
                                    textLabel.classList.add('status-active');
                                } else {
                                    textLabel.classList.remove('status-active');
                                    textLabel.classList.add('status-inactive');
                                }
                            }
                            showLiveToast(`Đã chuyển sản phẩm sang: \${statusText}`, true);
                        } else {
                            showLiveToast("Cập nhật trạng thái không thành công!", false);
                            this.checked = !isChecked; // Hoàn tác gạt nếu Server báo lỗi
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        showLiveToast("Lỗi kết nối đến máy chủ!", false);
                        this.checked = !isChecked; // Hoàn tác gạt nếu lỗi mạng
                    });
            });
        });

        // ⚡ Tự ẩn thông báo Toast mặc định sau 3 giây
        const toast = document.getElementById("toast-msg");
        if (toast) {
            setTimeout(function () {
                toast.style.opacity = "0";
                toast.style.transform = "translateY(-10px)";
                setTimeout(function () {
                    toast.remove();
                }, 500);
            }, 3000);
        }

        // =========================================================================
        // 🛠️ GIỮ NGUYÊN HOÀN TOÀN: LOGIC POPOVER OVERLAY KHOẢNG GIÁ
        // =========================================================================
        const dropdownBtn = document.getElementById('priceDropdownBtn');
        const popoverPanel = document.getElementById('pricePopoverPanel');
        const triggerText = document.getElementById('priceTriggerText');
        const rangeMin = document.getElementById('rangeMin');
        const rangeMax = document.getElementById('rangeMax');
        const inputMin = document.getElementById('inputMin');
        const inputMax = document.getElementById('inputMax');
        const displayMin = document.getElementById('displayMin');
        const displayMax = document.getElementById('displayMax');
        const sliderTrack = document.getElementById('sliderTrack');
        const tooltipMin = document.getElementById('tooltipMin');
        const tooltipMax = document.getElementById('tooltipMax');
        const limitMaxLabel = document.getElementById('limitMaxLabel');
        const quickTagMax = document.getElementById('quickTagMax');
        const validationError = document.getElementById('priceValidationError');
        const mainSubmitBtn = document.getElementById('mainSubmitBtn');
        const btnPopoverApply = document.getElementById('btnPopoverApply');
        const btnPopoverClear = document.getElementById('btnPopoverClear');
        const tagButtons = document.querySelectorAll('.filter-tag-btn');

        const minGap = 50000;

        let maxPriceFromData = 10000000;
        const productRows = document.querySelectorAll('.product-row');
        productRows.forEach(row => {
            const priceAttr = parseFloat(row.getAttribute('data-price')) || 0;
            if (priceAttr > maxPriceFromData) {
                maxPriceFromData = priceAttr;
            }
        });

        let sliderMaxLimit = Math.ceil(maxPriceFromData / 5000000) * 5000000;
        if(sliderMaxLimit < 10000000) sliderMaxLimit = 10000000;

        rangeMin.max = sliderMaxLimit;
        rangeMax.max = sliderMaxLimit;
        limitMaxLabel.textContent = formatCurrencyShort(sliderMaxLimit);

        quickTagMax.setAttribute('data-max', sliderMaxLimit);

        function formatCurrencyShort(val) {
            return new Intl.NumberFormat('vi-VN').format(val) + ' đ';
        }

        function formatNumberString(val) {
            return new Intl.NumberFormat('vi-VN').format(val);
        }

        function parseRawNumber(str) {
            return parseInt(str.replace(/\./g, '')) || 0;
        }

        dropdownBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            popoverPanel.classList.toggle('show');
            dropdownBtn.classList.toggle('open-active');
        });

        document.addEventListener('click', function (e) {
            if (!document.getElementById('pricePopoverContainer').contains(e.target)) {
                popoverPanel.classList.remove('show');
                dropdownBtn.classList.remove('open-active');
            }
        });

        popoverPanel.addEventListener('click', function(e){
            e.stopPropagation();
        });

        function updateSliderUI() {
            const valMin = parseInt(rangeMin.value);
            const valMax = parseInt(rangeMax.value);

            const percentMin = (valMin / sliderMaxLimit) * 100;
            const percentMax = (valMax / sliderMaxLimit) * 100;

            sliderTrack.style.left = percentMin + '%';
            sliderTrack.style.right = (100 - percentMax) + '%';

            tooltipMin.style.left = `calc(\${percentMin}% + (\${10 - percentMin * 0.2}px))`;
            tooltipMin.textContent = formatCurrencyShort(valMin);

            tooltipMax.style.left = `calc(\${percentMax}% + (\${10 - percentMax * 0.2}px))`;
            tooltipMax.textContent = formatCurrencyShort(valMax);

            displayMin.value = formatNumberString(valMin);
            displayMax.value = formatNumberString(valMax);

            inputMin.value = valMin;
            inputMax.value = valMax;

            triggerText.textContent = formatNumberString(valMin) + "đ - " + formatNumberString(valMax) + "đ";

            validatePrices(valMin, valMax);
        }

        function validatePrices(minVal, maxVal) {
            if (minVal > maxVal) {
                validationError.style.display = 'block';
                mainSubmitBtn.disabled = true;
                mainSubmitBtn.style.opacity = '0.5';
                btnPopoverApply.disabled = true;
            } else {
                validationError.style.display = 'none';
                mainSubmitBtn.disabled = false;
                mainSubmitBtn.style.opacity = '1';
                btnPopoverApply.disabled = false;
            }

            tagButtons.forEach(btn => {
                const tMin = parseInt(btn.getAttribute('data-min'));
                const tMax = parseInt(btn.getAttribute('data-max'));
                if (minVal === tMin && maxVal === tMax) {
                    btn.classList.add('active');
                } else {
                    btn.classList.remove('active');
                }
            });
        }

        rangeMin.addEventListener('input', () => {
            let valMin = parseInt(rangeMin.value);
            let valMax = parseInt(rangeMax.value);
            if (valMax - valMin < minGap) rangeMin.value = valMax - minGap;
            updateSliderUI();
        });

        rangeMax.addEventListener('input', () => {
            let valMin = parseInt(rangeMin.value);
            let valMax = parseInt(rangeMax.value);
            if (valMax - valMin < minGap) rangeMax.value = valMin + minGap;
            updateSliderUI();
        });

        [displayMin, displayMax].forEach(input => {
            input.addEventListener('input', (e) => {
                let rawVal = e.target.value.replace(/[^0-9]/g, '');
                if (rawVal === '') {
                    e.target.value = '';
                    return;
                }
                const num = parseInt(rawVal);
                e.target.value = formatNumberString(num);

                const currentMin = parseRawNumber(displayMin.value);
                const currentMax = parseRawNumber(displayMax.value);

                if (input === displayMin && currentMin <= sliderMaxLimit) rangeMin.value = currentMin;
                if (input === displayMax && currentMax <= sliderMaxLimit) rangeMax.value = currentMax;

                inputMin.value = currentMin;
                inputMax.value = currentMax;

                triggerText.textContent = formatNumberString(currentMin) + "đ - " + formatNumberString(currentMax) + "đ";

                const percentMin = (rangeMin.value / sliderMaxLimit) * 100;
                const percentMax = (rangeMax.value / sliderMaxLimit) * 100;
                sliderTrack.style.left = percentMin + '%';
                sliderTrack.style.right = (100 - percentMax) + '%';

                validatePrices(currentMin, currentMax);
            });
        });

        tagButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                const targetMin = btn.getAttribute('data-min');
                const targetMax = btn.getAttribute('data-max');

                rangeMin.value = targetMin;
                rangeMax.value = targetMax;

                updateSliderUI();
            });
        });

        btnPopoverClear.addEventListener('click', () => {
            rangeMin.value = 0;
            rangeMax.value = sliderMaxLimit;
            updateSliderUI();
            triggerText.textContent = "Khoảng giá";
            tagButtons.forEach(b => b.classList.remove('active'));
        });

        btnPopoverApply.addEventListener('click', () => {
            popoverPanel.classList.remove('show');
            dropdownBtn.classList.remove('open-active');
        });

        if(parseInt(inputMin.value) > 0 || parseInt(inputMax.value) < sliderMaxLimit) {
            rangeMin.value = inputMin.value;
            rangeMax.value = inputMax.value;
        } else {
            rangeMax.value = sliderMaxLimit;
        }
        updateSliderUI();

        if (!'${searchGiaTu}' && !'${searchGiaDen}') {
            triggerText.textContent = "Khoảng giá";
        }
    });
</script>
</body>
</html>