package school.faang.user_service.service.minio;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioService {

    String uploadFile(MultipartFile file, String folder);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}
