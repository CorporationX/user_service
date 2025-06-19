package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserAvatarDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.user.UserRepositoryAdapter;
import school.faang.user_service.service.image.ImageResizingService;
import school.faang.user_service.service.s3.S3Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;

    @Mock
    private ImageResizingService imageResizingService;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private AvatarService avatarService;

    @Mock
    private MultipartFile file;

    private final Long USER_ID = 1L;

    @Test
    void testGetAvatarWhenUserNotFound() {
        when(userRepositoryAdapter.findById(USER_ID)).thenThrow(new NotFoundException("User with id " + USER_ID + " not found"));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> avatarService.getAvatar(USER_ID));
        assertEquals(String.format("User with id %s not found", USER_ID), e.getMessage());
        verify(userRepositoryAdapter).findById(USER_ID);
        verifyNoInteractions(s3Service);
    }

    @Test
    void testGetAvatarWhenAvatarNotFound() {
        User user = new User();
        user.setUserProfilePic(null);
        when(userRepositoryAdapter.findById(USER_ID)).thenReturn(user);
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> avatarService.getAvatar(USER_ID));
        assertEquals("Avatar not found", e.getMessage());
        verify(userRepositoryAdapter).findById(USER_ID);
        verifyNoInteractions(s3Service);
    }

    @Test
    void testGetAvatar() {
        User user = new User();
        UserProfilePic avatar = new UserProfilePic();
        avatar.setFileId("largeKey");
        avatar.setSmallFileId("smallKey");
        user.setUserProfilePic(avatar);
        when(userRepositoryAdapter.findById(USER_ID)).thenReturn(user);
        when(s3Service.getFileUrl("largeKey")).thenReturn("largeUrl");
        when(s3Service.getFileUrl("smallKey")).thenReturn("smallUrl");

        UserAvatarDto dto = avatarService.getAvatar(USER_ID);

        assertEquals("largeUrl", dto.getLargeUrl());
        assertEquals("smallUrl", dto.getSmallUrl());
        verify(s3Service).getFileUrl("largeKey");
        verify(s3Service).getFileUrl("smallKey");
    }

    @Test
    void testAddAvatarWhenUserNotFound() {
        when(userRepositoryAdapter.findById(USER_ID)).thenThrow(new NotFoundException("User with id " + USER_ID + " not found"));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> avatarService.addAvatar(USER_ID, file));
        assertEquals(String.format("User with id %s not found", USER_ID), e.getMessage());
        verify(userRepositoryAdapter).findById(USER_ID);
        verifyNoInteractions(imageResizingService, s3Service);
    }

    @Test
    void testAddAvatar() {
        User user = new User();
        when(userRepositoryAdapter.findById(USER_ID)).thenReturn(user);
        when(file.getContentType()).thenReturn("image/png");

        byte[] largeBytes = {1, 2, 3};
        byte[] smallBytes = {4, 5, 6};
        when(imageResizingService.resizeImage(file, AvatarService.LARGE_IMAGE_SIZE)).thenReturn(largeBytes);
        when(imageResizingService.resizeImage(file, AvatarService.SMALL_IMAGE_SIZE)).thenReturn(smallBytes);
        when(s3Service.uploadFile(largeBytes, "image/png", "user_avatars_large"))
                .thenReturn("largeKey");
        when(s3Service.uploadFile(smallBytes, "image/png", "user_avatars_small"))
                .thenReturn("smallKey");

        avatarService.addAvatar(USER_ID, file);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertNotNull(savedUser.getUserProfilePic());
        assertEquals("largeKey", savedUser.getUserProfilePic().getFileId());
        assertEquals("smallKey", savedUser.getUserProfilePic().getSmallFileId());
    }

    @Test
    void testDeleteAvatarWhenUserNotFound() {
        when(userRepositoryAdapter.findById(USER_ID)).thenThrow(new NotFoundException("User with id " + USER_ID + " not found"));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> avatarService.deleteAvatar(USER_ID));
        assertEquals(String.format("User with id %s not found", USER_ID), e.getMessage());
        verify(userRepositoryAdapter).findById(USER_ID);
        verifyNoInteractions(s3Service);
    }

    @Test
    void testDeleteAvatarWhenAvatarNotFound() {
        User user = new User();
        user.setUserProfilePic(null);
        when(userRepositoryAdapter.findById(USER_ID)).thenReturn(user);
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> avatarService.deleteAvatar(USER_ID));
        assertEquals("Avatar not found", e.getMessage());
        verify(userRepositoryAdapter).findById(USER_ID);
        verifyNoInteractions(s3Service);
    }

    @Test
    void testDeleteAvatar() {
        User user = new User();
        UserProfilePic avatar = new UserProfilePic();
        avatar.setFileId("largeKey");
        avatar.setSmallFileId("smallKey");
        user.setUserProfilePic(avatar);
        when(userRepositoryAdapter.findById(USER_ID)).thenReturn(user);

        avatarService.deleteAvatar(USER_ID);

        verify(s3Service).deleteFile("largeKey");
        verify(s3Service).deleteFile("smallKey");

        assertNull(user.getUserProfilePic().getFileId());
        assertNull(user.getUserProfilePic().getSmallFileId());
    }
}
