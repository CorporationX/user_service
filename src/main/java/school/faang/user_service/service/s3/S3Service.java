package school.faang.user_service.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.ExceptionMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile, String folder) {
        ObjectMetadata objectMetadata = collectMetadata(multipartFile);
        String key = String.format("Origin-%s-%d-%s", folder, System.currentTimeMillis(), multipartFile.getOriginalFilename());
        try {
            sendRequestToTheCloud(bucketName, key, multipartFile.getInputStream(), objectMetadata);
        } catch (IOException e) {
            log.error("Error while preparing the file {} to upload", key, e);
        }
        return key;
    }

    public String uploadFileAsByteArray(byte[] bytes, String contentType, String folder, String fileName) {
        if (bytes == null || bytes.length == 0) {
            log.error(ExceptionMessages.IMAGE_BYTES_EMPTY);
            throw new IllegalArgumentException(ExceptionMessages.IMAGE_BYTES_EMPTY);
        }
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(bytes.length);
        objectMetadata.setContentType(contentType);
        String key = String.format("%s/%s", folder, fileName);
        sendRequestToTheCloud(bucketName, key, new ByteArrayInputStream(bytes), objectMetadata);
        return key;
    }

    public InputStream downloadingByteImage(String key) {
        try {
            return s3Client.getObject(bucketName, key).getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SdkClientException(e);
        }
    }

    public void deleteImage(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    private ObjectMetadata collectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }

    private void sendRequestToTheCloud(String bucketName,
                                       String key,
                                       InputStream inputStream,
                                       ObjectMetadata objectMetadata) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
        s3Client.putObject(putObjectRequest);
        log.info("Successfully uploaded the picture {} at {}.", key, bucketName);
    }
}