<%
    request.setAttribute("pageTitle", "Quản lý phiếu giảm giá");
    request.setAttribute("activeMenu", "discount");
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý phiếu giảm giá</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/personal-coupon.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="coupon-admin-screen">
<div class="admin-layout">
    <%@ include file="../layout/sidebar.jsp" %>

    <div class="main-content">
        <%@ include file="../layout/header.jsp" %>

        <div id="page-content">
            <c:set var="modalVisible" value="${editMode || not empty errorMessage}" />
            <c:set var="defaultDiscountType" value="${editingCoupon != null ? editingCoupon.loaiGiamGia : 'Giảm %'}" />

            <div class="coupon-page">
                <div class="coupon-page__header">
                    <div>
                        <h2 class="coupon-page__title">Quản lý phiếu giảm giá</h2>
                        <p class="coupon-page__subtitle">Theo dõi, thêm mới và cập nhật phiếu giảm giá đang áp dụng trên hệ thống.</p>
                    </div>
                    <button class="page-button page-button--toolbar" id="openCreateModalButton" type="button">
                        <i class="fas fa-plus"></i>
                        <span>Thêm mới</span>
                    </button>
                </div>

                <c:if test="${not empty successMessage}">
                    <div class="page-alert page-alert--success">${successMessage}</div>
                </c:if>
                <c:if test="${not empty errorMessage}">
                    <div class="page-alert page-alert--error">${errorMessage}</div>
                </c:if>

                <div class="coupon-modal-overlay coupon-editor-screen ${modalVisible ? 'coupon-modal-overlay--show' : ''}" id="couponModalOverlay" aria-hidden="${modalVisible ? 'false' : 'true'}">
                    <div class="coupon-modal coupon-editor">
                        <div class="coupon-modal__header coupon-editor__topbar">
                            <div>
                                <p class="coupon-editor__eyebrow">Phiếu giảm giá</p>
                                <h3 id="couponModalTitle">${editMode ? 'Cập nhật phiếu giảm giá' : 'Thêm phiếu giảm giá'}</h3>
                            </div>
                            <button class="page-button page-button--secondary page-button--back" id="closeCouponModalButton" type="button">
                                <i class="fas fa-arrow-left"></i>
                                <span>Quay lại danh sách</span>
                            </button>
                        </div>

                        <form id="couponForm" class="coupon-editor__form" action="${pageContext.request.contextPath}${editMode ? '/PhieuGiamGia/update' : '/PhieuGiamGia/insert'}" method="post">
                            <input type="hidden" id="couponId" name="id" value="${editingCoupon.id}">
                            <input type="hidden" id="couponCode" name="maVoucher" value="${editingCoupon.maVoucher}">
                            <input type="hidden" id="couponUsedQuantity" name="soLuongDaDung" value="${editingCoupon != null ? editingCoupon.soLuongDaDung : 0}">
                            <input type="hidden" id="couponStatus" name="trangThai" value="${editingCoupon == null || editingCoupon.trangThai == 1 ? 1 : 0}">

                            <div class="coupon-modal__body coupon-editor__body">
                                <div class="coupon-editor__panel">
                                    <div class="coupon-form-grid">
                                        <label class="coupon-field">
                                            <span>Tên phiếu giảm giá <em>*</em></span>
                                            <input id="couponName" type="text" name="tenVoucher" maxlength="100" required value="${editingCoupon.tenVoucher}" placeholder="VD: Giảm 10% toàn đơn">
                                        </label>

                                        <label class="coupon-field">
                                            <span>Số lượng <em>*</em></span>
                                            <input id="couponQuantity" type="number" name="soLuong" min="1" step="1" required value="${editingCoupon != null ? editingCoupon.soLuong : 1}">
                                        </label>

                                        <label class="coupon-field">
                                            <span>Ngày bắt đầu <em>*</em></span>
                                            <input id="couponStartDate" type="date" name="ngayBatDau" required value="${editingCoupon.startDateValue}">
                                        </label>

                                        <label class="coupon-field">
                                            <span>Ngày kết thúc <em>*</em></span>
                                            <input id="couponEndDate" type="date" name="ngayKetThuc" required value="${editingCoupon.endDateValue}">
                                        </label>

                                        <label class="coupon-field">
                                            <span>Loại giảm <em>*</em></span>
                                            <select id="couponDiscountType" name="loaiGiamGia" required>
                                                <option value="Giảm %" ${defaultDiscountType == 'Giảm %' ? 'selected' : ''}>Giảm %</option>
                                                <option value="Giảm tiền" ${defaultDiscountType == 'Giảm tiền' ? 'selected' : ''}>Giảm tiền</option>
                                            </select>
                                        </label>

                                        <label class="coupon-field">
                                            <span>Giá trị giảm <em>*</em></span>
                                            <span class="coupon-input-group">
                                                <input id="couponDiscountValue" type="text" inputmode="numeric" name="giaTriGiam" required value="${editingCoupon.giaTriGiam}" placeholder="Nhập giá trị giảm">
                                                <span class="coupon-input-group__suffix" id="couponDiscountUnit">${defaultDiscountType == 'Giảm %' ? '%' : 'VND'}</span>
                                            </span>
                                        </label>

                                        <label class="coupon-field">
                                            <span>Giảm tối đa (VND)</span>
                                            <span class="coupon-input-group">
                                                <input id="couponMaxDiscount" type="text" inputmode="numeric" name="giamToiDa" value="${editingCoupon.giamToiDa}" placeholder="0">
                                                <span class="coupon-input-group__suffix">VND</span>
                                            </span>
                                        </label>

                                        <label class="coupon-field">
                                            <span>Đơn hàng tối thiểu</span>
                                            <span class="coupon-input-group">
                                                <input id="couponMinOrder" type="text" inputmode="numeric" name="donToiThieu" value="${editingCoupon.donToiThieu}" placeholder="0">
                                                <span class="coupon-input-group__suffix">VND</span>
                                            </span>
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <div class="coupon-modal__footer coupon-editor__footer">
                                <button class="page-button page-button--secondary" id="cancelCouponModalButton" type="button">Hủy</button>
                                <button class="page-button page-button--primary coupon-editor__submit" type="submit">Lưu phiếu giảm giá</button>
                            </div>
                        </form>
                    </div>
                </div>

                <section class="coupon-card coupon-card--filters">
                    <div class="coupon-card__header">
                        <h3>Bộ lọc tìm kiếm</h3>
                    </div>
                    <form action="${pageContext.request.contextPath}/PhieuGiamGia" method="get" class="coupon-filter-form">
                        <label class="coupon-field coupon-field--wide coupon-search-field">
                            <span>Tìm kiếm</span>
                            <input type="text" name="keyword" value="${keyword}" placeholder="Nhập mã / tên phiếu giảm giá...">
                        </label>
                        <label class="coupon-field">
                            <span>Loại giảm</span>
                            <select name="discountType">
                                <option value="">Tất cả</option>
                                <option value="percent" ${discountTypeFilter == 'percent' ? 'selected' : ''}>Giảm phần trăm</option>
                                <option value="amount" ${discountTypeFilter == 'amount' ? 'selected' : ''}>Giảm tiền</option>
                            </select>
                        </label>
                        <label class="coupon-field">
                            <span>Trạng thái</span>
                            <select name="status">
                                <option value="">Tất cả</option>
                                <option value="active" ${statusFilter == 'active' ? 'selected' : ''}>Đang áp dụng</option>
                                <option value="inactive" ${statusFilter == 'inactive' ? 'selected' : ''}>Tạm dừng</option>
                                <option value="expired" ${statusFilter == 'expired' ? 'selected' : ''}>Hết hạn</option>
                            </select>
                        </label>
                        <div class="coupon-filter-form__actions">
                            <button class="page-button page-button--primary" type="submit">Lọc</button>
                            <a class="page-button page-button--secondary" href="${pageContext.request.contextPath}/PhieuGiamGia">Đặt lại</a>
                        </div>
                    </form>
                </section>

                <section class="coupon-card">
                    <div class="coupon-card__header coupon-card__header--stats">
                        <div>
                            <h3>Danh sách phiếu giảm giá</h3>
                            <p>${totalCoupons} phiếu theo bộ lọc hiện tại. Đang áp dụng: ${activeCoupons}. Sắp hết hạn: ${expiringCoupons}.</p>
                        </div>
                    </div>

                    <div class="coupon-table-wrapper">
                        <table class="coupon-table">
                            <thead>
                            <tr>
                                <th>STT</th>
                                <th>Mã giảm giá</th>
                                <th>Tên giảm giá</th>
                                <th>Loại phiếu</th>
                                <th>Giá trị giảm</th>
                                <th>Đơn hàng tối thiểu</th>
                                <th>Số lượng</th>
                                <th>Ngày bắt đầu</th>
                                <th>Ngày kết thúc</th>
                                <th>Trạng thái</th>
                                <th>Hành động</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="coupon" items="${couponViews}" varStatus="loop">
                                <tr>
                                    <td>${loop.index + 1}</td>
                                    <td>${coupon.maVoucher}</td>
                                    <td>${coupon.tenVoucher}</td>
                                    <td><span class="coupon-type-badge">Công khai</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${coupon.loaiGiamGiaText == 'Giảm %'}">
                                                ${coupon.giaTriGiam}%
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:formatNumber value="${coupon.giaTriGiam}" type="number" maxFractionDigits="0"/> đ
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><fmt:formatNumber value="${coupon.donToiThieu}" type="number" maxFractionDigits="0"/> đ</td>
                                    <td>${coupon.soLuongDaDung}/${coupon.soLuong}</td>
                                    <td>${coupon.startDateText}</td>
                                    <td>${coupon.endDateText}</td>
                                    <td><span class="coupon-status ${coupon.statusCssClass}">${coupon.trangThaiText}</span></td>
                                    <td>
                                        <div class="coupon-actions">
                                            <button
                                                    class="page-button page-button--secondary page-button--small edit-coupon-button"
                                                    type="button"
                                                    data-id="${coupon.id}"
                                                    data-ma="${coupon.maVoucher}"
                                                    data-ten="${coupon.tenVoucher}"
                                                    data-loai-giam="${coupon.loaiGiamGia}"
                                                    data-gia-tri="${coupon.giaTriGiam}"
                                                    data-giam-toi-da="${coupon.giamToiDa}"
                                                    data-don-toi-thieu="${coupon.donToiThieu}"
                                                    data-so-luong="${coupon.soLuong}"
                                                    data-so-luong-da-dung="${coupon.soLuongDaDung}"
                                                    data-ngay-bat-dau="${coupon.startDateValue}"
                                                    data-ngay-ket-thuc="${coupon.endDateValue}"
                                                    data-trang-thai="${coupon.trangThai}">
                                                Sửa
                                            </button>
                                            <form action="${pageContext.request.contextPath}/PhieuGiamGia/toggle" method="post" onsubmit="return confirm('Bạn có chắc muốn đổi trạng thái phiếu này không?');">
                                                <input type="hidden" name="id" value="${coupon.id}">
                                                <input type="hidden" name="currentStatus" value="${coupon.trangThai}">
                                                <input type="hidden" name="keyword" value="${keyword}">
                                                <input type="hidden" name="status" value="${statusFilter}">
                                                <input type="hidden" name="discountType" value="${discountTypeFilter}">
                                                <button class="page-button page-button--secondary page-button--small" type="submit" ${coupon.statusCssClass == 'status-badge--expired' ? 'disabled' : ''}>
                                                    <c:choose>
                                                        <c:when test="${coupon.statusCssClass == 'status-badge--expired'}">Hết hạn</c:when>
                                                        <c:when test="${coupon.trangThai == 1}">Tạm dừng</c:when>
                                                        <c:otherwise>Mở lại</c:otherwise>
                                                    </c:choose>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty couponViews}">
                                <tr>
                                    <td colspan="11" class="coupon-empty-state">Không có dữ liệu phù hợp.</td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/Admin/QuanLyMaGiamGia/quan_ly_giam_gia.js"></script>
