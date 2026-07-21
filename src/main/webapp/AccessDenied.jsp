<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Không có quyền truy cập</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            margin: 0;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #f5efe6;
            font-family: 'Segoe UI', Arial, sans-serif;
            text-align: center;
        }
        .box i { font-size: 56px; color: #b3261e; }
        .box h2 { color: #4a4a4a; margin: 16px 0 8px; }
        .box p { color: #777; margin-bottom: 24px; }
        .box a {
            display: inline-block;
            padding: 10px 22px;
            background: #a38058;
            color: #fff;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
        }
        .box a:hover { background: #8b6744; }
    </style>
</head>
<body>
    <div class="box">
        <i class="fas fa-lock"></i>
        <h2>Bạn không có quyền truy cập</h2>
        <p>${not empty error ? error : 'Chức năng này chỉ dành cho Quản lý.'}</p>
        <a href="${pageContext.request.contextPath}/SanPham">Về trang chủ</a>
    </div>
</body>
</html>
