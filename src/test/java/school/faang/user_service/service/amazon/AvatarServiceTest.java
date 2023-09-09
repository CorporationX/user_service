package school.faang.user_service.service.amazon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DiceBearApiException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {
    @InjectMocks
    private AvatarService avatarService;

    @Test
    public void testSaveToAmazonS3_Throws() throws IOException {
        UserProfilePic userProfilePic = UserProfilePic.builder()
                .fileId("fileId")
                .build();
        assertThrows(DiceBearApiException.class,
                () -> avatarService.saveToAmazonS3(userProfilePic));
    }

}