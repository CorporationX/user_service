package school.faang.user_service.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    private static final long USER_ID = 1;

    @Test
    @DisplayName("Ошибка если пользователя нет в БД")
    public void testCheckIfUserIsNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> userValidator.checkIfUserIsExist(USER_ID));
    }

    @Test
    @DisplayName("Успех если пользователь есть в БД")
    public void testCheckIfUserIsExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(new User()));

        userValidator.checkIfUserIsExist(USER_ID);

        verify(userRepository, times(2)).findById(USER_ID);
    }
}