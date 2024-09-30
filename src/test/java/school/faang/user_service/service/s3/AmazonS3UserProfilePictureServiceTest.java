package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.exception.FileUploadException;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AmazonS3UserProfilePictureServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private AmazonS3UserProfilePictureService service;

    @Captor
    private ArgumentCaptor<PutObjectRequest> requestArgumentCaptor;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private String key;
    private byte[] picture;
    private ObjectMetadata metadata;
    private PutObjectRequest request;

    @BeforeEach
    void setUp() {
        key = "key";
        picture = new byte[]{1, 2, 3};

        metadata = new ObjectMetadata();
        metadata.setContentLength(picture.length);
        metadata.setContentType("image/jpeg");
        metadata.setContentEncoding("utf-8");

        request = new PutObjectRequest(
                bucketName,
                key,
                new ByteArrayInputStream(picture),
                metadata
        );
    }

    @Test
    void uploadFile_WhenOk() {
        assertDoesNotThrow(() ->
                service.uploadFile(picture, metadata, key));

        assertRequest();
    }

    @Test
    void uploadFile_WhenException() {
        String correctStartMessage = "Error...";
        when(s3Client.putObject(requestArgumentCaptor.capture()))
                .thenThrow(new AmazonS3Exception("Error..."));

        Throwable exception = assertThrows(FileUploadException.class,
                () -> service.uploadFile(picture, metadata, key));

        assertTrue(exception.getMessage().startsWith(correctStartMessage));
        assertRequest();
    }

    private void assertRequest() {
        verify(s3Client).putObject(requestArgumentCaptor.capture());
        PutObjectRequest result = requestArgumentCaptor.getValue();

        assertEquals(request.getFile(), result.getFile());
        assertEquals(request.getMetadata(), result.getMetadata());
        assertEquals(request.getKey(), result.getKey());
    }
}