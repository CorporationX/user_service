package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.util.ImageHandler;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserProfilePicS3ServiceTest {
    private UserProfilePicS3Service userProfilePicS3Service;
    @Mock
    private ImageHandler imageHandler;
    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private MultipartFile file;
    private String folder;
    private String key;
    private final byte[] expectedBytes = "Test content".getBytes();

    @BeforeEach
    void setUp() throws IOException {
        userProfilePicS3Service = new UserProfilePicS3Service(
                amazonS3, imageHandler, "bucketName", 100, 50);

        folder = "folder";
        key = "test key";

        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream objectInputStream = mock(S3ObjectInputStream.class);

        when(s3Object.getObjectContent()).thenReturn(objectInputStream);
        when(objectInputStream.readAllBytes()).thenReturn(expectedBytes);

        when(amazonS3.getObject(anyString(), anyString())).thenReturn(s3Object);

        when(imageHandler.resizePic(file, 100)).thenReturn(new byte[100]);
        when(imageHandler.resizePic(file, 50)).thenReturn(new byte[50]);
    }

    @Test
    void upload_shouldInvokeAmazonS3PutObject2Times() {
        userProfilePicS3Service.upload(file, folder);
        verify(amazonS3, times(2)).putObject(any(), any(), any(), any());
    }

    @Test
    void download_shouldInvokeAmazonS3GetObject() throws IOException {
        InputStream resultInputStream = userProfilePicS3Service.download(key);

        byte[] resultBytes = resultInputStream.readAllBytes();
        verify(amazonS3, times(1)).getObject(anyString(), anyString());
        assertArrayEquals(expectedBytes, resultBytes);
    }

    @Test
    void delete() {
        userProfilePicS3Service.delete("test-key");
        verify(amazonS3, times(1)).deleteObject(anyString(), anyString());
    }
}