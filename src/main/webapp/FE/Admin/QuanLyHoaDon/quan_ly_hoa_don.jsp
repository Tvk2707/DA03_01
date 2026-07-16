<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    request.setAttribute("pageTitle", "Quản lý hóa đơn");
    request.setAttribute("activeMenu", "hoadon");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý hóa đơn - RIOR Admin</title>

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

    <main id="page-content" class="invoice-page">
        <section class="invoice-page-header">
            <div>
                <h1 class="invoice-title">Quản lý hóa đơn</h1>
                <p class="invoice-subtitle">Theo dõi đơn kính mắt online, tại quầy và giao hàng</p>
            </div>
            <div class="invoice-header-actions">
                <button class="invoice-btn invoice-btn--outline" id="btnResetFilters" type="button">
                    <i class="fas fa-rotate-right"></i>
                    Đặt lại
                </button>
                <button class="invoice-btn invoice-btn--primary" id="btnExportOrders" type="button">
                    <i class="fas fa-file-excel"></i>
                    Xuất Excel trang HĐ
                </button>
            </div>
        </section>

        <section class="invoice-filter-card">
            <button class="invoice-section-toggle" type="button" id="filterToggle">
                <span>
                    <i class="fas fa-filter"></i>
                    Bộ lọc tìm kiếm
                </span>
                <span class="toggle-note">Nhấn để thu gọn/mở rộng</span>
            </button>
            <div class="invoice-filter-body" id="filterBody">
                <label class="invoice-field">
                    <span>Tìm kiếm</span>
                    <input id="orderSearch" type="search" placeholder="Nhập mã hóa đơn / tên khách / SĐT...">
                </label>
                <label class="invoice-field">
                    <span>Loại hóa đơn</span>
                    <select id="orderTypeFilter">
                        <option value="all">Tất cả</option>
                        <option value="Tại quầy">Tại quầy</option>
                        <option value="Online">Online</option>
                        <option value="Tại quầy - giao hàng">Tại quầy - giao hàng</option>
                    </select>
                </label>
                <label class="invoice-field">
                    <span>Từ ngày</span>
                    <input id="fromDateFilter" type="date" value="2026-04-29">
                </label>
                <label class="invoice-field">
                    <span>Đến ngày</span>
                    <input id="toDateFilter" type="date" value="2026-04-29">
                </label>
            </div>
        </section>

        <section class="invoice-stats">
            <div class="invoice-stat">
                <span class="invoice-stat__label">Tổng hóa đơn</span>
                <strong>6</strong>
                <small>Ngày 29/04/2026</small>
            </div>
            <div class="invoice-stat">
                <span class="invoice-stat__label">Doanh thu</span>
                <strong>19.850.000 đ</strong>
                <small>Đơn hoàn tất</small>
            </div>
            <div class="invoice-stat">
                <span class="invoice-stat__label">Đang xử lý</span>
                <strong>2</strong>
                <small>Chờ nhân viên xác nhận</small>
            </div>
            <div class="invoice-stat">
                <span class="invoice-stat__label">Sản phẩm kính</span>
                <strong>11</strong>
                <small>Gọng, tròng, kính râm</small>
            </div>
        </section>

        <section class="invoice-list-card">
            <div class="invoice-card-heading">
                <div class="invoice-heading-icon">
                    <i class="fas fa-file-invoice-dollar"></i>
                </div>
                <div>
                    <h2>Danh sách hóa đơn</h2>
                    <p>Lọc nhanh theo trạng thái xử lý đơn</p>
                </div>
            </div>

            <div class="invoice-status-tabs" id="statusTabs" role="tablist" aria-label="Lọc trạng thái hóa đơn">
                <button class="invoice-tab is-active" type="button" data-status="all">Tất cả</button>
                <button class="invoice-tab" type="button" data-status="Chờ xác nhận">Chờ xác nhận</button>
                <button class="invoice-tab" type="button" data-status="Đã xác nhận">Đã xác nhận</button>
                <button class="invoice-tab" type="button" data-status="Đang xử lý">Đang xử lý</button>
                <button class="invoice-tab" type="button" data-status="Đang giao">Đang giao</button>
                <button class="invoice-tab" type="button" data-status="Đã giao">Đã giao</button>
                <button class="invoice-tab" type="button" data-status="Hoàn thành">Hoàn thành</button>
                <button class="invoice-tab" type="button" data-status="Đã hủy">Đã hủy</button>
            </div>

            <div class="invoice-table-wrap">
                <table class="invoice-table" id="ordersTable">
                    <thead>
                    <tr>
                        <th>STT</th>
                        <th>Mã hóa đơn</th>
                        <th>Tên nhân viên</th>
                        <th>Khách hàng</th>
                        <th>Số điện thoại</th>
                        <th>Loại hóa đơn</th>
                        <th>Tổng tiền</th>
                        <th>Ngày tạo</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr data-id="HD26042981918" data-search="HD26042981918 admin tbt nv001 duy quyet 0868219136 titanium flex amber" data-type="Tại quầy" data-status="Hoàn thành" data-date="2026-04-29">
                        <td>1</td>
                        <td><strong>HD26042981918</strong></td>
                        <td>Admin TBT <small>NV001</small></td>
                        <td>Duy Quyết</td>
                        <td>0868219136</td>
                        <td><span class="invoice-pill">Tại quầy</span></td>
                        <td class="invoice-money">6.000.000 đ</td>
                        <td>29/04/2026</td>
                        <td><span class="invoice-status invoice-status--done">Hoàn thành</span></td>
                        <td>
                            <div class="invoice-actions">
                                <a class="invoice-icon-btn" href="chi_tiet_hoa_don.jsp?id=HD26042981918" title="Xem chi tiết"><i class="fas fa-eye"></i></a>
                                <button class="invoice-icon-btn" type="button" data-print="HD26042981918" title="In hóa đơn"><i class="fas fa-print"></i></button>
                            </div>
                        </td>
                    </tr>
                    <tr data-id="HD26042925243" data-search="HD26042925243 admin tbt nv001 duy quyet 0868219136 gong acetate cafe" data-type="Tại quầy" data-status="Hoàn thành" data-date="2026-04-29">
                        <td>2</td>
                        <td><strong>HD26042925243</strong></td>
                        <td>Admin TBT <small>NV001</small></td>
                        <td>Duy Quyết</td>
                        <td>0868219136</td>
                        <td><span class="invoice-pill">Tại quầy</span></td>
                        <td class="invoice-money">2.800.000 đ</td>
                        <td>29/04/2026</td>
                        <td><span class="invoice-status invoice-status--done">Hoàn thành</span></td>
                        <td>
                            <div class="invoice-actions">
                                <a class="invoice-icon-btn" href="chi_tiet_hoa_don.jsp?id=HD26042925243" title="Xem chi tiết"><i class="fas fa-eye"></i></a>
                                <button class="invoice-icon-btn" type="button" data-print="HD26042925243" title="In hóa đơn"><i class="fas fa-print"></i></button>
                            </div>
                        </td>
                    </tr>
                    <tr data-id="HD26042908574" data-search="HD26042908574 admin tbt nv001 duy quyet 0868219136 kinh ram polarized" data-type="Tại quầy - giao hàng" data-status="Đã giao" data-date="2026-04-29">
                        <td>3</td>
                        <td><strong>HD26042908574</strong></td>
                        <td>Admin TBT <small>NV001</small></td>
                        <td>Duy Quyết</td>
                        <td>0868219136</td>
                        <td><span class="invoice-pill">Tại quầy - giao hàng</span></td>
                        <td class="invoice-money">2.800.000 đ</td>
                        <td>29/04/2026</td>
                        <td><span class="invoice-status invoice-status--shipped">Đã giao</span></td>
                        <td>
                            <div class="invoice-actions">
                                <a class="invoice-icon-btn" href="chi_tiet_hoa_don.jsp?id=HD26042908574" title="Xem chi tiết"><i class="fas fa-eye"></i></a>
                                <button class="invoice-icon-btn" type="button" data-print="HD26042908574" title="In hóa đơn"><i class="fas fa-print"></i></button>
                            </div>
                        </td>
                    </tr>
                    <tr data-id="HD26042972295" data-search="HD26042972295 admin tbt nv001 khach le kinh can gong oval" data-type="Tại quầy" data-status="Hoàn thành" data-date="2026-04-29">
                        <td>4</td>
                        <td><strong>HD26042972295</strong></td>
                        <td>Admin TBT <small>NV001</small></td>
                        <td>Khách lẻ</td>
                        <td>-</td>
                        <td><span class="invoice-pill">Tại quầy</span></td>
                        <td class="invoice-money">4.700.000 đ</td>
                        <td>29/04/2026</td>
                        <td><span class="invoice-status invoice-status--done">Hoàn thành</span></td>
                        <td>
                            <div class="invoice-actions">
                                <a class="invoice-icon-btn" href="chi_tiet_hoa_don.jsp?id=HD26042972295" title="Xem chi tiết"><i class="fas fa-eye"></i></a>
                                <button class="invoice-icon-btn" type="button" data-print="HD26042972295" title="In hóa đơn"><i class="fas fa-print"></i></button>
                            </div>
                        </td>
                    </tr>
                    <tr data-id="HD26042977687" data-search="HD26042977687 system quyet 0868219136 trong kinh chong anh sang xanh" data-type="Online" data-status="Đang xử lý" data-date="2026-04-29">
                        <td>5</td>
                        <td><strong>HD26042977687</strong></td>
                        <td>System <small>SYSTEM</small></td>
                        <td>Quyết</td>
                        <td>0868219136</td>
                        <td><span class="invoice-pill">Online</span></td>
                        <td class="invoice-money">1.375.000 đ</td>
                        <td>29/04/2026</td>
                        <td><span class="invoice-status invoice-status--processing">Đang xử lý</span></td>
                        <td>
                            <div class="invoice-actions">
                                <a class="invoice-icon-btn" href="chi_tiet_hoa_don.jsp?id=HD26042977687" title="Xem chi tiết"><i class="fas fa-eye"></i></a>
                                <button class="invoice-icon-btn" type="button" data-print="HD26042977687" title="In hóa đơn"><i class="fas fa-print"></i></button>
                            </div>
                        </td>
                    </tr>
                    <tr data-id="HD26042916162" data-search="HD26042916162 system quyet 0868219136 gong titanium tron den" data-type="Online" data-status="Chờ xác nhận" data-date="2026-04-29">
                        <td>6</td>
                        <td><strong>HD26042916162</strong></td>
                        <td>System <small>SYSTEM</small></td>
                        <td>Quyết</td>
                        <td>0868219136</td>
                        <td><span class="invoice-pill">Online</span></td>
                        <td class="invoice-money">1.375.000 đ</td>
                        <td>29/04/2026</td>
                        <td><span class="invoice-status invoice-status--waiting">Chờ xác nhận</span></td>
                        <td>
                            <div class="invoice-actions">
                                <a class="invoice-icon-btn" href="chi_tiet_hoa_don.jsp?id=HD26042916162" title="Xem chi tiết"><i class="fas fa-eye"></i></a>
                                <button class="invoice-icon-btn" type="button" data-print="HD26042916162" title="In hóa đơn"><i class="fas fa-print"></i></button>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div class="invoice-empty" id="emptyState">
                    <i class="fas fa-receipt"></i>
                    Không có dữ liệu phù hợp
                </div>
            </div>

            <div class="invoice-table-footer">
                <span id="orderCount">Hiển thị 6 / tổng 6 bản ghi</span>
                <div class="invoice-pagination">
                    <button type="button" disabled><i class="fas fa-chevron-left"></i></button>
                    <span>Trang <strong>1</strong></span>
                    <button type="button" disabled><i class="fas fa-chevron-right"></i></button>
                </div>
                <select aria-label="Số bản ghi mỗi trang">
                    <option>10 bản ghi / trang</option>
                    <option>20 bản ghi / trang</option>
                    <option>50 bản ghi / trang</option>
                </select>
            </div>
        </section>
    </main>
</div>

<div class="invoice-toast" id="invoiceToast" role="status" aria-live="polite">
    <i class="fas fa-circle-check"></i>
    <div>
        <strong>Thành công</strong>
        <span id="toastMessage">Đã cập nhật thao tác</span>
    </div>
</div>

<script src="<%= request.getContextPath() %>/FE/Admin/QuanLyHoaDon/hoa_don.js"></script>
</body>
</html>
