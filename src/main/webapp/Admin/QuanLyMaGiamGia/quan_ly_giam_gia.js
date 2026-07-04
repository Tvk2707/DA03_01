/**
 * Quản lý giảm giá - Tab Switching & Filters
 * File: quan-ly-giam-gia.js
 */

// ==================== TAB SWITCHING ====================

/**
 * Hiển thị tab Mã giảm giá
 */
function showCoupon() {
    // Cập nhật trạng thái tab
    document.querySelectorAll('.tab').forEach(function(tab) {
        tab.classList.remove('tab--active');
    });
    document.querySelector('[data-tab="coupon"]').classList.add('tab--active');

    // Cập nhật hiển thị nội dung
    document.getElementById('couponContent').classList.add('tab-content--active');
    document.getElementById('campaignContent').classList.remove('tab-content--active');

    // Cập nhật nút tạo mới
    var btnText = document.getElementById('btnCreateText');
    if (btnText) {
        btnText.textContent = 'Tạo mã giảm giá';
    }
}

/**
 * Hiển thị tab Đợt giảm giá sản phẩm
 */
function showCampaign() {
    // Cập nhật trạng thái tab
    document.querySelectorAll('.tab').forEach(function(tab) {
        tab.classList.remove('tab--active');
    });
    document.querySelector('[data-tab="campaign"]').classList.add('tab--active');

    // Cập nhật hiển thị nội dung
    document.getElementById('campaignContent').classList.add('tab-content--active');
    document.getElementById('couponContent').classList.remove('tab-content--active');

    // Cập nhật nút tạo mới
    var btnText = document.getElementById('btnCreateText');
    if (btnText) {
        btnText.textContent = 'Tạo đợt giảm giá';
    }
}

// ==================== KHỞI TẠO KHI DOM LOAD ====================

document.addEventListener('DOMContentLoaded', function() {

    // --- Filter chức năng ---
    var filterButtons = document.querySelectorAll('.filter-btn');
    var couponCards = document.querySelectorAll('.coupon-card');

    filterButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            // Cập nhật nút active
            filterButtons.forEach(function(b) {
                b.classList.remove('filter-btn--active');
            });
            this.classList.add('filter-btn--active');

            var filter = this.dataset.filter;

            // Lọc cards
            couponCards.forEach(function(card) {
                if (filter === 'all') {
                    card.style.display = 'block';
                } else {
                    var cardType = card.dataset.type;
                    if (cardType === filter) {
                        card.style.display = 'block';
                    } else {
                        card.style.display = 'none';
                    }
                }
            });
        });
    });

    // --- Nút tạo mới ---
    var createBtn = document.getElementById('btnCreate');
    if (createBtn) {
        createBtn.addEventListener('click', function() {
            var activeTab = document.querySelector('.tab--active');
            if (!activeTab) return;

            var tabName = activeTab.dataset.tab;

            if (tabName === 'coupon') {
                // Điều hướng đến trang tạo mã giảm giá
                // window.location.href = '/admin/discount/create-coupon';
                alert('Chuyển đến trang tạo mã giảm giá');
            } else {
                // Điều hướng đến trang tạo đợt giảm giá
                // window.location.href = '/admin/discount/create-campaign';
                alert('Chuyển đến trang tạo đợt giảm giá');
            }
        });
    }

    // --- Sao chép mã giảm giá ---
    var copyButtons = document.querySelectorAll('.btn-copy');
    copyButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            var codeElement = this.closest('.coupon-card__name')
                .querySelector('.coupon-card__code');
            if (!codeElement) return;

            var code = codeElement.textContent.trim();

            // Sao chép vào clipboard
            if (navigator.clipboard && navigator.clipboard.writeText) {
                navigator.clipboard.writeText(code).then(function() {
                    showCopyFeedback(btn);
                }).catch(function(err) {
                    console.error('Lỗi sao chép:', err);
                    fallbackCopy(code, btn);
                });
            } else {
                fallbackCopy(code, btn);
            }
        });
    });

    // --- Khởi tạo progress bars ---
    initProgressBars();
});

// ==================== HÀM TIỆN ÍCH ====================

/**
 * Khởi tạo thanh tiến trình từ data-width
 */
function initProgressBars() {
    var progressFills = document.querySelectorAll('.progress-bar__fill');
    progressFills.forEach(function(fill) {
        var width = fill.dataset.width || 0;
        // Delay nhỏ để animation chạy mượt
        setTimeout(function() {
            fill.style.width = width + '%';
        }, 100);
    });
}

/**
 * Hiển thị phản hồi khi sao chép thành công
 */
function showCopyFeedback(btn) {
    var originalHTML = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-check"></i>';
    btn.style.color = '#43a047';

    setTimeout(function() {
        btn.innerHTML = originalHTML;
        btn.style.color = '';
    }, 2000);
}

/**
 * Sao chép fallback cho trình duyệt cũ
 */
function fallbackCopy(text, btn) {
    var textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.position = 'fixed';
    textarea.style.opacity = '0';
    document.body.appendChild(textarea);
    textarea.select();

    try {
        document.execCommand('copy');
        showCopyFeedback(btn);
    } catch (err) {
        console.error('Lỗi sao chép fallback:', err);
    }

    document.body.removeChild(textarea);
}

/**
 * Format tiền tệ VND
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

/**
 * Tính phần trăm
 */
function calculatePercentage(used, total) {
    if (total === 0) return 0;
    return Math.round((used / total) * 100);
}