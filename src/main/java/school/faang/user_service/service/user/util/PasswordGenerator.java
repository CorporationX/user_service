package school.faang.user_service.service.user.util;

import java.util.Random;

public class PasswordGenerator {

    private static final String COMBINATION = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()_+{}:<>?";

    public static String generate() {
        int length = 10;
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(COMBINATION.charAt(random.nextInt(COMBINATION.length())));
        }
        return password.toString();
    }
}
