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
        .page-head-row { display: flex; align-items: flex-start; justify-content: space-between; gap: 20px; }
        .page-title { font-family: 'Playfair Display', serif; font-size: 28px; font-weight: 700; color: var(--brown-900); margin: 0;}
        .page-sub { color: var(--text-sub); font-size: 14px; margin-top: 4px;}
        .seller-meta {
            display: none;
            margin-top: 10px; color: var(--text-sub); font-size: 12.5px;
        }
        .seller-meta__item { display: inline-flex; align-items: center; gap: 6px; }
        .seller-meta__item i { color: var(--brown-600); width: 14px; text-align: center; }
        .seller-meta__item strong { color: var(--text-main); font-weight: 650; }
        .head-btns { display: flex; gap: 10px; margin-left: auto; flex-shrink: 0; }
        .pos-btn {
            border-radius: 10px; padding: 10px 18px; font-size: 13.5px; font-weight: 600;
            border: 1px solid transparent; cursor: pointer; display: flex; align-items: center; gap: 8px;
        }
        .pos-btn-outline { background: #fff; border-color: var(--brown-500); color: var(--brown-700); }
        .pos-btn-solid { background: var(--brown-600); color: #fff; }
        .pos-btn-solid:hover { background: var(--brown-700); }
        .pos-btn:disabled { opacity: .55; cursor: not-allowed; }
        .pos-empty {
            margin: 18px 28px 28px; min-height: 390px; padding: 20px;
            background: var(--panel); border: 1px solid var(--line); border-radius: var(--radius);
            box-shadow: 0 8px 24px rgba(74, 59, 39, .05);
        }
        .pos-empty__head {
            display: flex; align-items: center; justify-content: space-between;
            padding-bottom: 16px; border-bottom: 1px solid var(--line);
        }
        .pos-empty__head h2 { margin: 0; color: var(--text-main); font-size: 15px; }
        .pos-empty__body {
            min-height: 310px; display: flex; flex-direction: column; align-items: center;
            justify-content: center; text-align: center; color: var(--text-sub);
        }
        .pos-empty__icon {
            width: 58px; height: 58px; display: flex; align-items: center; justify-content: center;
            margin-bottom: 14px; border-radius: 50%; background: var(--gold-bg);
            color: var(--brown-600); font-size: 22px;
        }
        .pos-empty__title { margin: 0 0 6px; color: var(--text-main); font-size: 16px; font-weight: 700; }
        .pos-empty__text { margin: 0; font-size: 13px; }
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
        .tabs { display: flex; gap: 8px; padding: 18px 28px 0; flex-wrap: wrap; }
        .tab {
            position: relative;
            padding: 8px 16px; border-radius: 20px; font-size: 13px; font-weight: 600; margin-bottom: 10px;
            background: #f1f5f9; color: var(--text-sub); cursor: pointer; display: flex; align-items: center; gap: 8px; border: 1px solid transparent;
        }
        .tab.active { background: #fee2e2; color: #c2103a; border: 1px solid #fca5a5; }
        .tab-badge {
            position: absolute;
            top: -5px; right: -5px;
            background: #ef4444; color: white;
            font-size: 10px; font-weight: 700;
            padding: 2px 5px; border-radius: 10px;
            line-height: 1; z-index: 2;
        }
        .tab .dot { display: none; }
        .tab-add {
            width: 34px; height: 34px; border-radius: 10px; background: transparent; border: 1px dashed var(--brown-500);
            color: var(--brown-600); display: flex; align-items: center; justify-content: center; cursor: pointer; font-size: 16px;
        }

        /* Body layout */
        .pos-layout { display: flex; flex-direction: column; gap: 20px; padding: 28px; }
        .pos-top-section {
            background: var(--panel); border: 1px solid var(--line); border-radius: var(--radius);
            padding: 24px; display: flex; flex-direction: column;
        }
        .pos-bottom-section { display: flex; gap: 20px; align-items: stretch; }
        .pos-customer-panel {
            flex: 1; min-width: 0; background: var(--panel); border: 1px solid var(--line); border-radius: var(--radius); padding: 24px;
        }
        .pos-payment-panel {
            flex: 1; min-width: 0; background: var(--panel); border: 1px solid var(--line); border-radius: var(--radius); padding: 24px;
            display: flex; flex-direction: column;
        }
        .panel-title-row {
            display: flex; justify-content: space-between; align-items: center;
            margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid var(--line);
        }
        .panel-title-row h2 { margin: 0; font-size: 16px; color: var(--text-main); font-weight: 700; display: flex; align-items: center; gap: 8px; }

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

        /* Danh sach san pham dang bang de de quet ten, SKU, ton kho va gia. */
        .product-grid { display: block; margin-top: 18px; }
        .product-table-wrap { width: 100%; max-width: 100%; overflow: hidden; border: 1px solid var(--line); border-radius: 10px; background: #fff; }
        .product-table { width: 100%; max-width: 100%; border-collapse: separate; border-spacing: 0; table-layout: fixed; }
        .product-table th {
            padding: 8px 9px; border-bottom: 1px solid var(--line); background: var(--bg);
            color: var(--text-sub); font-size: 10.5px; font-weight: 700; letter-spacing: .02em; text-align: left;
            white-space: normal; overflow-wrap: anywhere;
        }
        .product-table th:first-child { width: 40%; }
        .product-table th:nth-child(2) { width: 29%; }
        .product-table th:nth-child(3) { width: 19%; text-align: right; }
        .product-table th:last-child { width: 12%; text-align: center; }
        .product-table td { padding: 8px 9px; border-bottom: 1px solid var(--line); vertical-align: middle; min-width: 0; overflow-wrap: anywhere; }
        .product-table tbody tr:last-child td { border-bottom: 0; }
        .product-table tbody tr.p-card { background: #fff; cursor: pointer; transition: background .15s ease; }
        .product-table tbody tr.p-card:hover { background: var(--gold-bg); }
        .product-table__product { display: flex; align-items: center; gap: 8px; min-width: 0; }
        .product-table .p-thumb {
            width: 44px; height: 44px; flex: 0 0 44px; margin: 0; border-radius: 7px; background: var(--gold-bg);
            display: flex; align-items: center; justify-content: center; color: var(--brown-600); overflow: hidden;
        }
        .product-table .p-thumb img { width: 100%; height: 100%; object-fit: cover; border-radius: 7px; display: block; }
        .product-table .p-name { font-size: 12px; font-weight: 700; line-height: 1.3; margin: 0; overflow-wrap: anywhere; word-break: break-word; }
        .product-table .p-sku { color: var(--text-sub); font-size: 10px; line-height: 1.25; margin-top: 2px; overflow-wrap: anywhere; }
        .product-table .p-meta { font-size: 11px; color: var(--text-sub); line-height: 1.35; margin: 0; overflow-wrap: anywhere; word-break: break-word; }
        .product-table .p-bottom { display: flex; align-items: center; justify-content: flex-end; min-width: 0; }
        .product-table .p-price { font-size: 11.5px; font-weight: 700; color: var(--brown-700); white-space: normal; overflow-wrap: anywhere; text-align: right; }
        .product-table__empty { padding: 40px 16px !important; text-align: center; color: var(--text-sub); font-size: 14px; }
        .product-table__empty i { display: block; margin-bottom: 10px; color: #ccc; font-size: 24px; }
        .p-add {
            width: 26px; height: 26px; border-radius: 8px; background: var(--gold-bg); color: var(--brown-700);
            display: inline-flex; align-items: center; justify-content: center; border: 0; padding: 0;
            font: inherit; font-weight: 700; font-size: 14px; cursor: pointer;
            transition: background .15s ease, color .15s ease, transform .15s ease, opacity .15s ease;
        }
        .p-add:hover {
            background: var(--brown-600);
            color: #fff;
            transform: scale(1.1);
        }
        .p-add[data-disabled="true"] {
            background: #eee;
            color: #aaa;
            cursor: not-allowed;
            opacity: .4;
            pointer-events: none;
        }
        @media (max-width: 720px) {
            .product-table th:first-child { width: 38%; }
            .product-table th:nth-child(2) { width: 30%; }
            .product-table th:nth-child(3) { width: 20%; }
            .product-table th:last-child { width: 12%; }
            .product-table td, .product-table th { padding: 7px 6px; }
            .product-table .p-thumb { width: 38px; height: 38px; flex-basis: 38px; }
            .product-table .p-name { font-size: 11.5px; }
            .product-table .p-sku, .product-table .p-meta { font-size: 10px; }
            .product-table .p-price { font-size: 10.5px; }
            .product-table .p-add { width: 24px; height: 24px; }
        }
        .stock-low { color: var(--red-text); }
        .cart-fly-item {
            position: fixed;
            z-index: 9999;
            width: 22px;
            height: 22px;
            border-radius: 50%;
            background: var(--brown-600);
            box-shadow: 0 8px 18px rgba(74, 59, 39, .22);
            pointer-events: none;
            transform: translate(-50%, -50%);
            overflow: hidden;
            will-change: transform, opacity;
        }
        .cart-fly-item::after {
            content: '';
            position: absolute;
            inset: 6px;
            border-radius: 50%;
            background: rgba(255, 255, 255, .72);
        }
        .cart-fly-item img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
        }
        .cart-fly-item.has-image::after { display: none; }

        /* Right column - cart */
        .cust-box {
            display: block; background: var(--bg); border: 1px solid var(--line);
            border-radius: 10px; padding: 11px 13px; margin-bottom: 16px; position: relative;
        }
        .cust-main-row { display: flex; align-items: center; justify-content: space-between; gap: 10px; }
        .cust-info { display: flex; align-items: center; gap: 10px; min-width: 0; }
        .cust-avatar { width: 32px; height: 32px; border-radius: 50%; background: var(--gold-bg); color: var(--brown-700); display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700;}
        .cust-name { font-size: 13px; font-weight: 600; }
        .cust-sub { font-size: 11px; color: var(--text-sub); }
        .cust-guest-action { flex-shrink: 0; }
        .cust-actions {
            display: flex; align-items: center; gap: 8px; width: 100%;
            margin-top: 10px; padding-top: 10px; border-top: 1px solid var(--line);
        }
        .cust-icon-btn {
            flex: 1; min-width: 0; height: 34px; padding: 0 10px;
            display: inline-flex; align-items: center; justify-content: center; gap: 6px;
            border: 1px solid var(--line); border-radius: 8px; background: #fff; color: var(--brown-600);
            font-size: 12px; font-weight: 700; cursor: pointer;
            transition: background .15s ease, color .15s ease, border-color .15s ease, transform .15s ease;
        }
        .cust-icon-btn:hover { background: var(--gold-bg); color: var(--brown-900); border-color: var(--brown-500); transform: translateY(-1px); }
        .link-btn {
            padding: 0; border: 0; background: transparent; color: var(--brown-600);
            font: inherit; font-size: 12px; font-weight: 600; cursor: pointer;
        }
        .link-btn:hover { color: var(--brown-900); text-decoration: underline; }
        .link-btn--guest { color: var(--green-text); }
        .customer-current { color: var(--green-text); font-size: 12px; font-weight: 700; }
        .cust-remove-btn {
            width: 28px; height: 28px; display: inline-flex; align-items: center; justify-content: center;
            border: 0; border-radius: 50%; background: var(--red-bg); color: var(--red-text);
            font-size: 19px; line-height: 1; cursor: pointer;
            transition: background .15s ease, color .15s ease, transform .15s ease;
        }
        .cust-remove-btn:hover { background: var(--red-text); color: #fff; transform: scale(1.06); }
        .cust-add-btn {
            flex: 1; min-width: 0; height: 34px; justify-content: center;
            border: 0; border-radius: 8px; background: var(--brown-600); color: #fff;
            padding: 0 10px; font-size: 12px; font-weight: 700; cursor: pointer;
            display: inline-flex; align-items: center; gap: 6px; transition: background .15s ease, transform .15s ease;
        }
        .cust-add-btn:hover { background: var(--brown-700); transform: translateY(-1px); }

        #panel-khach-hang {
            position: absolute; top: calc(100% + 6px); left: 0; right: 0; width: auto; background: #fff;
            border: 1px solid var(--line); border-radius: 10px; padding: 12px;
            box-shadow: 0 6px 20px rgba(0,0,0,.1); z-index: 10;
        }
        .customer-panel-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px; color: var(--text-main); font-size: 12px; font-weight: 700; }
        .customer-panel-close { border: 0; background: transparent; color: var(--text-sub); font-size: 20px; line-height: 1; cursor: pointer; padding: 0 2px; }
        .customer-panel-close:hover { color: var(--red-text); }
        #panel-khach-hang input { width: 100%; border: 1px solid var(--line); background: var(--bg); padding: 8px 10px; border-radius: 8px; font-size: 13px; }
        .customer-results { display: flex; flex-direction: column; gap: 6px; max-height: 190px; overflow-y: auto; margin-top: 8px; }
        .customer-result { width: 100%; border: 1px solid var(--line); background: #fff; border-radius: 8px; padding: 8px 9px; text-align: left; cursor: pointer; transition: border-color .15s ease, background .15s ease, transform .15s ease; }
        .customer-result:hover { border-color: var(--brown-500); background: var(--gold-bg); transform: translateY(-1px); }
        .customer-result-name { display: block; color: var(--text-main); font-size: 12px; font-weight: 700; overflow-wrap: anywhere; }
        .customer-result-meta { display: block; color: var(--text-sub); font-size: 11px; margin-top: 2px; overflow-wrap: anywhere; }
        .customer-result-empty { color: var(--text-sub); font-size: 12px; line-height: 1.4; padding: 2px 0; }
        .cart-title { font-size: 12px; font-weight: 700; letter-spacing: .04em; text-transform: uppercase; color: var(--text-sub); margin-bottom: 10px;}
        .cart-list {
            flex: 0 0 auto; overflow: visible; display: flex; flex-direction: column;
            gap: 12px; margin-bottom: 14px; padding-right: 2px;
        }
        .cart-empty { text-align: center; font-size: 13px; color: var(--text-sub); padding: 40px 0; }
        .cart-item { display: flex; gap: 10px; align-items: flex-start; padding-bottom: 12px; border-bottom: 1px solid var(--line); }
        .cart-item:last-child { padding-bottom: 0; border-bottom: 0; }
        .cart-item--new { animation: cart-row-added .55s ease-out; }
        @keyframes cart-row-added {
            from { opacity: .25; transform: translateY(10px); background: var(--gold-bg); }
            to { opacity: 1; transform: translateY(0); background: transparent; }
        }
        .ci-thumb { width: 44px; height: 44px; border-radius: 8px; background: var(--gold-bg); flex-shrink: 0; display: flex; align-items: center; justify-content: center; color: var(--brown-600); overflow: hidden;}
        .ci-thumb img { width: 100%; height: 100%; object-fit: cover; border-radius: 8px; display: block; }
        .ci-body { flex: 1; min-width: 0; }
        .ci-name { font-size: 12.5px; font-weight: 600; margin-bottom: 4px; }
        .ci-variant { display: flex; flex-wrap: wrap; gap: 4px; margin-bottom: 6px;}
        .ci-variant span { max-width: 100%; border: 1px solid var(--line); border-radius: 999px; background: var(--bg); color: var(--text-sub); padding: 2px 6px; font-size: 10.5px; line-height: 1.35; white-space: normal; overflow-wrap: anywhere; }
        .ci-row { display: flex; align-items: center; justify-content: space-between; }
        .qty-stepper { display: flex; align-items: center; gap: 8px; background: var(--bg); border: 1px solid var(--line); border-radius: 8px; padding: 3px 8px;}
        .qty-stepper span { font-size: 12px; font-weight: 600; width: 14px; text-align: center;}
        .qty-input { width: 34px; height: 24px; border: 1px solid transparent; border-radius: 6px; background: transparent; color: var(--text-main); font-size: 12px; font-weight: 700; text-align: center; outline: none; }
        .qty-input:focus { border-color: var(--brown-500); background: #fff; box-shadow: 0 0 0 2px rgba(180, 151, 85, .18); }
        .qty-input::-webkit-outer-spin-button, .qty-input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
        .qty-input { -moz-appearance: textfield; }
        .qty-stepper button { border: none; background: none; color: var(--brown-700); font-weight: 700; cursor: pointer; font-size: 13px; width: 16px;}
        .ci-price { font-size: 12.5px; font-weight: 700; color: var(--brown-700);}
        .ci-actions { display: flex; align-items: center; gap: 8px; }
        .ci-remove { border: 0; background: transparent; color: var(--red-text); font-size: 11px; cursor: pointer; padding: 3px 5px; }
        .ci-remove:hover { background: var(--red-bg); border-radius: 5px; }

        .voucher-row { display: flex; align-items: flex-start; gap: 8px; margin-bottom: 14px; }
        .voucher-input-wrap { position: relative; flex: 1; min-width: 0; }
        #input-voucher { width: 100%; box-sizing: border-box; border: 1px dashed var(--brown-500); border-radius: 9px; padding: 9px 12px; font-size: 12.5px; color: var(--text-sub); background: var(--bg); outline: none;}
        #input-voucher[readonly] { color: var(--brown-700); background: var(--gold-bg); font-weight: 700; cursor: default; }
        .voucher-apply { height: 38px; background: var(--gold-bg); color: var(--brown-700); border: none; border-radius: 9px; padding: 0 14px; font-size: 12px; font-weight: 700; cursor: pointer; white-space: nowrap;}
        .voucher-apply:disabled { background: var(--green-bg); color: var(--green-text); cursor: default; }
        .voucher-suggestions {
            position: absolute; top: calc(100% + 6px); left: 0; right: 0; z-index: 20;
            display: flex; flex-direction: column; gap: 5px; max-height: 230px; overflow-y: auto;
            padding: 6px; border: 1px solid var(--line); border-radius: 9px; background: #fff;
            box-shadow: 0 8px 22px rgba(0,0,0,.12);
        }
        .voucher-suggestions.hidden { display: none; }
        .voucher-suggestion {
            width: 100%; padding: 8px 9px; border: 1px solid var(--line); border-radius: 7px;
            background: #fff; color: var(--text-main); text-align: left; cursor: pointer;
            transition: border-color .15s ease, background .15s ease;
        }
        .voucher-suggestion:hover { border-color: var(--brown-500); background: var(--gold-bg); }
        .voucher-suggestion__head { display: flex; align-items: baseline; justify-content: space-between; gap: 8px; }
        .voucher-suggestion__code { color: var(--brown-700); font-size: 12px; font-weight: 700; overflow-wrap: anywhere; }
        .voucher-suggestion__discount { color: var(--green-text); font-size: 11px; font-weight: 700; white-space: nowrap; }
        .voucher-suggestion__meta { display: block; margin-top: 3px; color: var(--text-sub); font-size: 10.5px; line-height: 1.35; overflow-wrap: anywhere; }
        .voucher-suggestion-empty { padding: 8px 5px; color: var(--text-sub); font-size: 11.5px; line-height: 1.4; }
        .voucher-remove {
            flex: 0 0 auto; height: 38px; display: inline-flex; align-items: center; justify-content: center;
            border: 1px solid var(--red-text); border-radius: 9px; padding: 0 14px;
            background: var(--red-bg); color: var(--red-text); font-size: 12px; font-weight: 700; cursor: pointer;
            white-space: nowrap; transition: background .15s ease, color .15s ease, border-color .15s ease;
        }
        .voucher-remove:hover { background: var(--red-text); color: #fff; border-color: var(--red-text); }

        .totals { border-top: 1px dashed var(--line); padding-top: 12px; margin-bottom: 14px; }
        .t-row { display: flex; justify-content: space-between; font-size: 12.5px; color: var(--text-sub); margin-bottom: 7px;}
        .t-row.grand { color: var(--text-main); font-size: 16px; font-weight: 700; margin-top: 8px; padding-top: 10px; border-top: 1px solid var(--line);}
        .t-row.grand span:last-child { color: var(--brown-700); }
        .t-row .discount { color: var(--green-text); }

        .pay-methods { display: flex; gap: 8px; margin-bottom: 14px; }
        .pay-chip { flex: 1; text-align: center; padding: 10px 4px; border-radius: 9px; border: 1px solid var(--line); font-size: 11.5px; font-weight: 600; color: var(--text-sub); cursor: pointer;}
        .pay-chip.active { background: var(--brown-900); color: #fff; border-color: var(--brown-900);}
        .checkout-btn {
            background: var(--brown-600); color: #fff; border: none; border-radius: 11px; padding: 14px; width: 100%;
            font-size: 14.5px; font-weight: 700; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px;
        }
        .checkout-btn:hover { background: var(--brown-700); }
        .checkout-action-row {
            display: flex;
            margin-top: auto;
        }
        .checkout-action-row .checkout-btn {
            min-height: 54px;
            padding: 14px 18px;
            border-radius: 10px;
            font-size: 15px;
            font-weight: 700;
        }
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
        .customer-create-modal .transfer-modal__dialog {
            width: min(720px, 100%);
            padding: 30px 36px 28px;
            border-radius: 16px;
        }
        .customer-create-modal .transfer-modal__header {
            padding-bottom: 18px;
            margin-bottom: 22px;
            border-bottom: 1px solid var(--line);
        }
        .customer-create-modal .transfer-modal__header h2 {
            color: #b1985d;
            font-size: 24px;
            font-weight: 800;
        }
        .customer-create-modal .transfer-modal__hint {
            margin: -12px 0 20px;
            color: var(--text-sub);
            font-size: 14px;
        }
        .customer-form-grid {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 22px 24px;
        }
        .customer-form-field { display: flex; flex-direction: column; gap: 8px; color: #4a5568; font-size: 13px; font-weight: 700; }
        .customer-form-field input,
        .customer-form-field select {
            width: 100%; height: 44px; box-sizing: border-box;
            border: 1px solid #d9dee7; border-radius: 9px; background: #fff;
            padding: 0 13px; color: var(--text-main); font-size: 14px; outline: none;
            transition: border-color .15s ease, box-shadow .15s ease;
        }
        .customer-form-field input:focus,
        .customer-form-field select:focus {
            border-color: #b1985d;
            box-shadow: 0 0 0 3px rgba(177, 152, 93, .18);
        }
        .customer-required { color: var(--red-text); }
        .customer-form-actions {
            display: flex; justify-content: flex-end; align-items: center; gap: 12px;
            margin-top: 28px; padding-top: 20px; border-top: 1px solid var(--line);
        }
        .customer-form-actions button {
            border: 0; border-radius: 8px; padding: 12px 20px;
            font-weight: 700; cursor: pointer; font-size: 14px;
            display: inline-flex; align-items: center; gap: 8px;
        }
        .customer-form-cancel { background: #eef2f7; color: #516075; }
        .customer-form-submit { background: #b1985d; color: #fff; }
        @media (max-width: 760px) {
            .customer-create-modal .transfer-modal__dialog { padding: 22px; }
            .customer-form-grid { grid-template-columns: 1fr; gap: 14px; }
            .customer-form-actions { justify-content: stretch; }
            .customer-form-actions button { flex: 1; justify-content: center; }
        }
        .payment-success {
            display: flex; flex-direction: column; align-items: center;
            padding: 8px 0 18px; text-align: center;
        }
        .payment-success__icon {
            width: 58px; height: 58px; display: flex; align-items: center; justify-content: center;
            margin-bottom: 12px; border-radius: 50%; background: var(--green-bg);
            color: var(--green-text); font-size: 25px;
        }
        .payment-success__message { margin: 0; color: var(--text-main); font-size: 14px; font-weight: 650; }
        .payment-success__hint { margin: 6px 0 0; color: var(--text-sub); font-size: 12.5px; }

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
        @media (max-width: 900px) {
            .page-head-row { flex-direction: column; }
            .head-btns { margin-left: 0; }
            .pos-layout { flex-direction: column; }
            .left-col, .right-col { width: 100%; box-sizing: border-box; }
            .right-col { flex: 0 0 auto; }
        }
        /* ================= THỐNG NHẤT NHẬN DIỆN MÀU SẮC ================= */
        :root {
            --primary: #b4975a; /* Vàng kim (Gold) - Khớp với Quản lý sản phẩm */
            --primary-hover: #9f844b;
            --primary-active: #856d3c;
            --primary-light: #fbf9f4;
            --danger: #dc2626; /* Đỏ Danger chuẩn Tailwind (Khớp QLSP) */
            --danger-hover: #b91c1c;
            --disabled-bg: #f3f4f6;
            --disabled-text: #9ca3af;
        }

        /* Buttons (Solid) */
        .pos-btn-solid,
        #create-invoice-button,
        .checkout-btn,
        button[onclick*="product-list-modal\').classList.remove"],
        .p-add,
        button[data-customer-create-open] {
            background-color: var(--primary) !important;
            color: #fff !important;
            border: 1px solid var(--primary) !important;
            border-radius: 10px !important;
        }
        .pos-btn-solid:hover,
        #create-invoice-button:hover,
        .checkout-btn:hover,
        button[onclick*="product-list-modal\').classList.remove"]:hover,
        .p-add:hover,
        button[data-customer-create-open]:hover {
            background-color: var(--primary-hover) !important;
            border-color: var(--primary-hover) !important;
        }
        .pos-btn-solid:active,
        #create-invoice-button:active,
        .checkout-btn:active,
        button[onclick*="product-list-modal\').classList.remove"]:active,
        .p-add:active,
        button[data-customer-create-open]:active {
            background-color: var(--primary-active) !important;
            border-color: var(--primary-active) !important;
        }
        
        /* Buttons (Outline / Secondary) */
        .pos-btn-outline,
        #open-product-qr,
        button[data-customer-open],
        .close-modal-btn,
        button[onclick*="closeModal"]:not(.transfer-modal__close):not(.product-qr-modal__close),
        button[onclick*="classList.add('hidden')"]:not(.transfer-modal__close):not(.product-qr-modal__close) {
            background-color: #fff !important;
            color: var(--primary) !important;
            border: 1px solid var(--primary) !important;
            border-radius: 10px !important;
        }
        .pos-btn-outline:hover,
        #open-product-qr:hover,
        button[data-customer-open]:hover,
        .close-modal-btn:hover,
        button[onclick*="closeModal"]:not(.transfer-modal__close):not(.product-qr-modal__close):hover,
        button[onclick*="classList.add('hidden')"]:not(.transfer-modal__close):not(.product-qr-modal__close):hover {
            background-color: var(--primary-light) !important;
        }

        /* Disabled states */
        .pos-btn-solid:disabled,
        #create-invoice-button:disabled,
        .checkout-btn:disabled,
        .p-add[data-disabled="true"] {
            background-color: var(--disabled-bg) !important;
            color: var(--disabled-text) !important;
            border-color: transparent !important;
            cursor: not-allowed !important;
            pointer-events: none;
        }
        .pos-btn-outline:disabled,
        #open-product-qr:disabled,
        button[data-customer-open]:disabled {
            background-color: #fff !important;
            color: var(--disabled-text) !important;
            border-color: var(--disabled-bg) !important;
            cursor: not-allowed !important;
            pointer-events: none;
        }

        /* Danger buttons (Solid - Hủy Đơn) */
        button[onclick*="xoaHoaDonCho"]:not(.btn-close-tab) {
            background-color: var(--danger) !important;
            color: #fff !important;
            border: 1px solid var(--danger) !important;
            border-radius: 10px !important;
            box-shadow: none !important;
            transition: all 0.2s ease !important;
        }
        button[onclick*="xoaHoaDonCho"]:not(.btn-close-tab):hover {
            background-color: var(--danger-hover) !important;
            border-color: var(--danger-hover) !important;
        }

        /* Danger buttons (Outline / Text - Xóa sản phẩm, Gỡ khách hàng) */
        .cancel-btn,
        .ci-remove,
        button[onclick*="huyHoaDon"],
        .cust-guest-action button {
            background-color: transparent !important;
            color: var(--danger) !important;
            border: 1px solid var(--danger) !important;
            border-radius: 8px !important;
            box-shadow: none !important;
        }
        .ci-remove {
            border: none !important;
        }
        .cancel-btn:hover,
        .ci-remove:hover,
        button[onclick*="huyHoaDon"]:hover,
        .cust-guest-action button:hover {
            background-color: #fee2e2 !important;
            color: var(--danger-hover) !important;
            border-color: var(--danger-hover) !important;
        }
        
        /* Tabs */
        .tab.active {
            background-color: var(--primary-light) !important;
            color: var(--primary) !important;
            border-color: var(--primary) !important;
        }
        
        /* Dropdowns & Selections */
        .pttt-option[style*="background: #f0fdf4"], 
        .pttt-option[style*="background: rgb(240, 253, 244)"],
        #voucher-dropdown-items > div[style*="background: #fffbeb"],
        #voucher-dropdown-items > div[style*="background: rgb(255, 251, 235)"] {
            background-color: var(--primary-light) !important;
            color: var(--primary) !important;
        }
        .pttt-option[style*="background: #f0fdf4"] i, 
        .pttt-option[style*="background: rgb(240, 253, 244)"] i {
            color: var(--primary) !important;
        }

        #voucher-dropdown-btn[style*="border-color: #c2103a"],
        #pttt-dropdown-btn[style*="border-color: #c2103a"],
        #voucher-dropdown-btn[style*="border-color: rgb(194, 16, 58)"],
        #pttt-dropdown-btn[style*="border-color: rgb(194, 16, 58)"] {
            border-color: var(--primary) !important;
        }

        #voucher-dropdown-btn {
            gap: 8px;
            min-width: 0;
        }
        #voucher-selected-label {
            display: flex;
            align-items: center;
            flex: 1 1 auto;
            min-width: 0;
            white-space: nowrap;
            overflow: hidden;
        }
        #voucher-selected-label > i,
        #voucher-dropdown-btn > .fa-chevron-down {
            flex: 0 0 auto;
        }
        #voucher-dropdown-btn[data-locked="true"] {
            cursor: not-allowed !important;
            opacity: .82;
            pointer-events: none;
        }
        #voucher-dropdown-btn[data-locked="true"] > .fa-chevron-down {
            display: none;
        }
        #voucher-selected-label > strong {
            min-width: 0;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        .voucher-best-badge {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            flex: 0 0 auto;
            margin-left: 6px;
            padding: 2px 7px;
            border-radius: 999px;
            background: #fef3c7;
            color: #92400e;
            font-size: 10px;
            font-weight: 800;
            line-height: 1.2;
            white-space: nowrap;
        }
        
        /* Icons and text */
        .fa-check, .fa-tag, .fa-money-bill-wave, .fa-university {
            color: var(--primary) !important;
        }
        #sum-tongcong,
        .cust-name span,
        .ci-price {
            color: var(--primary) !important;
        }
        .totals .t-row.grand,
        .totals .t-row.grand strong,
        #sum-tongcong,
        .checkout-action-row .checkout-btn {
            font-family: 'Inter', sans-serif !important;
            letter-spacing: 0 !important;
        }
        .totals .t-row.grand strong {
            font-weight: 700 !important;
        }
        #sum-tongcong {
            font-weight: 700 !important;
        }
        .checkout-action-row .checkout-btn {
            font-weight: 700 !important;
        }

        /* Pagination */
        .page-item.active .page-link {
            background-color: var(--primary) !important;
            border-color: var(--primary) !important;
            color: #fff !important;
        }
        .page-link {
            color: var(--primary) !important;
        }
        .page-link:hover {
            background-color: var(--primary-light) !important;
            color: var(--primary) !important;
        }

        /* Checkboxes & Radios */
        input[type="checkbox"],
        input[type="radio"] {
            accent-color: var(--primary) !important;
        }
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
                            <h1 class="page-title">Bán hàng tại quầy  </h1>
                            <div class="seller-meta" aria-label="Thông tin ca bán hàng">
                                <span class="seller-meta__item">
                                    <i class="fas fa-user-tag" aria-hidden="true"></i>
                                    Người bán:
                                    <strong>${not empty nhanVienBanHang.hoTen ? nhanVienBanHang.hoTen : 'Chưa xác định'}</strong>
                                </span>
                                <span class="seller-meta__item">
                                    <i class="far fa-calendar-alt" aria-hidden="true"></i>
                                    Ngày:
                                    <strong>${ngayBanHang}</strong>
                                </span>
                                <span class="seller-meta__item">
                                    <i class="far fa-clock" aria-hidden="true"></i>
                                    Ca làm việc:
                                    <strong>${tenCaLamViec}</strong>
                                </span>
                            </div>
                            <div class="badge-limit">
                                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 3"/></svg>
                                ${danhSachHoaDonCho.size()}/10 hóa đơn chờ
                            </div>
                        </div>
                        <div class="head-btns">
                            <button type="button" class="pos-btn pos-btn-solid" id="create-invoice-button" style="background: #2e203b; color: white; border-radius: 20px; padding: 10px 20px;">
                                <i class="fas fa-plus" style="margin-right: 5px;"></i>
                                Tạo đơn hàng
                            </button>
                        </div>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${empty hoaDonDangTao}">
                        <section class="pos-empty" aria-label="Trạng thái chưa có đơn hàng">
                            <div class="pos-empty__head">
                                <h2>Sản phẩm</h2>
                            </div>
                            <div class="pos-empty__body">
                                <div class="pos-empty__icon">
                                    <i class="fas fa-shopping-bag" aria-hidden="true"></i>
                                </div>
                                <p class="pos-empty__title">Chưa có dữ liệu</p>
                                <p class="pos-empty__text">Vui lòng tạo đơn hàng để bắt đầu bán.</p>
                            </div>
                        </section>
                    </c:when>
                    <c:otherwise>
                <div class="tabs">
                    <c:forEach var="hd" items="${danhSachHoaDonCho}">
                        <div class="tab ${hd.id == idHoaDonDangTao ? 'active' : ''}" data-hoadon="${hd.id}">
                            <c:set var="sl" value="${soLuongSpMap[hd.id] != null ? soLuongSpMap[hd.id] : 0}"/>
                            <span class="tab-badge" style="display: ${sl > 0 ? 'inline-flex' : 'none'};" data-badge="${hd.id}">${sl}</span>
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
                    <!-- TOP SECTION: Products & Cart -->
                    <div class="pos-top-section">
                        <div class="panel-title-row">
                            <h2><i class="fas fa-box" style="margin-right: 5px;"></i> Sản phẩm</h2>
                            <div class="head-btns" style="margin: 0;">
                                <button type="button" class="pos-btn pos-btn-outline" id="open-product-qr"
                                        ${empty idHoaDonDangTao ? 'disabled' : ''}
                                        style="color: #c2103a; border-color: #fca5a5; background: #fff; padding: 8px 16px;"
                                        title="${empty idHoaDonDangTao ? 'Vui lòng tạo đơn hàng trước khi quét sản phẩm' : 'Quét QR sản phẩm'}">
                                    <i class="fas fa-qrcode"></i> QUÉT QR SẢN PHẨM
                                </button>
                                <button type="button" class="pos-btn pos-btn-solid" style="background: #2e203b; color: white; padding: 8px 16px;" onclick="document.getElementById('product-list-modal').classList.remove('hidden'); setTimeout(() => document.getElementById('client-search-product').focus(), 100);">
                                    <i class="fas fa-plus"></i> THÊM SẢN PHẨM
                                </button>
                            </div>
                        </div>

                        <!-- Giỏ hàng (Cart) -->
                        <div class="cart-list" style="margin-bottom: 20px;">
                            <c:if test="${empty hoaDonDangTao.chiTietHoaDons}">
                                <div class="cart-empty" style="padding: 40px 0;">
                                    <i class="fas fa-shopping-bag" style="font-size: 30px; color: #cbd5e1; margin-bottom: 10px;"></i>
                                    <br>Không có dữ liệu
                                </div>
                            </c:if>
                            <c:forEach var="ct" items="${hoaDonDangTao.chiTietHoaDons}">
                                <div class="cart-item" data-id="${ct.id}" data-spct="${ct.sanPhamChiTiet.id}">
                                    <div class="ci-thumb">
                                        <c:choose>
                                            <c:when test="${not empty ct.sanPhamChiTiet.hinhAnhHienThi && ct.sanPhamChiTiet.hinhAnhHienThi != 'null'}">
                                                <img src="${pageContext.request.contextPath}/${ct.sanPhamChiTiet.hinhAnhHienThi}"
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
                                        <div class="ci-variant">
                                            <c:if test="${not empty ct.sanPhamChiTiet.ma}">
                                                <span>Mã: ${ct.sanPhamChiTiet.ma}</span>
                                            </c:if>
                                            <c:if test="${not empty ct.sanPhamChiTiet.mauSac.tenMau}">
                                                <span>Màu: ${ct.sanPhamChiTiet.mauSac.tenMau}</span>
                                            </c:if>
                                            <c:if test="${not empty ct.sanPhamChiTiet.kichCo.tenKichCo}">
                                                <span>Size: ${ct.sanPhamChiTiet.kichCo.tenKichCo}</span>
                                            </c:if>
                                        </div>
                                        <div class="ci-row">
                                            <div class="qty-stepper">
                                                <button class="qty-minus" data-id="${ct.id}" data-qty="${ct.soLuong}">–</button>
                                                <input class="qty-input" type="number" min="1" inputmode="numeric"
                                                       value="${ct.soLuong}" data-id="${ct.id}" data-qty="${ct.soLuong}"
                                                       data-spct="${ct.sanPhamChiTiet.id}" aria-label="Số lượng ${ct.sanPhamChiTiet.sanPham.tenSanPham}">
                                                <button class="qty-plus" data-id="${ct.id}" data-qty="${ct.soLuong}" data-spct="${ct.sanPhamChiTiet.id}">+</button>
                                            </div>
                                            <div class="ci-actions">
                                                <div class="ci-price"><fmt:formatNumber value="${ct.tongTien}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></div>
                                                <button type="button" class="ci-remove" data-id="${ct.id}"
                                                        title="Xóa sản phẩm" aria-label="Xóa sản phẩm" style="color: #c2103a;">Xóa</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>


                    </div>

                    <!-- BOTTOM SECTION -->
                    <div class="pos-bottom-section">
                        <!-- LEFT: Customer -->
                        <div class="pos-customer-panel">
                            <div class="panel-title-row" style="border-bottom: 1px solid var(--line); margin-bottom: 15px; padding-bottom: 15px;">
                                <h2>Thông tin khách hàng</h2>
                                <button type="button" class="pos-btn pos-btn-outline" data-customer-open style="border-color: #fca5a5; color: #c2103a; padding: 6px 12px; font-size: 12px; border-radius: 8px;">
                                    Chọn khách hàng
                                </button>
                            </div>
                            <div class="cust-box" style="border: none; background: transparent; padding: 0;">
                                <div class="cust-main-row">
                                    <div class="cust-info">
                                        <c:set var="kh" value="${hoaDonDangTao.khachHang}"/>
                                        <div>
                                            <div class="cust-name" style="font-size: 14px;">Khách hàng: <span style="color: #c2103a; font-weight: bold;">${not empty kh ? kh.hoTen : 'Khách lẻ'}</span></div>
                                            <div class="cust-sub" style="margin-top: 8px; color: var(--text-sub);">${not empty kh ? kh.soDienThoai : 'Tại quầy: chỉ cần chọn sản phẩm và thanh toán.'}</div>
                                        </div>
                                    </div>
                                    <div class="cust-guest-action">
                                        <c:if test="${not empty kh}">
                                            <button type="button" class="cust-remove-btn" data-customer-guest
                                                    title="Gỡ khách hàng khỏi hóa đơn"
                                                    aria-label="Gỡ khách hàng khỏi hóa đơn">&times;</button>
                                        </c:if>
                                    </div>
                                </div>
                                
                                <div class="cust-actions" style="display:none;">
                                    <button type="button" class="cust-icon-btn" data-customer-open aria-expanded="false">Tìm khách</button>
                                    <button type="button" class="cust-add-btn" data-customer-create-open>Thêm mới</button>
                                </div>
                                <div id="panel-khach-hang" class="hidden" style="top: 100px;">
                                    <div class="customer-panel-head">
                                        <span>Chọn khách hàng</span>
                                        <button type="button" class="customer-panel-close" data-customer-close aria-label="Đóng">&times;</button>
                                    </div>
                                    <input type="text" id="input-sdt" placeholder="Số điện thoại, mã hoặc tên khách hàng" />
                                    <div id="ket-qua-khach-hang" class="customer-results hidden"></div>
                                    <div style="margin-top: 10px; text-align: center;">
                                        <button type="button" class="pos-btn pos-btn-solid" data-customer-create-open style="width: 100%; justify-content: center; background: #c2103a;">Thêm khách hàng mới</button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- RIGHT: Payment -->
                        <div class="pos-payment-panel">
                            <div class="panel-title-row" style="border-bottom: 1px solid var(--line); margin-bottom: 15px; padding-bottom: 15px;">
                                <h2>Thông tin thanh toán tại quầy</h2>
                            </div>
                            
                            <div style="display: flex; gap: 16px; margin-bottom: 15px;">
                                <!-- Phương thức thanh toán dropdown -->
                                <div style="flex: 1; min-width: 0; position: relative;">
                                    <label style="font-size: 12px; color: var(--text-sub); display: block; margin-bottom: 6px; font-weight: 600;">Phương thức thanh toán *</label>
                                    <div id="pttt-dropdown-btn" onclick="toggleDropdown('pttt')"
                                         style="display: flex; align-items: center; justify-content: space-between; background: #fff; border: 1.5px solid var(--line); border-radius: 10px; padding: 10px 14px; cursor: pointer; font-size: 13px; font-weight: 600; color: var(--text-main); user-select: none; transition: border-color 0.2s;">
                                        <span id="pttt-selected-label"><i class="fas fa-money-bill-wave" style="color: #10b981; margin-right: 7px;"></i>Tiền mặt</span>
                                        <i class="fas fa-chevron-down" id="pttt-chevron" style="font-size: 11px; color: var(--text-sub); transition: transform 0.2s;"></i>
                                    </div>
                                    <div id="pttt-dropdown-list" style="display:none; position: absolute; top: calc(100% + 6px); left: 0; right: 0; background: #fff; border: 1.5px solid var(--line); border-radius: 10px; box-shadow: 0 8px 24px rgba(0,0,0,0.12); z-index: 200; overflow: hidden;">
                                        <div class="pttt-option" data-ma="PTTT001" onclick="chonPhuongThucDropdown(this)"
                                             style="display: flex; align-items: center; gap: 10px; padding: 11px 14px; cursor: pointer; font-size: 13px; font-weight: 600; background: #f0fdf4; color: #10b981;"
                                             onmouseover="this.style.background='#dcfce7'" onmouseout="this.style.background='#f0fdf4'">
                                            <i class="fas fa-money-bill-wave"></i> Tiền mặt
                                            <i class="fas fa-check" style="margin-left: auto; font-size: 11px;"></i>
                                        </div>
                                        <div class="pttt-option" data-ma="PTTT002" onclick="chonPhuongThucDropdown(this)"
                                             style="display: flex; align-items: center; gap: 10px; padding: 11px 14px; cursor: pointer; font-size: 13px; font-weight: 600; color: var(--text-main);"
                                             onmouseover="this.style.background='#f8f6f3'" onmouseout="this.style.background=''">
                                            <i class="fas fa-university" style="color: #3b82f6;"></i> Chuyển khoản
                                        </div>
                                    </div>
                                </div>

                                <!-- Voucher dropdown -->
                                <div style="flex: 1; position: relative;">
                                    <label style="font-size: 12px; color: var(--text-sub); display: block; margin-bottom: 6px; font-weight: 600;">Chọn phiếu giảm giá</label>
                                    <c:set var="voucherDaApDung" value="${hoaDonDangTao.phieuGiamGia}"/>
                                    <div id="voucher-dropdown-btn" onclick="toggleDropdown('voucher')"
                                         data-locked="true"
                                         aria-disabled="true"
                                         style="display: flex; align-items: center; justify-content: space-between; background: #fff; border: 1.5px solid var(--line); border-radius: 10px; padding: 10px 14px; cursor: pointer; font-size: 13px; font-weight: 600; color: var(--text-main); user-select: none; transition: border-color 0.2s;">
                                        <span id="voucher-selected-label">
                                            <c:choose>
                                                <c:when test="${not empty voucherDaApDung}">
                                                    <i class="fas fa-tag" style="color: #f59e0b; margin-right: 7px;"></i>${voucherDaApDung.maVoucher}
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="fas fa-tag" style="color: #cbd5e1; margin-right: 7px;"></i><span style="color: var(--text-sub);">-- Không áp dụng --</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                        <i class="fas fa-chevron-down" id="voucher-chevron" style="font-size: 11px; color: var(--text-sub); transition: transform 0.2s;"></i>
                                    </div>
                                    <div id="voucher-dropdown-list" style="display:none; position: absolute; top: calc(100% + 6px); left: 0; right: 0; box-sizing: border-box; background: #fff; border: 1.5px solid var(--line); border-radius: 10px; box-shadow: 0 8px 24px rgba(0,0,0,0.12); z-index: 9999; overflow: hidden; flex-direction: column;">
                                        <div style="padding: 10px; border-bottom: 1px solid var(--line); background: #f8fafc; flex-shrink: 0;">
                                            <div style="position: relative;">
                                                <i class="fas fa-search" style="position: absolute; left: 10px; top: 50%; transform: translateY(-50%); color: var(--text-sub); font-size: 12px;"></i>
                                                <input type="text" id="voucher-search-input" placeholder="Tìm mã hoặc tên..." oninput="xuLyTimVoucherDropdown(this.value)" style="width: 100%; padding: 8px 10px 8px 30px; border: 1px solid var(--line); border-radius: 6px; font-size: 13px; outline: none;">
                                            </div>
                                        </div>
                                        <div id="voucher-dropdown-content" style="overflow-y: auto; flex: 1; min-height: 0;">
                                            <div id="voucher-dropdown-loading" style="padding: 16px; text-align: center; color: var(--text-sub); font-size: 13px;">
                                                <i class="fas fa-spinner fa-spin"></i> Đang tải...
                                            </div>
                                            <div id="voucher-dropdown-items"></div>
                                        </div>
                                    </div>
                                    <!-- hidden fields giữ giá trị hiện tại để JS đọc -->
                                    <input type="hidden" id="voucher-ma-hien-tai" value="${not empty voucherDaApDung ? voucherDaApDung.maVoucher : ''}">
                                    <div id="voucher-suggestions" class="voucher-suggestions hidden" role="listbox"></div>
                                </div>
                            </div>


                            <c:set var="tamTinhHoaDon" value="${0}"/>
                            <c:forEach var="ct" items="${hoaDonDangTao.chiTietHoaDons}">
                                <c:set var="tamTinhHoaDon" value="${tamTinhHoaDon + (ct.donGia * ct.soLuong)}"/>
                            </c:forEach>
                            <c:set var="tongThanhToanHienTai" value="${empty hoaDonDangTao.tongTienThanhToan ? 0 : hoaDonDangTao.tongTienThanhToan}"/>
                            <c:set var="giamGiaHienTai" value="${tamTinhHoaDon > tongThanhToanHienTai ? tamTinhHoaDon - tongThanhToanHienTai : 0}"/>
                            
                            <div class="totals" style="border-top: none; padding-top: 0; margin-bottom: 20px;">
                                <div id="best-voucher-message" ${empty voucherDaApDung ? 'hidden' : ''} style="margin-bottom: 10px; color: var(--brown-700); font-size: 12px; font-weight: 700;">Đã lựa chọn phiếu giảm giá tốt nhất</div>
                                <div class="t-row" style="margin-bottom: 12px;"><span>Tiền hàng</span><span id="sum-tamtinh"><fmt:formatNumber value="${tamTinhHoaDon}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span></div>
                                <div id="discount-summary" ${empty hoaDonResponse.moTaGiamGia ? 'hidden' : ''}>
                                    <div class="t-row" style="margin-bottom: 5px;"><span>Giảm giá</span><span class="discount" id="sum-giamgia" style="color: #ef4444;">-<fmt:formatNumber value="${giamGiaHienTai}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span></div>
                                    <div id="discount-description" style="margin-bottom: 12px; color: var(--text-sub); font-size: 12px;">${hoaDonResponse.moTaGiamGia}</div>
                                </div>
                                <div class="t-row grand" style="border-top: none; padding-top: 5px; margin-top: 5px;">
                                    <strong style="color: var(--text-main); font-size: 14px;">Tổng số tiền cần thanh toán</strong>
                                    <strong id="sum-tongcong" style="color: #c2103a; font-size: 16px;"><fmt:formatNumber value="${tongThanhToanHienTai}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></strong>
                                </div>
                            </div>

                            <div class="checkout-action-row">
                                <button type="button" id="checkout-btn" class="checkout-btn" ${empty idHoaDonDangTao ? 'disabled' : ''}>
                                    Xác nhận thanh toán
                                    <span id="checkout-total" data-amount="${tongThanhToanHienTai}" style="display: none;"><fmt:formatNumber value="${tongThanhToanHienTai}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                    </c:otherwise>
                </c:choose>
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
            Kiểm tra thông tin thanh toán trước khi xác nhận.
        </p>
        <div class="transfer-modal__qr" id="transfer-payment-qr-wrap">
            <img id="transfer-payment-qr" alt="Mã QR thanh toán hóa đơn">
        </div>
        <div id="cash-payment-fields" class="transfer-modal__cash hidden">
            <label class="transfer-modal__field" for="cash-paid-amount">
                Số tiền khách trả <span aria-hidden="true">*</span>
                <input id="cash-paid-amount" type="text" inputmode="numeric" autocomplete="off"
                       placeholder="Nhập số tiền khách trả">
            </label>
            <div class="cash-change-row">
                <span>Tiền thối lại</span>
                <strong id="cash-paid-change">0đ</strong>
            </div>
            <p id="cash-paid-error" class="cash-payment-error" hidden></p>
        </div>
        <div class="transfer-modal__actions">
            <button type="button" class="transfer-modal__cancel" id="cancel-transfer-payment">Hủy</button>
            <button type="button" class="transfer-modal__confirm" id="confirm-transfer-payment">Xác nhận thanh toán</button>
        </div>
    </section>
</div>

<div id="customer-create-modal" class="transfer-modal customer-create-modal hidden" aria-hidden="true">
    <section class="transfer-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="customer-create-title">
        <div class="transfer-modal__header">
            <h2 id="customer-create-title">Thêm khách hàng</h2>
            <button type="button" class="transfer-modal__close" id="close-customer-create" aria-label="Đóng">&times;</button>
        </div>
        <p class="transfer-modal__hint">Nhập thông tin để tạo khách hàng mới.</p>
        <form id="customer-create-form" autocomplete="off">
            <div class="customer-form-grid">
                <label class="customer-form-field">
                    <span>Họ tên <span class="customer-required">*</span></span>
                    <input type="text" id="input-khach-ho-ten" name="hoTen" required>
                </label>
                <label class="customer-form-field">
                    <span>Email</span>
                    <input type="email" id="input-khach-email" name="email">
                </label>
                <label class="customer-form-field">
                    <span>Số điện thoại <span class="customer-required">*</span></span>
                    <input type="tel" id="input-khach-sdt" name="soDienThoai" required>
                </label>
                <label class="customer-form-field">
                    <span>Ngày sinh</span>
                    <input type="date" id="input-khach-ngay-sinh" name="ngaySinh">
                </label>
                <div class="customer-form-field">
                    <span>Giới tính <span class="customer-required">*</span></span>
                    <div style="display: flex; gap: 20px; align-items: center; margin-top: 8px; height: 38px;">
                        <label style="display: flex; align-items: center; gap: 6px; cursor: pointer; font-weight: normal; margin: 0; font-size: 14px; color: var(--text-main);">
                            <input type="radio" name="gioiTinh" value="1" required style="width: 16px; height: 16px; cursor: pointer;">
                            <span>Nam</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 6px; cursor: pointer; font-weight: normal; margin: 0; font-size: 14px; color: var(--text-main);">
                            <input type="radio" name="gioiTinh" value="0" required style="width: 16px; height: 16px; cursor: pointer;">
                            <span>Nữ</span>
                        </label>
                    </div>
                </div>
            </div>
            <div class="customer-form-actions">
                <button type="button" class="customer-form-cancel" id="cancel-customer-create">Hủy</button>
                <button type="submit" class="customer-form-submit">
                    <i class="fas fa-plus" aria-hidden="true"></i>
                    Thêm
                </button>
            </div>
        </form>
    </section>
</div>

<div id="payment-success-modal" class="transfer-modal hidden" aria-hidden="true">
    <section class="transfer-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="payment-success-title">
        <div class="transfer-modal__header">
            <h2 id="payment-success-title">Thanh toán thành công</h2>
        </div>
        <div class="payment-success">
            <div class="payment-success__icon">
                <i class="fas fa-check" aria-hidden="true"></i>
            </div>
            <p class="payment-success__message" id="payment-success-message">
                Hóa đơn đã được thanh toán thành công.
            </p>
            <p class="payment-success__hint">Bạn có muốn in hóa đơn cho khách không?</p>
        </div>
        <div class="transfer-modal__actions">
            <button type="button" class="transfer-modal__cancel" id="skip-print-invoice">Không in</button>
            <button type="button" class="transfer-modal__confirm" id="print-paid-invoice">
                <i class="fas fa-print" aria-hidden="true"></i>
                In hóa đơn
            </button>
        </div>
    </section>
</div>

<div id="cancel-invoice-modal" class="transfer-modal hidden" aria-hidden="true">
    <section class="transfer-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="cancel-invoice-title">
        <div class="transfer-modal__header">
            <h2 id="cancel-invoice-title">Xác nhận hủy hóa đơn</h2>
            <button type="button" class="transfer-modal__close" id="close-cancel-invoice" aria-label="Thoát">&times;</button>
        </div>
        <p class="transfer-modal__hint" id="cancel-invoice-message">
            Bạn có chắc muốn hủy hóa đơn này không?
        </p>
        <div class="transfer-modal__actions">
            <button type="button" class="transfer-modal__cancel" id="exit-cancel-invoice">Thoát</button>
            <button type="button" class="transfer-modal__confirm" id="confirm-cancel-invoice">Xác nhận hủy hóa đơn</button>
        </div>
    </section>
</div>

<div id="product-qr-modal" class="product-qr-modal hidden" aria-hidden="true">
    <section class="product-qr-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="product-qr-title">
        <div class="product-qr-modal__header">
            <h2 id="product-qr-title">Quét mã QR sản phẩm</h2>
            <button type="button" class="product-qr-modal__close" id="close-product-qr" aria-label="Đóng">&times;</button>
        </div>
        <p class="product-qr-modal__hint">Đưa mã QR của biến thể sản phẩm vào khung quét. Sản phẩm tìm thấy sẽ được thêm vào hóa đơn đang chọn.</p>
        <select id="qr-camera-select" style="width: 100%; margin-bottom: 10px; padding: 8px; border-radius: 8px; border: 1px solid var(--line); display: none;"></select>
        <div id="product-qr-reader"></div>
        <p id="product-qr-status" role="status">Đang chờ camera hoặc mã QR.</p>
        <div class="product-qr-manual">
            <input id="product-qr-code" type="text" autocomplete="off" placeholder="Nhập mã QR thủ công nếu không dùng camera">
            <button type="button" id="submit-product-qr">Thêm sản phẩm</button>
        </div>
        <div class="product-qr-modal__actions">
            <button type="button" id="restart-product-qr" style="display: none;">Quét lại</button>
            <button type="button" id="cancel-product-qr">Đóng</button>
        </div>
    </section>
</div>

<div id="product-list-modal" class="transfer-modal hidden" aria-hidden="true" style="z-index: 1050;">
    <section class="transfer-modal__dialog" role="dialog" aria-modal="true" style="width: min(1050px, 96vw); max-width: 1050px; padding: 20px;">
        <div class="transfer-modal__header" style="border-bottom: 1px solid var(--line); padding-bottom: 15px; margin-bottom: 20px;">
            <h2 style="font-size: 18px; color: var(--text-main); font-weight: bold;">Chọn biến thể để thêm vào đơn</h2>
            <button type="button" class="transfer-modal__close" onclick="document.getElementById('product-list-modal').classList.add('hidden')" aria-label="Đóng">&times;</button>
        </div>
        
        <div style="display: flex; gap: 15px; margin-bottom: 20px;">
            <div style="flex: 1;">
                <label style="font-size: 12px; color: var(--text-sub); display: block; margin-bottom: 5px;">Tìm kiếm</label>
                <input type="text" id="client-search-product" placeholder="Tìm mã, tên..." style="width: 100%; border: 1px solid var(--line); border-radius: 8px; padding: 10px; font-size: 13px; outline: none;">
            </div>
            <div style="flex: 1;">
                <label style="font-size: 12px; color: var(--text-sub); display: block; margin-bottom: 5px;">Màu sắc</label>
                <select id="filter-color" style="width: 100%; border: 1px solid var(--line); border-radius: 8px; padding: 10px; font-size: 13px; outline: none; background: white;"><option value="">Tất cả màu</option></select>
            </div>
            <div style="flex: 1;">
                <label style="font-size: 12px; color: var(--text-sub); display: block; margin-bottom: 5px;">Kích cỡ</label>
                <select id="filter-size" style="width: 100%; border: 1px solid var(--line); border-radius: 8px; padding: 10px; font-size: 13px; outline: none; background: white;"><option value="">Tất cả kích cỡ</option></select>
            </div>
            <div style="flex: 1;">
                <label style="font-size: 12px; color: var(--text-sub); display: block; margin-bottom: 5px;">Sản phẩm</label>
                <select id="filter-product" style="width: 100%; border: 1px solid var(--line); border-radius: 8px; padding: 10px; font-size: 13px; outline: none; background: white;"><option value="">Tất cả sản phẩm</option></select>
            </div>
            <div style="display: flex; align-items: flex-end;">
                <button type="button" id="btn-reset-filters" style="border: 1px solid #fca5a5; background: white; color: #c2103a; border-radius: 8px; padding: 10px 15px; font-weight: bold; cursor: pointer;">Đặt lại</button>
            </div>
        </div>

        <div style="overflow-x: auto; border: 1px solid var(--line); border-radius: 8px; max-height: 310px; overflow-y: auto;">
            <table style="width: 100%; min-width: 750px; border-collapse: collapse;">
                <thead style="position: sticky; top: 0; background: #f8f6f3; z-index: 1;">
                    <tr style="border-bottom: 2px solid var(--line);">
                        <th style="padding: 12px 10px; text-align: center; font-size: 12px; color: var(--text-sub); font-weight: 700; white-space: nowrap; width: 45px;">STT</th>
                        <th style="padding: 12px 10px; text-align: center; font-size: 12px; color: var(--text-sub); font-weight: 700; white-space: nowrap; width: 60px;">Ảnh</th>
                        <th style="padding: 12px 10px; text-align: left; font-size: 12px; color: var(--text-sub); font-weight: 700; width: 140px;">Mã biến thể</th>
                        <th style="padding: 12px 10px; text-align: left; font-size: 12px; color: var(--text-sub); font-weight: 700; width: 180px;">Tên sản phẩm</th>
                        <th style="padding: 12px 10px; text-align: left; font-size: 12px; color: var(--text-sub); font-weight: 700; white-space: nowrap;">Màu sắc</th>
                        <th style="padding: 12px 10px; text-align: left; font-size: 12px; color: var(--text-sub); font-weight: 700; white-space: nowrap;">Kích cỡ</th>
                        <th style="padding: 12px 10px; text-align: right; font-size: 12px; color: var(--text-sub); font-weight: 700; white-space: nowrap; width: 80px;">Tồn kho</th>
                        <th style="padding: 12px 10px; text-align: right; font-size: 12px; color: var(--text-sub); font-weight: 700; white-space: nowrap; width: 110px;">Giá bán</th>
                        <th style="padding: 12px 10px; text-align: center; font-size: 12px; color: var(--text-sub); font-weight: 700; white-space: nowrap; width: 110px;">Hành động</th>
                    </tr>
                </thead>
                <tbody id="modal-product-tbody">
                    <c:forEach var="sp" items="${danhSachSanPham}" varStatus="loop">
                        <tr class="p-card" data-spct="${sp.id}" style="border-bottom: 1px solid var(--line); transition: background 0.15s;" onmouseover="this.style.background='#fdf8f2'" onmouseout="this.style.background=''">
                            <td style="padding: 12px 10px; font-size: 13px; text-align: center; color: var(--text-sub);">${loop.index + 1}</td>
                            <td style="padding: 12px 10px; text-align: center;">
                                <div class="p-thumb" style="width: 50px; height: 50px; border-radius: 8px; overflow: hidden; background: var(--gold-bg); display: inline-flex; align-items: center; justify-content: center; color: var(--brown-600);">
                                    <c:choose>
                                        <c:when test="${not empty sp.hinhAnhHienThi && sp.hinhAnhHienThi != 'null'}">
                                            <img src="${pageContext.request.contextPath}/${sp.hinhAnhHienThi}" style="width: 100%; height: 100%; object-fit: cover;" onerror="this.style.display='none';"/>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="font-size: 11px; font-weight: bold;">SP</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                            <td style="padding: 12px 10px; font-size: 13px; font-weight: 600; color: var(--text-main);">${sp.ma}</td>
                            <td style="padding: 12px 10px; font-size: 13px; color: var(--text-main);">
                                <div class="p-name">${sp.sanPham.tenSanPham}</div>
                            </td>
                            <td style="padding: 12px 10px; font-size: 13px; color: var(--text-main); white-space: nowrap;">${sp.mauSac.tenMau}</td>
                            <td style="padding: 12px 10px; font-size: 13px; color: var(--text-main); white-space: nowrap;">${sp.kichCo.tenKichCo}</td>
                            <td style="padding: 12px 10px; font-size: 13px; font-weight: 600; color: #10b981; text-align: right;" data-tonkho>${sp.soLuongTon}</td>
                            <td style="padding: 12px 10px; font-size: 13px; font-weight: 600; color: #c2103a; text-align: right; white-space: nowrap;">
                                <div class="p-price"><fmt:formatNumber value="${sp.giaBan}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></div>
                            </td>
                            <td style="padding: 10px 10px; text-align: center; white-space: nowrap;">
                                <button type="button" class="p-add"
                                        ${empty idHoaDonDangTao || hoaDonDangTao.trangThai == 3 || hoaDonDangTao.trangThai == 5 ? 'data-disabled="true"' : ''}
                                        style="display: inline-flex; align-items: center; gap: 4px; background: white; color: #c2103a; border: 1.5px solid #c2103a; border-radius: 6px; padding: 5px 14px; font-size: 13px; font-weight: 600; cursor: pointer; white-space: nowrap; min-width: 70px; justify-content: center; transition: all 0.15s;"
                                        onmouseover="if(!this.dataset.disabled){this.style.background='#c2103a';this.style.color='white';}"
                                        onmouseout="if(!this.dataset.disabled){this.style.background='white';this.style.color='#c2103a';}">
                                    Thêm
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        
        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 20px; padding-top: 15px;">
            <div id="page-info" style="font-size: 13px; color: var(--text-sub);">
                Đang tải...
            </div>
            <div style="display: flex; gap: 10px;">
                <button type="button" id="btn-prev-page" style="border: 1px solid var(--line); background: white; border-radius: 8px; padding: 8px 15px; cursor: pointer;">Trước</button>
                <button type="button" id="btn-next-page" style="border: 1px solid var(--line); background: white; border-radius: 8px; padding: 8px 15px; cursor: pointer;">Sau</button>
            </div>
        </div>
        
        <div style="display: flex; justify-content: flex-end; margin-top: 20px;">
            <button type="button" onclick="document.getElementById('product-list-modal').classList.add('hidden')" style="border: 1px solid #fca5a5; background: white; color: #c2103a; border-radius: 8px; padding: 10px 30px; font-weight: bold; cursor: pointer;">Đóng</button>
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
<script src="${pageContext.request.contextPath}/assets/js/banhang.js?v=202607303"></script>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('product-list-modal');
    if (!modal) return;
    
    const tbody = document.getElementById('modal-product-tbody');
    if (!tbody) return;
    const allRows = Array.from(tbody.querySelectorAll('tr.p-card'));
    
    const searchInput = document.getElementById('client-search-product');
    const colorSelect = document.getElementById('filter-color');
    const sizeSelect = document.getElementById('filter-size');
    const productSelect = document.getElementById('filter-product');
    const btnReset = document.getElementById('btn-reset-filters');
    const btnPrev = document.getElementById('btn-prev-page');
    const btnNext = document.getElementById('btn-next-page');
    const pageInfo = document.getElementById('page-info');
    
    let filteredRows = [...allRows];
    let currentPage = 1;
    const itemsPerPage = 5;

    const colors = new Set();
    const sizes = new Set();
    const products = new Set();
    
    allRows.forEach(row => {
        // New column order: 0=STT, 1=Ảnh, 2=Mã, 3=Tên, 4=Màu, 5=Kích cỡ, 6=Tồn kho, 7=Giá, 8=Hành động
        const ma = (row.cells[2] ? row.cells[2].innerText : '').trim();
        const tenElement = row.querySelector('.p-name');
        const ten = (tenElement ? tenElement.innerText : '').trim();
        const mau = (row.cells[4] ? row.cells[4].innerText : '').trim();
        const size = (row.cells[5] ? row.cells[5].innerText : '').trim();
        
        row.dataset.ma = ma.toLowerCase();
        row.dataset.ten = ten.toLowerCase();
        row.dataset.mau = mau;
        row.dataset.size = size;
        row.dataset.sp = ten;
        
        if (mau) colors.add(mau);
        if (size) sizes.add(size);
        if (ten) products.add(ten);
        
    });
    
    Array.from(colors).sort().forEach(c => colorSelect.add(new Option(c, c)));
    Array.from(sizes).sort().forEach(s => sizeSelect.add(new Option(s, s)));
    Array.from(products).sort().forEach(p => productSelect.add(new Option(p, p)));
    
    function renderPage() {
        allRows.forEach(row => row.style.display = 'none');
        
        const totalPages = Math.ceil(filteredRows.length / itemsPerPage) || 1;
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;
        
        const start = (currentPage - 1) * itemsPerPage;
        const end = start + itemsPerPage;
        
        const pageRows = filteredRows.slice(start, end);
        pageRows.forEach((row, index) => {
            row.style.display = '';
            row.cells[0].innerText = start + index + 1;
        });
        
        pageInfo.innerText = `Trang ${currentPage}/${totalPages} - ${filteredRows.length} biến thể`;
        
        btnPrev.disabled = currentPage === 1;
        btnNext.disabled = currentPage === totalPages;
        
        btnPrev.style.opacity = btnPrev.disabled ? '0.5' : '1';
        btnPrev.style.cursor = btnPrev.disabled ? 'not-allowed' : 'pointer';
        btnNext.style.opacity = btnNext.disabled ? '0.5' : '1';
        btnNext.style.cursor = btnNext.disabled ? 'not-allowed' : 'pointer';
    }
    
    function applyFilters() {
        const kw = searchInput.value.toLowerCase().trim();
        const c = colorSelect.value;
        const s = sizeSelect.value;
        const p = productSelect.value;
        
        filteredRows = allRows.filter(row => {
            const matchKw = !kw || row.dataset.ma.includes(kw) || row.dataset.ten.includes(kw);
            const matchC = c === '' || row.dataset.mau === c;
            const matchS = s === '' || row.dataset.size === s;
            const matchP = p === '' || row.dataset.sp === p;
            return matchKw && matchC && matchS && matchP;
        });
        
        currentPage = 1;
        renderPage();
    }
    
    if(searchInput) searchInput.addEventListener('input', applyFilters);
    if(colorSelect) colorSelect.addEventListener('change', applyFilters);
    if(sizeSelect) sizeSelect.addEventListener('change', applyFilters);
    if(productSelect) productSelect.addEventListener('change', applyFilters);
    
    if(btnReset) {
        btnReset.addEventListener('click', () => {
            searchInput.value = '';
            colorSelect.value = '';
            sizeSelect.value = '';
            productSelect.value = '';
            applyFilters();
        });
    }
    
    if(btnPrev) {
        btnPrev.addEventListener('click', () => {
            if (currentPage > 1) {
                currentPage--;
                renderPage();
            }
        });
    }
    
    if(btnNext) {
        btnNext.addEventListener('click', () => {
            const totalPages = Math.ceil(filteredRows.length / itemsPerPage);
            if (currentPage < totalPages) {
                currentPage++;
                renderPage();
            }
        });
    }
    
    renderPage();
});
</script>

</body>
</html>
