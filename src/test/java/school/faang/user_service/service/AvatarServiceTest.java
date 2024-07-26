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
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class AvatarServiceTest {

    private final String fullAvatarUrl = "https://api.dicebear.com/9.x/avataaars/svg?seed=";
    private final String smallAvatarUrl = "https://api.dicebear.com/9.x/avataaars/svg?size=32&seed=";


    @Mock
    private AmazonCredentials amazonCredentials =
            new AmazonCredentials(String.valueOf(1), String.valueOf(2), String.valueOf(3));
    @Mock
    private AmazonS3Service amazonS3Service;


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
                .username("test user")
                .email("user@email.com")
                .phone("+79211234567")
                .country(country)
                .password("abracadabra")
                .build();

        avatarService.setFullAvatarUrl(fullAvatarUrl);
        avatarService.setSmallAvatarUrl(smallAvatarUrl);
    }

    @Test
    @DisplayName("Create true url for avatar")
    public void test() {

        String expectedFullUrl = fullAvatarUrl + user.hashCode();
        String expectedSmallUrl = smallAvatarUrl + user.hashCode();

        byte[] file = new byte[1];
        when(restTemplateService.getImageBytes(any())).thenReturn(file);
        when(amazonS3Service.uploadFile(eq(expectedFullUrl), eq(file))).thenReturn(expectedFullUrl);
        when(amazonS3Service.uploadFile(eq(expectedSmallUrl), eq(file))).thenReturn(expectedSmallUrl);

        avatarService.setDefaultUserAvatar(user);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(user.getUserProfilePic()),
                () -> Assertions.assertEquals(expectedFullUrl, user.getUserProfilePic().getFileId()),
                () -> Assertions.assertEquals(expectedSmallUrl, user.getUserProfilePic().getSmallFileId())
        );
    }
}
