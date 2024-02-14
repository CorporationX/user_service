package school.faang.user_service.service.s3;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.apache.http.entity.ContentType.IMAGE_SVG;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(@NotNull byte[] fileContent, String fileName) {
        validateFileContentNotEmpty(fileContent);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileContent.length);
        metadata.setContentType(IMAGE_SVG.getMimeType());
        String fileKey = String.format("%s_%s", fileName, UUID.randomUUID());

        try {
            log.info("Загрузка файла с именем:: {}", fileName);
            PutObjectRequest putRequest = new PutObjectRequest(
                    bucketName,
                    fileKey,
                    new ByteArrayInputStream(fileContent),
                    metadata);

            amazonS3.putObject(putRequest);
            log.info("Файл успешно загружен с ключом: {}", fileKey);

            return amazonS3.getUrl(bucketName, fileKey).toString();
        } catch (AmazonServiceException e) {
            log.error("Ошибка при загрузке файла с именем: {}. Status: {}. Message: {}",
                    fileName, e.getStatusCode(), e.getMessage(), e);
            throw e;
        }
    }

    private void validateFileContentNotEmpty(@NotNull byte[] fileContent) {
        if (fileContent == null || fileContent.length == 0) {
            throw new DataValidationException("Содержимое файла не должно быть пустым.");
        }
    }
}