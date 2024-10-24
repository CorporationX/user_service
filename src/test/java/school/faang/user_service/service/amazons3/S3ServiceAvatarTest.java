package school.faang.user_service.service.amazons3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.service.s3.S3ServiceImpl;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceAvatarTest {

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Mock
    private AmazonS3 s3Client;

    @Test
    void uploadFileTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String key = "testKey";
        String bucketName = "testBucket";

        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);

        assertDoesNotThrow(() -> s3Service.uploadFile(outputStream, key));
        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void downloadFileTest() {
        String key = "testKey";
        String bucketName = "testBucket";
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream s3ObjectInputStream = mock(S3ObjectInputStream.class);
        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);

        when(s3Client.getObject(bucketName, key)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        S3ObjectInputStream result = (S3ObjectInputStream) s3Service.downloadFile(key);

        assertDoesNotThrow(() -> result.read());
        verify(s3Client).getObject(bucketName, key);
    }

    @Test
    void deleteFileTest() {
        String key = "testKey";
        String bucketName = "testBucket";

        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);

        assertDoesNotThrow(() -> s3Service.deleteFile(key));
        verify(s3Client).deleteObject(bucketName, key);
    }
}