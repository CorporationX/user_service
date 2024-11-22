package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
@Setter
public class S3Service {
    private static final String SVG_CONTENT_TYPE = "image/svg+xml";
    private static final String AVATAR_FOLDER = "avatar";

    private final AmazonS3 s3Client;
    @Value("${services.s3.buketName}")
    private String bucketName;

    public String saveSvg(String svg, String key) {
        InputStream inputStream = new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8));
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(svg.getBytes().length);
        objectMetadata.setContentType(SVG_CONTENT_TYPE);
        String keyWithFolder = String.format("%s/%s", AVATAR_FOLDER, key);
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyWithFolder, inputStream, objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception ex) {
            log.error("Exception when saving file to minio", ex);
            throw new IllegalStateException("Could not save a file to s3");
        }
        return keyWithFolder;
    }
}
