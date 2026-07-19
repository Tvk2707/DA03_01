/**
 * File này viết theo kiểu đơn giản, dễ đọc, dễ sửa.
 * Giữ lại các tên hàm cũ để JSP đang dùng không bị lỗi.
 */

function showCoupon() {
    var allTabs = document.querySelectorAll('.tab');
    var couponTab = document.querySelector('[data-tab="coupon"]');
    var couponContent = document.getElementById('couponContent');
    var campaignContent = document.getElementById('campaignContent');
    var btnText = document.getElementById('btnCreateText');

    allTabs.forEach(function(tab) {
        tab.classList.remove('tab--active');
    });

    if (couponTab) {
        couponTab.classList.add('tab--active');
    }
    if (couponContent) {
        couponContent.classList.add('tab-content--active');
    }
    if (campaignContent) {
        campaignContent.classList.remove('tab-content--active');
    }
    if (btnText) {
        btnText.textContent = 'Tạo mã giảm giá';
    }
}

function showCampaign() {
    var allTabs = document.querySelectorAll('.tab');
    var campaignTab = document.querySelector('[data-tab="campaign"]');
    var couponContent = document.getElementById('couponContent');
    var campaignContent = document.getElementById('campaignContent');
    var btnText = document.getElementById('btnCreateText');

    allTabs.forEach(function(tab) {
        tab.classList.remove('tab--active');
    });

    if (campaignTab) {
        campaignTab.classList.add('tab--active');
    }
    if (campaignContent) {
        campaignContent.classList.add('tab-content--active');
    }
    if (couponContent) {
        couponContent.classList.remove('tab-content--active');
    }
    if (btnText) {
        btnText.textContent = 'Tạo đợt giảm giá';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    initFilterButtons();
    initCreateButton();
    initCopyButtons();
    initProgressBars();
    initCouponActionIcons();
    initCouponSearchAutocomplete();
});

function initFilterButtons() {
    var filterButtons = document.querySelectorAll('.filter-btn');
    var couponCards = document.querySelectorAll('.coupon-card');

    filterButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            var selectedFilter = button.dataset.filter;

            filterButtons.forEach(function(item) {
                item.classList.remove('filter-btn--active');
            });
            button.classList.add('filter-btn--active');

            couponCards.forEach(function(card) {
                if (selectedFilter === 'all' || card.dataset.type === selectedFilter) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });
}

function initCreateButton() {
    var createBtn = document.getElementById('btnCreate');
    if (!createBtn) {
        return;
    }

    createBtn.addEventListener('click', function() {
        var activeTab = document.querySelector('.tab--active');
        if (!activeTab) {
            return;
        }

        if (activeTab.dataset.tab === 'coupon') {
            alert('Chuyển đến trang tạo mã giảm giá');
        } else {
            alert('Chuyển đến trang tạo đợt giảm giá');
        }
    });
}

function initCopyButtons() {
    var copyButtons = document.querySelectorAll('.btn-copy');

    copyButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            var nameBox = button.closest('.coupon-card__name');
            if (!nameBox) {
                return;
            }

            var codeElement = nameBox.querySelector('.coupon-card__code');
            if (!codeElement) {
                return;
            }

            var code = codeElement.textContent.trim();

            if (navigator.clipboard && navigator.clipboard.writeText) {
                navigator.clipboard.writeText(code).then(function() {
                    showCopyFeedback(button);
                }).catch(function() {
                    fallbackCopy(code, button);
                });
            } else {
                fallbackCopy(code, button);
            }
        });
    });
}

function initProgressBars() {
    var bars = document.querySelectorAll('.progress-bar__fill');

    bars.forEach(function(bar) {
        var width = bar.dataset.width || 0;
        setTimeout(function() {
            bar.style.width = width + '%';
        }, 100);
    });
}

function showCopyFeedback(button) {
    var oldHtml = button.innerHTML;
    button.innerHTML = '<i class="fas fa-check"></i>';
    button.style.color = '#43a047';

    setTimeout(function() {
        button.innerHTML = oldHtml;
        button.style.color = '';
    }, 1500);
}

