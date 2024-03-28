package school.faang.user_service.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.exception.FileException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final AmazonS3 amazonClient;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folderName) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = generateKey(file, folderName);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            amazonClient.putObject(putObjectRequest);
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            throw new FileException(exception.getMessage());
        }

        log.info("File {} uploaded to S3", file.getOriginalFilename());
        return Resource.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size((BigInteger.valueOf(fileSize)))
                .build();
    }

    public InputStream getFile(String key) {
        try {
            S3Object s3Object = amazonClient.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (RuntimeException exception) {
            log.error(exception.getMessage(), exception);
            throw new EntityNotFoundException(exception.getMessage());
        }
    }

    public void deleteFile(String key) {
        amazonClient.deleteObject(bucketName, key);
        log.info("File {} deleted from S3", key.split("/")[1]);
    }

    private String generateKey(MultipartFile file, String folder) {
        return String.format("%s/%s/%s/%d/%d",
                folder, file.getOriginalFilename(), file.getContentType(), System.currentTimeMillis(), file.getSize());
    }
}
