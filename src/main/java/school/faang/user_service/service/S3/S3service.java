package school.faang.user_service.service.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Импорт для логирования
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3service {

    private final AmazonS3 s3Client;
    private final String bucketName;

    public String uploadFileToS3(byte[] content, String key) throws Exception {
        log.info("Uploading file to S3. Bucket: {}, Key: {}, Size: {} bytes", bucketName, key, content.length);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(MediaType.IMAGE_PNG_VALUE);
        metadata.setContentLength(content.length);
        InputStream inputStream = new ByteArrayInputStream(content);

        s3Client.putObject(this.bucketName, key, inputStream, metadata);

        log.info("Successfully uploaded file to S3 with key: {}", key);
        return key;
    }

    public String getUrl(String fileId) {
        log.debug("Generating S3 URL for key: {}", fileId);
        return s3Client.getUrl(bucketName, fileId).toString();
    }

    public void deleteFileFromS3(String key) {
        log.info("Deleting file from S3. Bucket: {}, Key: {}", bucketName, key);
        try {
            s3Client.deleteObject(bucketName, key);
            log.info("Successfully deleted file from S3 with key: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete file from S3 with key: {}", key, e);
        }
    }

    public byte[] downloadFileFromS3(String key) {
        log.info("Downloading file from S3. Bucket: {}, Key: {}", bucketName, key);
        try {
            S3Object s3object = s3Client.getObject(bucketName, key);
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            byte[] content = inputStream.readAllBytes();
            log.info("Successfully downloaded file from S3 with key: {}. Size: {} bytes", key, content.length);
            return content;
        } catch (IOException e) {
            log.error("Failed to read file content from S3 with key: {}", key, e);
            throw new RuntimeException("Error reading file from S3", e);
        } catch (Exception e) {
            log.error("Failed to download file from S3 with key: {}", key, e);
            throw new RuntimeException("Error downloading file from S3", e);
        }
    }
}