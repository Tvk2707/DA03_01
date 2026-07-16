<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    request.setAttribute("pageTitle", "Chi tiết hóa đơn");
    request.setAttribute("activeMenu", "hoadon");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết hóa đơn - RIOR Admin</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/sidebar.css">
    <link rel="stylesheet" href="../css/header.css">
    <link rel="stylesheet" href="../css/hoa_don.css">
</head>
<body>
<%@ include file="../layout/sidebar.jsp" %>

<div class="main-content">
    <%@ include file="../layout/header.jsp" %>

    <main id="page-content" class="invoice-page invoice-detail-page">
        <section class="invoice-page-header">
            <div>
                <h1 class="invoice-title">Chi tiết hóa đơn</h1>
                <p class="invoice-subtitle">Kiểm tra thông tin khách hàng, giao nhận, thanh toán và sản phẩm kính mắt</p>
            </div>
            <a class="invoice-btn invoice-btn--outline" href="quan_ly_hoa_don.jsp">
                <i class="fas fa-arrow-left"></i>
                Quay lại
            </a>
        </section>

        <section class="invoice-timeline-card">
            <div class="invoice-timeline" id="invoiceTimeline"></div>
        </section>

        <section class="invoice-detail-actions">
            <div class="invoice-action-left">
                <button class="invoice-btn invoice-btn--primary" id="btnChangeStatus" type="button">
                    <i class="fas fa-arrows-rotate"></i>
                    <span>Đổi trạng thái</span>
                </button>
                <button class="invoice-btn invoice-btn--danger" id="btnCancelOrder" type="button">
                    <i class="fas fa-ban"></i>
                    Hủy đơn
                </button>
            </div>
            <div class="invoice-action-right">
                <button class="invoice-btn invoice-btn--outline" id="btnHistory" type="button">
                    <i class="fas fa-clock-rotate-left"></i>
                    Chi tiết lịch sử
                </button>
                <button class="invoice-btn invoice-btn--outline" id="btnPrintDetail" type="button">
                    <i class="fas fa-print"></i>
                    Xuất hóa đơn
                </button>
            </div>
        </section>

        <section class="invoice-detail-summary">
            <div>
                <div class="invoice-summary-title">
                    <h2>Thông tin đơn hàng</h2>
                    <span class="invoice-status" id="summaryStatus">Chờ xác nhận</span>
                    <span class="invoice-pill" id="summaryType">Online</span>
                </div>
                <p>
                    Mã: <strong id="summaryCode">HD26042916162</strong>
                    <span>•</span>
                    Tạo lúc: <strong id="summaryCreated">19:28 29/04/2026</strong>
                    <span>•</span>
                    NV xử lý: <strong id="summaryStaff">SYSTEM - System</strong>
                </p>
            </div>
            <div class="invoice-total-box">
                <span>Tổng thanh toán</span>
                <strong id="summaryTotal">1.375.000 đ</strong>
                <small>Đã thanh toán: <b id="summaryPaid">1.375.000 đ</b></small>
            </div>
        </section>

        <section class="invoice-info-grid">
            <article class="invoice-info-panel">
                <h3><i class="far fa-user"></i> Khách hàng</h3>
                <dl>
                    <dt>Họ tên</dt>
                    <dd id="customerName">Quyết</dd>
                    <dt>SĐT</dt>
                    <dd id="customerPhone">0868219136</dd>
                    <dt>Email</dt>
                    <dd id="customerEmail">quyet.eyewear@gmail.com</dd>
                    <dt>Địa chỉ</dt>
                    <dd id="customerAddress">Thanh Xuân, Hà Nội</dd>
                </dl>
            </article>

            <article class="invoice-info-panel">
                <h3><i class="fas fa-truck-fast"></i> Giao nhận</h3>
                <dl>
                    <dt>Người nhận</dt>
                    <dd id="receiverName">Quyết</dd>
                    <dt>SĐT nhận</dt>
                    <dd id="receiverPhone">0868219136</dd>
                    <dt>Địa chỉ nhận</dt>
                    <dd id="receiverAddress">Thanh Xuân, Hà Nội</dd>
                    <dt>Phí vận chuyển</dt>
                    <dd id="shippingFee">25.000 đ</dd>
                    <dt>Ghi chú</dt>
                    <dd id="orderNote">Khách đặt gọng titanium màu đen</dd>
                </dl>
            </article>

            <article class="invoice-info-panel">
                <h3><i class="fas fa-coins"></i> Giá trị đơn</h3>
                <dl>
                    <dt>Tổng tiền</dt>
                    <dd id="rawTotal">1.500.000 đ</dd>
                    <dt>Giảm giá</dt>
                    <dd id="discountTotal">150.000 đ</dd>
                    <dt>Phí vận chuyển</dt>
                    <dd id="valueShipping">25.000 đ</dd>
                    <dt>Phải trả</dt>
                    <dd class="invoice-money" id="mustPay">1.375.000 đ</dd>
                </dl>
            </article>
        </section>

        <section class="invoice-mini-grid">
            <div class="invoice-mini-card">
                <span>Trạng thái hiện tại</span>
                <strong id="currentStatusText">Chờ xác nhận</strong>
            </div>
            <div class="invoice-mini-card">
                <span>Số SP</span>
                <strong id="productCount">1</strong>
            </div>
            <div class="invoice-mini-card">
                <span>Đơn giá trị</span>
                <strong id="orderValue">1.500.000 đ</strong>
            </div>
            <div class="invoice-mini-card">
                <span>Còn lại</span>
                <strong id="remainingAmount">0 đ</strong>
            </div>
        </section>

        <section class="invoice-list-card invoice-detail-section">
            <div class="invoice-card-heading invoice-card-heading--compact">
                <div>
                    <h2>Lịch sử thanh toán</h2>
                    <p>Giao dịch ghi nhận cho hóa đơn này</p>
                </div>
            </div>
            <div class="invoice-table-wrap">
                <table class="invoice-table">
                    <thead>
                    <tr>
                        <th>Số tiền</th>
                        <th>Thời gian</th>
                        <th>Mã giao dịch</th>
                        <th>Phương thức</th>
                        <th>Ghi chú</th>
                    </tr>
                    </thead>
                    <tbody id="paymentRows"></tbody>
                </table>
            </div>
        </section>

        <section class="invoice-list-card invoice-detail-section">
            <div class="invoice-card-heading invoice-card-heading--compact">
                <div>
                    <h2>Sản phẩm</h2>
                    <p>Gọng kính, tròng kính và phụ kiện trong đơn</p>
                </div>
            </div>
            <div class="invoice-table-wrap">
                <table class="invoice-table invoice-product-table">
                    <thead>
                    <tr>
                        <th>STT</th>
                        <th>Ảnh</th>
                        <th>Sản phẩm</th>
                        <th>Số lượng</th>
                        <th>Đơn giá</th>
                        <th>Thành tiền</th>
                    </tr>
                    </thead>
                    <tbody id="productRows"></tbody>
                </table>
            </div>
        </section>
    </main>
