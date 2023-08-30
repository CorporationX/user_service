package school.faang.user_service.client;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Data
@Configuration
@RequiredArgsConstructor
public class MinIOClient {
    @Value("${minio.endpoint}")
    private static String endpoint;
    @Value("${minio.user}")
    private static String user;
    @Value("${minio.password}")
    private static String password;

    public static MinioClient getClient(){
        return MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(user, password)
                        .build();
    }
}