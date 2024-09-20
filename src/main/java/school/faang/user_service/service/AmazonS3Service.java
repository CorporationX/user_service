package school.faang.user_service.service;

import com.amazonaws.services.s3.model.ObjectMetadata;

public interface AmazonS3Service {

    void uploadFile(byte[] file, ObjectMetadata metadata, String key);
}
