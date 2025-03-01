package school.faang.user_service.service.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.FileSizeException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UserAvatarServiceTest {

    @InjectMocks private UserAvatarService userAvatarService;

    @Mock private UserRepository userRepository;

    @Mock private AmazonS3 s3Client;

    @Mock private MultipartFile multipartFile;

    private User testUser;
    private byte[] validImageBytes;

    @BeforeEach
    void setUp() throws IOException {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserProfilePic(new UserProfilePic("largeKey", "smallKey"));

        ReflectionTestUtils.setField(userAvatarService, "bucketName", "bucketName");
        ReflectionTestUtils.setField(userAvatarService, "maxFileSize", 5 * 1024 * 1024L);
        ReflectionTestUtils.setField(userAvatarService, "largePhotoSize", 1080);
        ReflectionTestUtils.setField(userAvatarService, "smallPhotoSize", 170);

        BufferedImage dummyImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "jpg", baos);
        validImageBytes = baos.toByteArray();
    }

    @Test
    @DisplayName("Test uploading avatar successfully")
    void uploadAvatar_success() throws Exception {
        testUser.setUserProfilePic(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getInputStream())
                .thenAnswer(invocation -> new ByteArrayInputStream(validImageBytes));

        userAvatarService.uploadAvatar(1L, multipartFile);

        verify(s3Client, times(2)).putObject(any(), any(), any(), any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test uploading avatar with file size exceeding the limit")
    void uploadAvatar_fileTooLarge() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(6 * 1024 * 1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        try {
            userAvatarService.uploadAvatar(1L, multipartFile);
            throw new AssertionError("Expected FileSizeException was not thrown");
        } catch (FileSizeException e) {
            assert e.getMessage().equals("File size exceeds limit");
        }
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test downloading avatar successfully")
    void downloadAvatar_success() throws IOException {
        String fileKey = "largeKey";
        byte[] mockFile = "mockFileContent".getBytes();
        S3Object mockS3Object = mock(S3Object.class);
        S3ObjectInputStream mockInputStream =
                new S3ObjectInputStream(new ByteArrayInputStream(mockFile), null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(s3Client.getObject("bucketName", fileKey)).thenReturn(mockS3Object);
        when(mockS3Object.getObjectContent()).thenReturn(mockInputStream);

        byte[] result = userAvatarService.downloadAvatar(1L, false);

        assert result.length == mockFile.length;
        verify(s3Client, times(1)).getObject((String) any(), any());
    }

    @Test
    @DisplayName("Test downloading avatar when user is not found")
    void downloadAvatar_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            userAvatarService.downloadAvatar(1L, false);
            throw new AssertionError("Expected UserNotFoundException was not thrown");
        } catch (UserNotFoundException e) {
            assert e.getMessage().equals("User not found");
        }
    }

    @Test
    @DisplayName("Test deleting avatar successfully")
    void deleteAvatar_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userAvatarService.deleteAvatar(1L);

        verify(s3Client, times(2)).deleteObject(any(), any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test deleting avatar when user is not found")
    void deleteAvatar_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            userAvatarService.deleteAvatar(1L);
            throw new AssertionError("Expected UserNotFoundException was not thrown");
        } catch (UserNotFoundException e) {
            assert e.getMessage().equals("User not found");
        }
    }

    @Test
    @DisplayName("Test deleting avatar when avatar is not found")
    void deleteAvatar_avatarNotFound() {
        User userWithoutAvatar = new User();
        userWithoutAvatar.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithoutAvatar));

        userAvatarService.deleteAvatar(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any());
    }
}
