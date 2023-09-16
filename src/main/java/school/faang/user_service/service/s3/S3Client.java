package school.faang.user_service.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

import java.io.ByteArrayInputStream;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class S3Client {

    private final String bucketName;
    private final AmazonS3 amazonS3;

    public void uploadProfilePicture(User user, byte[] data, String extension) {
        log.info("Uploading {}'s profile picture to S3 bucket {}...\n", user.getUsername(), bucketName);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            amazonS3.putObject(bucketName, user.getId() + extension, new ByteArrayInputStream(data), metadata);
            log.info("Done!");
        } catch (AmazonServiceException e) {
            log.error("Invalid request. Please, try again later.");
        }
    }

    public String getURLById(Long id, String extension) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, id + extension).withMethod(HttpMethod.GET);
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
}