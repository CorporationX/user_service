package school.faang.user_service.service.s3;

import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface S3Service {
    void uploadFile(ByteArrayOutputStream outputStream, String key);

    void uploadFile(String key, byte[] file, String bucketName, long contentLength, MediaType contentType);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}