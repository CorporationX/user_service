package school.faang.user_service.service;

import com.amazonaws.services.s3.model.ObjectMetadata;

public interface HelperAmazonS3Service {

    ObjectMetadata getMetadata(byte[] file);

    String getKey(byte[] file, String path, String... args);
}
