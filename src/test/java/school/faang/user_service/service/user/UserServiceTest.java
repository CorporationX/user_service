package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Throws exception when user not found")
        void whenUserNotFoundThenThrowException() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> userService.getUserById(anyLong()),
                    "User with this id does not exist in the database");
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Doesn't throws exception when user found")
        void whenUserNotFoundThenThrowException() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            assertNotNull(userService.getUserById(anyLong()));
        }
    }
}