package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserValidator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private MultipartFile file;

    @Mock
    private UserValidator userValidator;

    @Mock
    StringHelper stringHelper;

    @InjectMocks
    private S3Service s3Service;

    private Long userId;
    private User user;


    @BeforeEach
    public void setUp() {
        s3Service.bucketName = "test-bucket";
        userId = 1L;
        user = new User();
    }

    @Test
    void testUploadAvatar_Success() throws IOException {

        when(userValidator.findUserById(userId)).thenReturn(Optional.of(user));

        BufferedImage sampleImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(sampleImage, "jpg", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        when(file.getInputStream()).thenReturn(is);

        String result = s3Service.uploadAvatar(1L, file);

        assertEquals("file uploaded", result);
        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class));
        verify(userRepository).save(user);
    }

    @Test
    void testUploadAvatar_UserNotFound() {
        when(userValidator.findUserById(userId)).thenThrow(new IllegalArgumentException("User not found"));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            s3Service.uploadAvatar(userId, file);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testDownloadAvatar() {
        S3Object s3Object = mock(S3Object.class);

        byte[] sampleAvatarData = new byte[]{1, 2, 3, 4, 5};
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sampleAvatarData);
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(byteArrayInputStream, null);

        String avatarKey = stringHelper.createAvatarKey(userId, "large");

        when(s3Client.getObject(eq(s3Service.bucketName), eq(avatarKey))).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);

        byte[] result = s3Service.downloadAvatar(userId);

        assertNotNull(result);
        assertArrayEquals(sampleAvatarData, result);
        verify(s3Client).getObject(eq(s3Service.bucketName), eq(avatarKey));
    }

    @Test
    void testDeleteAvatar() {
        when(userValidator.findUserById(userId)).thenReturn(Optional.of(user));

        String result = s3Service.deleteAvatar(userId);

        assertEquals("Avatar removed", result);
        verify(s3Client, times(2)).deleteObject(anyString(), anyString());
    }

    @Test
    void testDeleteAvatar_UserNotFound() {
        when(userValidator.findUserById(userId)).thenThrow(new IllegalArgumentException("User not found"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> s3Service.deleteAvatar(userId));

        assertEquals("User not found", exception.getMessage());

        verify(s3Client, never()).deleteObject(anyString(), anyString());
    }
}
