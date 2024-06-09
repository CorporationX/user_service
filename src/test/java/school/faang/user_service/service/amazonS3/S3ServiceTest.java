package school.faang.user_service.service.amazonS3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @InjectMocks
    private S3Service s3Service;
    @Mock
    private S3Config s3Config;

    @Test
    void uploadFileTest() {
        AmazonS3 s3Client = mock(AmazonS3.class);
        when(s3Config.getS3client()).thenReturn(s3Client);

        assertDoesNotThrow(() -> s3Service.uploadFile(new ByteArrayOutputStream(), "key"));

        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void downloadFileTest() {
        AmazonS3 s3Client = mock(AmazonS3.class);
        when(s3Config.getS3client()).thenReturn(s3Client);
        when(s3Config.getBucketName()).thenReturn("bucket");
        when(s3Client.getObject(anyString(), anyString())).thenReturn(mock(S3Object.class));

        assertDoesNotThrow(() -> s3Service.downloadFile("key"));
    }

    @Test
    void deleteFileTest() {
        AmazonS3 s3Client = mock(AmazonS3.class);
        when(s3Config.getS3client()).thenReturn(s3Client);
        doNothing().when(s3Client).deleteObject(any(), anyString());

        assertDoesNotThrow(() -> s3Service.deleteFile("key"));
    }
}