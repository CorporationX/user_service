package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private UserValidator userValidator = new UserValidator();
    private UserDto userDto = new UserDto();

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .username("Elvis")
                .email("email")
                .password("password")
                .phone("12345")
                .countryId(4L).build();
    }

    @Test
    void validateCreateUser_whenUserNameIsNull_thenThrowDataValidationException() {
        //Arrange
        userDto.setUsername(null);
        //Assert
        assertThrows(DataValidationException.class, () -> userValidator.validateCreateUser(userDto));
    }

    @Test
    void validateCreateUser_whenEmailIsNull_thenThrowDataValidationException() {
        //Arrange
        userDto.setEmail(null);
        //Assert
        assertThrows(DataValidationException.class, () -> userValidator.validateCreateUser(userDto));
    }

    @Test
    void validateCreateUser_whenPasswordIsNull_thenThrowDataValidationException() {
        //Arrange
        userDto.setPassword(null);
        //Assert
        assertThrows(DataValidationException.class, () -> userValidator.validateCreateUser(userDto));
    }

    @Test
    void validateCreateUser_whenCountryIdIsNull_thenThrowDataValidationException() {
        //Arrange
        userDto.setCountryId(null);
        //Assert
        assertThrows(DataValidationException.class, () -> userValidator.validateCreateUser(userDto));
    }
}