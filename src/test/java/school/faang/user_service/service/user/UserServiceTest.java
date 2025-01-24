package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetUserSuccess() {
        long userId = 1;
        User expectedUser = User.builder()
                .id(userId)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUser(userId);

        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserThrowExceptionWhenNotFound() {
        long userId = 1;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.getUser(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserByIds() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<User> expectedUserList = ids.stream()
                .map(id -> User.builder()
                        .id(id)
                        .build()).toList();
        when(userRepository.findAllById(ids))
                .thenReturn(expectedUserList);

        List<User> actualUserList = userService.getUsersByIds(ids);

        assertEquals(expectedUserList, actualUserList);
        verify(userRepository, times(1)).findAllById(ids);
    }

    @Test
    public void testUserExists() {
        Long userId = 1L;
        boolean expectedResult = true;

        when(userRepository.existsById(userId))
                .thenReturn(expectedResult);

        boolean actualResult = userService.userExists(userId);

        verify(userRepository, times(1))
                .existsById(eq(userId));

        assertEquals(expectedResult, actualResult);
    }
}
