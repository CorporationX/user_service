package school.faang.user_service.service.s3;

import java.io.IOException;

public interface S3Service {
    void uploadFile(byte[] data, String filePath, Long userId, String contentType);
    byte[] downloadFile(String fileKey) throws IOException;
    void deleteFile(String fileKey);
}
