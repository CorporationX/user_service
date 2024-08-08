package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    public S3Service s3Service;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor;

    private String fileName;
    private byte[] imageBytes;
    private String contentType;
    private String bucketName;

    @BeforeEach
    public void setUp() {
        fileName = "fileName";
        imageBytes = new byte[1000];
        contentType = "image/jpeg";
        bucketName = "bucketName";
    }

    @Test
    @DisplayName("testing uploadToS3 method")
    public void testUploadToS3() {
        s3Service.uploadToS3(fileName, imageBytes, contentType, bucketName);

        verify(s3Client, times(1)).putObject(putObjectRequestArgumentCaptor.capture());
    }
}