<script>
    (function () {
        const contextPath = '${pageContext.request.contextPath}';
        const couponPage = document.querySelector('.coupon-page');
        const modalOverlay = document.getElementById('couponModalOverlay');
        const openCreateButton = document.getElementById('openCreateModalButton');
        const closeModalButton = document.getElementById('closeCouponModalButton');
        const cancelModalButton = document.getElementById('cancelCouponModalButton');
        const form = document.getElementById('couponForm');
        const title = document.getElementById('couponModalTitle');
        const discountUnit = document.getElementById('couponDiscountUnit');

        if (form) {
            form.noValidate = true;
        }

        const fields = {
            id: document.getElementById('couponId'),
            maVoucher: document.getElementById('couponCode'),
            tenVoucher: document.getElementById('couponName'),
            loaiGiamGia: document.getElementById('couponDiscountType'),
            giaTriGiam: document.getElementById('couponDiscountValue'),
            giamToiDa: document.getElementById('couponMaxDiscount'),
            donToiThieu: document.getElementById('couponMinOrder'),
            soLuong: document.getElementById('couponQuantity'),
            soLuongDaDung: document.getElementById('couponUsedQuantity'),
            ngayBatDau: document.getElementById('couponStartDate'),
            ngayKetThuc: document.getElementById('couponEndDate'),
            trangThai: document.getElementById('couponStatus')
        };

        function parseNumberText(value) {
            const cleaned = String(value || '').replace(/[^\d]/g, '');
            if (!cleaned) {
                return 0;
            }
            return Number(cleaned);
        }

        function formatNumberText(value) {
            const cleaned = String(value || '').replace(/[^\d]/g, '');
            if (!cleaned) {
                return '';
            }
            return cleaned.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
        }

        function formatCurrencyField(field) {
            if (!field) {
                return;
            }
            field.value = formatNumberText(field.value);
        }

        function normalizeCurrencyField(field) {
            if (!field) {
                return;
            }
            field.value = String(field.value || '').replace(/[^\d]/g, '');
        }

        function bindCurrencyField(field) {
            if (!field) {
                return;
            }

            field.addEventListener('input', function () {
                const caretAtEnd = field.selectionStart === field.value.length;
                field.value = formatNumberText(field.value);
                if (caretAtEnd) {
                    field.setSelectionRange(field.value.length, field.value.length);
                }
            });

            field.addEventListener('blur', function () {
                field.value = formatNumberText(field.value);
            });
        }

        function syncDiscountUnit() {
            if (!discountUnit || !fields.loaiGiamGia) {
                return;
            }
            discountUnit.textContent = fields.loaiGiamGia.value === 'Giảm %' ? '%' : 'VND';

            if (fields.loaiGiamGia.value === 'Giảm %') {
                normalizeCurrencyField(fields.giaTriGiam);
            } else {
                formatCurrencyField(fields.giaTriGiam);
            }
        }

        const validator = typeof setupBootstrapValidation === 'function'
            ? setupBootstrapValidation(form, {
                confirmMessage: 'Bạn có chắc muốn lưu phiếu giảm giá này không?',
                rules: {
                    giaTriGiam: function (field) {
                        const discountValue = parseNumberText(field.value);
                        if (discountValue <= 0) {
                            return 'Giá trị giảm phải lớn hơn 0.';
                        }
                        if (fields.loaiGiamGia.value === 'Giảm %' && discountValue > 100) {
                            return 'Giảm phần trăm không được vượt quá 100.';
                        }
                        return '';
                    },
                    soLuongDaDung: function (field) {
                        const usedQuantity = parseNumberText(field.value);
                        const totalQuantity = parseNumberText(fields.soLuong.value);
                        if (usedQuantity > totalQuantity) {
                            return 'Số lượng đã dùng không được lớn hơn số lượng.';
                        }
                        return '';
                    },
                    giamToiDa: function (field) {
                        if (parseNumberText(field.value) < 0) {
                            return 'Giảm tối đa không được âm.';
                        }
                        return '';
                    },
                    donToiThieu: function (field) {
                        if (parseNumberText(field.value) < 0) {
                            return 'Đơn hàng tối thiểu không được âm.';
                        }
                        return '';
                    },
                    ngayKetThuc: function (field) {
                        if (fields.ngayBatDau.value && field.value && field.value < fields.ngayBatDau.value) {
                            return 'Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.';
                        }
                        return '';
                    }
                }
            })
            : null;

        function openModal() {
            modalOverlay.classList.add('coupon-modal-overlay--show');
            modalOverlay.setAttribute('aria-hidden', 'false');
            if (couponPage) {
                couponPage.classList.add('coupon-page--editor-open');
            }
        }

        function closeModal() {
            modalOverlay.classList.remove('coupon-modal-overlay--show');
            modalOverlay.setAttribute('aria-hidden', 'true');
            if (couponPage) {
                couponPage.classList.remove('coupon-page--editor-open');
            }
        }

        function resetFormToCreateMode() {
            form.action = contextPath + '/PhieuGiamGia/insert';
            title.textContent = 'Thêm phiếu giảm giá';
            fields.id.value = '';
            fields.maVoucher.value = '';
            fields.tenVoucher.value = '';
            fields.loaiGiamGia.value = 'Giảm %';
            fields.giaTriGiam.value = '';
            fields.giamToiDa.value = '';
            fields.donToiThieu.value = '';
            fields.soLuong.value = '1';
            fields.soLuongDaDung.value = '0';
            fields.ngayBatDau.value = '';
            fields.ngayKetThuc.value = '';
            fields.trangThai.value = '1';
            formatCurrencyField(fields.giaTriGiam);
            formatCurrencyField(fields.giamToiDa);
            formatCurrencyField(fields.donToiThieu);
            syncDiscountUnit();
            if (validator) {
                validator.reset();
            }
        }

        function fillEditForm(button) {
            form.action = contextPath + '/PhieuGiamGia/update';
            title.textContent = 'Cập nhật phiếu giảm giá';
            fields.id.value = button.dataset.id || '';
            fields.maVoucher.value = button.dataset.ma || '';
            fields.tenVoucher.value = button.dataset.ten || '';
            fields.loaiGiamGia.value = button.dataset.loaiGiam || 'Giảm %';
            fields.giaTriGiam.value = button.dataset.giaTri || '';
            fields.giamToiDa.value = button.dataset.giamToiDa || '';
            fields.donToiThieu.value = button.dataset.donToiThieu || '';
            fields.soLuong.value = button.dataset.soLuong || '1';
            fields.soLuongDaDung.value = button.dataset.soLuongDaDung || '0';
            fields.ngayBatDau.value = button.dataset.ngayBatDau || '';
            fields.ngayKetThuc.value = button.dataset.ngayKetThuc || '';
            fields.trangThai.value = button.dataset.trangThai || '1';
            formatCurrencyField(fields.giaTriGiam);
            formatCurrencyField(fields.giamToiDa);
            formatCurrencyField(fields.donToiThieu);
            syncDiscountUnit();
            if (validator) {
                validator.reset();
            }
        }

        if (openCreateButton) {
            openCreateButton.addEventListener('click', function () {
                resetFormToCreateMode();
                openModal();
            });
        }

        document.querySelectorAll('.edit-coupon-button').forEach(function (button) {
            button.addEventListener('click', function () {
                fillEditForm(button);
                openModal();
            });
        });

        [closeModalButton, cancelModalButton].forEach(function (button) {
            if (button) {
                button.addEventListener('click', closeModal);
            }
        });

        if (modalOverlay) {
            modalOverlay.addEventListener('click', function (event) {
                if (event.target === modalOverlay) {
                    closeModal();
                }
            });
        }

        if (fields.loaiGiamGia) {
            fields.loaiGiamGia.addEventListener('change', syncDiscountUnit);
        }

        bindCurrencyField(fields.giaTriGiam);
        bindCurrencyField(fields.giamToiDa);
        bindCurrencyField(fields.donToiThieu);

        if (validator) {
            fields.loaiGiamGia.addEventListener('change', function () {
                validator.validateField(fields.giaTriGiam);
            });
            fields.soLuong.addEventListener('change', function () {
                validator.validateField(fields.soLuongDaDung);
            });
            fields.ngayBatDau.addEventListener('change', function () {
                validator.validateField(fields.ngayKetThuc);
            });
        }

        if (form) {
            form.addEventListener('submit', function () {
                normalizeCurrencyField(fields.giaTriGiam);
                normalizeCurrencyField(fields.giamToiDa);
                normalizeCurrencyField(fields.donToiThieu);
            });
        }

        if (modalOverlay && modalOverlay.classList.contains('coupon-modal-overlay--show') && couponPage) {
            couponPage.classList.add('coupon-page--editor-open');
        }

        formatCurrencyField(fields.giaTriGiam);
        formatCurrencyField(fields.giamToiDa);
        formatCurrencyField(fields.donToiThieu);
        syncDiscountUnit();
    })();
</script>
</body>
</html>
