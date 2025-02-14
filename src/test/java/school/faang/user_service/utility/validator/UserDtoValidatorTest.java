package school.faang.user_service.utility.validator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;

class UserDtoValidatorTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    UserDtoValidator userDtoValidator = new UserDtoValidator(validator);

    @Test
    void validate() {
        UserDto userDto = new UserDto(1L, "user", "user@mail.com",
                "54361", "blablabla", "Russia", "Pupok",
                2, "20.20.20");
        Assertions.assertTrue(userDtoValidator.validate(userDto));
    }

    @Test
    void validateEmailIsEmpty() {
        UserDto dto = new UserDto(1L, "user", "",
                "54361", "blablabla", "Russia", "Pupok",
                2, "20.20.20");
        String exceptionMessage = "email is not correct";
        Assertions.assertThrows(DataValidationException.class, () -> userDtoValidator.validate(dto), exceptionMessage);
    }

    @Test
    void validateEmailIsNotCorrect() {
        UserDto dto = new UserDto(1L, "user", "usermail.com",
                "54361", "blablabla", "Russia", "Pupok",
                2, "20.20.20");
        String exceptionMessage = "must be a well-formed email address";
        Assertions.assertThrows(DataValidationException.class, () -> userDtoValidator.validate(dto), exceptionMessage);
    }

    @Test
    void validateEmailIsNull() {
        UserDto dto = new UserDto(1L, "user", null,
                "54361", "blablabla", "Russia", "Pupok",
                2, "20.20.20");
        String exceptionMessage = "email must not be null";
        Assertions.assertThrows(DataValidationException.class, () -> userDtoValidator.validate(dto), exceptionMessage);
    }

    @Test
    void validateUserNameIsEmpty() {
        UserDto dto = new UserDto(1L, "", "user@mail.com",
                "54361", "blablabla", "Russia", "Pupok",
                2, "20.20.20");
        String exceptionMessage = "username must not be empty";
        Assertions.assertThrows(DataValidationException.class, () -> userDtoValidator.validate(dto), exceptionMessage);
    }

    @Test
    void validateUserNameIsNull() {
        UserDto dto = new UserDto(1L, null, "user@mail.com",
                "54361", "blablabla", "Russia", "Pupok",
                2, "20.20.20");
        String exceptionMessage = "username must not be null";
        Assertions.assertThrows(DataValidationException.class, () -> userDtoValidator.validate(dto), exceptionMessage);
    }
}