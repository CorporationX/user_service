package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void getAllUsersByIds() {
        when(userRepository.findAllById(anyList())).thenReturn(List.of(user));

        List<User> users = userService.getAllUsersByIds(List.of(1L, 2L, 3L));

        verify(userRepository).findAllById(anyList());
        assertEquals(users, List.of(user));
    }

    @Test
    void testGetUserIfUserDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.getUser(anyLong()));
    }

    @Test
    void testGetUserIsSuccessful() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.getUser(anyLong());

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void testSaveUserIfListIsEmpty() {

        assertThrows(DataValidationException.class, () -> userService.saveUsers(null));
        verify(userRepository, times(0)).saveAll(List.of());
    }

    @Test
    void testSaveUserIsSuccessful() {

        userService.saveUsers(List.of(user));

        verify(userRepository, times(1)).saveAll(List.of(user));
    }
}