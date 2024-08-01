package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Data
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s-%s", UUID.randomUUID(), file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
        } catch (Exception e) {
//            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        UserProfilePic userProfilePic = new UserProfilePic();
        return key;
    }

    @Override
    public void delete(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        return null;
    }

    public boolean isObjectExist(String key) {
        return s3Client.doesObjectExist(bucketName, key);
    }


}
