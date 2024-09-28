package school.faang.user_service.service.minio;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;

import java.io.InputStream;

public interface S3service {
    UserProfilePic uploadFile(MultipartFile file, long userId);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}
