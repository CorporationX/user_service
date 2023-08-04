package school.faang.user_service.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;

@Component
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${services.s3.bucket-name}")
    private String bucketName;
    private final MinioClient minioClient;

    public String uploadFile(byte[] resizedFile, MultipartFile file) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(resizedFile);

        PutObjectArgs args = UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(file.getOriginalFilename())
                .contentType(file.getContentType())
                .
                .stream(inputStream, resizedFile.length, -1)
                .build();

        minioClient.pu
        return file.getName();
    }

}
