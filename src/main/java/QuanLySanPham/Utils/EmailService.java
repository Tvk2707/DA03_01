package QuanLySanPham.Utils; // Đổi lại package đúng với dự án của bạn

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    // 1. Điền Gmail dùng để GỬI MAIL
    private static final String FROM_EMAIL = "bachnguyentien1808@gmail.com";
    // 2. Điền Mật khẩu ứng dụng (App Password 16 ký tự) tạo từ Google
    private static final String APP_PASSWORD = "sqhv fodo urhd mzil";

    public static boolean sendEmail(String toEmail, String subject, String bodyHtml) {
        // Cấu hình kết nối tới server SMTP của Gmail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Xác thực tài khoản
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            // Email người nhận (toEmail) truyền vào từ Form đăng ký
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(bodyHtml, "text/html; charset=UTF-8");

            // Tiến hành gửi
            Transport.send(message);
            System.out.println("Gửi email thành công tới: " + toEmail);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Gửi email thất bại!");
            return false;
        }
    }
}