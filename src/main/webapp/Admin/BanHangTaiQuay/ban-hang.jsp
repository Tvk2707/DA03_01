<%
    // Setup các thuộc tính cho layout chung
    request.setAttribute("pageTitle", "Bán hàng tại quầy (POS)");
    request.setAttribute("activeMenu", "pos");
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bán hàng tại quầy — RIOR</title>

    <!-- KẾ THỪA CSS DÙNG CHUNG CỦA HỆ THỐNG -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/danhmuc.css">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Font đặc thù của giao diện POS -->

    <style>
        /* ========================================================== */
        /* CSS FIX LỖI GIAO DIỆN BỊ SIDEBAR VÀ HEADER ĐÈ LÊN          */
        /* ========================================================== */
        .main-content-wrapper {
            /* Đã xóa margin-left vì class category-section bên ngoài đã tự động lùi lề rồi */
            padding-top: 0px; /* Đẩy nội dung xuống dưới Header */
            width: 100%;       /* Chiếm toàn bộ không gian còn lại để POS không bị bóp nghẹt */
            position: relative;
            z-index: 1; /* Giữ nội dung luôn nằm dưới Header khi cuộn trang */
        }

        /* ========================================================== */
        /* CSS ĐẶC THÙ RIÊNG CỦA TRANG POS (Không ảnh hưởng layout chung) */
        /* ========================================================== */
        :root {
            --bg: #F7F4EE;
            --panel: #FFFFFF;
            --brown-900: #4A3B27;
            --brown-700: #6B5738;
            --brown-600: #8B6B43;
            --brown-500: #9C7C4E;
            --gold-bg: #F3EBDA;
            --line: #E9E3D7;
            --text-main: #3A332A;
            --text-sub: #8B8478;
            --amber-bg: #FDF3D9; --amber-text: #B9891B;
            --blue-bg: #E7EEFC; --blue-text: #3B5FCE;
            --green-bg: #E4F5EA; --green-text: #2E9A5A;
            --red-bg: #FCE7E7; --red-text: #D65454;
            --radius: 14px;
        }

        .hidden { display: none; }

        .pos-wrapper {
            background: var(--bg);
            color: var(--text-main);
            font-family: 'Inter', sans-serif;
            min-height: calc(100vh - 75px); /* Bù trừ chiều cao khớp với padding-top ở trên */
            display: flex;
            flex-direction: column;
        }

        .pos-wrapper svg { display: block; }

        /* Page header */
        .page-head { padding: 26px 28px 0; }
        .page-head-row { display: flex; align-items: flex-start; justify-content: space-between; gap: 20px; flex-wrap: wrap;}
        .page-title { font-family: 'Playfair Display', serif; font-size: 28px; font-weight: 700; color: var(--brown-900); margin: 0;}
        .page-sub { color: var(--text-sub); font-size: 14px; margin-top: 4px;}
        .head-btns { display: flex; gap: 10px; }
        .pos-btn {
            border-radius: 10px; padding: 10px 18px; font-size: 13.5px; font-weight: 600;
            border: 1px solid transparent; cursor: pointer; display: flex; align-items: center; gap: 8px;
        }
        .pos-btn-outline { background: #fff; border-color: var(--brown-500); color: var(--brown-700); }
        .pos-btn-solid { background: var(--brown-600); color: #fff; }
        .pos-btn-solid:hover { background: var(--brown-700); }
        .pos-btn:disabled { opacity: .55; cursor: not-allowed; }
        .pos-order-meta {
            display: flex; align-items: center; gap: 10px; flex-wrap: wrap;
            margin: 14px 28px 0; padding: 12px 14px; background: var(--panel);
            border: 1px solid var(--line); border-radius: 10px; color: var(--text-sub); font-size: 12px;
        }
        .pos-order-meta__item { display: inline-flex; align-items: center; gap: 6px; }
        .pos-order-meta__item strong { color: var(--text-main); }
        .pos-order-meta__item--type strong { color: var(--brown-700); }
        .pos-order-meta__status {
            margin-left: auto; padding: 5px 9px; border-radius: 999px;
            background: var(--amber-bg); color: var(--amber-text); font-weight: 700;
        }

        .badge-limit {
            display: inline-flex; align-items: center; gap: 6px;
            background: var(--gold-bg); color: var(--brown-700); font-size: 12px; font-weight: 600;
            padding: 5px 10px; border-radius: 20px; margin-top: 10px;
        }

        /* Invoice tabs */
        .tabs { display: flex; gap: 8px; padding: 18px 28px 0; }
        .tab {
            padding: 9px 16px; border-radius: 10px 10px 0 0; font-size: 13px; font-weight: 600;
            background: #EFE9DC; color: var(--text-sub); cursor: pointer; display: flex; align-items: center; gap: 8px;
        }
        .tab.active { background: var(--panel); color: var(--brown-700); border: 1px solid var(--line); border-bottom: 1px solid var(--panel); }
        .tab .dot { width: 6px; height: 6px; border-radius: 50%; background: var(--amber-text); }
        .tab-add {
            width: 34px; height: 34px; border-radius: 10px; background: transparent; border: 1px dashed var(--brown-500);
            color: var(--brown-600); display: flex; align-items: center; justify-content: center; cursor: pointer; font-size: 16px;
        }

        /* Body layout */
        .pos-layout { display: flex; gap: 20px; padding: 0 28px 28px; flex: 1; min-height: 0; }
        .left-col { flex: 1; min-width: 0; background: var(--panel); border: 1px solid var(--line); border-radius: var(--radius); padding: 20px; }
        .right-col { width: 360px; flex-shrink: 0; background: var(--panel); border: 1px solid var(--line); border-radius: var(--radius); padding: 20px; display: flex; flex-direction: column; }

        .find-row { display: flex; gap: 10px; }
        .find-input {
            flex: 1; display: flex; align-items: center; gap: 10px;
            background: var(--bg); border: 1px solid var(--line); border-radius: 10px; padding: 11px 14px;
            color: var(--text-sub); font-size: 13.5px;
        }
        #search-product { width: 100%; border: none; background: transparent; outline: none; }
        .qr-btn {
            display: flex; align-items: center; gap: 8px; padding: 11px 16px; border-radius: 10px;
            background: var(--brown-900); color: #fff; font-size: 13px; font-weight: 600; cursor: pointer; white-space: nowrap;
        }

        .chip-row { display: flex; gap: 8px; margin: 16px 0 18px; flex-wrap: wrap; }
        .chip { padding: 7px 14px; border-radius: 20px; font-size: 12.5px; font-weight: 600; background: var(--bg); color: var(--text-sub); border: 1px solid var(--line); cursor: pointer;}
        .chip.active { background: var(--brown-600); color: #fff; border-color: var(--brown-600); }

        .product-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; }
        .p-card {
            border: 1px solid var(--line); border-radius: 12px; padding: 12px; cursor: pointer;
            transition: box-shadow .15s ease; background: var(--panel);
        }
        .p-card:hover { box-shadow: 0 4px 14px rgba(74,59,39,.08); border-color: var(--brown-500); }
        .p-thumb {
            height: 92px; border-radius: 9px; background: var(--gold-bg);
            display: flex; align-items: center; justify-content: center; margin-bottom: 10px; color: var(--brown-600);
        }
        .p-name { font-size: 13px; font-weight: 600; line-height: 1.3; margin-bottom: 4px; }
        .p-meta { font-size: 11px; color: var(--text-sub); margin-bottom: 8px;}
        .p-bottom { display: flex; align-items: center; justify-content: space-between; }
        .p-price { font-size: 13.5px; font-weight: 700; color: var(--brown-700); }
        .p-add {
            width: 26px; height: 26px; border-radius: 8px; background: var(--gold-bg); color: var(--brown-700);
            display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 15px; cursor: pointer;
        }
        .p-add[data-disabled="true"] { background: #eee; color: #aaa; cursor: not-allowed; }
        .stock-low { color: var(--red-text); }

        /* Right column - cart */
        .cust-box {
            display: flex; align-items: center; justify-content: space-between;
            background: var(--bg); border: 1px solid var(--line); border-radius: 10px; padding: 11px 13px; margin-bottom: 16px; position: relative;
        }
        .cust-info { display: flex; align-items: center; gap: 10px; }
        .cust-avatar { width: 32px; height: 32px; border-radius: 50%; background: var(--gold-bg); color: var(--brown-700); display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700;}
        .cust-name { font-size: 13px; font-weight: 600; }
        .cust-sub { font-size: 11px; color: var(--text-sub); }
        .cust-actions { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }
        .link-btn {
            padding: 0; border: 0; background: transparent; color: var(--brown-600);
            font: inherit; font-size: 12px; font-weight: 600; cursor: pointer;
        }
        .link-btn:hover { color: var(--brown-900); text-decoration: underline; }
        .link-btn--guest { color: var(--green-text); }
        .customer-current { color: var(--green-text); font-size: 12px; font-weight: 700; }

        #panel-khach-hang {
            position: absolute; top: 100%; right: 0; width: 280px; background: #fff;
            border: 1px solid var(--line); border-radius: 10px; padding: 12px;
            box-shadow: 0 6px 20px rgba(0,0,0,.1); z-index: 10;
        }
        .customer-panel-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px; color: var(--text-main); font-size: 12px; font-weight: 700; }
        .customer-panel-close { border: 0; background: transparent; color: var(--text-sub); font-size: 20px; line-height: 1; cursor: pointer; padding: 0 2px; }
        .customer-panel-close:hover { color: var(--red-text); }
        #panel-khach-hang input { width: 100%; border: 1px solid var(--line); background: var(--bg); padding: 8px 10px; border-radius: 8px; font-size: 13px; }
        #form-them-khach-nhanh { margin-top: 10px; padding-top: 10px; border-top: 1px dashed var(--line); display: flex; flex-direction: column; gap: 8px; }
        .btn-them-khach { background: var(--brown-600); color: #fff; border: none; border-radius: 8px; padding: 8px; font-size: 12px; font-weight: 600; cursor: pointer; }


        .cart-title { font-size: 12px; font-weight: 700; letter-spacing: .04em; text-transform: uppercase; color: var(--text-sub); margin-bottom: 10px;}
        .cart-list { flex: 1; overflow-y: auto; display: flex; flex-direction: column; gap: 12px; margin-bottom: 14px; padding-right: 2px;}
        .cart-empty { text-align: center; font-size: 13px; color: var(--text-sub); padding: 40px 0; }
        .cart-item { display: flex; gap: 10px; align-items: flex-start; }
        .ci-thumb { width: 44px; height: 44px; border-radius: 8px; background: var(--gold-bg); flex-shrink: 0; display: flex; align-items: center; justify-content: center; color: var(--brown-600); overflow: hidden;}
        .ci-thumb img { width: 100%; height: 100%; object-fit: cover; border-radius: 8px; display: block; }
        .ci-body { flex: 1; min-width: 0; }
        .ci-name { font-size: 12.5px; font-weight: 600; margin-bottom: 2px; }
        .ci-variant { font-size: 11px; color: var(--text-sub); margin-bottom: 6px;}
        .ci-row { display: flex; align-items: center; justify-content: space-between; }
        .qty-stepper { display: flex; align-items: center; gap: 8px; background: var(--bg); border: 1px solid var(--line); border-radius: 8px; padding: 3px 8px;}
        .qty-stepper span { font-size: 12px; font-weight: 600; width: 14px; text-align: center;}
        .qty-stepper button { border: none; background: none; color: var(--brown-700); font-weight: 700; cursor: pointer; font-size: 13px; width: 16px;}
        .ci-price { font-size: 12.5px; font-weight: 700; color: var(--brown-700);}
        .ci-actions { display: flex; align-items: center; gap: 8px; }
        .ci-remove { border: 0; background: transparent; color: var(--red-text); font-size: 11px; cursor: pointer; padding: 3px 5px; }
        .ci-remove:hover { background: var(--red-bg); border-radius: 5px; }

        .voucher-row { display: flex; gap: 8px; margin-bottom: 14px; }
        #input-voucher { flex: 1; border: 1px dashed var(--brown-500); border-radius: 9px; padding: 9px 12px; font-size: 12.5px; color: var(--text-sub); background: var(--bg); outline: none;}
        .voucher-apply { background: var(--gold-bg); color: var(--brown-700); border: none; border-radius: 9px; padding: 0 14px; font-size: 12px; font-weight: 700; cursor: pointer;}

        .totals { border-top: 1px dashed var(--line); padding-top: 12px; margin-bottom: 14px; }
        .t-row { display: flex; justify-content: space-between; font-size: 12.5px; color: var(--text-sub); margin-bottom: 7px;}
        .t-row.grand { color: var(--text-main); font-size: 16px; font-weight: 700; margin-top: 8px; padding-top: 10px; border-top: 1px solid var(--line);}
        .t-row.grand span:last-child { color: var(--brown-700); }
        .t-row .discount { color: var(--green-text); }

        .pay-methods { display: flex; gap: 8px; margin-bottom: 14px; }
        .pay-chip { flex: 1; text-align: center; padding: 10px 4px; border-radius: 9px; border: 1px solid var(--line); font-size: 11.5px; font-weight: 600; color: var(--text-sub); cursor: pointer;}
        .pay-chip.active { background: var(--brown-900); color: #fff; border-color: var(--brown-900);}
        .cash-payment-panel { margin-top: -5px; margin-bottom: 14px; padding: 11px; border: 1px solid var(--line); border-radius: 9px; background: #fffaf4; }
        .cash-payment-panel[hidden] { display: none; }
        .cash-payment-panel label { display: block; margin-bottom: 6px; font-size: 11px; color: var(--text-sub); }
        .cash-payment-panel input { width: 100%; box-sizing: border-box; padding: 9px 10px; border: 1px solid var(--line); border-radius: 7px; font-size: 13px; }
        .cash-change-row { display: flex; justify-content: space-between; margin-top: 9px; font-size: 12px; }
        .cash-change-row strong { color: var(--brown-900); }
        .cash-payment-error { margin: 7px 0 0; color: #b42318; font-size: 11px; }

        .checkout-btn {
            background: var(--brown-600); color: #fff; border: none; border-radius: 11px; padding: 14px; width: 100%;
            font-size: 14.5px; font-weight: 700; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px;
        }
        .checkout-btn:hover { background: var(--brown-700); }
        /* Nút X đóng tab */
        .btn-close-tab {
            margin-left: 5px;
            background: transparent;
            border: none;
            font-size: 16px;
            line-height: 1;
            color: var(--text-sub);
            cursor: pointer;
            border-radius: 4px;
            padding: 2px 6px;
            transition: all 0.2s ease;
        }
        .btn-close-tab:hover {
            color: var(--red-text);
            background-color: var(--red-bg);
        }

        .transfer-modal {
            position: fixed;
            inset: 0;
            z-index: 1000;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            background: rgba(25, 20, 16, .5);
        }
        .transfer-modal.hidden { display: none; }
        .transfer-modal__dialog {
            width: min(430px, 100%);
            background: #fff;
            border-radius: 16px;
            padding: 22px;
            box-shadow: 0 20px 60px rgba(25, 20, 16, .25);
        }
        .transfer-modal__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
        .transfer-modal__header h2 { margin: 0; font-size: 18px; color: var(--text-main); }
        .transfer-modal__close { border: 0; background: transparent; font-size: 24px; cursor: pointer; color: var(--text-sub); }
        .transfer-modal__qr { text-align: center; padding: 8px 0 14px; }
        .transfer-modal__qr img { width: 230px; height: 230px; object-fit: contain; border: 1px solid var(--line); border-radius: 10px; }
        .transfer-modal__hint { margin: 0 0 14px; color: var(--text-sub); font-size: 12px; line-height: 1.5; }
        .transfer-modal__field { display: block; margin-bottom: 14px; color: var(--text-main); font-size: 12px; font-weight: 600; }
        .transfer-modal__field input { width: 100%; box-sizing: border-box; margin-top: 6px; border: 1px solid var(--line); border-radius: 8px; padding: 10px; font-size: 13px; }
        .transfer-modal__actions { display: flex; gap: 8px; justify-content: flex-end; }
        .transfer-modal__actions button { border: 0; border-radius: 8px; padding: 10px 14px; cursor: pointer; font-weight: 600; }
        .transfer-modal__cancel { background: #f3f0ed; color: var(--text-main); }
        .transfer-modal__confirm { background: var(--brown-600); color: #fff; }

        .product-qr-modal {
            position: fixed;
            inset: 0;
            z-index: 1100;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            background: rgba(25, 20, 16, .5);
        }
        .product-qr-modal.hidden { display: none; }
        .product-qr-modal__dialog {
            width: min(470px, 100%);
            background: #fff;
            border-radius: 16px;
            padding: 22px;
            box-shadow: 0 20px 60px rgba(25, 20, 16, .25);
        }
        .product-qr-modal__header { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
        .product-qr-modal__header h2 { margin: 0; font-size: 18px; color: var(--text-main); }
        .product-qr-modal__close { border: 0; background: transparent; font-size: 24px; cursor: pointer; color: var(--text-sub); }
        .product-qr-modal__hint { margin: 8px 0 14px; color: var(--text-sub); font-size: 12px; line-height: 1.5; }
        #product-qr-reader { min-height: 220px; overflow: hidden; border: 1px solid var(--line); border-radius: 12px; background: #111; }
        #product-qr-status { min-height: 18px; margin: 10px 0 0; color: var(--text-sub); font-size: 12px; }
        .product-qr-manual { display: flex; gap: 8px; margin-top: 12px; }
        .product-qr-manual input { flex: 1; min-width: 0; border: 1px solid var(--line); border-radius: 8px; padding: 9px 10px; font-size: 13px; }
        .product-qr-manual button, .product-qr-modal__actions button { border: 0; border-radius: 8px; padding: 9px 12px; cursor: pointer; font-weight: 600; }
        .product-qr-manual button { background: var(--brown-600); color: #fff; }
        .product-qr-modal__actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 14px; }
        .product-qr-modal__actions button { background: #f3f0ed; color: var(--text-main); }
        .product-qr-modal__actions #restart-product-qr { background: var(--gold-bg); color: var(--brown-700); }
    </style>
</head>
<body>

<!-- NHÚNG SIDEBAR CHUNG -->
<%@include file="/Admin/layout/sidebar.jsp" %>

<div class="dashboard-container">

    <!-- NHÚNG HEADER CHUNG -->
    <%@include file="/Admin/layout/header.jsp" %>
    <div class="category-section">

        <!-- ĐÃ BỔ SUNG CLASS NÀY ĐỂ FIX LỖI ĐÈ GIAO DIỆN -->
        <div class="main-content-wrapper">

            <!-- GIAO DIỆN BÁN HÀNG TẠI QUẦY (Được bọc trong pos-wrapper) -->
            <div class="pos-wrapper">
                <div class="page-head">
                    <div class="page-head-row">
                        <div>
                            <h1 class="page-title">Bán hàng tại quầy</h1>
                            <div class="page-sub">Tạo hóa đơn, thêm sản phẩm và thanh toán trực tiếp cho khách</div>
                            <div class="badge-limit">
                                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 3"/></svg>
                                ${danhSachHoaDonCho.size()}/10 hóa đơn chờ
                            </div>
                        </div>
                        <div class="head-btns">
                            <button type="button" class="pos-btn pos-btn-outline" id="open-product-qr">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h4v4H4zM16 4h4v4h-4zM4 16h4v4H4z"/><path d="M14 14h2v2h-2zM18 14h2v6h-4v-2M14 18h2v2h-2z"/></svg>
                                Quét QR
                            </button>
                            <c:if test="${not empty idHoaDonDangTao}">
                                <a class="pos-btn pos-btn-outline" target="_blank" rel="noopener"
                                   href="${pageContext.request.contextPath}/admin/hoa-don/chi-tiet?id=${idHoaDonDangTao}"
                                   title="Xem chi tiết hóa đơn">
                                    <i class="fas fa-eye" aria-hidden="true"></i>
                                    Chi tiết
                                </a>
                                <a class="pos-btn pos-btn-outline" target="_blank" rel="noopener"
                                   href="${pageContext.request.contextPath}/admin/hoa-don/chi-tiet?id=${idHoaDonDangTao}&amp;print=1"
                                   title="In hóa đơn">
                                    <i class="fas fa-print" aria-hidden="true"></i>
                                    In hóa đơn
                                </a>
                            </c:if>
                            <button type="button" class="pos-btn pos-btn-solid" id="create-invoice-button">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
                                Tạo hóa đơn
                            </button>
                        </div>
                    </div>
                </div>

                <div class="tabs">
                    <c:forEach var="hd" items="${danhSachHoaDonCho}">
                        <div class="tab ${hd.id == idHoaDonDangTao ? 'active' : ''}" data-hoadon="${hd.id}">
                            <span class="dot"></span>
                            Đơn #${hd.id} · ${hd.maHoaDon}

                            <!-- Nút X để xóa hóa đơn -->
                            <button class="btn-close-tab" onclick="xoaHoaDonCho(event, ${hd.id})" title="Hủy đơn này">×</button>
                        </div>
                    </c:forEach>
                    <div class="tab-add">+</div>
                </div>

                <c:if test="${not empty hoaDonDangTao}">
                    <div class="pos-order-meta" aria-label="Thông tin đơn đang xử lý">
                        <span class="pos-order-meta__item">
                            <span>Hóa đơn:</span>
                            <strong>${hoaDonDangTao.maHoaDon}</strong>
                        </span>
                        <span class="pos-order-meta__item pos-order-meta__item--type">
                            <span>Loại đơn:</span>
                            <strong>Tại quầy</strong>
                        </span>
                        <span class="pos-order-meta__status">Chờ thanh toán</span>
                    </div>
                </c:if>

                <div class="pos-layout">
                    <!-- LEFT: product search / grid -->
                    <div class="left-col">
                        <div class="find-row">
                            <div class="find-input">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
                                <input type="text" id="search-product" placeholder="Tìm sản phẩm theo tên, mã SKU...">
                            </div>
                        </div>

                        <div class="chip-row">
                            <div class="chip active" data-danhmuc="">Tất cả</div>
                            <c:forEach var="dm" items="${danhSachDanhMuc}">
                                <div class="chip" data-danhmuc="${dm.id}">${dm.tenDanhMuc}</div>
                            </c:forEach>
                        </div>

                        <div class="product-grid">
                            <c:forEach var="sp" items="${danhSachSanPham}">
                                <div class="p-card" data-spct="${sp.id}">
                                    <div class="p-thumb">
                                        <c:choose>
                                            <c:when test="${not empty sp.hinhAnh && sp.hinhAnh != 'null'}">
                                                <img src="${pageContext.request.contextPath}/FE/Admin/hinh_anh_san_pham/${sp.hinhAnh}" style="width: 100%; height: 100%; object-fit: cover; border-radius: 9px;" onerror="this.style.display='none';"/>
                                            </c:when>
                                            <c:otherwise>
                                                <svg width="34" height="34" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><circle cx="6.5" cy="12" r="3.2"/><circle cx="17.5" cy="12" r="3.2"/><path d="M9.7 12h4.6M3 12l-1.5-1M21 12l1.5-1"/></svg>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="p-name">${sp.sanPham.tenSanPham} (${sp.ma})</div>
                                    <div class="p-meta ${sp.soLuongTon <= 3 ? 'stock-low' : ''}" data-tonkho>
                                        Còn ${sp.soLuongTon} · ${sp.mauSac.tenMau} - ${sp.kichCo.tenKichCo}
                                    </div>
                                    <div class="p-bottom">
                                        <div class="p-price"><fmt:formatNumber value="${sp.giaBan}" type="currency" currencySymbol="đ"/></div>
                                        <div class="p-add" ${empty idHoaDonDangTao || hoaDonDangTao.trangThai == 3 || hoaDonDangTao.trangThai == 5 || sp.soLuongTon <= 0 ? 'data-disabled="true"' : ''}
                                             title="${empty idHoaDonDangTao ? 'Tạo hóa đơn trước' : 'Thêm vào giỏ'}">+</div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                    <!-- RIGHT: cart -->
                    <div class="right-col">
                        <div class="cust-box">
                            <div class="cust-info">
                                <c:set var="kh" value="${hoaDonDangTao.khachHang}"/>
                                <div class="cust-avatar">
                                    <c:choose>
                                        <c:when test="${not empty kh}">${kh.hoTen.substring(0,1)}${kh.hoTen.contains(' ') ? kh.hoTen.substring(kh.hoTen.lastIndexOf(' ') + 1, kh.hoTen.lastIndexOf(' ') + 2) : ''}</c:when>
                                        <c:otherwise>KL</c:otherwise>
                                    </c:choose>
                                </div>
                                <div>
                                    <div class="cust-name">${not empty kh ? kh.hoTen : 'Khách lẻ'}</div>
                                    <div class="cust-sub">${not empty kh ? kh.soDienThoai : 'Chưa gắn số điện thoại'}</div>
                                </div>
                            </div>
                            <div class="cust-actions">
                                <button type="button" class="link-btn" data-customer-open aria-expanded="false">Đổi</button>
                                <c:choose>
                                    <c:when test="${not empty kh}">
                                        <button type="button" class="link-btn link-btn--guest" data-customer-guest>Khách lẻ</button>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="customer-current">Khách lẻ</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div id="panel-khach-hang" class="hidden">
                                <div class="customer-panel-head">
                                    <span>Chọn khách hàng</span>
                                    <button type="button" class="customer-panel-close" data-customer-close aria-label="Đóng">&times;</button>
                                </div>
                                <input type="text" id="input-sdt" placeholder="Nhập số điện thoại khách hàng" />
                                <div id="form-them-khach-nhanh" class="hidden">
                                    <input type="text" id="input-sdt-moi" placeholder="Số điện thoại" />
                                    <input type="text" id="input-ten-moi" placeholder="Họ tên" />
                                    <button type="button" class="btn-them-khach">Thêm &amp; chọn</button>
                                </div>
                            </div>
                        </div>

                        <div class="cart-title">Giỏ hàng · <span id="cart-count">${empty hoaDonDangTao.chiTietHoaDons ? 0 : hoaDonDangTao.chiTietHoaDons.size()}</span> sản phẩm</div>
                        <div class="cart-list">
                            <c:if test="${empty hoaDonDangTao.chiTietHoaDons}">
                                <div class="cart-empty">Chưa có sản phẩm nào trong giỏ</div>
                            </c:if>
                            <c:forEach var="ct" items="${hoaDonDangTao.chiTietHoaDons}">
                                <div class="cart-item" data-id="${ct.id}">
                                    <div class="ci-thumb">
                                        <c:choose>
                                            <c:when test="${not empty ct.sanPhamChiTiet.hinhAnh && ct.sanPhamChiTiet.hinhAnh != 'null'}">
                                                <img src="${pageContext.request.contextPath}/FE/Admin/hinh_anh_san_pham/${ct.sanPhamChiTiet.hinhAnh}"
                                                     alt="${ct.sanPhamChiTiet.sanPham.tenSanPham}"
                                                     onerror="this.style.display='none'; this.nextElementSibling.style.display='block';"/>
                                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="display:none"><circle cx="6.5" cy="12" r="3.2"/><circle cx="17.5" cy="12" r="3.2"/><path d="M9.7 12h4.6"/></svg>
                                            </c:when>
                                            <c:otherwise>
                                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><circle cx="6.5" cy="12" r="3.2"/><circle cx="17.5" cy="12" r="3.2"/><path d="M9.7 12h4.6"/></svg>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="ci-body">
                                        <div class="ci-name">${ct.sanPhamChiTiet.sanPham.tenSanPham}</div>
                                        <div class="ci-variant">${ct.sanPhamChiTiet.ma}</div>
                                        <div class="ci-row">
                                            <div class="qty-stepper">
                                                <button class="qty-minus" data-id="${ct.id}" data-qty="${ct.soLuong}">–</button>
                                                <span>${ct.soLuong}</span>
                                                <button class="qty-plus" data-id="${ct.id}" data-qty="${ct.soLuong}" data-spct="${ct.sanPhamChiTiet.id}">+</button>
                                            </div>
                                            <div class="ci-actions">
                                                <div class="ci-price"><fmt:formatNumber value="${ct.tongTien}" type="currency" currencySymbol="đ"/></div>
                                                <button type="button" class="ci-remove" data-id="${ct.id}"
                                                        title="Xóa sản phẩm" aria-label="Xóa sản phẩm">Xóa</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <div class="voucher-row">
                            <input type="text" id="input-voucher" placeholder="Nhập mã voucher..." autocomplete="off">
                            <button type="button" class="voucher-apply">Áp dụng</button>
                        </div>

                        <c:set var="tamTinhHoaDon" value="${0}"/>
                        <c:forEach var="ct" items="${hoaDonDangTao.chiTietHoaDons}">
                            <c:set var="tamTinhHoaDon" value="${tamTinhHoaDon + (ct.donGia * ct.soLuong)}"/>
                        </c:forEach>
                        <c:set var="tongThanhToanHienTai" value="${empty hoaDonDangTao.tongTienThanhToan ? 0 : hoaDonDangTao.tongTienThanhToan}"/>
                        <c:set var="giamGiaHienTai" value="${tamTinhHoaDon > tongThanhToanHienTai ? tamTinhHoaDon - tongThanhToanHienTai : 0}"/>
                        <div class="totals">
                            <div class="t-row"><span>Tạm tính</span><span id="sum-tamtinh"><fmt:formatNumber value="${tamTinhHoaDon}" type="currency" currencySymbol="đ"/></span></div>
                            <div class="t-row"><span>Giảm giá</span><span class="discount" id="sum-giamgia"><fmt:formatNumber value="${giamGiaHienTai}" type="currency" currencySymbol="đ"/></span></div>
                            <div class="t-row grand"><span>Tổng cộng</span><span id="sum-tongcong"><fmt:formatNumber value="${tongThanhToanHienTai}" type="currency" currencySymbol="đ"/></span></div>
                        </div>

                        <div class="pay-methods">
                            <div class="pay-chip active" data-ma="PTTT001">Tiền mặt</div>
                            <div class="pay-chip" data-ma="PTTT004">Thẻ ngân hàng</div>
                            <div class="pay-chip" data-ma="PTTT002">Chuyển khoản</div>
                        </div>

                        <div class="cash-payment-panel" id="cash-payment-panel">
                            <label for="cash-amount">Khách đưa (VNĐ)</label>
                            <input id="cash-amount" type="text" inputmode="numeric"
                                   autocomplete="off" placeholder="Nhập số tiền khách đưa">
                            <div class="cash-change-row">
                                <span>Tiền thối lại</span>
                                <strong id="cash-change">0 đ</strong>
                            </div>
                            <p id="cash-payment-error" class="cash-payment-error" hidden></p>
                        </div>

                        <button type="button" class="checkout-btn" ${empty idHoaDonDangTao ? 'disabled' : ''}>
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M20 7L9 18l-5-5"/></svg>
                            Thanh toán · <span id="checkout-total"><fmt:formatNumber value="${tongThanhToanHienTai}" type="currency" currencySymbol="đ"/></span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div><!-- Kết thúc main-content-wrapper -->
</div>

<div id="transfer-payment-modal" class="transfer-modal hidden" aria-hidden="true">
    <section class="transfer-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="transfer-payment-title">
        <div class="transfer-modal__header">
            <h2 id="transfer-payment-title">Thanh toán chuyển khoản / QR</h2>
            <button type="button" class="transfer-modal__close" id="close-transfer-payment" aria-label="Đóng">&times;</button>
        </div>
        <p class="transfer-modal__hint">
            Khách quét mã QR, hoàn tất chuyển khoản rồi nhập mã giao dịch ngân hàng để xác nhận.
        </p>
        <div class="transfer-modal__qr" id="transfer-payment-qr-wrap">
            <img id="transfer-payment-qr" alt="Mã QR thanh toán hóa đơn">
        </div>
        <label class="transfer-modal__field" for="transfer-transaction-code">
            Mã giao dịch ngân hàng <span aria-hidden="true">*</span>
            <input id="transfer-transaction-code" type="text" maxlength="100" autocomplete="off" placeholder="Ví dụ: FT260722123456">
        </label>
        <label class="transfer-modal__field" for="transfer-payment-note">
            Ghi chú
            <input id="transfer-payment-note" type="text" maxlength="255" value="Thanh toán bằng QR/chuyển khoản">
        </label>
        <div class="transfer-modal__actions">
            <button type="button" class="transfer-modal__cancel" id="cancel-transfer-payment">Để sau</button>
            <button type="button" class="transfer-modal__confirm" id="confirm-transfer-payment">Đã nhận tiền</button>
        </div>
    </section>
</div>

<div id="product-qr-modal" class="product-qr-modal hidden" aria-hidden="true">
    <section class="product-qr-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="product-qr-title">
        <div class="product-qr-modal__header">
            <h2 id="product-qr-title">Quét QR sản phẩm</h2>
            <button type="button" class="product-qr-modal__close" id="close-product-qr" aria-label="Đóng">&times;</button>
        </div>
        <p class="product-qr-modal__hint">Đưa mã QR của biến thể sản phẩm vào khung quét. Sản phẩm tìm thấy sẽ được thêm vào hóa đơn đang chọn.</p>
        <div id="product-qr-reader"></div>
        <p id="product-qr-status" role="status">Đang chờ camera hoặc mã QR.</p>
        <div class="product-qr-manual">
            <input id="product-qr-code" type="text" autocomplete="off" placeholder="Nhập mã QR thủ công nếu không dùng camera">
            <button type="button" id="submit-product-qr">Thêm sản phẩm</button>
        </div>
        <div class="product-qr-modal__actions">
            <button type="button" id="restart-product-qr">Quét lại</button>
            <button type="button" id="cancel-product-qr">Đóng</button>
        </div>
    </section>
</div>

<%-- Form ẩn để tạo hóa đơn mới --%>
<form id="form-tao-hoa-don" action="ban-hang" method="post" class="hidden">
    <input type="hidden" name="action" value="taoHoaDon" />
</form>

<script>
    window.idHoaDonHienTai = ${empty idHoaDonDangTao ? 'null' : idHoaDonDangTao};
</script>
<script src="https://unpkg.com/html5-qrcode"></script>
<script src="${pageContext.request.contextPath}/assets/js/banhang.js"></script>

</body>
</html>
