package school.faang.user_service.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {

    String uploadFile(MultipartFile file);

    void delete(String key);

    InputStream downloadFile(String key);
}
