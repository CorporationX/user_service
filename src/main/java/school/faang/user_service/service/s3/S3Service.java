package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
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
import school.faang.user_service.exception.remote.AmazonS3CustomException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

    private final String FILE_NAME_PATTERN = "filename=\"?([^\"]+)\"?";
    private final String CONTENT_DISPOSITION = Headers.CONTENT_DISPOSITION;

    @Value("${services.s3.bucketDefaultAvatarsName}")
    private String bucketDefaultAvatarsName;

    @Value("${services.s3.defaultProfilePicture}")
    private String defaultPictureName;

    @Value("${services.s3.remoteFilename}")
    private String remoteFileName;

    private final Pattern PATTERN_GET_IMAGE_NAME_FROM_HEADER = Pattern.compile(FILE_NAME_PATTERN);

    public String uploadHttpData(ResponseEntity<byte[]> data, String folder) {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(data.getHeaders().getContentLength());
            objectMetadata.setContentType(data.getHeaders().getContentType().toString());

            String key = String.format("%s%d%s", folder, System.currentTimeMillis(), getFileName(data.getHeaders()));

            if (!isBucketExists(bucketDefaultAvatarsName)) {
                createBucket(bucketDefaultAvatarsName);
            }

            putObjectInS3(bucketDefaultAvatarsName, key, new ByteArrayInputStream(data.getBody()), objectMetadata);

            return key;
        } catch (Exception e) {
            log.error("Error while saving file in cloud: {}", e.getMessage());
            throw new AmazonS3CustomException("Failed to upload image");
        }
    }

    public boolean isDefaultPictureExistsOnCloud() {
        return s3Client.doesObjectExist(bucketDefaultAvatarsName.toLowerCase(), defaultPictureName);
    }

    private void putObjectInS3(String bucketName, String key, InputStream inputStream, ObjectMetadata objectMetadata) {
        log.info("Trying to save data in s3. BucketName = {}, key = {}", bucketName, key);

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, key, inputStream, objectMetadata);
        s3Client.putObject(putObjectRequest);
    }

    private String getFileName(Map<String, List<String>> headers) {
        String filename = remoteFileName;

        if (headers.containsKey(CONTENT_DISPOSITION)) {
            if (headers.get(CONTENT_DISPOSITION).get(0) != null) {
                Matcher matcher = PATTERN_GET_IMAGE_NAME_FROM_HEADER.matcher(headers.get(CONTENT_DISPOSITION).get(0));
                if (matcher.find()) {
                    filename = matcher.group(1);
                }
            }
        }

        return filename;
    }

    private boolean isBucketExists(String bucketName) {
        log.info("Check does bucket with name {} exists", bucketName);

        try {
            return s3Client.doesBucketExistV2(bucketName);
        } catch (AmazonS3Exception e) {
            log.error("Error while checking bucket existence");
            return false;
        }
    }

    private void createBucket(String bucketName) {
        log.info("Trying to create bucket with name {}", bucketName);

        s3Client.createBucket(new CreateBucketRequest(bucketName.toLowerCase()));
    }
}
