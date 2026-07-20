<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #f5efe6 0%, #e9dfd0 100%);
            font-family: 'Segoe UI', Arial, sans-serif;
        }
        .login-card {
            width: 380px;
            max-width: 92vw;
            background: #fff;
            border-radius: 12px;
            padding: 36px 32px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.12);
        }
        .login-header { text-align: center; margin-bottom: 28px; }
        .login-header i { font-size: 34px; color: #a38058; }
        .login-header h2 { margin: 12px 0 4px; color: #4a4a4a; font-size: 20px; }
        .login-header p { margin: 0; color: #999; font-size: 13px; }
        .form-group { margin-bottom: 18px; }
        .form-group label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            color: #4a4a4a;
            font-weight: 600;
        }
        .form-group input {
            width: 100%;
            padding: 11px 14px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            outline: none;
            transition: border-color 0.2s, box-shadow 0.2s;
        }
        .form-group input:focus {
            border-color: #a38058;
            box-shadow: 0 0 0 3px rgba(163, 128, 88, 0.15);
        }
        .btn-login {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 8px;
            background: #a38058;
            color: #fff;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.2s;
        }
        .btn-login:hover { background: #8b6744; }
        .error-box {
            background: #fdecea;
            color: #b3261e;
            padding: 10px 14px;
            border-radius: 8px;
            font-size: 13px;
            margin-bottom: 18px;
        }
        .success-box {
            background: #eaf7ed;
            color: #1e7e34;
            padding: 10px 14px;
            border-radius: 8px;
            font-size: 13px;
            margin-bottom: 18px;
        }
        .switch-link {
            text-align: center;
            margin-top: 18px;
            font-size: 13px;
            color: #777;
        }
        .switch-link a { color: #a38058; font-weight: 600; text-decoration: none; }
        .switch-link a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="login-card">
        <div class="login-header">
            <i class="fas fa-user-tie"></i>
            <h2>Đăng nhập hệ thống</h2>
            <p>Dành cho Quản lý &amp; Nhân viên</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error-box">
                <i class="fas fa-circle-exclamation"></i> <%= request.getAttribute("error") %>
            </div>
        <% } %>
        <% if ("1".equals(request.getParameter("registered"))) { %>
            <div class="success-box">
                <i class="fas fa-circle-check"></i> Đăng ký thành công! Hãy đăng nhập.
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/Login" method="post">
            <div class="form-group">
                <label>Email hoặc mã nhân viên</label>
                <input type="text" name="taiKhoan" value="${taiKhoan != null ? taiKhoan : ''}" placeholder="vd: nv001 hoặc email@congty.com" required autofocus>
            </div>
            <div class="form-group">
                <label>Mật khẩu</label>
                <input type="password" name="matKhau" placeholder="Nhập mật khẩu" required>
            </div>
            <button type="submit" class="btn-login">
                <i class="fas fa-right-to-bracket"></i> Đăng nhập
            </button>
        </form>

        <div class="switch-link">
            Chưa có tài khoản? <a href="${pageContext.request.contextPath}/Register">Đăng ký ngay</a>
        </div>
    </div>
</body>
</html>
