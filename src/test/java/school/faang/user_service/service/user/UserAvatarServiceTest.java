package school.faang.user_service.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.properties.ProfilePicProperties;
import school.faang.user_service.config.properties.S3Properties;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.*;
import school.faang.user_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserAvatarServiceTest {

    @InjectMocks private UserAvatarService userAvatarService;

    @Mock private UserRepository userRepository;

    @Mock private AmazonS3 s3Client;

    @Mock private MultipartFile multipartFile;

    @Mock private S3Properties s3Properties;

    @Mock private ProfilePicProperties profilePicProperties;

    private User testUser;
    private byte[] validImageBytes;

    @BeforeEach
    void setUp() throws IOException {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserProfilePic(new UserProfilePic("largeKey", "smallKey"));

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        validImageBytes = baos.toByteArray();

        lenient().when(s3Properties.getBucketName()).thenReturn("test-bucket");
        lenient().when(profilePicProperties.getMaxSize()).thenReturn(5 * 1024 * 1024L);
        lenient().when(profilePicProperties.getLargePhotoSize()).thenReturn(1080);
        lenient().when(profilePicProperties.getSmallPhotoSize()).thenReturn(170);
    }

    @Test
    @DisplayName("Error when uploading avatar: file too large")
    void uploadAvatar_fileTooLarge() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(6 * 1024 * 1024L);

        FileSizeException thrown =
                assertThrows(
                        FileSizeException.class,
                        () -> {
                            userAvatarService.uploadAvatar(1L, multipartFile);
                        });

        assertEquals("File size exceeds the limit", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Error when uploading avatar: incorrect file type")
    void uploadAvatar_invalidFileType() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");

        InvalidFileTypeException thrown =
                assertThrows(
                        InvalidFileTypeException.class,
                        () -> {
                            userAvatarService.uploadAvatar(1L, multipartFile);
                        });

        assertEquals("Only images are allowed", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Successful avatar download")
    void downloadAvatar_success() {
        String fileKey = "largeKey";
        S3Object mockS3Object = mock(S3Object.class);
        S3ObjectInputStream mockInputStream =
                new S3ObjectInputStream(new ByteArrayInputStream(validImageBytes), null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(s3Client.getObject("test-bucket", fileKey)).thenReturn(mockS3Object);
        when(mockS3Object.getObjectContent()).thenReturn(mockInputStream);

        InputStreamResource result = userAvatarService.downloadLargeAvatar(1L);

        assertNotNull(result);
        verify(s3Client, times(1)).getObject("test-bucket", fileKey);
    }

    @Test
    @DisplayName("Error when downloading avatar: user not found")
    void downloadAvatar_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userAvatarService.downloadLargeAvatar(1L));
    }

    @Test
    @DisplayName("Error when downloading avatar: avatar not found")
    void downloadAvatar_avatarNotFound() {
        User userWithoutAvatar = new User();
        userWithoutAvatar.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithoutAvatar));

        assertThrows(
                AvatarNotFoundException.class, () -> userAvatarService.downloadLargeAvatar(1L));
    }

    @Test
    @DisplayName("Successful avatar deletion")
    void deleteAvatar_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(s3Client.doesObjectExist("test-bucket", "largeKey")).thenReturn(true);
        when(s3Client.doesObjectExist("test-bucket", "smallKey")).thenReturn(true);

        userAvatarService.deleteAvatar(1L);

        verify(s3Client, times(1)).deleteObject("test-bucket", "largeKey");
        verify(s3Client, times(1)).deleteObject("test-bucket", "smallKey");
        verify(userRepository, times(1)).save(any(User.class));
        assertNull(testUser.getUserProfilePic());
    }

    @Test
    @DisplayName("Error when deleting avatar: user not found")
    void deleteAvatar_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userAvatarService.deleteAvatar(1L));
    }

    @Test
    @DisplayName("Delete avatar if it does not exist")
    void deleteAvatar_avatarNotFound() {
        User userWithoutAvatar = new User();
        userWithoutAvatar.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithoutAvatar));

        userAvatarService.deleteAvatar(1L);

        verify(userRepository, never()).save(any());
        verify(s3Client, never()).deleteObject(anyString(), anyString());
    }
}
