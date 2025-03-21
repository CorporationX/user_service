package school.faang.user_service.service.avatar;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {
    private static final String AVATAR_API_URL = "https://api.dicebear.com/5.x/avataaars";
    private static final String DICEBEAR_PNG_ENDPOINT = "/png?seed=";
    private static final String BUCKET_NAME = "corpbucket";
    private static final String LOCALHOST_URL_PREFIX = "http://localhost:9000/";
    private static final String FILE_EXTENSION = ".png";
    private static final String CONTENT_TYPE_PNG = "image/png";

    private static final String GENERATED_DICEBEAR_URL_LOG = "Generated Dicebear URL {}";
    private static final String FETCHING_AVATAR_LOG = "Fetching avatar data from Dicebear URL {}";
    private static final String AVATAR_FETCH_SUCCESS_LOG = "Successfully fetched avatar data from Dicebear.";
    private static final String AVATAR_FETCH_ERROR_LOG = "Failed to fetch avatar from Dicebear API at URL {}";
    private static final String UPLOAD_AVATAR_LOG = "Uploading avatar to MinIO with object name {}";
    private static final String AVATAR_UPLOAD_SUCCESS_LOG = "Avatar successfully uploaded to MinIO.";
    private static final String MINIO_UPLOAD_ERROR_LOG = "Error while uploading avatar to MinIO";
    private static final String FORMULATED_AVATAR_URL_LOG = "Formulated avatar URL {}";
    private static final String INITIALIZING_BUCKET_LOG = "Initializing bucket {}";
    private static final String BUCKET_ALREADY_EXISTS_LOG = "Bucket '{}' already exists.";
    private static final String BUCKET_CREATED_LOG = "Bucket '{}' created successfully.";

    private static final String AVATAR_FETCH_EXCEPTION_MSG = "Failed to fetch avatar from Dicebear API at URL ";
    private static final String MINIO_UPLOAD_EXCEPTION_MSG = "Error while uploading avatar to MinIO";
    private static final String BUCKET_INIT_EXCEPTION_MSG = "Error initializing bucket in MinIO";

    private final MinioClient minioClient;
    private final RestTemplate restTemplate;

    public String generateAndUploadAvatar(String userId) {
        String dicebearUrl = AVATAR_API_URL + DICEBEAR_PNG_ENDPOINT + userId;
        log.info(GENERATED_DICEBEAR_URL_LOG, dicebearUrl);
        byte[] avatarData = fetchAvatarData(dicebearUrl);
        String objectName = generateObjectName(userId);
        uploadAvatarToMinio(avatarData, objectName);
        return formulateAvatarUrl(objectName);
    }

    @PostConstruct
    public void initializeBucket() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(BUCKET_NAME)
                            .build()
            );
            if (!found) {
                log.info(INITIALIZING_BUCKET_LOG, BUCKET_NAME);
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(BUCKET_NAME)
                                .build());
                log.info(BUCKET_CREATED_LOG, BUCKET_NAME);
            } else {
                log.info(BUCKET_ALREADY_EXISTS_LOG, BUCKET_NAME);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(BUCKET_INIT_EXCEPTION_MSG, e);
        }
    }

    private byte[] fetchAvatarData(String url) {
        log.info(FETCHING_AVATAR_LOG, url);
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error(AVATAR_FETCH_ERROR_LOG, url);
            throw new RuntimeException(AVATAR_FETCH_EXCEPTION_MSG + url);
        }
        log.info(AVATAR_FETCH_SUCCESS_LOG);
        return response.getBody();
    }

    private String generateObjectName(String userId) {
        return userId + FILE_EXTENSION;
    }

    private void uploadAvatarToMinio(byte[] avatarData, String objectName) {
        log.info(UPLOAD_AVATAR_LOG, objectName);
        try {
            InputStream stream = new ByteArrayInputStream(avatarData);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .stream(stream, avatarData.length, -1)
                            .contentType(CONTENT_TYPE_PNG)
                            .build());
            log.info(AVATAR_UPLOAD_SUCCESS_LOG);
        } catch (Exception e) {
            log.error(MINIO_UPLOAD_ERROR_LOG, e);
            throw new RuntimeException(MINIO_UPLOAD_EXCEPTION_MSG, e);
        }
    }

    private String formulateAvatarUrl(String objectName) {
        String avatarUrl = LOCALHOST_URL_PREFIX + BUCKET_NAME + "/" + objectName;
        log.info(FORMULATED_AVATAR_URL_LOG, avatarUrl);
        return avatarUrl;
    }
}
