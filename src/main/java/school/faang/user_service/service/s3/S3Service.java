package school.faang.user_service.service.s3;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.ResourceDto;
import school.faang.user_service.entity.UserProfilePic;

import java.io.InputStream;

public interface S3Service {

    String uploadFile(MultipartFile file, String folder);

    void delete(String key);

    InputStream downloadFile(String key);
}
