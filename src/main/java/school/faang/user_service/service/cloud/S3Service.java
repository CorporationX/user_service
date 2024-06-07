package school.faang.user_service.service.cloud;

import com.amazonaws.services.s3.model.S3Object;

import java.io.InputStream;

public interface S3Service {

    void uploadFile(String bucketName, String fileName, InputStream inputStream);

    S3Object getFile(String bucketName, String key);

    void deleteFile(String bucketName, String key);
}
