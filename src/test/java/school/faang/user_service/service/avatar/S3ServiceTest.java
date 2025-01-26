package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.exceptions.S3Exception;
import school.faang.user_service.service.S3Service;

import java.io.ByteArrayInputStream;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;


public class S3ServiceTest {

    private AmazonS3 amazonS3;
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        amazonS3 = Mockito.mock(AmazonS3.class);
        s3Service = new S3Service(amazonS3);
    }

    @Test
    void testUploadFile_Failure() {
        byte[] fileContent = "test-content".getBytes();
        String fileName = "test-file.svg";

        doThrow(new RuntimeException("S3 error")).when(amazonS3).putObject(eq("bucket-name"), eq(fileName), any(ByteArrayInputStream.class), any(ObjectMetadata.class));

        assertThrows(S3Exception.class, () -> s3Service.uploadFile(fileContent, fileName));
    }
}