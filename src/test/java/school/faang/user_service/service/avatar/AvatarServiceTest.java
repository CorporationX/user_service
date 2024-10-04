package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.AvatarStyle;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarFetchException;
import school.faang.user_service.service.minio.MinioService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private MinioService minioService;
    @InjectMocks
    private AvatarService avatarService;
    private AvatarStyle style;
    private byte[] avatarData;

    @BeforeEach
    public void setup() {
        style = AvatarStyle.AVATAAARS;
        avatarData = new byte[]{1, 2, 3};
    }

    @Test
    public void testGenerateAndSaveAvatar_Success() {
        when(restTemplate.getForEntity(anyString(), eq(byte[].class)))
                .thenReturn(new ResponseEntity<>(avatarData, HttpStatus.OK));
        doNothing().when(minioService).uploadFile(anyString(), any(byte[].class), anyString());

        UserProfilePic userProfilePic = avatarService.generateAndSaveAvatar(style);

        assertNotNull(userProfilePic);
        assertNotNull(userProfilePic.getFileId());
        assertNotNull(userProfilePic.getSmallFileId());

        verify(minioService, times(2)).
                uploadFile(anyString(), eq(avatarData), eq("image/png"));
        verify(restTemplate, times(2)).getForEntity(anyString(), eq(byte[].class));
    }

    @Test
    public void testGetRandomAvatar_Success() {
        when(restTemplate.getForEntity(anyString(), eq(byte[].class)))
                .thenReturn(new ResponseEntity<>(avatarData, HttpStatus.OK));

        byte[] result = avatarService.getRandomAvatar(style, "png", 200);

        assertNotNull(result);
        assertEquals(avatarData.length, result.length);

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(byte[].class));
    }

    @Test
    public void testGetRandomAvatar_Failure() {
        when(restTemplate.getForEntity(anyString(), eq(byte[].class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(AvatarFetchException.class, () -> {
            avatarService.getRandomAvatar(style, "png", 200);
        });

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(byte[].class));
    }

    @Test
    void uploadUserAvatar() {
    }

    @Test
    void getUserAvatar() {
    }

    @Test
    void deleteUserAvatar() {
    }
}