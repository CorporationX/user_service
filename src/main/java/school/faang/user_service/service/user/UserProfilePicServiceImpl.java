package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.ProfilePicEvent;
import school.faang.user_service.publisher.ProfilePicEventPublisher;
import school.faang.user_service.service.UserProfilePicService;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProfilePicServiceImpl implements UserProfilePicService {

    private final AmazonS3 amazonS3;
    private final UserContext userContext;
    private final ProfilePicEventPublisher profilePicEventPublisher;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public void uploadAvatar(MultipartFile file) {
        Long userId = userContext.getUserId();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentEncoding("utf-8");
        String key = String.format("%s-%s", userId, file.getOriginalFilename());

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            amazonS3.putObject(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        profilePicEventPublisher.publish(new ProfilePicEvent(userId, key, LocalDateTime.now()));
    }
}
