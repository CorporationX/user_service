package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.service.s3.S3service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomAvatarServiceTest {
    @Mock
    private S3service s3service;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RandomAvatarServiceImpl service;

    private String username;
    private byte[] imageBytes;

    @BeforeEach
    void setUp() throws IOException {
        ReflectionTestUtils.setField(service, "dicebearBaseUrl", "http://dicebear-test-png");
        ReflectionTestUtils.setField(service, "dicebearDefaultSize", 256);
        username = "Gleb";
        BufferedImage testImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
        ImageIO.write(testImage, "png", bytesStream);
        imageBytes = bytesStream.toByteArray();
    }

    @Test
    @DisplayName("generateRandomAvatarForUser - success case")
    void testSuccessfulAvatarGeneration() throws Exception {
        when(restTemplate.getForEntity(anyString(), eq(byte[].class)))
                .thenReturn(new ResponseEntity<>(imageBytes, HttpStatus.OK));
        when(s3service.uploadFileToS3(any(byte[].class), anyString()))
                .thenReturn("fake-s3-path");

        UserProfilePic profilePic = service.generateRandomAvatarForUser(username);

        assertTrue(profilePic.getFileId().startsWith("random-user-avatars/" + username + "/big-"));
        assertTrue(profilePic.getSmallFileId().startsWith("random-user-avatars/" + username + "/small-"));
        verify(s3service, times(2)).uploadFileToS3(any(), anyString());
    }

    @Test
    @DisplayName("Exception when restTemplate return null")
    void testRestTemplateReturnNull() throws Exception {
        when(restTemplate.getForEntity(anyString(), eq(byte[].class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThrows(IllegalStateException.class, () -> {
            service.generateRandomAvatarForUser(username);
        });
        verify(s3service, never()).uploadFileToS3(any(), anyString());
    }

    @Test
    @DisplayName("Exception when restTemplate return  empty array")
    void testRestTemplateReturnEmptyArray() throws Exception {
        when(restTemplate.getForEntity(anyString(), eq(byte[].class)))
                .thenReturn(new ResponseEntity<>(new byte[0], HttpStatus.OK));

        assertThrows(IllegalStateException.class, () -> {
            service.generateRandomAvatarForUser(username);
        });
        verify(s3service, never()).uploadFileToS3(any(), anyString());
    }

    @Test
    @DisplayName("Exception when restTemplate return too large array")
    void testRestTemplateReturnLargeArray() throws Exception {
        when(restTemplate.getForEntity(anyString(), eq(byte[].class)))
                .thenReturn(new ResponseEntity<>(new byte[3_000_000], HttpStatus.OK));

        assertThrows(IllegalStateException.class, () -> {
            service.generateRandomAvatarForUser(username);
        });
        verify(s3service, never()).uploadFileToS3(any(), anyString());
    }
}
