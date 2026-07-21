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
    <c:set var="isEditMode" value="${formMode == 'edit'}" />

    <main class="coupon-page">
        <%-- Form thêm hoặc cập nhật phiếu: nhận coupon, formMode, formAction, errors từ Servlet --%>
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
            <%-- Form thêm hoặc cập nhật phiếu --%>
            <form id="couponForm" action="${formAction}" method="post" class="coupon-form" data-mode="${formMode}">
                <input type="hidden" name="id" value="${coupon.id}">
                <c:if test="${not empty errors.id}">
                    <div class="coupon-alert coupon-alert--error">${errors.id}</div>
                </c:if>

                <div class="coupon-form-grid">
                    <label class="coupon-field">
                        <span>Mã giảm giá <em>*</em></span>
                        <%-- Mã phiếu chỉ hiển thị; backend tự sinh khi thêm và giữ mã cũ khi sửa --%>
                        <input class="coupon-code-input" type="text" maxlength="8" readonly
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

                    <c:if test="${isEditMode}">
                        <div class="coupon-field">
                            <span>Trạng thái hiện tại</span>
                            <%-- Trạng thái chỉ hiển thị; bật/tắt dùng nút POST ở danh sách --%>
                            <span class="coupon-status ${coupon.trangThaiCssClass}">${coupon.trangThaiHienThi}</span>
                        </div>
                    </c:if>
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
        // PHẦN 2: JavaScript hỗ trợ định dạng tiền/phần trăm trước khi gửi form
        const form = document.getElementById('couponForm');
        const discountType = document.getElementById('discountType');
        const discountValue = document.getElementById('discountValue');
        const discountUnit = document.getElementById('discountUnit');
        const maxDiscountGroup = document.getElementById('maxDiscountGroup');
        const maxDiscount = document.getElementById('maxDiscount');
        const moneyInputs = document.querySelectorAll('[data-money-input]');
        let submitted = false;

        function extractDigits(value) {
            // Định dạng tiền: lấy phần số để backend nhận số VND thuần.
            const text = String(value || '').trim();
            const decimalNumber = text.match(/^(\d+)[.,](0+)$/);
            if (decimalNumber) {
                return decimalNumber[1];
            }
            return text.replace(/\D/g, '');
        }

        function formatMoneyInput(input) {
            // Định dạng tiền theo kiểu Việt Nam để người dùng dễ đọc.
            const digits = extractDigits(input.value);
            input.value = digits ? Number(digits).toLocaleString('vi-VN') : '';
        }

        function normalizeMoneyInput(input) {
            // Chuyển dữ liệu tiền về dạng số trước khi submit.
            input.value = extractDigits(input.value);
        }

        function normalizePercentInput(input) {
            // Giữ giá trị phần trăm là số nguyên tối đa 3 chữ số.
            input.value = extractDigits(input.value).slice(0, 3);
        }

        function syncDiscountFields() {
            // Thay đổi giao diện theo loại giảm.
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
            // Hiển thị confirm và ngăn submit hai lần.
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

            // Chuyển dữ liệu tiền về dạng số trước khi submit.
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
