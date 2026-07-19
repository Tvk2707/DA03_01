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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/phieu-giam-gia.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="coupon-admin-page">
<%@ include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@ include file="../layout/header.jsp" %>

    <main class="coupon-page">
        <div class="coupon-header">
            <div>
                <h2>${formMode == 'edit' ? 'Sửa phiếu giảm giá' : 'Thêm phiếu giảm giá'}</h2>
                <p>Nhập thông tin phiếu giảm giá. Các trường có dấu * là bắt buộc.</p>
            </div>
            <a class="coupon-btn coupon-btn--secondary" href="${pageContext.request.contextPath}/PhieuGiamGia">
                <i class="fas fa-arrow-left"></i> Quay lại danh sách
            </a>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="coupon-alert coupon-alert--error">${errorMessage}</div>
        </c:if>
        <section class="coupon-card">
            <form id="couponForm" action="${formAction}" method="post" class="coupon-form">
                <input type="hidden" name="id" value="${coupon.id}">

                <div class="coupon-form-grid">
                    <label class="coupon-field">
                        <span>Mã giảm giá <em>*</em></span>
                        <input class="coupon-code-input" type="text" name="maVoucher" maxlength="8" required readonly
                               value="${coupon.maVoucher}" placeholder="VC000001">
                        <c:if test="${not empty errors.maVoucher}">
                            <small class="coupon-error">${errors.maVoucher}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Tên phiếu giảm giá <em>*</em></span>
                        <input type="text" name="tenVoucher" maxlength="250" required
                               value="${coupon.tenVoucher}" placeholder="VD: Giảm 10% toàn đơn">
                        <c:if test="${not empty errors.tenVoucher}">
                            <small class="coupon-error">${errors.tenVoucher}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Số lượng <em>*</em></span>
                        <input type="number" name="soLuong" min="1" step="1" required value="${coupon.soLuong}">
                        <c:if test="${not empty errors.soLuong}">
                            <small class="coupon-error">${errors.soLuong}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Ngày bắt đầu <em>*</em></span>
                        <input type="date" name="ngayBatDau" required value="${coupon.ngayBatDauValue}">
                        <c:if test="${not empty errors.ngayBatDau}">
                            <small class="coupon-error">${errors.ngayBatDau}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Ngày kết thúc <em>*</em></span>
                        <input type="date" name="ngayKetThuc" required value="${coupon.ngayKetThucValue}">
                        <c:if test="${not empty errors.ngayKetThuc}">
                            <small class="coupon-error">${errors.ngayKetThuc}</small>
                        </c:if>
                    </label>

                    <label class="coupon-field">
                        <span>Loại giảm <em>*</em></span>
                        <select id="discountType" name="loaiGiamGia" required>
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
                            <input id="discountValue" type="text" name="giaTriGiam" inputmode="numeric" required
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
                            <input id="maxDiscount" type="text" name="giamToiDa" inputmode="numeric" data-money-input
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
                            <input id="minimumOrder" type="text" name="donToiThieu" inputmode="numeric" data-money-input
                                   value="${coupon.donToiThieu}">
                            <span>VND</span>
                        </div>
                        <c:if test="${not empty errors.donToiThieu}">
                            <small class="coupon-error">${errors.donToiThieu}</small>
                        </c:if>
                    </label>

                    <div class="coupon-field coupon-radio-group">
                        <span>Loại phiếu <em>*</em></span>
                        <label>
                            <input type="radio" name="loaiPhieu" value="public" <c:if test="${coupon.loaiPhieuFilterValue == 'public'}">checked</c:if>>
                            Công khai
                        </label>
                        <label>
                            <input type="radio" name="loaiPhieu" value="personal" <c:if test="${coupon.loaiPhieuFilterValue == 'personal'}">checked</c:if>>
                            Cá nhân
                        </label>
                        <c:if test="${not empty errors.loaiPhieu}">
                            <small class="coupon-error">${errors.loaiPhieu}</small>
                        </c:if>
                    </div>

                    <label class="coupon-field">
                        <span>Trạng thái <em>*</em></span>
                        <select name="trangThai" required>
                            <option value="1" <c:if test="${coupon.trangThai == 1}">selected</c:if>>Bật</option>
                            <option value="0" <c:if test="${coupon.trangThai == 0}">selected</c:if>>Ngừng áp dụng</option>
                        </select>
                        <c:if test="${not empty errors.trangThai}">
                            <small class="coupon-error">${errors.trangThai}</small>
                        </c:if>
                    </label>
                </div>

                <div class="coupon-form-actions">
                    <a class="coupon-btn coupon-btn--light" href="${pageContext.request.contextPath}/PhieuGiamGia">Hủy</a>
                    <a class="coupon-btn coupon-btn--secondary" href="${pageContext.request.contextPath}/PhieuGiamGia">
                        Quay lại danh sách
                    </a>
                    <button class="coupon-btn coupon-btn--primary" type="submit">
                        <i class="fas fa-save"></i> Lưu phiếu giảm giá
                    </button>
                </div>
            </form>
        </section>
    </main>
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

        form.addEventListener('submit', function () {
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
        });
    })();
</script>
</body>
</html>
