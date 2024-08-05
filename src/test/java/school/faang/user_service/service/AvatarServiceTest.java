package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.AmazonCredentials;
import school.faang.user_service.config.StyleAvatarConfig;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class AvatarServiceTest {

    private final String fullPostfix = "/svg?seed=";
    private final String smallPostfix = "/svg?size=32&seed=";
    private final String url = "https://api.dicebear.com/9.x/";
    private final String avatarPattern = "avatar_%d.jpeg";
    private final String smallAvatarPattern = "small_avatar_%d.jpeg";


    @Mock
    private AmazonCredentials amazonCredentials =
            new AmazonCredentials(String.valueOf(1), String.valueOf(2), String.valueOf(3), String.valueOf(4));
    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private StyleAvatarConfig styleAvatarConfig;

    @Mock
    private RestTemplateService restTemplateService;

    @InjectMocks
    private AvatarService avatarService;

    private User user;

    @BeforeEach
    public void init() {
        Country country = Country.builder()
                .title("country")
                .build();

        user = User.builder()
                .id(1L)
                .username("test user")
                .email("user@email.com")
                .phone("+79211234567")
                .country(country)
                .password("abracadabra")
                .build();

        avatarService.setFullPostfix(fullPostfix);
        avatarService.setSmallPostfix(smallPostfix);
        avatarService.setUrl(url);
        avatarService.setAvatarPattern(avatarPattern);
        avatarService.setSmallAvatarPattern(smallAvatarPattern);
    }

    @Test
    @DisplayName("Create true url for avatar")
    public void test() {

        String expectedFullUrl = "avatar_1.jpeg";
        String expectedSmallUrl = "small_avatar_1.jpeg";

        String fullAvatarKey = String.format(avatarPattern, user.getId());
        String smallAvatarKey = String.format(smallAvatarPattern, user.getId());

        byte[] file = new byte[1];
        when(restTemplateService.getImageBytes(any())).thenReturn(file);
        when(styleAvatarConfig.getStyles()).thenReturn(List.of("avataaars"));
        when(amazonS3Service.uploadFile(eq(fullAvatarKey), eq(file))).thenReturn(fullAvatarKey);
        when(amazonS3Service.uploadFile(eq(smallAvatarKey), eq(file))).thenReturn(smallAvatarKey);

        avatarService.setDefaultUserAvatar(user);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(user.getUserProfilePic()),
                () -> Assertions.assertEquals(expectedFullUrl, user.getUserProfilePic().getFileId()),
                () -> Assertions.assertEquals(expectedSmallUrl, user.getUserProfilePic().getSmallFileId())
        );
    }
}
