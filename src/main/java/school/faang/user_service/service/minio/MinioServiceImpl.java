package school.faang.user_service.service.minio;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class MinioServiceImpl implements MinioService{
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentLength(fileSize);
        objectMetaData.setContentType(file.getContentType());
        String key = String.format("%s%d%s", folder, fileSize, file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetaData);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error during uploading file");
        }

        return key;
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException("Error in downloading file from Amazon Cloud");
        }
    }
}
