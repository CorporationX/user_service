package school.faang.user_service.service.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AmazonS3ServiceTest {

    @InjectMocks
    private AmazonS3Service amazonS3Service;

    @Mock
    private AmazonS3 amazonS3;

    private byte[] file;
    private String bucketName;
    private String fileName;
    private String contentType;

    @BeforeEach
    void setup() {
        file = new byte[0];
        bucketName = "test";
        fileName = "test.jpg";
        contentType = "image/jpeg";
    }

    @Test
    void testUploadFile() {
        amazonS3Service.uploadFile(file, bucketName, fileName, contentType);

        verify(amazonS3).putObject(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class));
    }
}
