package school.faang.user_service.service.Integrations.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.model.UserProfilePic;
import school.faang.user_service.service.S3Service;
import school.faang.user_service.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private final S3Service s3Service;
    private final ImageUtils imageUtils;

    private static final String FOLDER_NAME = "avatars";
    private static final int AVATAR_SMALL_SIZE = 170;
    private static final int AVATAR_BIG_SIZE = 1080;

    @Value("${integration.dice-bear.base-url}")
    private String baseUrl;

    @Value("${integration.dice-bear.styles}")
    private List<String> styles;

    @Value("${integration.dice-bear.seed-names}")
    private List<String> seedNames;

    @Value("${integration.dice-bear.version}")
    private String version;

    private final Random random = new Random();

    private void validateConfiguration() {
        if (styles.isEmpty() || seedNames.isEmpty() || baseUrl.isBlank() || version.isBlank()) {
            throw new IllegalStateException("Invalid DiceBear configuration");
        }
    }

    private String generateUrlRandomAvatar() {
        validateConfiguration();

        String randomStyle = styles.get(random.nextInt(styles.size()));
        String randomSeed = seedNames.get(random.nextInt(seedNames.size()));

        return String.format("%s/%s/%s/png?seed=%s", baseUrl, version, randomStyle, randomSeed);
    }

    public UserProfilePic generateAndUploadUserAvatars(String userId) {
        String avatarUrl = generateUrlRandomAvatar();
        log.info("Generated avatar URL: {}", avatarUrl);
        try (InputStream inputStream = new URL(avatarUrl).openStream()) {
            BufferedImage originalImage = ImageIO.read(inputStream);

            if (originalImage == null) {
                throw new RuntimeException(String.format("Failed to load avatar image from URL: %s", avatarUrl));
            }

            String largeFileId = uploadAvatar(originalImage, userId, false);
            String smallFileId = uploadAvatar(originalImage, userId, true);

            return UserProfilePic.builder()
                    .fileId(largeFileId)
                    .smallFileId(smallFileId)
                    .build();
        } catch (IOException e) {
            log.error("IOException while fetching or processing avatar: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save avatar to S3", e);
        }
    }

    public String uploadAvatar(BufferedImage image, String userId, boolean isSmall) {
        int size = isSmall ? AVATAR_SMALL_SIZE : AVATAR_BIG_SIZE;
        BufferedImage resizedImage = imageUtils.resizeImage(image, size);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(resizedImage, "png", outputStream);
            byte[] byteArray = outputStream.toByteArray();

            MultipartFile multipartFile = new CustomMultipartFile(
                    "avatar",
                    "avatar_user_" + userId + (isSmall ? "_small" : "_large") + ".png",
                    "image/png",
                    byteArray
            );

            return s3Service.uploadImage(multipartFile, FOLDER_NAME, multipartFile.getOriginalFilename(), resizedImage);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process and upload avatar", e);
        }
    }

}