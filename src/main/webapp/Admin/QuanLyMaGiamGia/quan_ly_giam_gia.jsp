
<%
    request.setAttribute("pageTitle", "Quản lý giảm giá");
    request.setAttribute("activeMenu", "discount");

%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý giảm giá - RIOR Admin</title>

    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Common Layout CSS -->
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/sidebar.css">
    <link rel="stylesheet" href="../css/header.css">

    <!-- Discount Page CSS -->
    <link rel="stylesheet" href="../css/quan_ly_giam_gia.css">
</head>
<body>
<div class="admin-layout">
    <!-- Include Sidebar -->
    <%@ include file="../layout/sidebar.jsp" %>

    <div class="overlay" id="overlay"></div>

    <div class="main-content">
        <!-- Include Header -->
        <%@ include file="../layout/header.jsp" %>

        <!-- Page Content -->
        <div id="page-content" class="discount-page">

            <!-- Page Header -->
            <div class="page-header">
                <div class="page-header__info">
                    <h1 class="page-header__title">Quản lý giảm giá</h1>
                    <p class="page-header__subtitle">Mã khuyến mãi & đợt sale sản phẩm</p>
                </div>
                <button class="btn btn--primary" id="btnCreate" type="button">
                    <i class="fas fa-plus"></i>
                    <span id="btnCreateText">Tạo mã giảm giá</span>
                </button>
            </div>

            <!-- Tabs -->
            <div class="tabs" role="tablist">
                <button class="tab tab--active" data-tab="coupon" onclick="showCoupon()" type="button" role="tab">
                    <i class="fas fa-hashtag"></i>
                    <span>Mã giảm giá</span>
                </button>
                <button class="tab" data-tab="campaign" onclick="showCampaign()" type="button" role="tab">
                    <i class="fas fa-chart-bar"></i>
                    <span>Đợt giảm giá sản phẩm</span>
                </button>
            </div>

            <!-- Coupon Content -->
            <div id="couponContent" class="tab-content tab-content--active">
                <%@ include file="ma_giam_gia.jsp" %>
            </div>

            <!-- Campaign Content -->
            <div id="campaignContent" class="tab-content">
                <%@ include file="dot_giam_gia.jsp" %>
            </div>
        </div>
    </div>
</div>

<!-- JavaScript -->
<script src="quan_ly_giam_gia.js"></script>
</body>
</html>