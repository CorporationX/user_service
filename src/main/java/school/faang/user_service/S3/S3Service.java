package school.faang.user_service.S3;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;

import java.io.IOException;
import java.io.InputStream;

public interface S3Service {
    User uploadFile(MultipartFile file, String folder) throws IOException;
    void deleteFile(String folder, String key) throws IOException;
    InputStream downloadFile(String folder, String key) throws IOException;
}