function fallbackCopy(text, button) {
    var textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.position = 'fixed';
    textarea.style.opacity = '0';
    document.body.appendChild(textarea);
    textarea.select();

    try {
        document.execCommand('copy');
        showCopyFeedback(button);
    } catch (error) {
        console.error('Không thể sao chép mã giảm giá', error);
    }

    document.body.removeChild(textarea);
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function calculatePercentage(used, total) {
    if (!total) {
        return 0;
    }
    return Math.round((used / total) * 100);
}

function initCouponActionIcons() {
    var editButtons = document.querySelectorAll('.edit-coupon-button');
    var toggleButtons = document.querySelectorAll('.coupon-actions form .page-button');

    editButtons.forEach(function(button) {
        button.classList.add('page-button--icon');
        button.innerHTML = '<i class="fas fa-pen"></i>';
        button.title = 'Sửa phiếu';
        button.setAttribute('aria-label', 'Sửa phiếu');
    });

    toggleButtons.forEach(function(button) {
        var label = (button.textContent || '').toLowerCase().trim();

        button.classList.add('page-button--icon');

        if (button.disabled || label.indexOf('hết') === 0 || label.indexOf('h') === 0) {
            button.innerHTML = '<i class="fas fa-clock"></i>';
            button.title = 'Phiếu đã hết hạn';
            button.setAttribute('aria-label', 'Phiếu đã hết hạn');
            return;
        }

        if (label.indexOf('tạm') === 0 || label.indexOf('t') === 0) {
            button.innerHTML = '<i class="fas fa-pause"></i>';
            button.title = 'Tạm dừng phiếu';
            button.setAttribute('aria-label', 'Tạm dừng phiếu');
            return;
        }

        button.innerHTML = '<i class="fas fa-play"></i>';
        button.title = 'Mở lại phiếu';
        button.setAttribute('aria-label', 'Mở lại phiếu');
    });
}

function setupBootstrapValidation(form, options) {
    if (!form) {
        return null;
    }

    var settings = options || {};
    var rules = settings.rules || {};
    var submitted = false;
    var fields = form.querySelectorAll('input, select, textarea');

    function getUsableFields() {
        return Array.from(fields).filter(function(field) {
            return field.type !== 'hidden' && !field.disabled;
        });
    }

    function addBootstrapClass(field) {
        if (field.tagName === 'SELECT') {
            field.classList.add('form-select');
        } else if (field.type !== 'checkbox' && field.type !== 'radio') {
            field.classList.add('form-control');
        }
    }

    function getFeedback(field) {
        var next = field.nextElementSibling;
        if (next && next.classList.contains('invalid-feedback')) {
            return next;
        }

        var feedback = document.createElement('div');
        feedback.className = 'invalid-feedback';
        field.insertAdjacentElement('afterend', feedback);
        return feedback;
    }

    function getFieldLabel(field) {
        var label = field.closest('label');
        if (!label) {
            return 'Trường này';
        }

        var span = label.querySelector('span');
        if (!span) {
            return 'Trường này';
        }

        return span.textContent.replace('*', '').trim();
    }

    function getDefaultError(field) {
        var label = getFieldLabel(field);

        if (field.validity.valueMissing) {
            return label + ' không được để trống.';
        }
        if (field.validity.rangeUnderflow) {
            return label + ' phải lớn hơn hoặc bằng ' + field.min + '.';
        }
        if (field.validity.rangeOverflow) {
            return label + ' phải nhỏ hơn hoặc bằng ' + field.max + '.';
        }
        if (field.validity.typeMismatch || field.validity.badInput) {
            return label + ' không hợp lệ.';
        }
        if (field.validity.stepMismatch) {
            return label + ' không đúng định dạng.';
        }

        return field.validationMessage || 'Dữ liệu không hợp lệ.';
    }

    function getCustomError(field) {
        var rule = rules[field.name] || rules[field.id];
        if (typeof rule !== 'function') {
            return '';
        }
        return rule(field, form) || '';
    }

    function validateOneField(field, showGreen) {
        var feedback = getFeedback(field);
        var customError = getCustomError(field);

        field.setCustomValidity('');
        if (customError) {
            field.setCustomValidity(customError);
        }

        if (field.checkValidity()) {
            feedback.textContent = '';
            field.classList.remove('is-invalid');
            if (submitted || showGreen) {
                field.classList.add('is-valid');
            }
            return true;
        }

        feedback.textContent = getDefaultError(field);
        field.classList.remove('is-valid');
        field.classList.add('is-invalid');
        return false;
    }

    function validateForm() {
        var usableFields = getUsableFields();
        var firstErrorField = null;

        usableFields.forEach(function(field) {
            var isValid = validateOneField(field, false);
            if (!isValid && !firstErrorField) {
                firstErrorField = field;
            }
        });

        if (firstErrorField) {
            firstErrorField.focus();
            return false;
        }

        return true;
    }

    function resetValidation() {
        submitted = false;
        form.classList.remove('was-validated');

        getUsableFields().forEach(function(field) {
            field.setCustomValidity('');
            field.classList.remove('is-invalid', 'is-valid');
            getFeedback(field).textContent = '';
        });
    }

    getUsableFields().forEach(function(field) {
        addBootstrapClass(field);
        getFeedback(field);

        field.addEventListener('input', function() {
            if (submitted || field.classList.contains('is-invalid')) {
                validateOneField(field, false);
            }
        });

        field.addEventListener('change', function() {
            if (submitted || field.classList.contains('is-invalid')) {
                validateOneField(field, false);
            }
        });
    });

    form.addEventListener('submit', function(event) {
        submitted = true;
        form.classList.add('was-validated');

        if (!validateForm()) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }

        if (settings.confirmMessage && !window.confirm(settings.confirmMessage)) {
            event.preventDefault();
            event.stopPropagation();
        }
    });

    return {
        reset: resetValidation,
        validateField: function(field) {
            if (!field) {
                return true;
            }
            return validateOneField(field, true);
        },
        validateForm: validateForm
    };
}

