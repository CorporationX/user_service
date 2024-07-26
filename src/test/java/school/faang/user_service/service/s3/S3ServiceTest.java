package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @InjectMocks
    private S3Service s3Service;
    @Mock
    private AmazonS3 s3Client;
    @Mock
    private MultipartFileCopyUtil multipartFileCopyUtil;
    private MultipartFile multipartFile;
    String contentType = "image/jpeg";
    String folder = "folder";
    @Value("${services.s3.bucketName}")
    private String bucketName;

    @BeforeEach
    void init() {
        multipartFile = Mockito.mock(MultipartFile.class);
    }

    @Test
    @DisplayName("compressionException")
    void testUploadProfileCompressionException() throws IOException {
        when(multipartFileCopyUtil.compressionMultipartFile(any(MultipartFile.class), anyInt()))
                .thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                s3Service.uploadProfile(multipartFile, folder));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("privateTestCollectMetadata")
    void testCollectMetadata() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        when(multipartFile.getContentType()).thenReturn(contentType);

        Method method = S3Service.class.getDeclaredMethod("collectMetadata", MultipartFile.class);
        method.setAccessible(true);
        ObjectMetadata objectMetadata = (ObjectMetadata) method.invoke(s3Service, multipartFile);

        assertEquals(contentType, objectMetadata.getContentType());
        verify(multipartFile, times(1)).getContentType();
    }

    @Test
    @DisplayName("collectMetadataValid")
    void testUploadProfileCollectMetadataException() throws IOException {
        when(multipartFileCopyUtil.compressionMultipartFile(any(MultipartFile.class), anyInt()))
                .thenReturn(multipartFile);
        when(multipartFile.getContentType()).thenReturn(contentType);
        s3Service.uploadProfile(multipartFile, folder);

        verify(multipartFileCopyUtil, times(2))
                .compressionMultipartFile(any(MultipartFile.class), anyInt());
        verify(multipartFile, times(2)).getContentType();
    }

    @Test
    @DisplayName("sendingRequestToTheCloudValid")
    void testSendingRequestToTheCloudValid() throws IOException {
        when(multipartFileCopyUtil.compressionMultipartFile(any(MultipartFile.class), anyInt()))
                .thenReturn(multipartFile);
        when(multipartFile.getContentType())
                .thenReturn(contentType);
        when(s3Client.putObject(any(PutObjectRequest.class)))
                .thenReturn(new PutObjectResult());

        s3Service.uploadProfile(multipartFile, folder);

        verify(multipartFileCopyUtil, times(2))
                .compressionMultipartFile(any(MultipartFile.class), anyInt());
        verify(multipartFile, times(2))
                .getContentType();
        verify(s3Client, times(2))
                .putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("downloadingByteImage")
    void testDownloadingByteImage() throws IOException {
        String key = "file-key";
        String testData = "test data";
        InputStream expectedInputStream = new ByteArrayInputStream(testData.getBytes());
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(expectedInputStream, null);

        when(s3Client.getObject(eq(bucketName), eq(key))).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);

        InputStream actualInputStream = s3Service.downloadingByteImage(key);

        assertNotNull(actualInputStream);
        byte[] expectedContent = testData.getBytes();
        byte[] actualContent = new byte[expectedContent.length];
        actualInputStream.read(actualContent);

        assertArrayEquals(expectedContent, actualContent);

        verify(s3Client, times(1)).getObject(eq(bucketName), eq(key));
        verify(s3Object, times(1)).getObjectContent();
    }

    @Test
    @DisplayName("deleteImage")
    void testDeleteImage() {
        String fileId = "file-id";
        String smallFileId = "small-file-id";

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(fileId);
        userProfilePic.setSmallFileId(smallFileId);

        doNothing().when(s3Client).deleteObject(eq(bucketName), eq(fileId));
        doNothing().when(s3Client).deleteObject(eq(bucketName), eq(smallFileId));

        s3Service.deleteImage(userProfilePic);

        verify(s3Client, times(1)).deleteObject(eq(bucketName), eq(fileId));
        verify(s3Client, times(1)).deleteObject(eq(bucketName), eq(smallFileId));
    }
}