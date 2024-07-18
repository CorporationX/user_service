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

import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @BeforeEach
    public void setUp() {
        userId = 1L;
    }

    @Test
    @DisplayName("testing getUser method")
    public void testGetUser() {
        User user = User.builder()
                .id(userId).build();
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
}