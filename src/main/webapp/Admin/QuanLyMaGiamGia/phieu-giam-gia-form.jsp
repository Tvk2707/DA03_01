<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    if (request.getAttribute("pageTitle") == null) {
        String mode = String.valueOf(request.getAttribute("formMode"));
        String actionTitle = "edit".equals(mode) ? "Cập nhật phiếu giảm giá" : "Thêm phiếu giảm giá";
        request.setAttribute("pageTitle", "Quản lý phiếu giảm giá / " + actionTitle);
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${formMode == 'edit' ? 'Sửa phiếu giảm giá' : 'Thêm phiếu giảm giá'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        /* ========================================================== */
        /* 🛠️ SỬA LỖI TRÀN / MAT CSS & ĐỒNG BỘ CÙNG HỆ THỐNG          */
        /* ========================================================== */
        body {
            background-color: #f8fafc;
            margin: 0;
            padding: 0;
        }

        .dashboard-container {
            margin-left: 260px !important; /* Đẩy nội dung tránh bị Sidebar che */
            padding: 24px 32px !important;
            min-height: 100vh;
            box-sizing: border-box;
            transition: all 0.3s ease;
        }

        /* Card chứa form chính */
        .coupon-form-card {
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            margin-top: 20px;
        }

        /* Lưới 2 cột cho các ô nhập liệu */
        .coupon-form-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }

        .coupon-field {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .coupon-field span {
            font-size: 13px;
            font-weight: 600;
            color: #374151;
        }

        .coupon-field span em {
            color: #dc2626;
            font-style: normal;
        }

        .coupon-input,
        .coupon-select {
            width: 100%;
            height: 40px;
            padding: 8px 12px;
            font-size: 14px;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            background-color: #ffffff;
            color: #1f2937;
            box-sizing: border-box;
            transition: all 0.2s ease;
        }

        .coupon-input:focus,
        .coupon-select:focus {
            border-color: #b4975a !important;
            outline: none;
            box-shadow: 0 0 0 3px rgba(180, 151, 90, 0.15);
        }

        .coupon-input-group {
            display: flex;
            align-items: center;
            position: relative;
        }

        .coupon-input-group input {
            padding-right: 48px;
        }

        .coupon-input-group span {
            position: absolute;
            right: 12px;
            font-size: 13px;
            font-weight: 600;
            color: #6b7280;
        }

        .coupon-error {
            color: #dc2626;
            font-size: 12px;
            margin-top: 4px;
        }

        /* Nút thao tác dưới form */
        .coupon-form-actions {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            gap: 12px;
            margin-top: 24px;
            padding-top: 20px;
            border-top: 1px dashed #e5e7eb;
        }

        .add-new-btn {
            background-color: #b4975a !important;
            color: #ffffff !important;
            border: none !important;
            border-radius: 8px;
            padding: 10px 20px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: background-color 0.2s ease;
            text-decoration: none;
        }

        .add-new-btn:hover {
            background-color: #9a8048 !important;
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

        /* Thông báo Lỗi */
        .coupon-alert-error {
            background-color: #fdecea;
            color: #b3261e;
            border: 1px solid #fad2cf;
            padding: 12px 16px;
            border-radius: 8px;
            margin-top: 16px;
            font-weight: 500;
            font-size: 14px;
        }

        @media (max-width: 992px) {
            .dashboard-container {
                margin-left: 0 !important;
                padding: 16px !important;
            }
            .coupon-form-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<%@ include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@ include file="../layout/header.jsp" %>
    <c:set var="isEditMode" value="${formMode == 'edit'}" />

    <div class="category-section">
        <div class="category-header" style="display: flex; justify-content: space-between; align-items: center;">
            <div>
                <h2 class="category-title">${formMode == 'edit' ? 'Sửa phiếu giảm giá' : 'Thêm phiếu giảm giá'}</h2>
                <p style="font-size: 13px; color: #6b7280; margin-top: 4px;">Nhập thông tin phiếu giảm giá. Các trường có dấu * là bắt buộc.</p>
            </div>
            <a class="btn-secondary-outline" href="${pageContext.request.contextPath}/PhieuGiamGia">
                <i class="fas fa-arrow-left"></i> Quay lại danh sách
            </a>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="coupon-alert-error">
                <i class="fas fa-circle-exclamation" style="margin-right: 8px;"></i>${errorMessage}
            </div>
        </c:if>

        <div class="coupon-form-card">
            <form id="couponForm" action="${formAction}" method="post" data-mode="${formMode}">
                <input type="hidden" name="id" value="${coupon.id}">
                <c:if test="${not empty errors.id}">
                    <div class="coupon-alert-error" style="margin-bottom: 16px;">${errors.id}</div>
                </c:if>

                <div class="coupon-form-grid">
                    <label class="coupon-field">
                        <span>Mã giảm giá <em>*</em></span>
                        <input class="coupon-input" type="text" maxlength="8" readonly
                               value="${coupon.maVoucher}" placeholder="VC000001" style="background-color: #f3f4f6;">
                        <c:if test="${not empty errors.maVoucher}">
                            <small class="coupon-error">${errors.maVoucher}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Tên phiếu giảm giá <em>*</em></span>
                        <input class="coupon-input" type="text" name="tenVoucher" maxlength="250" required
                               value="${coupon.tenVoucher}" placeholder="VD: Giảm 10% toàn đơn">
                        <c:if test="${not empty errors.tenVoucher}">
                            <small class="coupon-error">${errors.tenVoucher}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Số lượng <em>*</em></span>
                        <input class="coupon-input" type="number" name="soLuong" min="1" step="1" required value="${coupon.soLuong}">
                        <c:if test="${not empty errors.soLuong}">
                            <small class="coupon-error">${errors.soLuong}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Ngày bắt đầu <em>*</em></span>
                        <input class="coupon-input" type="date" name="ngayBatDau" required value="${coupon.ngayBatDauValue}">
                        <c:if test="${not empty errors.ngayBatDau}">
                            <small class="coupon-error">${errors.ngayBatDau}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Ngày kết thúc <em>*</em></span>
                        <input class="coupon-input" type="date" name="ngayKetThuc" required value="${coupon.ngayKetThucValue}">
                        <c:if test="${not empty errors.ngayKetThuc}">
                            <small class="coupon-error">${errors.ngayKetThuc}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Loại giảm <em>*</em></span>
                        <select id="discountType" class="coupon-select" name="loaiGiamGia" required>
                            <option value="percent" <c:if test="${coupon.loaiGiamGiaFilterValue == 'percent'}">selected</c:if>>Giảm phần trăm</option>
                            <option value="amount" <c:if test="${coupon.loaiGiamGiaFilterValue == 'amount'}">selected</c:if>>Giảm tiền cố định</option>
                        </select>
                        <c:if test="${not empty errors.loaiGiamGia}">
                            <small class="coupon-error">${errors.loaiGiamGia}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Giá trị giảm <em>*</em></span>
                        <div class="coupon-input-group">
                            <input id="discountValue" class="coupon-input" type="text" name="giaTriGiam" inputmode="numeric" required
                                   value="${coupon.giaTriGiam}">
                            <span id="discountUnit">%</span>
                        </div>
                        <c:if test="${not empty errors.giaTriGiam}">
                            <small class="coupon-error">${errors.giaTriGiam}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field" id="maxDiscountGroup">
                        <span>Giảm tối đa</span>
                        <div class="coupon-input-group">
                            <input id="maxDiscount" class="coupon-input" type="text" name="giamToiDa" inputmode="numeric" data-money-input
                                   value="${coupon.giamToiDa}">
                            <span>VND</span>
                        </div>
                        <c:if test="${not empty errors.giamToiDa}">
                            <small class="coupon-error">${errors.giamToiDa}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Đơn hàng tối thiểu</span>
                        <div class="coupon-input-group">
                            <input id="minimumOrder" class="coupon-input" type="text" name="donToiThieu" inputmode="numeric" data-money-input
                                   value="${coupon.donToiThieu}">
                            <span>VND</span>
                        </div>
                        <c:if test="${not empty errors.donToiThieu}">
                            <small class="coupon-error">${errors.donToiThieu}</small>
                        </c:if>
                    </label>

                    <c:if test="${isEditMode}">
                        <div class="coupon-field">
                            <span>Trạng thái hiện tại</span>
                            <div style="padding-top: 8px;">
                                <span class="category-status ${coupon.trangThaiCssClass == 'status-active' ? 'status-active' : 'status-inactive'}">
                                        ${coupon.trangThaiHienThi}
                                </span>
                            </div>
                        </div>
                    </c:if>
                </div>

                <div class="coupon-form-actions">
                    <a class="btn-secondary-outline" href="${pageContext.request.contextPath}/PhieuGiamGia">Hủy</a>
                    <button class="add-new-btn" type="submit">
                        <i class="fas fa-save"></i> Lưu phiếu giảm giá
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    (function () {
        const form = document.getElementById('couponForm');
        const discountType = document.getElementById('discountType');
        const discountValue = document.getElementById('discountValue');
        const discountUnit = document.getElementById('discountUnit');
        const maxDiscountGroup = document.getElementById('maxDiscountGroup');
        const maxDiscount = document.getElementById('maxDiscount');
        const moneyInputs = document.querySelectorAll('[data-money-input]');
        let submitted = false;

        function extractDigits(value) {
            const text = String(value || '').trim();
            const decimalNumber = text.match(/^(\d+)[.,](0+)$/);
            if (decimalNumber) {
                return decimalNumber[1];
            }
            return text.replace(/\D/g, '');
        }

        function formatMoneyInput(input) {
            const digits = extractDigits(input.value);
            input.value = digits ? Number(digits).toLocaleString('vi-VN') : '';
        }

        function normalizeMoneyInput(input) {
            input.value = extractDigits(input.value);
        }

        function normalizePercentInput(input) {
            input.value = extractDigits(input.value).slice(0, 3);
        }

        function syncDiscountFields() {
            const isPercent = discountType.value === 'percent';
            discountUnit.textContent = isPercent ? '%' : 'VND';
            maxDiscountGroup.style.display = isPercent ? 'flex' : 'none';
            maxDiscount.disabled = !isPercent;

            if (isPercent) {
                normalizePercentInput(discountValue);
                formatMoneyInput(maxDiscount);
            } else {
                formatMoneyInput(discountValue);
                maxDiscount.value = '';
            }
        }

        discountValue.addEventListener('input', function () {
            if (discountType.value === 'amount') {
                formatMoneyInput(discountValue);
            } else {
                normalizePercentInput(discountValue);
            }
        });

        moneyInputs.forEach(function (input) {
            input.addEventListener('input', function () {
                formatMoneyInput(input);
            });
            formatMoneyInput(input);
        });

        discountType.addEventListener('change', syncDiscountFields);
        syncDiscountFields();

        form.addEventListener('submit', function (event) {
            if (submitted) {
                event.preventDefault();
                return;
            }

            const message = form.dataset.mode === 'edit'
                ? 'Bạn có chắc muốn cập nhật phiếu giảm giá này không?'
                : 'Bạn có chắc muốn thêm phiếu giảm giá này không?';
            if (!window.confirm(message)) {
                event.preventDefault();
                return;
            }

            if (discountType.value === 'amount') {
                normalizeMoneyInput(discountValue);
                maxDiscount.value = '';
            } else {
                normalizePercentInput(discountValue);
                normalizeMoneyInput(maxDiscount);
            }
            moneyInputs.forEach(function (input) {
                normalizeMoneyInput(input);
            });

            const button = form.querySelector('button[type="submit"]');
            if (button) {
                button.disabled = true;
            }
            submitted = true;
        });
    })();
</script>
</body>
</html>