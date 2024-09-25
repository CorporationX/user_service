package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface S3Service {
    String uploadFile( String fileName, InputStream data, ObjectMetadata metadata, Long userId);
    InputStream downloadFile(String bucketName, String fileName);
    String deleteFile(String bucketName, String fileName);
}
