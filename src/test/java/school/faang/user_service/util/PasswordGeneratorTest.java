package school.faang.user_service.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    private PasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new PasswordGenerator();
    }

    @Test
    void generatePassword_shouldGenerateAtLeastTwoUpperCaseChars() {
        String password = passwordGenerator.generatePassword();
        int upperCaseCharsCount = 0;
        for (char c : password.toCharArray()) {
            if (c >= 65 && c <= 90) {
                upperCaseCharsCount++;
            }
        }
        assertTrue(upperCaseCharsCount >= 2);

    }

    @Test
    void generatePassword_shouldGenerateAtLeastTwoLowerCaseChars() {
        String password = passwordGenerator.generatePassword();
        int lowerCaseCharsCount = 0;
        for (char c : password.toCharArray()) {
            if (c >= 97 && c <= 122) {
                lowerCaseCharsCount++;
            }
        }
        assertTrue(lowerCaseCharsCount >= 2);
    }

    @Test
    void generatePassword_shouldGenerateAtLeastTwoNumbers() {
        String password = passwordGenerator.generatePassword();
        int numCount = 0;
        for (char c : password.toCharArray()) {
            if (c >= 48 && c <= 57) {
                numCount++;
            }
        }
        assertTrue(numCount >= 2);
    }

    @Test
    public void generatePassword_shouldGenerateExactlyTwoSpecialChars() {
        String password = passwordGenerator.generatePassword();
        int specialCharCount = 0;
        for (char c : password.toCharArray()) {
            if (c >= 33 && c <= 47) {
                specialCharCount++;
            }
        }
        assertEquals(2, specialCharCount);
    }

    @Test
    public void generatePassword_shouldGenerateExactlyTenSymbolsLengthPassword() {
        String password = passwordGenerator.generatePassword();
        assertEquals(10, password.length());
    }
}