package school.faang.user_service.service.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractS3Service<T> implements S3Service<T> {
    private final AmazonS3 amazonS3;

    protected void uploadFile(String bucketName, String key, InputStream file, ObjectMetadata metadata) {
        try {
            amazonS3.putObject(bucketName, key, file, metadata);
            log.debug("File with key {} uploaded to bucket {}", key, bucketName);
        } catch (AmazonClientException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    protected ObjectMetadata getMetadata(String contentType, long size) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(size);
        return metadata;
    }
}
