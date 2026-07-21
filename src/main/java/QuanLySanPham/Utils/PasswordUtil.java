package QuanLySanPham.Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Băm mật khẩu bằng SHA-256 - không cần thêm thư viện ngoài.
 * Mật khẩu lưu trong DB (cột mat_khau) sẽ ở dạng chuỗi hex đã băm, không phải plain text.
 */
public class PasswordUtil {

    private PasswordUtil() {}

    public static String hash(String rawPassword) {
        if (rawPassword == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Không thể băm mật khẩu", e);
        }
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) return false;
        return hash(rawPassword).equalsIgnoreCase(hashedPassword);
    }
}
