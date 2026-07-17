package QuanLySanPham.Utils;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9\\s-/]");
    private static final Pattern ALL_NUMBERS_PATTERN = Pattern.compile("^[0-9]+$");
    private static final Pattern VALID_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    public static String normalizeString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        return input.trim().replaceAll("\\s+", " ");
    }

    public static void validateTen(String ten) {
        if (ten == null || ten.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên không được để trống.");
        }
        String normalizedTen = normalizeString(ten);
        if (normalizedTen.length() < 2 || normalizedTen.length() > 100) {
            throw new IllegalArgumentException("Tên phải có từ 2 đến 100 ký tự.");
        }
        if (SPECIAL_CHAR_PATTERN.matcher(normalizedTen).find()) {
            throw new IllegalArgumentException("Tên không được chứa ký tự đặc biệt.");
        }
        if (ALL_NUMBERS_PATTERN.matcher(normalizedTen).find()) {
            throw new IllegalArgumentException("Tên không được chỉ chứa toàn số.");
        }
    }

    public static void validateMa(String ma) {
        if (ma == null || ma.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã không được để trống.");
        }
        String trimmedMa = ma.trim();
        if (trimmedMa.length() > 50) {
            throw new IllegalArgumentException("Mã không được quá 50 ký tự.");
        }
        if (!VALID_CODE_PATTERN.matcher(trimmedMa).find()) {
            throw new IllegalArgumentException("Mã chỉ được chứa chữ, số, '-' và '_'.");
        }
    }
     public static void validateTenSanPham(String ten) {
        if (ten == null || ten.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
        }
        String normalizedTen = normalizeString(ten);
        if (normalizedTen.length() < 2 || normalizedTen.length() > 200) {
            throw new IllegalArgumentException("Tên sản phẩm phải có từ 2 đến 200 ký tự.");
        }
    }
}
