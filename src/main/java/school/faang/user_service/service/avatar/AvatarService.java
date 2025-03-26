package school.faang.user_service.service.avatar;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.exception.AvatarFetchException;
import school.faang.user_service.exception.MinioUploadException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {
    private static final String GENERATED_DICEBEAR_URL_LOG = "Generated Dicebear URL {}";
    private static final String FETCHING_AVATAR_LOG = "Fetching avatar data from Dicebear URL {}";
    private static final String AVATAR_FETCH_SUCCESS_LOG = "Successfully fetched avatar data from Dicebear.";
    private static final String AVATAR_FETCH_ERROR_LOG = "Failed to fetch avatar from Dicebear API at URL {}";
    private static final String UPLOAD_AVATAR_LOG = "Uploading avatar to MinIO with object name {}";
    private static final String AVATAR_UPLOAD_SUCCESS_LOG = "Avatar successfully uploaded to MinIO.";
    private static final String MINIO_UPLOAD_ERROR_LOG = "Error while uploading avatar to MinIO";
    private static final String FORMULATED_AVATAR_URL_LOG = "Formulated avatar URL {}";

    @Value("${app.avatar.apiUrl}")
    private String avatarApiUrl;

    @Value("${app.avatar.dicebearPngEndpoint}")
    private String dicebearPngEndpoint;

    @Value("${app.minio.bucket.name}")
    private String bucketName;

    @Value("${app.avatar.localhostUrlPrefix}")
    private String localhostUrlPrefix;

    @Value("${app.avatar.fileExtension}")
    private String fileExtension;

    @Value("${app.avatar.contentType}")
    private String contentTypePng;

    private final MinioClient minioClient;
    private final RestTemplate restTemplate;

    public String generateAndUploadAvatar(String userId) {
        String dicebearUrl = avatarApiUrl + dicebearPngEndpoint + userId;
        log.info(GENERATED_DICEBEAR_URL_LOG, dicebearUrl);
        byte[] avatarData = fetchAvatarData(dicebearUrl);
        String objectName = userId + fileExtension;
        uploadAvatarToMinio(avatarData, objectName);
        return formulateAvatarUrl(objectName);
    }

    private byte[] fetchAvatarData(String url) {
        log.info(FETCHING_AVATAR_LOG, url);
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error(AVATAR_FETCH_ERROR_LOG, url);
            throw new AvatarFetchException(url);
        }
        log.info(AVATAR_FETCH_SUCCESS_LOG);
        return response.getBody();
    }

    private void uploadAvatarToMinio(byte[] avatarData, String objectName) {
        log.info(UPLOAD_AVATAR_LOG, objectName);
        try (InputStream stream = new ByteArrayInputStream(avatarData)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(stream, avatarData.length, -1)
                            .contentType(contentTypePng)
                            .build());
            log.info(AVATAR_UPLOAD_SUCCESS_LOG);
        } catch (Exception e) {
            log.error(MINIO_UPLOAD_ERROR_LOG, e);
            throw new MinioUploadException();
        }
    }

    private String formulateAvatarUrl(String objectName) {
        String avatarUrl = localhostUrlPrefix + bucketName + "/" + objectName;
        log.info(FORMULATED_AVATAR_URL_LOG, avatarUrl);
        return avatarUrl;
    }
}
