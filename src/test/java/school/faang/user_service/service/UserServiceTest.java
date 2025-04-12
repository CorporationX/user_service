package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserBanDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserService userService;
    private final Long userId = 1L;

    @Test
    void testGetUser_success() {
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        UserDto userDto = new UserDto(userId, "testuser", "test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(userId);

        assertEquals(userDto, result);
        verify(userRepository).findById(userId);
        verify(userMapper).toDto(user);
    }

    @Test
    void testGetUser_notFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void testBanUser_success() {
        UserBanDto banDto = new UserBanDto();
        banDto.setUserId(userId);

        User user = new User();
        user.setId(userId);
        user.setBanned(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.banUser(banDto);

        assertTrue(user.isBanned());
        verify(userRepository).findById(userId);
    }

    @Test
    void testBanUser_notFound() {
        UserBanDto banDto = new UserBanDto();
        banDto.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.banUser(banDto));
        verify(userRepository).findById(userId);
    }
}
