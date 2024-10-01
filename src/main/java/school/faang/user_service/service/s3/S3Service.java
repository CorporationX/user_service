package school.faang.user_service.service.s3;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface S3Service {
    public void uploadFile(ByteArrayOutputStream outputStream, String key);

    public void deleteFile(String key);

    public InputStream downloadFile(String key);
}