package school.faang.user_service.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableRetry
public class S3service {
    private static final int BIG_AVATAR_SIZE = 1080;
    private static final int SMALL_AVATAR_SIZE = 170;

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public UserProfilePic uploadAvatar(long userId, MultipartFile file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new DataValidationException("The provided file is corrupted or not a valid image.");
            }

            BufferedImage resizedBig = resizeImage(originalImage, BIG_AVATAR_SIZE);
            BufferedImage resizedSmall = resizeImage(originalImage, SMALL_AVATAR_SIZE);

            byte[] bigImageBytes = imageToPngBytes(resizedBig);
            byte[] smallImageBytes = imageToPngBytes(resizedSmall);

            String bigFileKey = "avatars/" + userId + "/" + UUID.randomUUID() + ".png";
            String smallFileKey = "avatars/" + userId + "/" + UUID.randomUUID() + ".png";

            uploadFileToS3(bigImageBytes, bigFileKey);
            uploadFileToS3(smallImageBytes, smallFileKey);

            UserProfilePic userProfilePic = new UserProfilePic();
            userProfilePic.setFileId(bigFileKey);
            userProfilePic.setSmallFileId(smallFileKey);
            return userProfilePic;
        } catch (Exception e) {
            log.error("Error processing or uploading avatar for user ID: {}", userId, e);
            throw new RuntimeException("Error processing or uploading avatar.", e);
        }
    }

    @Retryable(retryFor = {SdkClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public String uploadFileToS3(byte[] content, String key) throws Exception {
        log.info("Uploading file to S3. Bucket: {}, Key: {}, Size: {} bytes", bucketName, key, content.length);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(MediaType.IMAGE_PNG_VALUE);
        metadata.setContentLength(content.length);
        InputStream inputStream = new ByteArrayInputStream(content);
        s3Client.putObject(this.bucketName, key, inputStream, metadata);
        log.info("Successfully uploaded file to S3 with key: {}", key);
        return key;
    }

    @Retryable(retryFor = {SdkClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public String getUrl(String fileId) {
        log.debug("Generating S3 URL for key: {}", fileId);
        return s3Client.getUrl(bucketName, fileId).toString();
    }

    @Retryable(retryFor = {SdkClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void deleteFileFromS3(String key) {
        log.info("Deleting file from S3. Bucket: {}, Key: {}", bucketName, key);
        s3Client.deleteObject(bucketName, key);
        log.info("Successfully deleted file from S3 with key: {}", key);
    }

    @Retryable(retryFor = {SdkClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public byte[] downloadFileFromS3(String key) {
        log.info("Downloading file from S3. Bucket: {}, Key: {}", bucketName, key);
        try {
            S3Object s3object = s3Client.getObject(bucketName, key);
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Failed to read file content from S3 with key: {}", key, e);
            throw new RuntimeException("Error reading file from S3", e);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetSize) {
        Scalr.Mode mode = originalImage.getWidth() > originalImage.getHeight()
                ? Scalr.Mode.FIT_TO_WIDTH : Scalr.Mode.FIT_TO_HEIGHT;
        return Scalr.resize(originalImage, Scalr.Method.QUALITY, mode, targetSize);
    }

    private byte[] imageToPngBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}