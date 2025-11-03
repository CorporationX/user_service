package school.faang.user_service.amazon_s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client; //Именно в нем проблема, он не создается!

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    @Transactional
    public String uploadFile(long userId, MultipartFile file, String folder, int maxWidthAndLength) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format(
                "%s/%d%suser_id=%d",
                folder,
                System.currentTimeMillis(),
                file.getOriginalFilename(),
                userId
        );
        try {
            InputStream scaledImageInputStream = scaleImage(file, maxWidthAndLength);
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, scaledImageInputStream, objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileException("Во время загрузки файла произошла ошибка.");
        }
        return key;
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileException("Во время загрузки файла произошла ошибка.");
        }
    }

    private InputStream scaleImage(MultipartFile image, int maxWidthAndLength) throws IOException {
        BufferedImage originalImage = ImageIO.read(image.getInputStream());
        if (originalImage.getWidth() > maxWidthAndLength && originalImage.getWidth() > originalImage.getHeight()) {
            Scalr.resize(originalImage, Scalr.Mode.FIT_TO_WIDTH, maxWidthAndLength);
        }
        if (originalImage.getHeight() > maxWidthAndLength && originalImage.getHeight() > originalImage.getWidth()) {
            Scalr.resize(originalImage, Scalr.Mode.FIT_TO_HEIGHT, maxWidthAndLength);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
