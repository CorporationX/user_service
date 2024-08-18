package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    private long userId;
    private User user;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        user = User.builder()
                .id(userId)
                .build();
    }

    @Test
    @DisplayName("testing doAllUsersExist method with non appropriate value")
    public void testDoAllUsersExistWithNonAppropriateValue() {
        when(userRepository.findAllById(List.of(userId))).thenReturn(List.of());
        assertFalse(() -> userValidator.doAllUsersExist(List.of(userId)));
    }

    @Test
    @DisplayName("testing doAllUsersExist method with appropriate value")
    public void testDoAllUsersExistWithAppropriateValue() {
        when(userRepository.findAllById(List.of(userId))).thenReturn(List.of(user));
        assertTrue(() -> userValidator.doAllUsersExist(List.of(userId)));
    }
}
