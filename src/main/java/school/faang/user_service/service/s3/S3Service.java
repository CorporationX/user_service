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

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.FindException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3 s3Client;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile, String folder) {
        ObjectMetadata objectMetadataOne = collectMetadata(multipartFile);
        String keyOne = String.format("Origin%s%d%s",
                folder, System.currentTimeMillis(), multipartFile.getOriginalFilename());
        sendingRequestToTheCloud(bucketName, keyOne, multipartFile, objectMetadataOne);

        return keyOne;
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
        long fileSize = multipartFile.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }

    private void sendingRequestToTheCloud(String bucketName,
                                          String key,
                                          MultipartFile multipartFile,
                                          ObjectMetadata objectMetadata) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, multipartFile.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error(ExceptionMessages.CLOUD_SENDING, e);
            throw new FindException(ExceptionMessages.CLOUD_SENDING, e);
        }
    }
}