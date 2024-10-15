package school.faang.user_service.service.avatar;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.client.DefaultAvatarClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {
    private String prefixFileName = "default_avatar_for_user_";

    @InjectMocks
    private AvatarService service;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DefaultAvatarClient defaultAvatarClient;
    @Mock
    private S3Service s3Service;
    private final long userId = 1L;
    @Captor
    ArgumentCaptor<User> userCaptor;

    @Test
    public void createDefaultAvatarForUser_UserNotExist() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class,
                () -> service.createDefaultAvatarForUser(userId));
        Assertions.assertEquals(exception.getMessage(), String.format("User with id = %d has not in system", userId));
    }

    @Test
    public void createDefaultAvatarForUser_UserAlreadyHaveAvatar() {
        // Arrange
        User user = new User();
        user.setUserProfilePic(new UserProfilePic());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class,
                () -> service.createDefaultAvatarForUser(userId));
        Assertions.assertEquals("User with id = %d already has an avatar".formatted(userId), exception.getMessage());
    }

    @Test
    public void createDefaultAvatarForUser() {
        // Arrange
        User user = User.builder().id(1L).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        byte[] file = {1, 2, 3};
        ResponseEntity<byte[]> fileResponse = ResponseEntity.of(Optional.of(file));
        when(defaultAvatarClient.getAvatar(any(), any(), any())).thenReturn(fileResponse);
        ReflectionTestUtils.setField(service, "prefixFileName", prefixFileName);

        // Act and Assert
        service.createDefaultAvatarForUser(userId);
        verify(userRepository, times(1)).save(userCaptor.capture());
        UserProfilePic userPic = userCaptor.getValue().getUserProfilePic();
        String filedId = userPic.getFileId();
        filedId = filedId.substring(0, StringUtils.ordinalIndexOf(filedId, "_", 5) + 1);
        String smallFileId = userPic.getSmallFileId();
        smallFileId = smallFileId.substring(0, StringUtils.ordinalIndexOf(smallFileId, "_", 5) + 1);
        String key = prefixFileName + userId + "_";
        Assertions.assertEquals(key, filedId);
        Assertions.assertEquals(key, smallFileId);
    }
}
