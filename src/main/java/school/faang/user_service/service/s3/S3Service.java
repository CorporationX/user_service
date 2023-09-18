package school.faang.user_service.service.s3;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Component
public interface S3Service<T> {
    T upload(MultipartFile file, String folder);
    InputStream download(String key);
    void delete(String key);
}
