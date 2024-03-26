package school.faang.user_service.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.exception.FileException;

import java.io.IOException;
import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final AmazonS3 amazonClient;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    // TODO: добавить FileException в Exception handler
    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = generateKey(file, folder);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            amazonClient.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileException(e.getMessage());
        }

        log.info("File {} uploaded to S3", file.getOriginalFilename());
        return Resource.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size((BigInteger.valueOf(fileSize)))
                .build();
    }

    private String generateKey(MultipartFile file, String folder) {
        return String.format("%s/%s/%s/%d",
                folder, file.getOriginalFilename(), file.getContentType(), System.currentTimeMillis());
    }
}
