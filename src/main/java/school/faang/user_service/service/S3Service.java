package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface S3Service {
    void deleteFile(String folder, String key) throws IOException;

    InputStream downloadFile(String folder, String key) throws IOException;

    String[] saveResizedImages(MultipartFile file, String folder) throws IOException;
}
