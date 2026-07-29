<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - RIOR</title>
    <!-- FontAwesome Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        }

        body {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: #f7f6f2; /* Màu nền kem nhạt chuẩn ảnh mẫu */
            padding: 20px;
        }

        .login-container {
            width: 100%;
            max-width: 420px;
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        .login-container {
            width: 100%;
            max-width: 440px; /* Tăng nhẹ kích thước cho cân đối */
            display: flex;
            flex-direction: column;
            align-items: center;

            /* --- CÁC THUỘC TÍNH TÁCH KHUNG NỔI BẬT --- */
            background-color: #ffffff;                        /* 1. Nền trắng tách biệt khỏi nền kem */
            padding: 40px 35px;                               /* 2. Tạo khoảng cách đệm bên trong */
            border-radius: 20px;                              /* 3. Bo góc mềm mại */
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.08),       /* 4. Đổ bóng nâng khung lên */
            0 5px 15px rgba(0, 0, 0, 0.04);
            border: 1px solid rgba(199, 156, 74, 0.15);       /* 5. Viền màu vàng kim nhẹ nhàn */
        }
        /* --- LOGO SECTION --- */
        .logo-box {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-bottom: 35px;
        }

        .logo-icon {
            width: 90px;
            height: 90px;
            background: linear-gradient(135deg, #c79c4a 0%, #ba8e3e 100%);
            border-radius: 22px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 8px 20px rgba(186, 142, 62, 0.3), inset 0 2px 3px rgba(255,255,255,0.3);
            margin-bottom: 12px;
        }

        .logo-icon i {
            font-size: 42px;
            color: #ffffff;
        }

        .logo-text {
            text-align: center;
        }

        .logo-text h1 {
            font-size: 26px;
            font-weight: 700;
            color: #2b2b2b;
            letter-spacing: 3px;
            line-height: 1;
            margin-bottom: 4px;
        }

        .logo-text span {
            font-size: 10px;
            font-weight: 600;
            color: #7a7a7a;
            letter-spacing: 2px;
            text-transform: uppercase;
        }

        /* --- FORM SECTION --- */
        .login-form {
            width: 100%;
        }

        .form-group {
            margin-bottom: 22px;
        }

        .form-group label {
            display: block;
            font-size: 15px;
            font-weight: 700;
            color: #000000;
            margin-bottom: 12px;
        }

        .input-wrapper {
            position: relative;
            display: flex;
            align-items: center;
        }

        .input-wrapper i.left-icon {
            position: absolute;
            left: 18px;
            font-size: 18px;
            color: #6c757d;
        }

        .input-wrapper input {
            width: 100%;
            height: 54px;
            background-color: #f1f3f7; /* Màu nền input xám xám nhạt */
            border: 1px solid transparent;
            border-radius: 12px;
            padding: 0 48px;
            font-size: 15px;
            color: #333;
            outline: none;
            transition: all 0.2s ease;
        }

        .input-wrapper input:focus {
            background-color: #ffffff;
            border-color: #c79c4a;
            box-shadow: 0 0 0 3px rgba(199, 156, 74, 0.15);
        }

        .toggle-password {
            position: absolute;
            right: 18px;
            cursor: pointer;
            color: #6c757d;
            font-size: 18px;
        }

        /* --- REMEMBER ME CHECKBOX --- */
        .remember-group {
            display: flex;
            align-items: center;
            margin-top: -5px;
            margin-bottom: 25px;
        }

        .remember-group input[type="checkbox"] {
            width: 18px;
            height: 18px;
            accent-color: #c79c4a;
            margin-right: 10px;
            cursor: pointer;
            border-radius: 4px;
        }

        .remember-group label {
            font-size: 14px;
            color: #555555;
            cursor: pointer;
            user-select: none;
        }

        /* --- SUBMIT BUTTON --- */
        .btn-submit {
            width: 100%;
            height: 54px;
            background-color: #c79c4a; /* Màu nút vàng đất/mật tính */
            color: #ffffff;
            border: none;
            border-radius: 12px;
            font-size: 18px;
            font-weight: 700;
            cursor: pointer;
            transition: background-color 0.2s ease, transform 0.1s ease;
            margin-bottom: 20px;
        }

        .btn-submit:hover {
            background-color: #b38a3d;
        }

        .btn-submit:active {
            transform: scale(0.99);
        }

        /* Thông báo lỗi/thành công */
        .alert-box {
            width: 100%;
            padding: 12px 16px;
            border-radius: 10px;
            font-size: 14px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .alert-error { background-color: #f8d7da; color: #721c24; }
    </style>
</head>
<body>

<div class="login-container">
    <!-- Logo Section -->
    <div class="logo-box">
        <div class="logo-icon">
            <i class="fas fa-glasses"></i>
        </div>
        <div class="logo-text">
            <h1>RIOR</h1>
            <span>LUXURY EYEWEAR</span>
        </div>
    </div>

    <!-- Thông báo từ server (nếu có) -->
    <% if (request.getAttribute("error") != null) { %>
    <div class="alert-box alert-error">
        <i class="fas fa-exclamation-circle"></i>
        <span><%= request.getAttribute("error") %></span>
    </div>
    <% } %>
    <!-- Form Đăng Nhập -->
    <form class="login-form" action="${pageContext.request.contextPath}/Login" method="post">

        <!-- Tài khoản -->
        <div class="form-group">
            <label for="taiKhoan">Tài Khoản</label>
            <div class="input-wrapper">
                <i class="far fa-user left-icon"></i>
                <input type="text" id="taiKhoan" name="taiKhoan"
                       value="${taiKhoan != null ? taiKhoan : ''}"
                       placeholder="Admin" required autofocus>
            </div>
        </div>

        <!-- Mật khẩu -->
        <div class="form-group">
            <label for="matKhau">Mật Khẩu</label>
            <div class="input-wrapper">
                <i class="fas fa-lock left-icon"></i>
                <input type="password" id="matKhau" name="matKhau"
                       placeholder="••••••••••••" required>
                <i class="far fa-eye toggle-password" id="togglePassword"></i>
            </div>
        </div>

        <!-- Ghi nhớ -->
        <div class="remember-group">
            <input type="checkbox" id="ghiNho" name="ghiNho">
            <label for="ghiNho">Ghi nhớ</label>
        </div>

        <!-- Nút bấm -->
        <button type="submit" class="btn-submit">Đăng nhập</button>

    </form>
</div>

<!-- Script xử lý ẩn/hiện mật khẩu -->
<script>
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('matKhau');

    togglePassword.addEventListener('click', function () {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);

        this.classList.toggle('fa-eye');
        this.classList.toggle('fa-eye-slash');
    });
</script>
</body>
</html>
