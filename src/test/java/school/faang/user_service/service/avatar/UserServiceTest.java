package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.AvatarService;
import school.faang.user_service.service.S3Service;
import school.faang.user_service.service.UserService;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class UserServiceTest {

    private UserRepository userRepository;
    private AvatarService avatarService;
    private S3Service s3Service;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        avatarService = Mockito.mock(AvatarService.class);
        s3Service = Mockito.mock(S3Service.class);
        userService = new UserService(userRepository, avatarService, s3Service);
    }

    @Test
    void testCreateUserWithAvatar_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        when(userRepository.save(any())).thenReturn(user);
        when(s3Service.generateS3Url("avatars/testuser.svg"))
                .thenReturn("https://your-bucket-name.s3.us-east-1.amazonaws.com/avatars/testuser.svg");

        User result = userService.createUserWithAvatar(user);

        verify(avatarService, times(1)).generateAndSaveAvatar("testuser");
        verify(userRepository, times(2)).save(any());
        assertEquals(
                "https://your-bucket-name.s3.us-east-1.amazonaws.com/avatars/testuser.svg",
                result.getUserProfilePic().getAvatarUrl()
        );
    }

    @Test
    void testUpdateAvatar_Success() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setUserProfilePic(new UserProfilePic());

        when(userRepository.findByUsername(eq(username))).thenReturn(Optional.of(user));
        when(s3Service.generateS3Url("avatars/testuser.svg"))
                .thenReturn("https://your-bucket-name.s3.us-east-1.amazonaws.com/avatars/testuser.svg");

        String result = userService.updateAvatar(username);

        verify(avatarService, times(1)).generateAndSaveAvatar(username);
        verify(userRepository, times(1)).save(user);
        assertEquals(
                "https://your-bucket-name.s3.us-east-1.amazonaws.com/avatars/testuser.svg",
                result
        );
    }

    @Test
    void testUpdateAvatar_UserNotFound() {
        String username = "unknownuser";

        when(userRepository.findByUsername(eq(username))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.updateAvatar(username));
    }
}