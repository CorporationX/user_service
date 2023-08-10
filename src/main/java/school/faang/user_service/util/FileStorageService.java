package school.faang.user_service.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    @Value("${services.s3.bucket-name}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    public String uploadFile(byte[] resizedFile, MultipartFile file, long userId, String size) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(resizedFile);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(inputStream.available());

        String objectKey = "u" + userId + "_" + size + "_" + file.getOriginalFilename();
        try {
            amazonS3.putObject(bucketName, objectKey, inputStream, metadata);
        } catch (AmazonServiceException ase) {
            log.error("Amazon S3 couldn't process operation", ase);
            throw ase;
        } catch (SdkClientException sce) {
            log.error("Amazon S3 couldn't be contacted for a response", sce);
            throw sce;
        }
        return objectKey;
    }

}
