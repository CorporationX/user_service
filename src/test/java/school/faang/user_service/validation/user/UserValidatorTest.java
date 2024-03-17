package school.faang.user_service.validation.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;
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

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("ValidUsername")
                .phone("+79123456789")
                .active(true)
                .build();
    }

    @Test
    void validateUserExistsById_UserExists_ShouldNotThrow() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() ->
                userValidator.validateIfUserExistsById(5L));
    }

    @Test
    void validateUserExistsById_UserDoesntExist_ShouldThrowNoSuchElementException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () ->
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
