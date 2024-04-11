package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;

@Service
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 amazonS3Client;
    private final String bucketName;
    private final UserRepository userRepository;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3Client, @Value("${services.s3.bucketName}") String bucketName, UserRepository userRepository) {
        this.amazonS3Client = amazonS3Client;
        this.bucketName = bucketName;
        this.userRepository = userRepository;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        return resizedImage;
    }

    public String[] saveResizedImages(MultipartFile file, String folder) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        BufferedImage largeImage = resizeImage(originalImage, 1080, 1080);
        String largeKey = folder + "/large_" + Objects.requireNonNull(file.getOriginalFilename());
        saveImageToS3(largeImage, largeKey);

        BufferedImage smallImage = resizeImage(originalImage, 170, 170);
        String smallKey = folder + "/small_" + Objects.requireNonNull(file.getOriginalFilename());
        saveImageToS3(smallImage, smallKey);

        return new String[]{largeKey, smallKey};
    }

    private void saveImageToS3(BufferedImage image, String key) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, is, null);
        amazonS3Client.putObject(putObjectRequest);
        log.info("Файл успешно сохранен в Amazon S3. Ключ: {}", key);
    }

    @Override
    public void deleteFile(String folder, String key) throws IOException {
        try {
            amazonS3Client.deleteObject(bucketName, key);
            log.info("Файл успешно удален из Amazon S3. Ключ: {}", key);
        } catch (Exception e) {
            log.error("Ошибка при удалении файла из Amazon S3", e);
            throw new IOException("Произошла ошибка при удалении файла из Amazon S3", e);
        }
    }

    @Override
    public InputStream downloadFile(String folder, String key) throws IOException {
        try {
            InputStream inputStream = amazonS3Client.getObject(bucketName, key).getObjectContent();
            log.info("Файл успешно загружен из Amazon S3. Ключ: {}", key);
            return inputStream;
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла из Amazon S3", e);
            throw new IOException("Произошла ошибка при загрузке файла из Amazon S3", e);
        }
    }
}
