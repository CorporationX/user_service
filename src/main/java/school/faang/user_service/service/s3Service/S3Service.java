package school.faang.user_service.service.s3Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.s3.FileDownloadException;
import school.faang.user_service.exception.s3.FileUploadException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3Client;
    @Value("${aws.bucket-name}")
    private String bucketName;

    public String uploadFile(File file, String folder) {
        String key = generateBucketKey(folder, file);

        ObjectMetadata objectMetadata = generateObjectMetadata(file);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, new FileInputStream(file), objectMetadata);
            amazonS3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileUploadException(e.getMessage());
        }

        return key;
    }

    public InputStream getFile(String key) {
        try {
            S3Object object = amazonS3Client.getObject(bucketName, key);
            return object.getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileDownloadException(e.getMessage());
        }
    }

    private String generateBucketKey(String folder, File file) {
        return String.format("%s/%d_%s", folder, System.currentTimeMillis(), file.getName());
    }

    private ObjectMetadata generateObjectMetadata(File file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        try {
            objectMetadata.setContentType(Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileUploadException(e.getMessage());
        }

        objectMetadata.setContentLength(file.length());

        return objectMetadata;
    }
}
