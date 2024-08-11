package school.faang.user_service.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
