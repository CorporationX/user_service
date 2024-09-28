package school.faang.user_service.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadAvatar(byte[] image, String folder, String contentType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.length);
        objectMetadata.setContentType(contentType);

        String key = String.format("%s%d%s", folder, System.currentTimeMillis(), image.length);
        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, key, new ByteArrayInputStream(image), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (SdkClientException ex) {
            throw new RuntimeException("Failed to upload picture", ex.getCause());
        }

        return key;
    }

    public InputStream downloadAvatar(String key) {
        S3Object s3Object;
        try {
            s3Object = s3Client.getObject(bucketName, key);
        } catch (SdkClientException ex) {
            throw new SdkClientException("Failed to download picture", ex.getCause());
        }

        return s3Object.getObjectContent();
    }

    public void deleteAvatar(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (SdkClientException ex) {
            throw new SdkClientException("Failed to delete picture", ex.getCause());
        }
    }
}
