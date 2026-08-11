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
        body {
            background-color: #f8fafc;
            margin: 0;
            padding: 0;
        }

        .dashboard-container {
            margin-left: 280px !important;
            padding: 0 !important;
            width: calc(100% - 280px) !important;
            max-width: none !important;
            min-height: 100vh;
            box-sizing: border-box;
            transition: all 0.3s ease;
        }

        .coupon-form-card {
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            margin-top: 20px;
        }

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

        .add-new-btn:disabled {
            cursor: wait;
            opacity: 0.72;
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

        body.coupon-modal-open {
            overflow: hidden;
        }

        .coupon-confirm-modal {
            position: fixed;
            inset: 0;
            z-index: 1000000;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            background: rgba(15, 23, 42, 0.52);
            backdrop-filter: blur(3px);
            opacity: 0;
            visibility: hidden;
            transition: opacity 0.2s ease, visibility 0.2s ease;
        }

        .coupon-confirm-modal.is-visible {
            opacity: 1;
            visibility: visible;
        }

        .coupon-confirm-dialog {
            width: min(440px, 100%);
            overflow: hidden;
            background: #ffffff;
            border: 1px solid rgba(180, 151, 90, 0.28);
            border-radius: 18px;
            box-shadow: 0 24px 70px rgba(15, 23, 42, 0.24);
            transform: translateY(14px) scale(0.97);
            transition: transform 0.22s ease;
        }

        .coupon-confirm-modal.is-visible .coupon-confirm-dialog {
            transform: translateY(0) scale(1);
        }

        .coupon-confirm-content {
            position: relative;
            padding: 30px 30px 24px;
            text-align: center;
        }

        .coupon-confirm-close {
            position: absolute;
            top: 14px;
            right: 14px;
            display: inline-flex;
            width: 34px;
            height: 34px;
            align-items: center;
            justify-content: center;
            border: 0;
            border-radius: 50%;
            background: #f8fafc;
            color: #64748b;
            cursor: pointer;
            transition: background-color 0.2s ease, color 0.2s ease;
        }

        .coupon-confirm-close:hover {
            background: #f1f5f9;
            color: #0f172a;
        }

        .coupon-confirm-icon {
            position: relative;
            display: inline-flex;
            width: 72px;
            height: 72px;
            align-items: center;
            justify-content: center;
            margin-bottom: 18px;
            border-radius: 22px;
            background: linear-gradient(145deg, #f8f2e7, #efe2c7);
            color: #9a8048;
            font-size: 29px;
            box-shadow: 0 10px 25px rgba(180, 151, 90, 0.2);
        }

        .coupon-confirm-icon::after {
            content: '';
            position: absolute;
            inset: -7px;
            border: 1px solid rgba(180, 151, 90, 0.2);
            border-radius: 27px;
        }

        .coupon-confirm-title {
            margin: 0;
            color: #172033;
            font-size: 21px;
            font-weight: 700;
            line-height: 1.35;
        }

        .coupon-confirm-message {
            max-width: 345px;
            margin: 10px auto 0;
            color: #64748b;
            font-size: 14px;
            line-height: 1.6;
        }

        .coupon-confirm-actions {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px;
            padding: 18px 24px 24px;
            background: #fbfcfe;
            border-top: 1px solid #eef2f7;
        }

        .coupon-confirm-button {
            min-height: 44px;
            padding: 10px 16px;
            border-radius: 10px;
            font-size: 14px;
            font-weight: 700;
            cursor: pointer;
            transition: transform 0.15s ease, background-color 0.2s ease,
                        border-color 0.2s ease, box-shadow 0.2s ease;
        }

        .coupon-confirm-button:hover:not(:disabled) {
            transform: translateY(-1px);
        }

        .coupon-confirm-button:focus-visible,
        .coupon-confirm-close:focus-visible {
            outline: 3px solid rgba(180, 151, 90, 0.25);
            outline-offset: 2px;
        }

        .coupon-confirm-button--cancel {
            border: 1px solid #dbe1e8;
            background: #ffffff;
            color: #475569;
        }

        .coupon-confirm-button--cancel:hover:not(:disabled) {
            border-color: #b8c1cc;
            background: #f8fafc;
        }

        .coupon-confirm-button--save {
            border: 1px solid #b4975a;
            background: #b4975a;
            color: #ffffff;
            box-shadow: 0 8px 18px rgba(180, 151, 90, 0.24);
        }

        .coupon-confirm-button--save:hover:not(:disabled) {
            border-color: #9a8048;
            background: #9a8048;
        }

        .coupon-confirm-button:disabled,
        .coupon-confirm-close:disabled {
            cursor: wait;
            opacity: 0.7;
        }

        .coupon-confirm-button .fa-spinner {
            margin-right: 7px;
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

        @media (max-width: 520px) {
            .coupon-confirm-content {
                padding: 28px 22px 22px;
            }

            .coupon-confirm-actions {
                grid-template-columns: 1fr;
                padding: 16px 20px 20px;
            }

            .coupon-confirm-button--save {
                grid-row: 1;
            }
        }

        @media (prefers-reduced-motion: reduce) {
            .coupon-confirm-modal,
            .coupon-confirm-dialog,
            .coupon-confirm-button {
                transition: none;
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
                    <button id="couponSubmitButton" class="add-new-btn" type="submit">
                        <i class="fas fa-save"></i> Lưu phiếu giảm giá
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<div id="couponConfirmModal" class="coupon-confirm-modal" aria-hidden="true">
    <div class="coupon-confirm-dialog" role="dialog" aria-modal="true"
         aria-labelledby="couponConfirmTitle" aria-describedby="couponConfirmMessage">
        <div class="coupon-confirm-content">
            <button id="couponConfirmClose" class="coupon-confirm-close" type="button" aria-label="Đóng hộp thoại">
                <i class="fas fa-xmark" aria-hidden="true"></i>
            </button>
            <div class="coupon-confirm-icon" aria-hidden="true">
                <i class="fas fa-ticket"></i>
            </div>
            <h3 id="couponConfirmTitle" class="coupon-confirm-title">Xác nhận lưu phiếu giảm giá</h3>
            <p id="couponConfirmMessage" class="coupon-confirm-message">
                Vui lòng kiểm tra thông tin trước khi xác nhận lưu.
            </p>
        </div>
        <div class="coupon-confirm-actions">
            <button id="couponConfirmCancel" class="coupon-confirm-button coupon-confirm-button--cancel" type="button">
                Quay lại kiểm tra
            </button>
            <button id="couponConfirmSave" class="coupon-confirm-button coupon-confirm-button--save" type="button">
                <i class="fas fa-check" aria-hidden="true"></i>
                <span>Xác nhận lưu</span>
            </button>
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
        const startDate = form.querySelector('[name="ngayBatDau"]');
        const endDate = form.querySelector('[name="ngayKetThuc"]');
        const submitButton = document.getElementById('couponSubmitButton');
        const confirmModal = document.getElementById('couponConfirmModal');
        const confirmTitle = document.getElementById('couponConfirmTitle');
        const confirmMessage = document.getElementById('couponConfirmMessage');
        const confirmSave = document.getElementById('couponConfirmSave');
        const confirmSaveLabel = confirmSave.querySelector('span');
        const confirmCancel = document.getElementById('couponConfirmCancel');
        const confirmClose = document.getElementById('couponConfirmClose');
        let lastFocusedElement = null;
        let submitted = false;

        /**
         * Lấy phần nguyên từ chuỗi số.
         * Xử lý cả dạng "50000.00" (BigDecimal từ Java) và "50.000" (đã format VND).
         * - "50000.00"  -> "50000"
         * - "50.000"    -> "50000"
         * - "1.000.000" -> "1000000"
         */
        function extractDigits(value) {
            let text = String(value || '').trim();

            // Kiểm tra xem đây là số thập phân kiểu BigDecimal ("50000.00")
            // hay số đã được format kiểu VND ("50.000" / "1.000.000")
            // Dấu hiệu: nếu chỉ có 1 dấu chấm/phẩy và phần sau là toàn số 0 -> phần thập phân, bỏ đi
            const separators = text.match(/[.,]/g) || [];
            if (separators.length === 1) {
                const sepIdx = Math.max(text.lastIndexOf('.'), text.lastIndexOf(','));
                const afterSep = text.slice(sepIdx + 1);
                // Nếu sau dấu phân cách cuối cùng chỉ là số 0 (phần .00 của BigDecimal)
                if (/^0{1,2}$/.test(afterSep)) {
                    text = text.slice(0, sepIdx);
                }
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

        function todayValue() {
            const today = new Date();
            const year = today.getFullYear();
            const month = String(today.getMonth() + 1).padStart(2, '0');
            const day = String(today.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        }

        function validateEndDate() {
            if (!endDate || !endDate.value) {
                return true;
            }
            if (startDate && startDate.value && endDate.value < startDate.value) {
                endDate.setCustomValidity('Ngày kết thúc không được trước ngày bắt đầu.');
                endDate.reportValidity();
                return false;
            }
            if (endDate.value < todayValue()) {
                endDate.setCustomValidity('Ngày kết thúc không được trước ngày hiện tại.');
                endDate.reportValidity();
                return false;
            }
            endDate.setCustomValidity('');
            return true;
        }

        function syncDiscountFields() {
            const isPercent = discountType.value === 'percent';
            discountUnit.textContent = isPercent ? '%' : 'VND';
            maxDiscountGroup.style.display = isPercent ? '' : 'none';
            maxDiscount.disabled = !isPercent;

            if (isPercent) {
                normalizePercentInput(discountValue);
                formatMoneyInput(maxDiscount);
            } else {
                formatMoneyInput(discountValue);
            }
        }

        function openConfirmModal() {
            const isEditMode = form.dataset.mode === 'edit';
            confirmTitle.textContent = isEditMode
                ? 'Xác nhận cập nhật phiếu?'
                : 'Xác nhận thêm phiếu mới?';
            confirmMessage.textContent = isEditMode
                ? 'Thông tin phiếu giảm giá sẽ được cập nhật theo các thay đổi bạn vừa nhập.'
                : 'Phiếu giảm giá sẽ được thêm vào danh sách và áp dụng theo thời gian đã thiết lập.';
            confirmSaveLabel.textContent = isEditMode ? 'Cập nhật phiếu' : 'Thêm phiếu';
            lastFocusedElement = document.activeElement;
            confirmModal.classList.add('is-visible');
            confirmModal.setAttribute('aria-hidden', 'false');
            document.body.classList.add('coupon-modal-open');
            window.setTimeout(function () {
                confirmCancel.focus();
            }, 0);
        }

        function closeConfirmModal() {
            if (submitted) {
                return;
            }
            confirmModal.classList.remove('is-visible');
            confirmModal.setAttribute('aria-hidden', 'true');
            document.body.classList.remove('coupon-modal-open');
            if (lastFocusedElement) {
                lastFocusedElement.focus();
            }
        }

        function prepareFormData() {
            // Strip formatting trước khi submit để gửi số nguyên thuần
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
        }

        function showSavingState() {
            submitted = true;
            confirmSave.disabled = true;
            confirmCancel.disabled = true;
            confirmClose.disabled = true;
            confirmSave.innerHTML = '<i class="fas fa-spinner fa-spin" aria-hidden="true"></i><span>Đang lưu...</span>';
            if (submitButton) {
                submitButton.disabled = true;
                submitButton.innerHTML = '<i class="fas fa-spinner fa-spin" aria-hidden="true"></i> Đang lưu...';
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
            // Format giá trị pre-fill từ server khi trang load
            formatMoneyInput(input);
        });

        discountType.addEventListener('change', syncDiscountFields);
        startDate?.addEventListener('change', validateEndDate);
        endDate?.addEventListener('change', validateEndDate);
        syncDiscountFields();

        form.addEventListener('submit', function (event) {
            event.preventDefault();

            if (submitted) {
                return;
            }

            if (!validateEndDate()) {
                return;
            }

            openConfirmModal();
        });

        confirmSave.addEventListener('click', function () {
            if (submitted) {
                return;
            }
            prepareFormData();
            showSavingState();

            // Cho trình duyệt kịp hiển thị trạng thái đang lưu trước khi chuyển trang.
            window.setTimeout(function () {
                HTMLFormElement.prototype.submit.call(form);
            }, 120);
        });

        confirmCancel.addEventListener('click', closeConfirmModal);
        confirmClose.addEventListener('click', closeConfirmModal);
        confirmModal.addEventListener('click', function (event) {
            if (event.target === confirmModal) {
                closeConfirmModal();
            }
        });

        document.addEventListener('keydown', function (event) {
            if (!confirmModal.classList.contains('is-visible') || submitted) {
                return;
            }

            if (event.key === 'Escape') {
                event.preventDefault();
                closeConfirmModal();
                return;
            }

            if (event.key === 'Tab') {
                const focusableElements = [confirmClose, confirmCancel, confirmSave]
                    .filter(function (element) { return !element.disabled; });
                const firstElement = focusableElements[0];
                const lastElement = focusableElements[focusableElements.length - 1];

                if (event.shiftKey && document.activeElement === firstElement) {
                    event.preventDefault();
                    lastElement.focus();
                } else if (!event.shiftKey && document.activeElement === lastElement) {
                    event.preventDefault();
                    firstElement.focus();
                }
            }
        });
    })();
</script>
</body>
</html>
