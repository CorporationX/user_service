package school.faang.user_service.service.avatar;

import com.amazonaws.SdkClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.service.s3.S3service;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RandomAvatarServiceImpl implements RandomAvatarService {

    private final S3service s3service;
    private final RestTemplate restTemplate;

    @Value("${avatar.dicebear.base-url}")
    private String dicebearBaseUrl;
    @Value("${avatar.dicebear.default-size}")
    private int dicebearDefaultSize;

    private static final int SMALL_SIZE = 64;
    private static final int BIG_SIZE = 128;
    private static final int MAX_AVATAR_SIZE_BYTES = 2_000_000;

    @Override
    public UserProfilePic generateRandomAvatarForUser(String username) {
        String randomSeed = UUID.randomUUID().toString();
        try {
            byte[] avatarBytes = fetchDiceBearAvatar(randomSeed);
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(avatarBytes));
            if (originalImage == null) {
                log.warn("DiceBear returned bytes but ImageIO.read() produced null for seed {}", randomSeed);
                return serveFallbackAvatar(username);
            }

            String bigKey = "random-user-avatars/" + username + "/big-" + randomSeed + ".png";
            String smallKey = "random-user-avatars/" + username + "/small-" + randomSeed + ".png";

            byte[] bigBytes = imageToPngBytes(resizeImage(originalImage, BIG_SIZE));
            byte[] smallBytes = imageToPngBytes(resizeImage(originalImage, SMALL_SIZE));

            s3service.uploadFileToS3(bigBytes, bigKey);
            s3service.uploadFileToS3(smallBytes, smallKey);

            UserProfilePic userProfilePic = new UserProfilePic();
            userProfilePic.setFileId(bigKey);
            userProfilePic.setSmallFileId(smallKey);

            log.info("Avatars uploaded to S3 for user {}", username);
            return userProfilePic;

        } catch (Exception e) {
            log.error("Failed to generate or upload avatars for user {}: {}", username, e.getMessage(), e);
            return serveFallbackAvatar(username);
        }
    }

    @Retryable(
            retryFor = {RestClientException.class, SdkClientException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    private byte[] fetchDiceBearAvatar(String seed) {
        String url = String.format("%s?size=%d&seed=%s", dicebearBaseUrl, dicebearDefaultSize, seed);
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        byte[] avatarBytes = response.getBody();
        validateAvatarBytes(avatarBytes, seed);
        log.info("Random avatar fetched from DiceBear: {}", url);
        return avatarBytes;
    }

    private void validateAvatarBytes(byte[] bytes, String seed) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalStateException("Empty avatar from DiceBear for seed " + seed);
        }
        if (bytes.length > MAX_AVATAR_SIZE_BYTES) {
            throw new IllegalStateException("Avatar too large for seed " + seed);
        }
    }

    private byte[] imageToPngBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bytes);
        return bytes.toByteArray();
    }

    private BufferedImage resizeImage(BufferedImage original, int size) {
        Image scaled = original.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    private UserProfilePic serveFallbackAvatar(String username) {
        String fallbackUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/default-user-avatar.png")
                .toUriString();

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(fallbackUrl);
        userProfilePic.setSmallFileId(fallbackUrl);

        log.info("Fallback avatars served from static for user {}", username);
        return userProfilePic;
    }
}
