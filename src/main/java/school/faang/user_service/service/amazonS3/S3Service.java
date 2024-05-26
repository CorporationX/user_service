package school.faang.user_service.service.amazonS3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 clientAmazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Transactional
    public String savePic(String folder, ByteArrayOutputStream pic, ObjectMetadata picMetadata) {
        String key = folder + "/" + picMetadata.getUserMetadata().get("originalFileName") + "." + System.currentTimeMillis();

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, key, new ByteArrayInputStream(pic.toByteArray()), picMetadata);

        log.info("Началась загрузка картинки c размером {} в облако", pic.size());
        clientAmazonS3.putObject(putObjectRequest);
        log.info("Загрузка картинки с размером {} в облако завершена", pic.size());

        return key;
    }

    public InputStream downloadPic(String key) {
        return clientAmazonS3.getObject(bucketName, key).getObjectContent();
    }

    public void deletePic(String key) {
        log.info("Удаление картинки с ключом: {} началось", key);
        clientAmazonS3.deleteObject(bucketName, key);
        log.info("Удаление картинки с ключом: {} завершилось", key);
    }
}