package school.faang.user_service.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Data
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s-%s", UUID.randomUUID(), file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
            log.info(bucketName, key, file.getOriginalFilename(), objectMetadata);
        } catch (IOException e) {
            log.error("S3Service" + e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return key;
    }

    @Override
    public void delete(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            return s3Client.getObject(bucketName, key).getObjectContent();
        } catch (Exception e) {
            log.error("S3Service downloadFile" + e.getMessage(), e);
            throw new SdkClientException(e);
        }
    }
}
