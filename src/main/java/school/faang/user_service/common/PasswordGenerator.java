package school.faang.user_service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class PasswordGenerator {

    private final Random random;

    @Value("${user.password.length}")
    private int passwordLength;

    public String generatePassword(String keyWord) {
        String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "0123456789" +
                "!@#$%^&*()-_+=<>?/{}[]|\\:;\"',.";
        StringBuilder password = new StringBuilder();
        if (keyWord.length() < passwordLength) {
            password.append(keyWord);
        } else {
            password.append(keyWord, 0, passwordLength);
        }
        for (int i = keyWord.length(); i <= passwordLength; i++) {
            password.append(symbols.charAt(random.nextInt(symbols.length())));
        }
        return Base64.getEncoder().encodeToString(password.toString().getBytes());
    }
}
