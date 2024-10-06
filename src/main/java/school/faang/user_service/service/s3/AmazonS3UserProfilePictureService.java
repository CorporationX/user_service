package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.FileUploadException;
import school.faang.user_service.service.AmazonS3Service;

import java.io.ByteArrayInputStream;

@Slf4j
@Service
public class AmazonS3UserProfilePictureService implements AmazonS3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Autowired
    public AmazonS3UserProfilePictureService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void uploadFile(byte[] picture, ObjectMetadata metadata, String key) {
        log.info("Upload profile picture");

        PutObjectRequest request = new PutObjectRequest(
                bucketName,
                key,
                new ByteArrayInputStream(picture),
                metadata
        );
        putObject(request);

        log.info("Uploaded profile picture");
    }

    private void putObject(PutObjectRequest request) {
        try {
            s3Client.putObject(request);
        } catch (Exception exception) {
            throw new FileUploadException(exception.getMessage(), exception);
        }
    }
}
