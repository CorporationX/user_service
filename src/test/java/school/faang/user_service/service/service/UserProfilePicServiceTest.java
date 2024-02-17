package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.AmazonS3Service;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.user.UserProfilePicService;
import school.faang.user_service.util.ImageService;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserProfilePicServiceTest {

    @Mock
    private AmazonS3Service s3Service;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private UserProfilePicService userProfilePicService;

    @Test
    public void uploadProfilePicTest() {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        byte[] bytes = new byte[2];
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(imageService.resize(mockFile, true)).thenReturn(bytes);
        userProfilePicService.uploadProfilePic(mockFile, user.getId());
        Mockito.verify(s3Service, Mockito.times(1))
                .uploadFile(bytes, mockFile, user.getId(), "big");
    }
}