function initCouponSearchAutocomplete() {
    var searchInput = document.querySelector('.coupon-filter-form input[name="keyword"]');
    var table = document.querySelector('.coupon-table');
    var searchField;
    var suggestionBox;
    var coupons;
    var currentList = [];
    var selectedIndex = -1;

    if (!searchInput || !table) {
        return;
    }

    searchField = searchInput.closest('.coupon-field');
    if (!searchField) {
        return;
    }

    searchField.classList.add('coupon-search-field');
    searchInput.setAttribute('autocomplete', 'off');
    searchInput.setAttribute('spellcheck', 'false');

    suggestionBox = document.createElement('div');
    suggestionBox.className = 'coupon-search-suggestions';
    searchField.appendChild(suggestionBox);

    coupons = readCouponsFromTable(table);

    function hideSuggestions() {
        suggestionBox.classList.remove('coupon-search-suggestions--show');
        suggestionBox.innerHTML = '';
        currentList = [];
        selectedIndex = -1;
    }

    function showSuggestions(list) {
        if (!list.length) {
            hideSuggestions();
            return;
        }

        currentList = list;
        selectedIndex = -1;
        suggestionBox.innerHTML = '';

        list.forEach(function(coupon, index) {
            suggestionBox.innerHTML += renderCouponSuggestion(coupon, index);
        });

        suggestionBox.classList.add('coupon-search-suggestions--show');
    }

    function highlightItem(index) {
        var items = suggestionBox.querySelectorAll('.coupon-search-suggestion');

        if (!items.length) {
            selectedIndex = -1;
            return;
        }

        if (index < 0) {
            index = items.length - 1;
        }
        if (index >= items.length) {
            index = 0;
        }

        items.forEach(function(item) {
            item.classList.remove('coupon-search-suggestion--active');
        });

        items[index].classList.add('coupon-search-suggestion--active');
        items[index].scrollIntoView({ block: 'nearest' });
        selectedIndex = index;
    }

    function chooseCoupon(coupon) {
        searchInput.value = coupon.maPhieu;
        hideSuggestions();
        searchInput.focus();
    }

    searchInput.addEventListener('input', function() {
        var keyword = normalizeCouponSearch(searchInput.value);
        var list = [];

        if (!keyword) {
            hideSuggestions();
            return;
        }

        coupons.forEach(function(coupon) {
            var text = normalizeCouponSearch(
                coupon.maPhieu + ' ' +
                coupon.tenPhieu + ' ' +
                coupon.loaiPhieu + ' ' +
                coupon.giamGia + ' ' +
                coupon.donToiThieu + ' ' +
                coupon.soLuong + ' ' +
                coupon.ngayBatDau + ' ' +
                coupon.ngayKetThuc + ' ' +
                coupon.trangThai
            );

            if (text.indexOf(keyword) !== -1) {
                list.push(coupon);
            }
        });

        showSuggestions(list.slice(0, 6));
    });

    searchInput.addEventListener('keydown', function(event) {
        if (!suggestionBox.classList.contains('coupon-search-suggestions--show')) {
            return;
        }

        if (event.key === 'ArrowDown') {
            event.preventDefault();
            highlightItem(selectedIndex + 1);
        }

        if (event.key === 'ArrowUp') {
            event.preventDefault();
            highlightItem(selectedIndex - 1);
        }

        if (event.key === 'Escape') {
            hideSuggestions();
        }

        if (event.key === 'Enter' && selectedIndex >= 0) {
            event.preventDefault();
            chooseCoupon(currentList[selectedIndex]);
        }
    });

    suggestionBox.addEventListener('mousedown', function(event) {
        var item = event.target.closest('.coupon-search-suggestion');
        var index;

        if (!item) {
            return;
        }

        event.preventDefault();
        index = Number(item.dataset.index);

        if (!Number.isNaN(index) && currentList[index]) {
            chooseCoupon(currentList[index]);
        }
    });

    document.addEventListener('click', function(event) {
        if (!searchField.contains(event.target)) {
            hideSuggestions();
        }
    });
}

