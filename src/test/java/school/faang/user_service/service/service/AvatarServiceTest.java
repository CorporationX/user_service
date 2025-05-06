package school.faang.user_service.service.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.client.AvatarClient;
import school.faang.user_service.exception.MinioUploadException;
import school.faang.user_service.service.avatar.AvatarService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private AvatarClient avatarClient;

    @InjectMocks
    private AvatarService avatarService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(avatarService, "avatarApiUrl", "http://avatar-api");
        ReflectionTestUtils.setField(avatarService, "pngEndpoint", "/png/");
        ReflectionTestUtils.setField(avatarService, "bucketName", "avatars");
        ReflectionTestUtils.setField(avatarService, "localhostUrlPrefix", "http://localhost/");
        ReflectionTestUtils.setField(avatarService, "fileExtension", ".png");
        ReflectionTestUtils.setField(avatarService, "contentTypePng", "image/png");
    }

    @Test
    void generateAndUploadAvatar_Success() throws Exception {

        String userId = "user123";
        String dicebearUrl = "http://avatar-api/png/" + userId;
        byte[] avatarData = "dummy image".getBytes();

        when(avatarClient.fetchAvatarData(dicebearUrl)).thenReturn(avatarData);
        String avatarUrl = avatarService.generateAndUploadAvatar(userId);

        verify(avatarClient).fetchAvatarData(dicebearUrl);
        verify(minioClient).putObject(any(PutObjectArgs.class));

        String expectedObjectName = userId + ".png";
        String expectedUrl = "http://localhost/avatars/" + expectedObjectName;
        assertEquals(expectedUrl, avatarUrl);
    }

    @Test
    void generateAndUploadAvatar_ThrowsMinioUploadException() throws Exception {

        String userId = "user123";
        String dicebearUrl = "http://avatar-api/png/" + userId;
        byte[] avatarData = "dummy image".getBytes();

        when(avatarClient.fetchAvatarData(dicebearUrl)).thenReturn(avatarData);
        doThrow(new RuntimeException("upload failed"))
                .when(minioClient)
                .putObject(any(PutObjectArgs.class));

        assertThrows(MinioUploadException.class, () -> avatarService.generateAndUploadAvatar(userId));
        verify(avatarClient).fetchAvatarData(dicebearUrl);
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }
}