package school.faang.user_service.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    private static final String SVG_CONTENT_TYPE = "image/svg+xml";
    private static final String COULD_NOT_SAVE_FILE_MESSAGE = "Could not save a file to s3";
    private static final String AVATAR_FOLDER = "avatar";

    @Mock
    private AmazonS3 s3Client;
    @Value("${services.s3.buketName}")
    private String bucketName;
    @InjectMocks
    private S3Service s3Service;
    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor;

    @BeforeEach
    public void setUp() {
        s3Service.setBucketName(bucketName);
    }

    @Test
    void testSaveSvgSaved() throws IOException {
        String svg = "<svg>...</svg>";
        String key = "key";
        String updatedKey = s3Service.saveSvg(svg, key);
        verify(s3Client).putObject(putObjectRequestArgumentCaptor.capture());
        PutObjectRequest putObjectRequest = putObjectRequestArgumentCaptor.getValue();
        assertEquals(bucketName, putObjectRequest.getBucketName());
        assertEquals(AVATAR_FOLDER + "/" + key, putObjectRequest.getKey());
        assertEquals(svg, new String(putObjectRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
        assertEquals(svg.getBytes().length, putObjectRequest.getMetadata().getContentLength());
        assertEquals(SVG_CONTENT_TYPE, putObjectRequest.getMetadata().getContentType());
        assertEquals(AVATAR_FOLDER + "/" + key, putObjectRequest.getKey(), updatedKey);
    }

    @Test
    void testSaveSvgWithException() {
        String svg = "<svg>...</svg>";
        String key = "key";
        when(s3Client.putObject(any())).thenThrow(new SdkClientException("some message"));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> s3Service.saveSvg(svg, key));
        assertEquals(COULD_NOT_SAVE_FILE_MESSAGE, ex.getMessage());
    }
}