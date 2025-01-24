package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.AvatarDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.AvatarService;
import school.faang.user_service.service.UserService;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class UserServiceTest {

    private UserRepository userRepository;
    private AvatarService avatarService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        avatarService = Mockito.mock(AvatarService.class);
        userService = new UserService(userRepository, avatarService);
    }

    @Test
    void testCreateUserWithAvatar_Success() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("testuser@example.com");

        User user = new User();
        user.setId(1L);
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        when(userRepository.save(any())).thenReturn(user);
        when(avatarService.generateAndSaveAvatar(eq(userDto.getUsername()))).thenReturn("https://s3.amazonaws.com/avatars/testuser.svg");

        User result = userService.createUserWithAvatar(userDto);

        assertNotNull(result);
        assertEquals("https://s3.amazonaws.com/avatars/testuser.svg", result.getUserProfilePic().getAvatarUrl());
        verify(userRepository, times(2)).save(any());
    }

    @Test
    void testUpdateAvatar_Success() {
        String username = "testuser";
        String newAvatarUrl = "https://s3.amazonaws.com/avatars/testuser-new.svg";

        User user = new User();
        user.setUsername(username);
        user.setUserProfilePic(new UserProfilePic());

        when(userRepository.findByUsername(eq(username))).thenReturn(Optional.of(user));
        when(avatarService.generateAndSaveAvatar(eq(username))).thenReturn(newAvatarUrl);

        AvatarDto result = userService.updateAvatar(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(newAvatarUrl, result.getAvatarUrl());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateAvatar_UserNotFound() {
        String username = "unknownuser";

        when(userRepository.findByUsername(eq(username))).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.updateAvatar(username));
        assertEquals("User not found: unknownuser", exception.getMessage());
    }
}