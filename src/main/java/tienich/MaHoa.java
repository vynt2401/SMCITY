package tienich;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.math.BigInteger;

public class MaHoa {

    // Hàm 1: Băm mật khẩu
    public static String bamSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }

    // Hàm 2: Chuẩn hóa tên
    public static String chuanHoaTen(String ten) {
        if (ten == null || ten.isEmpty()) return "";

        String[] tu = ten.trim().toLowerCase().split("\\s+");
        StringBuilder tenMoi = new StringBuilder();

        for (String t : tu) {
            if (t.length() > 0) {
                tenMoi.append(Character.toUpperCase(t.charAt(0)))
                        .append(t.substring(1))
                        .append(" ");
            }
        }
        return tenMoi.toString().trim();
    }

}