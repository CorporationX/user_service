package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Service
public class MinioService implements S3CompatibleService {
    private final String bucketName;
    private final AmazonS3 s3Client;

    public MinioService(@Value("${services.s3.bucketName}") String bucketName, AmazonS3 s3Client) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void uploadFile(byte[] fileData, String fileKey, String contentType) {
        validateFileData(fileData, fileKey, contentType);
        ObjectMetadata objectMetadata = prepareFileMetadata(contentType, fileData.length);
        try(InputStream fileStream = new ByteArrayInputStream(fileData)) {
            s3Client.putObject(bucketName, fileKey, fileStream, objectMetadata);
        } catch(Exception e) {
            throw new RuntimeException("Failed to save file to storage");
        }
    }

    @Override
    public byte[] downloadFile(String fileKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteFile(String fileKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private ObjectMetadata prepareFileMetadata(String contentType, long sizeInBytes) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(sizeInBytes);
        return metadata;
    }

    private void validateFileData(byte[] fileData, String fileKey, String contentType) {
        if (Objects.isNull(fileData) || fileData.length == 0) {
            throw new IllegalArgumentException("File data can't be null/empty");
        }
        if (fileKey.isBlank() || contentType.isBlank()) {
            throw new IllegalArgumentException("File key or content type can't be blank");
        }
    }
}
