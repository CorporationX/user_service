package school.faang.user_service.amazon_s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface S3Service {
    String uploadFile(long userId, MultipartFile file, String folder, int maxWidthAndLength) throws IOException;

    void deleteFile(String key);

    InputStream downloadFile(String key);
}
