package school.faang.user_service.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

@Component
@Getter
public class PasswordGeneration {
    private final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    private final String NUMBERS = "0123456789";
    private final String SET = UPPER + LOWER + NUMBERS;
    private final Supplier<String> password = () -> {
        int size = new Random().nextInt(10, 20);
        char[] chars = new char[size];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = SET.charAt(new Random().nextInt(SET.length()));
        }
        return new String(chars);
    };
}
