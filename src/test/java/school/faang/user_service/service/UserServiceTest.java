package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private long userId;
    private User user;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        user = User.builder()
                .followers(List.of(new User()))
                .id(userId).build();
    }

    @Test
    @DisplayName("testing getUser method")
    public void testGetUser() {
        when(userValidator.validateUserExistence(userId)).thenReturn(user);
        userService.getUser(userId);
        verify(userValidator, times(1)).validateUserExistence(userId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    @DisplayName("testing checkUserExistence method")
    public void testCheckUserExistence() {
        when(userRepository.existsById(userId)).thenReturn(true);
        assertTrue(userService.checkUserExistence(userId));
    }

    @Test
    @DisplayName("testing getUserFollowers method")
    public void testGetUserFollowers() {
        when(userValidator.validateUserExistence(userId)).thenReturn(user);
        userService.getUserFollowers(userId);
        verify(userValidator, times(1)).validateUserExistence(userId);
    }

    @Test
    @DisplayName("testing checkAllFollowersExist method")
    public void testCheckAllFollowersExist() {
        userService.checkAllFollowersExist(List.of(1L, 2L));
        verify(userValidator, times(1)).checkAllFollowersExist(anyList());
    }
}