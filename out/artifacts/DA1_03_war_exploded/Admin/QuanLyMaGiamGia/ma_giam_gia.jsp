<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="filters">
    <button class="filter-btn filter-btn--active" data-filter="all" type="button">Tất cả</button>
</div>

<div class="coupons-grid">
    <div class="coupon-card" data-type="public" data-status="active">
        <div class="coupon-card__header">
            <div class="coupon-card__top">
                <div class="coupon-card__name">
                    <span class="coupon-card__code">RIOR2025</span>
                    <button class="btn-icon btn-copy" title="Sao chép" type="button">
                        <i class="far fa-copy"></i>
                    </button>
                </div>
            </div>
            <p class="coupon-card__desc">Giảm 15% toàn bộ đơn hàng</p>
            <div class="coupon-card__status">
                <span class="status-dot status-dot--active"></span>
                <span class="status-text">Đang hoạt động</span>
            </div>
        </div>

        <div class="coupon-card__stats">
            <div class="stat-item">
                <div class="stat-item__value">15%</div>
                <div class="stat-item__label">Giảm</div>
            </div>
            <div class="stat-item">
                <div class="stat-item__value">47/100</div>
                <div class="stat-item__label">Đã dùng</div>
            </div>
            <div class="stat-item">
                <div class="stat-item__value">31/12/2025</div>
                <div class="stat-item__label">Hết hạn</div>
            </div>
        </div>

        <div class="coupon-card__min-order">
            Đơn tối thiểu: <strong>2.000.000đ</strong>
        </div>

        <div class="coupon-card__progress">
            <div class="progress-label">
                <span>Đã sử dụng</span>
                <span class="progress-percent">47%</span>
            </div>
            <div class="progress-bar">
                <div class="progress-bar__fill" data-width="47"></div>
            </div>
        </div>

        <div class="coupon-card__actions">
            <button class="btn btn--outline btn--sm" type="button">
                <i class="fas fa-edit"></i>
                <span>Chỉnh sửa</span>
            </button>
        </div>
    </div>

    <div class="coupon-card" data-type="public" data-status="active">
        <div class="coupon-card__header">
            <div class="coupon-card__top">
                <div class="coupon-card__name">
                    <span class="coupon-card__code">SUMMER25</span>
                    <button class="btn-icon btn-copy" title="Sao chép" type="button">
                        <i class="far fa-copy"></i>
                    </button>
                </div>
            </div>
            <p class="coupon-card__desc">Giảm 500k cho đơn từ 3 triệu</p>
            <div class="coupon-card__status">
                <span class="status-dot status-dot--active"></span>
                <span class="status-text">Đang hoạt động</span>
            </div>
        </div>

        <div class="coupon-card__stats">
            <div class="stat-item">
                <div class="stat-item__value">500.000đ</div>
                <div class="stat-item__label">Giảm</div>
            </div>
            <div class="stat-item">
                <div class="stat-item__value">23/50</div>
                <div class="stat-item__label">Đã dùng</div>
            </div>
            <div class="stat-item">
                <div class="stat-item__value">31/08/2025</div>
                <div class="stat-item__label">Hết hạn</div>
            </div>
        </div>

        <div class="coupon-card__min-order">
            Đơn tối thiểu: <strong>3.000.000đ</strong>
        </div>

        <div class="coupon-card__progress">
            <div class="progress-label">
                <span>Đã sử dụng</span>
                <span class="progress-percent">46%</span>
            </div>
            <div class="progress-bar">
                <div class="progress-bar__fill" data-width="46"></div>
            </div>
        </div>

        <div class="coupon-card__actions">
            <button class="btn btn--outline btn--sm" type="button">
                <i class="fas fa-edit"></i>
                <span>Chỉnh sửa</span>
            </button>
        </div>
    </div>
</div>
