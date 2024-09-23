package school.faang.user_service.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketDefaultAvatarsName}")
    private String bucketDefaultAvatarsName;

    @Value("${services.s3.defaultProfilePicture}")
    private String defaultPictureName;

    public String uploadHttpData(ResponseEntity<byte[]> data, String folder) throws RuntimeException {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(data.getHeaders().getContentLength());
            objectMetadata.setContentType(data.getHeaders().getContentType().toString());

            String key = String.format("%s%d%s", folder, System.currentTimeMillis(), getFileName(data.getHeaders()));

            if (!isBucketExists(bucketDefaultAvatarsName)) {
                createBucket(bucketDefaultAvatarsName);
            }

            log.info("Trying to save data in s3 {}", bucketDefaultAvatarsName);

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketDefaultAvatarsName, key, new ByteArrayInputStream(data.getBody()), objectMetadata);
            s3Client.putObject(putObjectRequest);

            return key;
        } catch (Exception e) {
            log.error("Error while trying to save file in cloud: {}", e.getMessage());
            throw new RuntimeException("Failed to upload image");
        }
    }

    public boolean isDefaultPictureExistsOnCloud() {
        return s3Client.doesObjectExist(bucketDefaultAvatarsName.toLowerCase(), defaultPictureName);
    }

    private String getFileName(Map<String, List<String>> headers) {
        String filename = "pic.png";

        if (headers.containsKey("Content-Disposition")) {
            if (headers.get("Content-Disposition").get(0) != null) {
                Pattern pattern = Pattern.compile("filename=\"?([^\"]+)\"?");
                Matcher matcher = pattern.matcher(headers.get("Content-Disposition").get(0));
                if (matcher.find()) {
                    filename = matcher.group(1);
                }
            }
        }

        return filename;
    }

    public boolean isBucketExists(String bucketName) {
        try {
            log.info("Check does bucket with name {} exists", bucketName);

            s3Client.doesBucketExistV2(bucketName);
            log.info("Bucket with name {} exists", bucketName);

            return true;
        } catch (AmazonS3Exception e) {
            log.info("Bucket with name {} doesn't exists", bucketName);
            return false;
        }
    }

    public void createBucket(String bucketName) {
        try {
            log.info("Creating bucket with name {}", bucketName);
            s3Client.createBucket(new CreateBucketRequest(bucketName.toLowerCase()));
        } catch (AmazonServiceException a) {
            log.error("Error while creating new bucket");
        }
    }
}