function readCouponsFromTable(table) {
    var rows = table.querySelectorAll('tbody tr');
    var result = [];

    rows.forEach(function(row) {
        var cells = row.querySelectorAll('td');

        if (cells.length < 10 || row.querySelector('[colspan]')) {
            return;
        }

        result.push({
            maPhieu: cells[1].textContent.trim(),
            tenPhieu: cells[2].textContent.trim(),
            loaiPhieu: cells[3].textContent.trim(),
            giamGia: cells[4].textContent.trim(),
            donToiThieu: cells[5].textContent.trim(),
            soLuong: cells[6].textContent.trim(),
            ngayBatDau: cells[7].textContent.trim(),
            ngayKetThuc: cells[8].textContent.trim(),
            trangThai: cells[9].textContent.trim()
        });
    });

    return result;
}

function normalizeCouponSearch(value) {
    return (value || '')
        .toString()
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .replace(/đ/g, 'd')
        .replace(/[^a-z0-9/%\s]/g, ' ')
        .replace(/\s+/g, ' ')
        .trim();
}

function renderCouponSuggestion(coupon, index) {
    return [
        '<button class="coupon-search-suggestion" type="button" data-index="', index, '">',
        '<div class="coupon-search-suggestion__top">',
        '<span class="coupon-search-suggestion__code">', escapeCouponHtml(coupon.maPhieu), '</span>',
        '<span class="coupon-search-suggestion__name">', escapeCouponHtml(coupon.tenPhieu), '</span>',
        '</div>',
        '<div class="coupon-search-suggestion__grid">',
        renderCouponMeta('Mã phiếu', coupon.maPhieu),
        renderCouponMeta('Tên phiếu', coupon.tenPhieu),
        renderCouponMeta('Loại phiếu', coupon.loaiPhieu),
        renderCouponMeta('Giảm giá', coupon.giamGia),
        renderCouponMeta('Đơn tối thiểu', coupon.donToiThieu),
        renderCouponMeta('Số lượng', coupon.soLuong),
        renderCouponMeta('Ngày bắt đầu', coupon.ngayBatDau),
        renderCouponMeta('Ngày kết thúc', coupon.ngayKetThuc),
        renderCouponMeta('Trạng thái', coupon.trangThai),
        '</div>',
        '</button>'
    ].join('');
}

function renderCouponMeta(label, value) {
    return [
        '<div class="coupon-search-suggestion__meta">',
        '<span class="coupon-search-suggestion__meta-label">', escapeCouponHtml(label), '</span>',
        '<span class="coupon-search-suggestion__meta-value">', escapeCouponHtml(value), '</span>',
        '</div>'
    ].join('');
}

function escapeCouponHtml(value) {
    return (value || '')
        .toString()
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}
