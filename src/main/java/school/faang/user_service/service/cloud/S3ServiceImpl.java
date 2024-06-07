package school.faang.user_service.service.cloud;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@PropertySource(ignoreResourceNotFound = true, value = "classpath:s3.properties")
@Slf4j
public class S3ServiceImpl implements S3Service{
    private final AmazonS3 s3Client;

    @Override
    public void uploadFile(String bucketName, String fileName, @NonNull InputStream inputStream){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");
        try {
            s3Client.putObject(bucketName, fileName, inputStream, metadata);
        } catch (SdkClientException e) {
            log.error("Error uploading a file to Amazon S3", e);
            throw new RuntimeException("Error uploading a file to Amazon S3", e);
        }
    }

    @Override
    public S3Object getFile(String bucketName, String key){
        S3Object s3Object;
        try {
            s3Object = s3Client.getObject(bucketName, key);
        } catch (SdkClientException e) {
            log.error("Error getting a file from Amazon S3", e);
            throw new RuntimeException("Error getting a file from Amazon S3", e);
        }
        return s3Object;
    }

    @Override
    public void deleteFile(String bucketName, String key){
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (SdkClientException e) {
            log.error("Error deleting a file from Amazon S3", e);
            throw new RuntimeException("Error deleting a file from Amazon S3", e);
        }
    }
}
