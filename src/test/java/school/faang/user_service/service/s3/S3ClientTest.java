package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ClientTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Client s3Client;

    @Test
    public void testUploadProfilePicture() {
        User user = new User();
        user.setId(123L);
        user.setUsername("testuser");
        byte[] imageData = {};
        String extension = ".jpg";

        s3Client.uploadProfilePicture(user, imageData, extension);
        verify(amazonS3).putObject(eq(s3Client.getBucketName()), eq(user.getId() + extension), any(ByteArrayInputStream.class), any(ObjectMetadata.class));
    }

    @Test
    public void testGetURLById() throws MalformedURLException {
        Long id = 123L;
        String extension = ".jpg";

        when(amazonS3.generatePresignedUrl(any())).thenReturn(new URL("https://example.com/image.jpg"));
        String url = s3Client.getURLById(id, extension);

        verify(amazonS3).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
        assertEquals("https://example.com/image.jpg", url);
    }
}