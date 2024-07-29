package school.faang.user_service.service.user;

import java.io.InputStream;

public interface StorageManager {

    void putObject(String bucketName, String filePath, InputStream content);
}

