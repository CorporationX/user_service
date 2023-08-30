package school.faang.user_service.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.MinIOClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class MinIOService {

    public void uploadToMinioBucket(MultipartFile file, String bucketName){
        MinioClient minioClient = MinIOClient.getClient();
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object("profilePics/" + file.getName())
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(),-1, 10485760)
                    .build());

        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException
                | XmlParserException e) {
            e.printStackTrace();
        }
    }

    public URL getUrl(MultipartFile profilePicFile, String bucketName){
        MinioClient minioClient = MinIOClient.getClient();
        String urlString = null;
        try {
            urlString = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object("profilePics/" + profilePicFile.getName())
                            .build());
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException
                | XmlParserException e) {
            e.printStackTrace();
        }

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}
