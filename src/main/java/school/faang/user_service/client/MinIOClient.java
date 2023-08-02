package school.faang.user_service.client;

import io.minio.MinioClient;

public class MinIOClient {
    public static MinioClient getClient(){
        return MinioClient.builder()
                        .endpoint("http://localhost:9000")
                        .credentials("user", "password")
                        .build();
    }

}