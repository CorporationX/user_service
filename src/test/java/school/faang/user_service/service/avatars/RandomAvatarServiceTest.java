package school.faang.user_service.service.avatars;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.dicebear.DicebearClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomAvatarServiceTest {

    @Mock
    private S3Service s3Service;
    @Mock
    private DicebearClient dicebearClient;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RandomAvatarService randomAvatarService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(randomAvatarService, "avatarStyles", Arrays.asList("style1", "style2", "style3"));
    }

    @Test
    void testGenerateAndStoreAvatar_Success() {
        long userId = 1L;
        byte[] svgBytes = "testSvg".getBytes();
        String fileKey = "testFileKey";
        User user = new User();

        when(dicebearClient.getAvatar(anyString())).thenReturn(svgBytes);
        when(s3Service.uploadFile(any(MultipartFile.class), eq("default-avatars"))).thenReturn(fileKey);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        randomAvatarService.generateAndStoreAvatar(userId);

        verify(dicebearClient).getAvatar(anyString());
        verify(s3Service).uploadFile(any(MultipartFile.class), eq("default-avatars"));
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);

        assertNotNull(user.getUserProfilePic());
        assertEquals(fileKey, user.getUserProfilePic().getFileId());
        assertEquals(fileKey, user.getUserProfilePic().getSmallFileId());
    }

    @Test
    void testGenerateAndStoreAvatar_userNotFound() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> randomAvatarService.generateAndStoreAvatar(userId));

        verify(dicebearClient, never()).getAvatar(anyString());
        verify(s3Service, never()).uploadFile(any(MultipartFile.class), eq("default-avatars"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGenerateAndStoreAvatar_ExceptionHandling() {
        long userId = 1L;
        User user = new User();
        when(dicebearClient.getAvatar(anyString())).thenThrow(new RuntimeException("Test exception"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        randomAvatarService.generateAndStoreAvatar(userId);

        verify(dicebearClient).getAvatar(anyString());
        verify(s3Service, never()).uploadFile(any(MultipartFile.class), eq("default-avatars"));
        verify(userRepository, never()).save(any(User.class));
    }
}