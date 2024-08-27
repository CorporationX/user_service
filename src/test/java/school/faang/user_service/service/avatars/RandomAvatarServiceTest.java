package school.faang.user_service.service.avatars;

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

    private final User user = new User();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(randomAvatarService, "avatarStyles", Arrays.asList("style1", "style2", "style3"));
    }

    @Test
    void testGenerateAndStoreAvatar_Success() {
        byte[] svgBytes = "testSvg".getBytes();
        String fileKey = "testFileKey";

        when(dicebearClient.getAvatar(anyString())).thenReturn(svgBytes);
        when(s3Service.uploadFile(any(MultipartFile.class), eq("default-avatars"))).thenReturn(fileKey);
        when(userRepository.save(any(User.class))).thenReturn(user);

        randomAvatarService.generateAndStoreAvatar(user);

        verify(dicebearClient).getAvatar(anyString());
        verify(s3Service).uploadFile(any(MultipartFile.class), eq("default-avatars"));
        verify(userRepository).save(user);

        assertNotNull(user.getUserProfilePic());
        assertEquals(fileKey, user.getUserProfilePic().getFileId());
        assertEquals(fileKey, user.getUserProfilePic().getSmallFileId());
    }

    @Test
    void testGenerateAndStoreAvatar_ExceptionHandling() {
        when(dicebearClient.getAvatar(anyString())).thenThrow(new RuntimeException("Test exception"));

        randomAvatarService.generateAndStoreAvatar(user);

        verify(dicebearClient).getAvatar(anyString());
        verify(s3Service, never()).uploadFile(any(MultipartFile.class), eq("default-avatars"));
        verify(userRepository, never()).save(any(User.class));
    }
}