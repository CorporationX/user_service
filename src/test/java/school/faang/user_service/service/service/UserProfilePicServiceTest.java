package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.AmazonS3Service;
import school.faang.user_service.service.user.UserProfilePicService;
import school.faang.user_service.service.user.UserService;

@ExtendWith(MockitoExtension.class)
public class UserProfilePicServiceTest {

    @Mock
    private AmazonS3Service s3Service;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserProfilePicService userProfilePicService;

    @Test
    public void uploadProfilePicTest() {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        User user = new User();
        user.setId(1L);
        userProfilePicService.uploadProfilePic(mockFile, user.getId());
        Mockito.verify(s3Service, Mockito.times(1)).uploadProfilePic(mockFile, user.getId());
    }
}
