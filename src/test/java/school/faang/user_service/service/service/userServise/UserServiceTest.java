package school.faang.user_service.service.service.userServise;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldgetUserById() {
        User user = new User();
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(userId);

        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionForInvalidId() {
        long userId = 0L;
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(userId));
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(userId));
    }
}