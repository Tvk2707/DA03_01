<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký</title>
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
            padding: 24px 0;
        }
        .login-card {
            width: 440px;
            max-width: 92vw;
            background: #fff;
            border-radius: 12px;
            padding: 36px 32px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.12);
        }
        .login-header { text-align: center; margin-bottom: 24px; }
        .login-header i { font-size: 34px; color: #a38058; }
        .login-header h2 { margin: 12px 0 4px; color: #4a4a4a; font-size: 20px; }
        .login-header p { margin: 0; color: #999; font-size: 13px; }
        .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
        .form-group { margin-bottom: 16px; }
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
        .switch-link {
            text-align: center;
            margin-top: 18px;
            font-size: 13px;
            color: #777;
        }
        .switch-link a { color: #a38058; font-weight: 600; text-decoration: none; }
        .switch-link a:hover { text-decoration: underline; }
        .hint { font-size: 12px; color: #999; margin-top: 6px; }
    </style>
</head>
<body>
    <div class="login-card">
        <div class="login-header">
            <i class="fas fa-user-plus"></i>
            <h2>Đăng ký tài khoản</h2>
            <p>Tài khoản mới mặc định là Nhân viên</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error-box">
                <i class="fas fa-circle-exclamation"></i> <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/Register" method="post">
            <div class="form-row">
                <div class="form-group">
                    <label>Mã nhân viên</label>
                    <input type="text" name="maNhanVien" value="${maNhanVien != null ? maNhanVien : ''}" placeholder="vd: NV002" required>
                </div>
                <div class="form-group">
                    <label>Họ tên</label>
                    <input type="text" name="hoTen" value="${hoTen != null ? hoTen : ''}" placeholder="Nguyễn Văn A" required>
                </div>
            </div>

            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" value="${email != null ? email : ''}" placeholder="email@congty.com" required>
            </div>

            <div class="form-group">
                <label>Số điện thoại</label>
                <input type="text" name="soDienThoai" value="${soDienThoai != null ? soDienThoai : ''}" placeholder="09xxxxxxxx">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Mật khẩu</label>
                    <input type="password" name="matKhau" placeholder="Tối thiểu 6 ký tự" required>
                </div>
                <div class="form-group">
                    <label>Xác nhận mật khẩu</label>
                    <input type="password" name="xacNhanMatKhau" placeholder="Nhập lại mật khẩu" required>
                </div>
            </div>

            <button type="submit" class="btn-login">
                <i class="fas fa-user-plus"></i> Đăng ký
            </button>
        </form>

        <div class="switch-link">
            Đã có tài khoản? <a href="${pageContext.request.contextPath}/Login">Đăng nhập</a>
        </div>
    </div>
</body>
</html>
