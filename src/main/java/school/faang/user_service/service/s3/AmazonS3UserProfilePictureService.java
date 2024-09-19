package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.service.AmazonS3Service;
import school.faang.user_service.service.HelperAmazonS3Service;

import java.io.ByteArrayInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3UserProfilePictureService implements AmazonS3Service<UserRegistrationDto> {

    private final HelperAmazonS3Service helper;
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFileAndGetKey(byte[] picture, UserRegistrationDto user) {
        log.info("Upload file for user: {}", user.username());

        ObjectMetadata metadata = helper.getMetadata(picture);
        String key = helper.getKey(picture, user.username(), user.email());

        PutObjectRequest request = new PutObjectRequest(
                bucketName,
                key,
                new ByteArrayInputStream(picture),
                metadata
        );
        s3Client.putObject(request);

        log.info("Uploaded file for user: {}", user.username());
        return key;
    }
}
