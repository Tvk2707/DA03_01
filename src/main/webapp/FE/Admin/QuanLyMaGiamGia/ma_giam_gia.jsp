<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- Filters -->
<div class="filters">
    <button class="filter-btn filter-btn--active" data-filter="all">Tất cả</button>
    <button class="filter-btn" data-filter="public">Công khai</button>
    <button class="filter-btn" data-filter="personal">Cá nhân</button>
</div>

<!-- Coupons Grid: dữ liệu sẽ được đổ từ backend/database sau -->
<div class="coupons-grid">
    <div class="empty-state">
        <i class="fas fa-ticket"></i>
        <p>Chưa có mã giảm giá từ database</p>
    </div>
</div>
