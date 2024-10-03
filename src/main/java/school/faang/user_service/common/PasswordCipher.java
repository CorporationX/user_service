package school.faang.user_service.common;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class PasswordCipher {

    public String encryptPassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }
}
