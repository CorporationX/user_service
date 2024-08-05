package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.properties.AvatarProperties;
import school.faang.user_service.properties.DiceBearProperties;
import school.faang.user_service.service.aws.AmazonS3Service;
import school.faang.user_service.service.image.ImageService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {

    @InjectMocks
    private AvatarService avatarService;

    @Mock
    AmazonS3Service amazonS3Service;

    @Mock
    ImageService imageService;

    @Mock
    DiceBearProperties diceBearProperties;

    @Mock
    AvatarProperties avatarProperties;

    private List<String> styles;
    private String urlPattern;
    private String fileType;
    private String normalAvatarPattern;
    private String smallAvatarPattern;
    private int smallAvatarSize;
    private User user;
    byte[] avatar;
    byte[] smallAvatar;

    @BeforeEach
    void setup() {
        styles = List.of("Style1", "Style2", "Style3");
        urlPattern = "https://example.com/avatar/%s/%d";
        fileType = "png";
        smallAvatarSize = 50;
        normalAvatarPattern = "avatar_%d.png";
        smallAvatarPattern = "small_avatar_%d.png";

        user = new User();
        user.setId(1L);
        avatar = new byte[smallAvatarSize];
        smallAvatar = new byte[smallAvatarSize];
    }

    @Test
    void testSetRandomAvatar() {
        when(diceBearProperties.getStyles()).thenReturn(styles);
        when(diceBearProperties.getUrlPattern()).thenReturn(urlPattern);
        when(diceBearProperties.getFileType()).thenReturn(fileType);
        when(avatarProperties.getSmallSize()).thenReturn(smallAvatarSize);
        when(avatarProperties.getNormalPattern()).thenReturn(normalAvatarPattern);
        when(avatarProperties.getSmallPattern()).thenReturn(smallAvatarPattern);
        when(imageService.fetchImage(anyString())).thenReturn(avatar);
        when(imageService.resizeImage(any(byte[].class), anyInt(), anyInt(), anyString())).thenReturn(smallAvatar);

        avatarService.setRandomAvatar(user);

        verify(imageService).fetchImage(anyString());
        verify(imageService).resizeImage(any(byte[].class), anyInt(), anyInt(), anyString());
        verify(amazonS3Service, times(2)).uploadFile(any(byte[].class), any(), anyString(), any());

        assertEquals("avatar_1.png", user.getUserProfilePic().getFileId());
        assertEquals("small_avatar_1.png", user.getUserProfilePic().getSmallFileId());
    }

}
