/**
 * Discount Management - Tab Switching & Filters
 */

// ==================== TAB SWITCHING ====================

/**
 * Show Coupon tab content
 */
function showCoupon() {
    // Update tab buttons
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.remove('tab--active');
    });
    document.querySelector('[data-tab="coupon"]').classList.add('tab--active');

    // Update content visibility
    document.getElementById('couponContent').classList.add('tab-content--active');
    document.getElementById('campaignContent').classList.remove('tab-content--active');

    // Update create button text
    document.getElementById('btnCreateText').textContent = 'Tạo mã giảm giá';
}

/**
 * Show Campaign tab content
 */
function showCampaign() {
    // Update tab buttons
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.remove('tab--active');
    });
    document.querySelector('[data-tab="campaign"]').classList.add('tab--active');

    // Update content visibility
    document.getElementById('campaignContent').classList.add('tab-content--active');
    document.getElementById('couponContent').classList.remove('tab-content--active');

    // Update create button text
    document.getElementById('btnCreateText').textContent = 'Tạo đợt giảm giá';
}

// ==================== FILTER FUNCTIONALITY ====================

/**
 * Filter coupons by type
 */
document.addEventListener('DOMContentLoaded', function() {
    const filterButtons = document.querySelectorAll('.filter-btn');
    const couponCards = document.querySelectorAll('.coupon-card');

    filterButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            // Update active button
            filterButtons.forEach(b => b.classList.remove('filter-btn--active'));
            this.classList.add('filter-btn--active');

            const filter = this.dataset.filter;

            // Filter cards
            couponCards.forEach(card => {
                if (filter === 'all') {
                    card.style.display = 'block';
                } else {
                    const cardType = card.dataset.type;
                    if (cardType === filter) {
                        card.style.display = 'block';
                    } else {
                        card.style.display = 'none';
                    }
                }
            });
        });
    });

    // ==================== CREATE BUTTON ====================
    const createBtn = document.getElementById('btnCreate');
    createBtn.addEventListener('click', function() {
        const activeTab = document.querySelector('.tab--active').dataset.tab;

        if (activeTab === 'coupon') {
            // Navigate to create coupon page
            window.location.href = '/admin/discount/create-coupon';
        } else {
            // Navigate to create campaign page
            window.location.href = '/admin/discount/create-campaign';
        }
    });

    // ==================== COPY COUPON CODE ====================
    const copyButtons = document.querySelectorAll('.btn-icon[title="Sao chép"]');
    copyButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const code = this.closest('.coupon-card__name').querySelector('.coupon-card__code').textContent;

            // Copy to clipboard
            navigator.clipboard.writeText(code).then(() => {
                // Show feedback
                const originalIcon = this.innerHTML;
                this.innerHTML = '<i class="fas fa-check"></i>';
                this.style.color = '#43a047';

                setTimeout(() => {
                    this.innerHTML = originalIcon;
                    this.style.color = '';
                }, 2000);
            }).catch(err => {
                console.error('Failed to copy:', err);
            });
        });
    });
});

// ==================== UTILITY FUNCTIONS ====================

/**
 * Format currency
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

/**
 * Calculate percentage
 */
function calculatePercentage(used, total) {
    return Math.round((used / total) * 100);
}

/**
 * Update progress bar
 */
function updateProgressBar(cardElement, used, total) {
    const percentage = calculatePercentage(used, total);
    const progressBar = cardElement.querySelector('.progress-bar__fill');
    const percentageLabel = cardElement.querySelector('.progress-label span:last-child');

    if (progressBar) {
        progressBar.style.width = percentage + '%';
    }
    if (percentageLabel) {
        percentageLabel.textContent = percentage + '%';
    }
}