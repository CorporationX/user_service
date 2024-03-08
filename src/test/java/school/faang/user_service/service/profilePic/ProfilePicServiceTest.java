package school.faang.user_service.service.profilePic;

import com.amazonaws.services.s3.AmazonS3;
import net.coobird.thumbnailator.Thumbnails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProfilePicServiceTest {
    @InjectMocks
    private ProfilePicService profilePicService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AmazonS3 s3Client;

    @Test
    void testUserAddProfilePicSuccessful() throws IOException {
        User user = User.builder()
                .id(1L)
                .build();
        BufferedImage img = new BufferedImage(
                10,
                10,
                BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Thumbnails.of(img)
                .size(100, 100)
                .outputFormat("jpg")
                .toOutputStream(byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        MockMultipartFile file = new MockMultipartFile(
                "testFile",
                "testFile.jpg",
                "image/jpeg",
                new ByteArrayInputStream(imageBytes));

        ReflectionTestUtils.setField(profilePicService, "smallSize", 170);
        ReflectionTestUtils.setField(profilePicService, "largeSize", 1080);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserProfilePic userProfile = profilePicService.addProfilePic(1L, file);

        verify(s3Client, times(2)).putObject(any(), any(), any(), any());
        assertNotNull(userProfile);
        assertNotNull(user.getUserProfilePic());
    }
}
