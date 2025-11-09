package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.S3.S3service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3service s3service;

    @InjectMocks
    private AvatarServiceImpl avatarService;

    private User user;
    private final long userId = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        ReflectionTestUtils.setField(avatarService, "dicebearBaseUrl", "http://test.com");
        ReflectionTestUtils.setField(avatarService, "dicebearDefaultSize", 256);
    }

    @Nested
    @DisplayName("Tests for uploadAvatar method")
    class UploadAvatarTests {

        @Test
        @DisplayName("Upload success when user has no previous avatar")
        void testUploadAvatar_NewUser_Success() throws Exception {
            // Arrange
            MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[1024]);
            BufferedImage mockImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

            when(userRepository.getByIdOrThrow(userId)).thenReturn(user); // user.getUserProfilePic() is null
            when(s3service.uploadFileToS3(any(), anyString())).thenReturn("new-file-id");

            try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
                mockedImageIO.when(() -> ImageIO.read(any(InputStream.class))).thenReturn(mockImage);

                // Act
                UserProfilePic result = avatarService.uploadAvatar(userId, file);

                // Assert
                assertNotNull(result);
                assertEquals("new-file-id", result.getFileId());
                verify(userRepository, times(1)).save(user);
                verify(s3service, times(2)).uploadFileToS3(any(), anyString());
                verify(s3service, never()).deleteFileFromS3(anyString()); // Verify old files are NOT deleted
            }
        }

        @Test
        @DisplayName("Upload success and delete old avatar when user has a previous custom avatar")
        void testUploadAvatar_WithPreviousAvatar_DeletesOldFiles() throws Exception {
            // Arrange
            UserProfilePic oldPic = new UserProfilePic();
            oldPic.setFileId("old_big_key");
            oldPic.setSmallFileId("old_small_key");
            user.setUserProfilePic(oldPic);

            MultipartFile file = new MockMultipartFile("file", "new.png", "image/png", new byte[1024]);
            BufferedImage mockImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            when(s3service.uploadFileToS3(any(), anyString())).thenReturn("new-file-id");

            try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
                mockedImageIO.when(() -> ImageIO.read(any(InputStream.class))).thenReturn(mockImage);

                // Act
                avatarService.uploadAvatar(userId, file);

                // Assert
                verify(s3service, times(1)).deleteFileFromS3("old_big_key");
                verify(s3service, times(1)).deleteFileFromS3("old_small_key");
                verify(s3service, times(2)).uploadFileToS3(any(), anyString());
                verify(userRepository, times(1)).save(user);
            }
        }

        @Test
        @DisplayName("Upload success when user has a default avatar (http link)")
        void testUploadAvatar_WithDefaultAvatar_DeletesNothing() throws Exception {
            // Arrange
            UserProfilePic oldPic = new UserProfilePic();
            oldPic.setFileId("http://dicebear.com/api/...");
            user.setUserProfilePic(oldPic);

            MultipartFile file = new MockMultipartFile("file", "new.png", "image/png", new byte[1024]);
            BufferedImage mockImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            when(s3service.uploadFileToS3(any(), anyString())).thenReturn("new-file-id");

            try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
                mockedImageIO.when(() -> ImageIO.read(any(InputStream.class))).thenReturn(mockImage);

                // Act
                avatarService.uploadAvatar(userId, file);

                // Assert
                verify(s3service, never()).deleteFileFromS3(anyString()); // Default avatar should not be deleted
                verify(s3service, times(2)).uploadFileToS3(any(), anyString());
            }
        }

        @Test
        @DisplayName("Should throw exception when file is too large")
        void testUploadAvatar_FileTooLarge_ThrowsException() {
            MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[6 * 1024 * 1024]);
            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            assertThrows(DataValidationException.class, () -> avatarService.uploadAvatar(userId, file));
        }

        @Test
        @DisplayName("Should throw exception for invalid content type")
        void testUploadAvatar_InvalidContentType_ThrowsException() {
            MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", new byte[1024]);
            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            assertThrows(DataValidationException.class, () -> avatarService.uploadAvatar(userId, file));
        }
    }

    @Nested
    @DisplayName("Tests for deleteAvatar method")
    class DeleteAvatarTests {

        @Test
        @DisplayName("Delete success when user has a custom avatar")
        void testDeleteAvatar_WithCustomAvatar_Success() {
            // Arrange
            UserProfilePic pic = new UserProfilePic();
            pic.setFileId("avatars/1/some-id.png");
            pic.setSmallFileId("avatars/1/some-small-id.png");
            user.setUserProfilePic(pic);

            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);

            // Act
            String resultUrl = avatarService.deleteAvatar(userId);

            // Assert
            verify(s3service, times(1)).deleteFileFromS3("avatars/1/some-id.png");
            verify(s3service, times(1)).deleteFileFromS3("avatars/1/some-small-id.png");
            verify(userRepository, times(1)).save(user);
            assertTrue(resultUrl.contains("http://test.com"));
            assertEquals(resultUrl, user.getUserProfilePic().getFileId());
        }

        @Test
        @DisplayName("Delete success when user has a default avatar")
        void testDeleteAvatar_WithDefaultAvatar_DeletesNothing() {
            // Arrange
            UserProfilePic pic = new UserProfilePic();
            pic.setFileId("http://test.com/avatar.png");
            user.setUserProfilePic(pic);

            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);

            // Act
            avatarService.deleteAvatar(userId);

            // Assert
            verify(s3service, never()).deleteFileFromS3(anyString()); // Nothing to delete
            verify(userRepository, times(1)).save(user); // Still saves the new default URL
        }
    }

    @Nested
    @DisplayName("Tests for downloadAvatar method")
    class DownloadAvatarTests {

        @Test
        @DisplayName("Download success for a custom avatar")
        void testDownloadAvatar_Success() {
            // Arrange
            UserProfilePic pic = new UserProfilePic();
            pic.setFileId("avatars/1/some-id.png");
            user.setUserProfilePic(pic);
            byte[] imageBytes = new byte[]{1, 2, 3};

            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            when(s3service.downloadFileFromS3("avatars/1/some-id.png")).thenReturn(imageBytes);

            // Act
            byte[] result = avatarService.downloadAvatar(userId);

            // Assert
            assertArrayEquals(imageBytes, result);
        }

        @Test
        @DisplayName("Should throw exception when avatar does not exist")
        void testDownloadAvatar_WhenAvatarNotExists() {
            user.setUserProfilePic(null);
            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            assertThrows(EntityNotFoundException.class, () -> avatarService.downloadAvatar(userId));
        }

        @Test
        @DisplayName("Should throw exception when trying to download a default avatar")
        void testDownloadAvatar_WhenDefaultAvatarIsSet() {
            UserProfilePic pic = new UserProfilePic();
            pic.setFileId("http://test.com/avatar.png");
            user.setUserProfilePic(pic);
            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            assertThrows(DataValidationException.class, () -> avatarService.downloadAvatar(userId));
        }
    }
}