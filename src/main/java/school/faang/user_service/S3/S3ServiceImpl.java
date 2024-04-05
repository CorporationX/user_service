package school.faang.user_service.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class S3ServiceImpl implements S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);

    private final AmazonS3 amazonS3Client;
    private final String bucketName;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3Client, @Value("${services.s3.bucketName}") String bucketName) {
        this.amazonS3Client = amazonS3Client;
        this.bucketName = bucketName;
    }

    // Метод загрузки файла в Amazon S3 и связывания его с пользователем
    @Override
    public User uploadFile(MultipartFile file, String folder) throws IOException {
        logger.info("Загрузка файла в Amazon S3 в папку: {}", folder);

        try {
            // Формирование ключа для сохранения файла в S3
            String key = folder + "/" + file.getOriginalFilename();
            // Создание запроса на загрузку файла
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), null);
            // Выполнение запроса на загрузку файла
            PutObjectResult putObjectResult = amazonS3Client.putObject(putObjectRequest);

            logger.info("Файл успешно загружен в Amazon S3. Ключ: {}", key);

            // Создание объекта User и связывание его с загруженным файлом
            User user = new User();
            user.setUserProfilePic(new UserProfilePic(key));
            return user;
        } catch (Exception e) {
            logger.error("Ошибка при загрузке файла в Amazon S3", e);
            throw new IOException("Произошла ошибка при загрузке файла в Amazon S3", e);
        }
    }

    // Метод для изменения размера изображения
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        return resizedImage;
    }

    // Метод для сохранения изображения разных размеров
    public String[] saveResizedImages(MultipartFile file, String folder) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        // Сохранение большого изображения (максимальная сторона 1080px)
        BufferedImage largeImage = resizeImage(originalImage, 1080, 1080);
        String largeKey = folder + "/large_" + file.getOriginalFilename();
        saveImageToS3(largeImage, largeKey);

        // Сохранение маленького изображения (максимальная сторона 170px)
        BufferedImage smallImage = resizeImage(originalImage, 170, 170);
        String smallKey = folder + "/small_" + file.getOriginalFilename();
        saveImageToS3(smallImage, smallKey);

        return new String[]{largeKey, smallKey};
    }

    // Метод для сохранения изображения в S3
    private void saveImageToS3(BufferedImage image, String key) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        // Сохранение в S3
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, is, null);
        amazonS3Client.putObject(putObjectRequest);
    }

    // Метод удаления файла из Amazon S3
    @Override
    public void deleteFile(String folder, String key) throws IOException {
        try {
            // Удаление файла из S3
            amazonS3Client.deleteObject(bucketName, key);
            logger.info("Файл успешно удален из Amazon S3. Ключ: {}", key);
        } catch (Exception e) {
            logger.error("Ошибка при удалении файла из Amazon S3", e);
            throw new IOException("Произошла ошибка при удалении файла из Amazon S3", e);
        }
    }

    // Метод загрузки файла из Amazon S3
    @Override
    public InputStream downloadFile(String folder, String key) throws IOException {
        try {
            // Загрузка файла из S3
            InputStream inputStream = amazonS3Client.getObject(bucketName, key).getObjectContent();
            logger.info("Файл успешно загружен из Amazon S3. Ключ: {}", key);
            return inputStream;
        } catch (Exception e) {
            logger.error("Ошибка при загрузке файла из Amazon S3", e);
            throw new IOException("Произошла ошибка при загрузке файла из Amazon S3", e);
        }
    }
}
