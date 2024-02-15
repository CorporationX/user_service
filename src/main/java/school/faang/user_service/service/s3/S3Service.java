package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.UserProfilePic;

import java.io.ByteArrayInputStream;

import static org.apache.http.entity.ContentType.IMAGE_SVG;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public UserProfilePic uploadFile(byte[] file, String folder) {

        int size = file != null ? file.length : 0;
        String key = String.format("%s/%d", folder, System.currentTimeMillis());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(IMAGE_SVG.getMimeType());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key,
                new ByteArrayInputStream(file), objectMetadata);
        amazonS3.putObject(putObjectRequest);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(key);
        return userProfilePic;
    }
}