package school.faang.user_service.validator;

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
class UserInDbValidatorTest {

    @InjectMocks
    private UserInDbValidator userInDbValidator;
    @Mock
    private UserRepository userRepository;

    private final long USER_ID = 1;

    @Test
    public void testCheckIfUserInDbIsEmpty() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> userInDbValidator.checkIfUserInDbIsEmpty(USER_ID));
    }

    @Test
    public void testCheckIfUserInDbIsNotEmpty() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(new User()));

        userInDbValidator.checkIfUserInDbIsEmpty(USER_ID);

        verify(userRepository, times(2)).findById(USER_ID);
    }
}