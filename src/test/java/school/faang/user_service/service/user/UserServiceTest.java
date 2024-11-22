package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.s3.S3Service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final String USER_NOT_FOUNT_MESSAGE = "User not found";

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private AvatarService avatarService;
    @Mock
    private S3Service s3Service;
    @InjectMocks
    private UserService userService;
    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void testGenerateRandomAvatarGenerated() {
        Long userId = 1L;
        String avatar = "<svg>...</svg>";
        User user = new User();
        String updatedKey = "updatedKey";
        user.setId(userId);
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(avatarService.generateRandomAvatar()).thenReturn(avatar);
        when(s3Service.saveSvg(eq(avatar), any(String.class))).thenReturn(updatedKey);
        String result = userService.generateRandomAvatar();
        assertNotNull(result);
        verify(s3Service).saveSvg(eq(avatar), any(String.class));
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertNotNull(capturedUser.getUserProfilePic());
        assertEquals(updatedKey, capturedUser.getUserProfilePic().getFileId());
        assertEquals(updatedKey, result);
    }

    @Test
    void testGenerateRandomAvatarUserNotFound() {
        Long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.generateRandomAvatar();
        });
        assertEquals(USER_NOT_FOUNT_MESSAGE, exception.getMessage());
        verify(avatarService, never()).generateRandomAvatar();
        verify(userRepository, never()).save(any());
    }
}