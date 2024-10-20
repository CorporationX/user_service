package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.ProfilePicEvent;
import school.faang.user_service.publisher.ProfilePicEventPublisher;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfilePicServiceImplTest {

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private UserContext userContext;

    @Mock
    private ProfilePicEventPublisher profilePicEventPublisher;

    @InjectMocks
    private UserProfilePicServiceImpl userProfilePicService;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Test
    void testUploadAvatar() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "image-content".getBytes());
        Long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);

        userProfilePicService.uploadAvatar(file);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(amazonS3, times(1)).putObject(captor.capture());
        verify(profilePicEventPublisher).publish(any(ProfilePicEvent.class));

        PutObjectRequest request = captor.getValue();
        assertEquals(bucketName, request.getBucketName());
        assertEquals(String.format("%s-%s", userId, file.getOriginalFilename()), request.getKey());

        ObjectMetadata metadata = request.getMetadata();
        assertEquals(file.getSize(), metadata.getContentLength());
        assertEquals(file.getContentType(), metadata.getContentType());
    }
}
