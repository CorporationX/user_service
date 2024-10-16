package school.faang.user_service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class PasswordGenerator {

    private final Random random;
    private static final String SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            + "0123456789!@#$%^&*()-_+=<>?/{}[]|\\:;\"',.";

    @Value("${user.password.length}")
    private int passwordLength;

    public String generatePassword(String keyWord) {
        StringBuilder password = new StringBuilder();
        if (keyWord.length() < passwordLength) {
            password.append(keyWord);
        } else {
            password.append(keyWord, 0, passwordLength);
        }
        for (int i = keyWord.length(); i <= passwordLength; i++) {
            password.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
        }
        return password.toString();
    }
}
