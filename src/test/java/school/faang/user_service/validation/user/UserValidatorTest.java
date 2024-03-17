package school.faang.user_service.validation.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.PasswordValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserValidator userValidator;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("ValidUsername")
                .email("valid@email.ru")
                .phone("+79123456789")
                .password("val1dp@ssworD")
                .active(true)
                .build();
        userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .password(user.getPassword())
                .active(user.isActive())
                .build();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    void validateUserDtoFields_InvalidUsername_ShouldThrowDataValidationException(String blankUsername) {
        userDto.setUsername(blankUsername);

        assertThrows(DataValidationException.class, () ->
                userValidator.validateUserDtoFields(userDto));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    void validateUserDtoFields_InvalidEmail_ShouldThrowDataValidationException(String blankEmail) {
        userDto.setEmail(blankEmail);

        assertThrows(DataValidationException.class, () ->
                userValidator.validateUserDtoFields(userDto));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    void validateUserDtoFields_InvalidPhone_ShouldThrowDataValidationException(String blankPhone) {
        userDto.setPhone(blankPhone);

        assertThrows(DataValidationException.class, () ->
                userValidator.validateUserDtoFields(userDto));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    void validateUserDtoFields_InvalidFields_ShouldThrowDataValidationException(String blankPassword) {
        userDto.setPassword(blankPassword);

        assertThrows(DataValidationException.class, () ->
                userValidator.validateUserDtoFields(userDto));
    }

    @Test
    void validatePassword_ValidPassword_ShouldNotThrow() {
        assertDoesNotThrow(() -> userValidator.validatePassword(userDto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalidpassword", "Invalidpassword", "invp", "12121212", "^sju9poossd", "NONONONONO1111"})
    void validatePassword_InvalidPassword_ShouldThrowPasswordValidationException(String invalidPassword) {
        userDto.setPassword(invalidPassword);

        assertThrows(PasswordValidationException.class, () ->
                userValidator.validatePassword(userDto));
    }

    @Test
    void validateUserExistsById_UserExists_ShouldNotThrow() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() ->
                userValidator.validateIfUserExistsById(5L));
    }

    @Test
    void validateUserExistsById_UserDoesntExist_ShouldThrowEntityNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                userValidator.validateIfUserExistsById(666L));
    }

    @Test
    void validateIfUserIsActive_UserIsActive_ShouldNotThrow() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        assertDoesNotThrow(() ->
                userValidator.validateIfUserIsActive(1L));
    }

    @Test
    void validateIfUserIsActive_UserIsNotActive_ShouldThrowDataValidationException() {
        user.setActive(false);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        assertThrows(DataValidationException.class, () ->
                userValidator.validateIfUserIsActive(1L));
    }
}
