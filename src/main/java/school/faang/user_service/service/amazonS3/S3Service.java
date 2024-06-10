package school.faang.user_service.service.amazonS3;


import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Config s3Config;

    public void uploadFile(ByteArrayOutputStream outputStream, String key) {
        ObjectMetadata objectMetadata = getObjectMetadata(outputStream);

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                s3Config.getBucketName(), key, new ByteArrayInputStream(outputStream.toByteArray()), objectMetadata
        );

        s3Config.getS3client().putObject(putObjectRequest);
    }

    public InputStream downloadFile(String key) {
        return s3Config.getS3client().getObject(s3Config.getBucketName(), key).getObjectContent();
    }

    public void deleteFile(String key) {
        s3Config.getS3client().deleteObject(s3Config.getBucketName(), key);
    }

    private ObjectMetadata getObjectMetadata(ByteArrayOutputStream outputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(outputStream.size());
        objectMetadata.setContentType(MediaType.IMAGE_JPEG.getType());
        return objectMetadata;
    }
}
