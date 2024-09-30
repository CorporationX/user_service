package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UserProfilePicServiceImplTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private UserProfilePicServiceImpl userProfilePicService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadAvatar() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "image-content".getBytes());
        Long userId = 1L;

        userProfilePicService.uploadAvatar(userId, file);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(amazonS3, times(1)).putObject(captor.capture());

        PutObjectRequest request = captor.getValue();
        assertEquals("corpbucket", request.getBucketName());
        assertEquals(String.format("%s-%s", userId, file.getOriginalFilename()), request.getKey());

        ObjectMetadata metadata = request.getMetadata();
        assertEquals(file.getSize(), metadata.getContentLength());
        assertEquals(file.getContentType(), metadata.getContentType());
    }
}
