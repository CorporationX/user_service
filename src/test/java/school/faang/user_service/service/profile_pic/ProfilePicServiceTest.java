package school.faang.user_service.service.profile_pic;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.mapper.user_profile_pic.UserProfilePicMapper;
import school.faang.user_service.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfilePicServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AmazonS3 s3Client;
    @Mock
    private UserProfilePicMapper userProfilePicMapper;

    @InjectMocks
    private ProfilePicService profilePicService;

    @Test
    public void testUploadAvatarUserNotFound() {
        MultipartFile file = null;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> profilePicService.uploadAvatar(userId, file));
    }

    @Test
    public void testUploadAvatarSuccess() throws IOException {
        Long userId = 1L;
        User user = new User();
        BufferedImage img = new BufferedImage(
                10,
                10,
                BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        outputStream.close();
        MockMultipartFile file = new MockMultipartFile("image.jpg", "image.jpg", "image/jpg", imageBytes);
        String uniqueSmallPicName = file.getOriginalFilename() + "_small" + System.currentTimeMillis();
        String uniqueLargePicName = file.getOriginalFilename() + "_large" + System.currentTimeMillis();
        UserProfilePic userProfilePic = new UserProfilePic(uniqueSmallPicName, uniqueLargePicName);
        user.setUserProfilePic(userProfilePic);
        ReflectionTestUtils.setField(profilePicService, "small_photo", 170);
        ReflectionTestUtils.setField(profilePicService, "large_photo", 1080);
        ReflectionTestUtils.setField(profilePicService, "s3BucketName", "user-service-bucket");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProfilePicMapper.toDto(any(UserProfilePic.class))).thenReturn(any(UserProfilePicDto.class));

        assertDoesNotThrow(() -> profilePicService.uploadAvatar(userId, file));
        verify(userRepository).findById(userId);
        verify(userProfilePicMapper).toDto(any(UserProfilePic.class));
    }
    @Test
    public void testDeleteAvatarSuccess(){
        Long userId = 1L;
        User userBeforeDeletePhoto = new User();
        User userAfterDeletePhoto = new User();
        String uniqueSmallPicName = "small" + System.currentTimeMillis();
        String uniqueLargePicName = "large" + System.currentTimeMillis();
        UserProfilePic userProfilePicBefore = new UserProfilePic(uniqueLargePicName, uniqueSmallPicName);
        UserProfilePic userProfilePicAfter = new UserProfilePic(null, null);
        userBeforeDeletePhoto.setUserProfilePic(userProfilePicBefore);
        userAfterDeletePhoto.setUserProfilePic(userProfilePicAfter);
        ReflectionTestUtils.setField(profilePicService, "s3BucketName", "user-service-bucket");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userBeforeDeletePhoto));
        when(userRepository.save(userAfterDeletePhoto)).thenReturn(userAfterDeletePhoto);

        assertDoesNotThrow(() -> profilePicService.deleteAvatar(userId));
        verify(userRepository).findById(userId);
        verify(s3Client, times(2)).deleteObject(anyString(), anyString());
        verify(userRepository).save(userAfterDeletePhoto);
    }

    @Test
    void test_DownloadAvatarLarge_Success() throws IOException {
        Long userId = 1L;
        User user = new User();
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream s3ObjectInputStream = mock(S3ObjectInputStream.class);
        String uniqueSmallPicName = "small" + System.currentTimeMillis();
        String uniqueLargePicName = "large" + System.currentTimeMillis();
        UserProfilePic userProfilePic = new UserProfilePic(uniqueLargePicName, uniqueSmallPicName);
        user.setUserProfilePic(userProfilePic);
        ReflectionTestUtils.setField(profilePicService, "s3BucketName", "user-service-bucket");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Client.getObject(anyString(), eq(uniqueLargePicName))).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        when(s3ObjectInputStream.readAllBytes()).thenReturn(new byte[]{});

        assertDoesNotThrow(() -> profilePicService.downloadAvatarLarge(userId));
        verify(userRepository).findById(userId);
        verify(s3Client).getObject(anyString(), anyString());
    }

    @Test
    void test_DownloadAvatarSmall_Success() throws IOException {
        Long userId = 1L;
        User user = new User();
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream s3ObjectInputStream = mock(S3ObjectInputStream.class);
        String uniqueSmallPicName = "small" + System.currentTimeMillis();
        String uniqueLargePicName = "large" + System.currentTimeMillis();
        UserProfilePic userProfilePic = new UserProfilePic(uniqueLargePicName, uniqueSmallPicName);
        user.setUserProfilePic(userProfilePic);
        ReflectionTestUtils.setField(profilePicService, "s3BucketName", "user-service-bucket");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Client.getObject(anyString(), eq(uniqueSmallPicName))).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        when(s3ObjectInputStream.readAllBytes()).thenReturn(new byte[]{});

        assertDoesNotThrow(() -> profilePicService.downloadAvatarSmall(userId));
        verify(userRepository).findById(userId);
        verify(s3Client).getObject(anyString(), anyString());
    }
}
