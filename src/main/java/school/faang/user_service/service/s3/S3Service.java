package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.httpResponse.HttpResponseData;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.defaultProfilePicture}")
    private String defaultPictureName;

    public String uploadHttpData(HttpResponseData data, String folder) throws RuntimeException {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(Long.parseLong(data.getHeaders().get("Content-Length").get(0)));
            objectMetadata.setContentType(data.getHeaders().get("Content-Type").get(0));

            String key = String.format("%s%d%s", folder, System.currentTimeMillis(), getFileName(data.getHeaders()));

            log.info("Trying to save httpResponseData in s3 {}", bucketName);

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, new ByteArrayInputStream(data.getContent()), objectMetadata);
            s3Client.putObject(putObjectRequest);

            return key;
        } catch (Exception e) {
            log.error("Error while trying to save file in cloud: {}", e.getMessage());
            throw new RuntimeException("Failed to upload image");
        }
    }

    public boolean isDefaultPictureExistsOnCloud() {
        return s3Client.doesObjectExist(bucketName, defaultPictureName);
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
}
