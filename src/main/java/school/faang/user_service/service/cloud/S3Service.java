package school.faang.user_service.service.cloud;

import java.io.InputStream;

public interface S3Service {

    void uploadFile(InputStream inputStream, String fileName);
}
