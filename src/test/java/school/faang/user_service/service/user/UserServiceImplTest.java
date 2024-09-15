package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private final long userId = 1L;
    private User user;

    @Test
    void findUserById_whenUserExists_thenReturnUser() {
        // given
        user = buildUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // when
        var result = userService.findUserById(userId);
        // then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void findUserById_UserDoesNotExist_ThrowsException() {
        // given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        // when/then
        assertThrows(IllegalStateException.class, () -> userService.findUserById(userId),
                "User with ID: %d does not exist.".formatted(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    private User buildUser() {
        return User.builder()
                .id(userId)
                .build();
    }
}