</div>

<div class="invoice-modal" id="statusModal" aria-hidden="true">
    <div class="invoice-modal__backdrop" data-close-modal></div>
    <section class="invoice-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="statusModalTitle">
        <header class="invoice-modal__header">
            <h2 id="statusModalTitle"><i class="fas fa-triangle-exclamation"></i> Xác nhận đổi trạng thái</h2>
            <button class="invoice-modal__close" type="button" data-close-modal aria-label="Đóng">
                <i class="fas fa-xmark"></i>
            </button>
        </header>
        <div class="invoice-modal__body">
            <p>Hóa đơn: <strong id="modalOrderCode">HD26042916162</strong></p>
            <label class="invoice-field invoice-field--full">
                <span>Ghi chú</span>
                <textarea id="statusNote" rows="4" placeholder="Nhập ghi chú xử lý hóa đơn"></textarea>
            </label>
            <div class="status-change-line">
                <span>Trạng thái hiện tại:</span>
                <b class="invoice-status" id="modalCurrentStatus">Chờ xác nhận</b>
                <i class="fas fa-arrow-right"></i>
                <span>Trạng thái mới:</span>
                <b class="invoice-status" id="modalNextStatus">Đã xác nhận</b>
            </div>
            <small>Hành động này sẽ cập nhật trạng thái và ghi lịch sử hóa đơn.</small>
        </div>
        <footer class="invoice-modal__footer">
            <button class="invoice-btn invoice-btn--ghost" type="button" data-close-modal>Hủy</button>
            <button class="invoice-btn invoice-btn--primary" id="btnConfirmStatus" type="button">Xác nhận</button>
        </footer>
    </section>
</div>

<div class="invoice-modal" id="historyModal" aria-hidden="true">
    <div class="invoice-modal__backdrop" data-close-history></div>
    <section class="invoice-modal__dialog invoice-modal__dialog--wide" role="dialog" aria-modal="true" aria-labelledby="historyModalTitle">
        <header class="invoice-modal__header">
            <h2 id="historyModalTitle">Chi tiết lịch sử</h2>
            <button class="invoice-modal__close" type="button" data-close-history aria-label="Đóng">
                <i class="fas fa-xmark"></i>
            </button>
        </header>
        <div class="invoice-modal__body">
            <div class="invoice-table-wrap">
                <table class="invoice-table">
                    <thead>
                    <tr>
                        <th>Trạng thái</th>
                        <th>Thời gian</th>
                        <th>Mã NV</th>
                        <th>Tên NV</th>
                        <th>Hành động</th>
                        <th>Mô tả</th>
                    </tr>
                    </thead>
                    <tbody id="historyRows"></tbody>
                </table>
            </div>
        </div>
        <footer class="invoice-modal__footer">
            <button class="invoice-btn invoice-btn--primary" type="button" data-close-history>Đóng</button>
        </footer>
    </section>
</div>

<div class="invoice-toast" id="invoiceToast" role="status" aria-live="polite">
    <i class="fas fa-circle-check"></i>
    <div>
        <strong>Thành công</strong>
        <span id="toastMessage">Đã cập nhật trạng thái thành công</span>
    </div>
</div>

<script src="<%= request.getContextPath() %>/FE/Admin/QuanLyHoaDon/hoa_don.js"></script>
</body>
</html>
