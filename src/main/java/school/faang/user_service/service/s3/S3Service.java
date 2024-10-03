package school.faang.user_service.service.s3;

import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface S3Service {
    public void uploadFile(ByteArrayOutputStream outputStream, String key);

    public void deleteFile(String key);

    public InputStream downloadFile(String key);

    void uploadFile(String key, byte[] file, String bucketName, long contentLength, MediaType contentType);
}