package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private AmazonS3 amazonS3;
    @InjectMocks
    private S3Service s3Service;

    @Test
    void uploadFile() {
        // Expect
        assertDoesNotThrow(() -> s3Service.uploadFile(new byte[1], "folder"));
        verify(amazonS3, times((1))).putObject(any());
    }
}