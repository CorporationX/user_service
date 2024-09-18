package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final Long USER_ID = 1L;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
    }

    @Nested
    @DisplayName("Get User by ID Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Returns user when user exists")
        void whenUserExistsThenReturnUser() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

            User result = userService.getUserById(USER_ID);

            verify(userRepository).findById(USER_ID);
            assertEquals(user, result, "The returned user should match the mock user");
        }

        @Test
        @DisplayName("Throws NotFoundException when user does not exist")
        void whenUserDoesNotExistThenThrowNotFoundException() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.getUserById(USER_ID));
            verify(userRepository).findById(USER_ID);
        }
    }
}
