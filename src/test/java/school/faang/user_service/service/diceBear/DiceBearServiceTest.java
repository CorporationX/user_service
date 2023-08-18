package school.faang.user_service.service.diceBear;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.amazon.AvatarService;

@ExtendWith(MockitoExtension.class)
class DiceBearServiceTest {
    @InjectMocks
    private DiceBearService diceBearService;
    @Mock
    private AvatarService avatarService;

    @Test
    public void testCreateAvatar() {
        UserProfilePic userProfilePic = UserProfilePic.builder().build();

        Mockito.verify(avatarService, Mockito.times(1))
                .saveToAmazonS3(userProfilePic);
    }
}