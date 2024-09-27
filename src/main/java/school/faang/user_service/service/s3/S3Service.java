package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.InputStream;

public interface S3Service {
    void uploadFile(InputStream data, ObjectMetadata metadata, String folder, Long userId);

    InputStream downloadFile(String fileKey);

    void deleteFile(String fileKey);
}
