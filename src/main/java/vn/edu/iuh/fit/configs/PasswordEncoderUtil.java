package vn.edu.iuh.fit.configs;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Mật khẩu cần mã hóa
        String rawPassword = "123";

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Mật khẩu đã mã hóa: " + encodedPassword);
    }
}

