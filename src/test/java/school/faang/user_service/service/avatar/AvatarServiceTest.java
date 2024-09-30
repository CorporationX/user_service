package school.faang.user_service.service.avatar;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.DefaultAvatarClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {
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
        Exception exception = Assertions.assertThrows(DataValidationException.class,() -> service.createDefaultAvatarForUser(userId));
        Assertions.assertEquals(exception.getMessage(), "Пользователя с id = " + userId + " нет в системе");
    }

    @Test
    public void createDefaultAvatarForUser_UserAlreadyHaveAvatar() {
        // Arrange
        User user = new User();
        user.setUserProfilePic(new UserProfilePic());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class,() -> service.createDefaultAvatarForUser(userId));
        Assertions.assertEquals(exception.getMessage(), "Пользователь с id = " + userId + " уже имеет аватар");
    }

    @Test
    public void createDefaultAvatarForUser() {
        // Arrange
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        byte[] file = {1, 2, 3};
        when(defaultAvatarClient.getAvatar(any(), any(), any())).thenReturn(file);
        String key = "default_avatar_for_user_" + userId + "_";

        // Act and Assert
        service.createDefaultAvatarForUser(userId);
        verify(userRepository, times(1)).save(userCaptor.capture());
        UserProfilePic userPic = userCaptor.getValue().getUserProfilePic();
        String filedId = userPic.getFileId();
        filedId = filedId.substring(0, StringUtils.ordinalIndexOf(filedId, "_", 5) + 1);
        String smallFileId = userPic.getSmallFileId();
        smallFileId = smallFileId.substring(0, StringUtils.ordinalIndexOf(smallFileId, "_", 5) + 1);
        Assertions.assertEquals(key, filedId);
        Assertions.assertEquals(key, smallFileId);
    }
}
