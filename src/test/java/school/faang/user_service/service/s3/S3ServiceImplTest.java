package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ServiceImplTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private final String key = "test-key";

    @BeforeEach
    void setUp() {
        s3Service = new S3ServiceImpl(s3Client);
        s3Service.setBucketName(bucketName);
    }

    // Positive Tests

    @Test
    @DisplayName("successful upload test")
    public void uploadFile_successTest() {
        //arrange
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());

        //act
        String key = s3Service.uploadFile(multipartFile);


        //
        assertNotNull(key);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("successful test delete")
    void deleteFile() {
        // Arrange & Act
        s3Service.deleteFile(key);

        // Assert
        verify(s3Client, times(1)).deleteObject(bucketName, key);
    }

    @Test
    @DisplayName("successful test download")
    public void downloadFile_successTest() throws IOException {
        //arrange
        String testDate = "test data";
        InputStream exptectedStream = new ByteArrayInputStream(testDate.getBytes());
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(exptectedStream, null);

        //when
        when(s3Client.getObject(bucketName, key)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);

        //act
        InputStream actualInputStream = s3Service.downloadFile(key);

        //arrange
        assertNotNull(actualInputStream);
        byte[] actualContent = actualInputStream.readAllBytes();

        assertArrayEquals(testDate.getBytes(), actualContent);

        verify(s3Client, times(1)).getObject(bucketName, key);
        verify(s3Object, times(1)).getObjectContent();
    }

}
