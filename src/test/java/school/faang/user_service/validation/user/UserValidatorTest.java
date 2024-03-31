package school.faang.user_service.validation.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.handler.exception.DataValidationException;
import school.faang.user_service.validator.user.UserValidator;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {
    private UserValidator userValidator;
    @BeforeEach
    void setUp(){
        userValidator = new UserValidator();
    }
    @Test
    void test_ValidatePassword_IsShort(){
        UserDto userDto = new UserDto();
        userDto.setPassword("qweasdz");

        Assertions.assertThrows(DataValidationException.class, () -> userValidator.validatePassword(userDto));
    }

    @Test
    void test_ValidatePassword_DontHaveUppercaseSymbol(){
        UserDto userDto = new UserDto();
        userDto.setPassword("qweasdzxc");

        Assertions.assertThrows(DataValidationException.class, () -> userValidator.validatePassword(userDto));
    }

    @Test
    void test_ValidatePassword_DontHaveDigitSymbol(){
        UserDto userDto = new UserDto();
        userDto.setPassword("Qweasdzxc");

        Assertions.assertThrows(DataValidationException.class, () -> userValidator.validatePassword(userDto));
    }

    @Test
    void test_ValidatePassword_DontHaveSpecialSymbol(){
        UserDto userDto = new UserDto();
        userDto.setPassword("Qweasdzxc1");

        Assertions.assertThrows(DataValidationException.class, () -> userValidator.validatePassword(userDto));
    }
}
