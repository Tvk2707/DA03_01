<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- Filters -->
<div class="filters">
    <button class="filter-btn filter-btn--active" data-filter="all">Tất cả</button>
    <button class="filter-btn" data-filter="public">Công khai</button>
    <button class="filter-btn" data-filter="personal">Cá nhân</button>
</div>

<<<<<<< HEAD
<!-- Coupons Grid -->
<div class="coupons-grid">
    <!-- Card 1: RIOR2025 -->
    <div class="coupon-card" data-type="public" data-status="active">
        <div class="coupon-card__header">
            <div class="coupon-card__top">
                <div class="coupon-card__name">
                    <span class="coupon-card__code">RIOR2025</span>
                    <button class="btn-icon" title="Sao chép">
                        <i class="far fa-copy"></i>
                    </button>
                </div>
                <div class="coupon-card__badges">
                    <span class="badge badge--public">Công khai</span>
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
            Đơn tối thiểu: <strong>2.000.000₫</strong>
        </div>

        <div class="coupon-card__progress">
            <div class="progress-label">
                <span>Đã sử dụng</span>
                <span>47%</span>
            </div>
            <div class="progress-bar">
                <div class="progress-bar__fill" style="width: 47%"></div>
            </div>
        </div>

        <div class="coupon-card__actions">
            <button class="btn btn--outline btn--sm">
                <i class="fas fa-edit"></i>
                <span>Chỉnh sửa</span>
            </button>
            <button class="btn btn--outline btn--sm">
                <i class="fas fa-paper-plane"></i>
                <span>Gửi cho KH</span>
            </button>
        </div>
    </div>

    <!-- Card 2: SUMMER25 -->
    <div class="coupon-card" data-type="public" data-status="active">
        <div class="coupon-card__header">
            <div class="coupon-card__top">
                <div class="coupon-card__name">
                    <span class="coupon-card__code">SUMMER25</span>
                    <button class="btn-icon" title="Sao chép">
                        <i class="far fa-copy"></i>
                    </button>
                </div>
                <div class="coupon-card__badges">
                    <span class="badge badge--public">Công khai</span>
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
                <div class="stat-item__value">500.000₫</div>
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
            Đơn tối thiểu: <strong>3.000.000₫</strong>
        </div>

        <div class="coupon-card__progress">
            <div class="progress-label">
                <span>Đã sử dụng</span>
                <span>46%</span>
            </div>
            <div class="progress-bar">
                <div class="progress-bar__fill" style="width: 46%"></div>
            </div>
        </div>

        <div class="coupon-card__actions">
            <button class="btn btn--outline btn--sm">
                <i class="fas fa-edit"></i>
                <span>Chỉnh sửa</span>
            </button>
            <button class="btn btn--outline btn--sm">
                <i class="fas fa-paper-plane"></i>
                <span>Gửi cho KH</span>
            </button>
        </div>
    </div>

    <!-- Card 3: VIP-LAN01 -->
    <div class="coupon-card" data-type="personal" data-status="active">
        <div class="coupon-card__header">
            <div class="coupon-card__top">
                <div class="coupon-card__name">
                    <span class="coupon-card__code">VIP-LAN01</span>
                    <button class="btn-icon" title="Sao chép">
                        <i class="far fa-copy"></i>
                    </button>
                </div>
                <div class="coupon-card__badges">
                    <span class="badge badge--personal">Cá nhân</span>
                </div>
            </div>
            <p class="coupon-card__desc">Ưu đãi VIP cho Nguyễn Thị Lan</p>
            <div class="coupon-card__status">
                <span class="status-dot status-dot--active"></span>
                <span class="status-text">Đang hoạt động</span>
            </div>
        </div>

        <div class="coupon-card__stats">
            <div class="stat-item">
                <div class="stat-item__value">20%</div>
                <div class="stat-item__label">Giảm</div>
            </div>
            <div class="stat-item">
                <div class="stat-item__value">1/1</div>
                <div class="stat-item__label">Đã dùng</div>
            </div>
            <div class="stat-item">
                <div class="stat-item__value">15/07/2025</div>
                <div class="stat-item__label">Hết hạn</div>
            </div>
        </div>

        <div class="coupon-card__min-order">
            Đơn tối thiểu: <strong>Không yêu cầu</strong>
        </div>

        <div class="coupon-card__progress">
            <div class="progress-label">
                <span>Đã sử dụng</span>
                <span>100%</span>
            </div>
            <div class="progress-bar">
                <div class="progress-bar__fill" style="width: 100%"></div>
            </div>
        </div>

        <div class="coupon-card__actions">
            <button class="btn btn--outline btn--sm">
                <i class="fas fa-edit"></i>
                <span>Chỉnh sửa</span>
            </button>
            <button class="btn btn--outline btn--sm">
                <i class="fas fa-paper-plane"></i>
                <span>Gửi cho KH</span>
            </button>
        </div>
    </div>

    <!-- Card 4: BIRTHDAY-HUY -->
    <div class="coupon-card" data-type="personal" data-status="inactive">
        <div class="coupon-card__header">
            <div class="coupon-card__top">
                <div class="coupon-card__name">
                    <span class="coupon-card__code">BIRTHDAY-HUY</span>
                    <button class="btn-icon" title="Sao chép">
                        <i class="far fa-copy"></i>
                    </button>
                </div>
                <div class="coupon-card__badges">
                    <span class="badge badge--personal">Cá nhân</span>
                </div>
            </div>
            <p class="coupon-card__desc">Sinh nhật Phạm Đức Huy</p>
            <div class="coupon-card__status">
                <span class="status-dot status-dot--inactive"></span>
                <span class="status-text">Tạm dừng</span>
            </div>
        </div>

        <div class="coupon-card__stats">
            <div class="stat-item">
                <div class="stat-item__value">10%</div>
                <div class="stat-item__label">Giảm</div>
            </div>
            <div class="stat-item">
                <div class="stat-item__value">0/1</div>
                <div class="stat-item__label">Đã dùng</div>
            </div>
            <div class="stat-item">
                <div class="stat-item__value">10/07/2025</div>
                <div class="stat-item__label">Hết hạn</div>
            </div>
        </div>

        <div class="coupon-card__min-order">
            Đơn tối thiểu: <strong>1.000.000₫</strong>
        </div>

        <div class="coupon-card__progress">
            <div class="progress-label">
                <span>Đã sử dụng</span>
                <span>0%</span>
            </div>
            <div class="progress-bar">
                <div class="progress-bar__fill" style="width: 0%"></div>
            </div>
        </div>

        <div class="coupon-card__actions">
            <button class="btn btn--outline btn--sm">
                <i class="fas fa-edit"></i>
                <span>Chỉnh sửa</span>
            </button>
            <button class="btn btn--outline btn--sm">
                <i class="fas fa-paper-plane"></i>
                <span>Gửi cho KH</span>
            </button>
        </div>
    </div>
</div>
=======
<!-- Coupons Grid: dữ liệu sẽ được đổ từ backend/database sau -->
<div class="coupons-grid">
    <div class="empty-state">
        <i class="fas fa-ticket"></i>
        <p>Chưa có mã giảm giá từ database</p>
    </div>
</div>
>>>>>>> THONG_KE
