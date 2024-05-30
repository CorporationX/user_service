package school.faang.user_service.generator.password;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class UserPasswordGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();


    public String createPassword() {
        final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String DIGITS = "0123456789";
        final String SPECIAL_CHARS = "!@#$%&*()+?";
        final String ALL_CHARS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARS;
        int length = 8 + RANDOM.nextInt(5);

        StringBuilder password = new StringBuilder(length);

        password.append(getRandomChar(LOWERCASE));
        password.append(getRandomChar(UPPERCASE));
        password.append(getRandomChar(DIGITS));
        password.append(getRandomChar(SPECIAL_CHARS));

        for (int i = 4; i < length; i++) {
            password.append(getRandomChar(ALL_CHARS));
        }

        return shuffleString(password.toString());
    }

    private char getRandomChar(String charSet) {
        return charSet.charAt(RANDOM.nextInt(charSet.length()));
    }

    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = RANDOM.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}
