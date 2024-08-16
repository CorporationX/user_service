package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.repository.UserRepository;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

public class UserValidatorTest {
    @InjectMocks
    UserValidator userValidator;

    @Mock
    UserRepository userRepository;

    private long userId;

    @BeforeEach
    public void setUp() {
        userId = 1L;

        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    @DisplayName("test that validateUserId throws EntityNotFoundException when there is no User with given id in the database")
//    public void testValidateUserId() {
//        when(userRepository.existsById(userId)).thenReturn(false);
//
//        assertThrows(EntityNotFoundException.class,
//                () -> userValidator.validateUserId(userId));
//    }
}
