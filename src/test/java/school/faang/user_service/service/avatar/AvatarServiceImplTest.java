package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.s3.S3service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        file = new MockMultipartFile("file", "test.png", "image/png", new byte[1024]);
        ReflectionTestUtils.setField(avatarService, "dicebearBaseUrl", "http://test.com");
        ReflectionTestUtils.setField(avatarService, "dicebearDefaultSize", 256);
    }

    @Nested
    @DisplayName("Tests for uploadAvatar method")
    class UploadAvatarTests {

        @Test
        @DisplayName("Upload success, should call s3service.uploadAvatar and save user")
        void testUploadAvatarSuccess() {
            UserProfilePic newPic = new UserProfilePic();
            newPic.setFileId("new-big-key");
            newPic.setSmallFileId("new-small-key");

            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            when(s3service.uploadAvatar(anyLong(), any(MultipartFile.class))).thenReturn(newPic);

            UserProfilePic result = avatarService.uploadAvatar(userId, file);

            assertNotNull(result);
            assertEquals("new-big-key", result.getFileId());
            assertEquals("new-small-key", result.getSmallFileId());

            verify(s3service, times(1)).uploadAvatar(userId, file);
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Upload success and delete old avatar")
        void testUploadAvatarWithPreviousAvatarDeletesOldFiles() {
            UserProfilePic oldPic = new UserProfilePic();
            oldPic.setFileId("old_big_key");
            oldPic.setSmallFileId("old_small_key");
            user.setUserProfilePic(oldPic);

            UserProfilePic newPic = new UserProfilePic();

            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            when(s3service.uploadAvatar(userId, file)).thenReturn(newPic);

            avatarService.uploadAvatar(userId, file);

            verify(s3service, times(1)).deleteFileFromS3("old_big_key");
            verify(s3service, times(1)).deleteFileFromS3("old_small_key");
            verify(s3service, times(1)).uploadAvatar(userId, file);
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Should throw exception when file is too large")
        void testUploadAvatarFileTooLargeThrowsException() {
            MultipartFile largeFile = new MockMultipartFile("file", "test.png", "image/png", new byte[6 * 1024 * 1024]);
            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            assertThrows(DataValidationException.class, () -> avatarService.uploadAvatar(userId, largeFile));
        }

        @Test
        @DisplayName("Should throw exception for invalid content type")
        void testUploadAvatarInvalidContentTypeThrowsException() {
            MultipartFile invalidFile = new MockMultipartFile("file", "test.txt", "text/plain", new byte[1024]);
            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            assertThrows(DataValidationException.class, () -> avatarService.uploadAvatar(userId, invalidFile));
        }
    }

    @Nested
    @DisplayName("Tests for deleteAvatar method")
    class DeleteAvatarTests {

        @Test
        @DisplayName("Delete success when user has a custom avatar")
        void testDeleteAvatarWithCustomAvatarSuccess() {
            UserProfilePic pic = new UserProfilePic();
            pic.setFileId("avatars/1/some-id.png");
            pic.setSmallFileId("avatars/1/some-small-id.png");
            user.setUserProfilePic(pic);

            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);

            final String resultUrl = avatarService.deleteAvatar(userId);

            verify(s3service, times(1)).deleteFileFromS3("avatars/1/some-id.png");
            verify(s3service, times(1)).deleteFileFromS3("avatars/1/some-small-id.png");
            verify(userRepository, times(1)).save(user);
            assertTrue(resultUrl.contains("http://test.com"));
            assertEquals(resultUrl, user.getUserProfilePic().getFileId());
        }

        @Test
        @DisplayName("Delete success when user has a default avatar")
        void testDeleteAvatarWithDefaultAvatarDeletesNothing() {
            UserProfilePic pic = new UserProfilePic();
            pic.setFileId("http://test.com/avatar.png");
            user.setUserProfilePic(pic);

            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);

            avatarService.deleteAvatar(userId);

            verify(s3service, never()).deleteFileFromS3(anyString());
            verify(userRepository, times(1)).save(user);
        }
    }

    @Nested
    @DisplayName("Tests for downloadAvatar method")
    class DownloadAvatarTests {

        @Test
        @DisplayName("Download success for a custom avatar")
        void testDownloadAvatarSuccess() {
            UserProfilePic pic = new UserProfilePic();
            pic.setFileId("avatars/1/some-id.png");
            user.setUserProfilePic(pic);
            byte[] imageBytes = new byte[]{1, 2, 3};

            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            when(s3service.downloadFileFromS3("avatars/1/some-id.png")).thenReturn(imageBytes);

            byte[] result = avatarService.downloadAvatar(userId);

            assertArrayEquals(imageBytes, result);
        }

        @Test
        @DisplayName("Should throw exception when avatar does not exist")
        void testDownloadAvatarWhenAvatarNotExists() {
            user.setUserProfilePic(null);
            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            assertThrows(EntityNotFoundException.class, () -> avatarService.downloadAvatar(userId));
        }

        @Test
        @DisplayName("Should throw exception when trying to download a default avatar")
        void testDownloadAvatarWhenDefaultAvatarIsSet() {
            UserProfilePic pic = new UserProfilePic();
            pic.setFileId("http://test.com/avatar.png");
            user.setUserProfilePic(pic);
            when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
            assertThrows(DataValidationException.class, () -> avatarService.downloadAvatar(userId));
        }
    }
}