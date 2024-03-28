package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserValidator;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Value("${dicebear.avatar}")
    private String avatarUrl;
    @Value("${dicebear.small_avatar}")
    private String smallAvatarUrl;

    @Test
    void testCreateUserSuccess() {
        UserDto userDto = new UserDto();
        User user = new User();

        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        assertDoesNotThrow(() -> userService.create(userDto));

        verify(userValidator).validatePassword(userDto);
        verify(userMapper).toEntity(userDto);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void testCreateUserFailure() {
        UserDto userDto = null;

        assertThrows(NullPointerException.class, () -> userService.create(userDto));

    }
}

