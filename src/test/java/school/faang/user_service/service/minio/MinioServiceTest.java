package school.faang.user_service.service.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.MinioException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MinioServiceTest {
    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioService minioService;
    private String fileName;
    private byte[] data;
    private String contentType;

    @BeforeEach
    public void setup() {
        minioService.setBucketName("corpbucket");
        fileName = "testfile.png";
        data = new byte[10];
        contentType = "image/png";
    }

    @Test
    public void testUploadFile() throws Exception {
        minioService.uploadFile(fileName, data, contentType);
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    public void testUploadFileFailure() throws Exception {
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenThrow(new RuntimeException(new io.minio.errors.MinioException("Failed to upload")));

        assertThrows(MinioException.class, () -> {
            minioService.uploadFile(fileName, data, contentType);
        });
    }
}