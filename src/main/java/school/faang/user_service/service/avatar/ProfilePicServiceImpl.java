package school.faang.user_service.service.avatar;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.cloud.S3Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@PropertySource(value = "classpath:s3.properties")
public class ProfilePicServiceImpl implements ProfilePicService {
    @Value("${randomAvatar.url}")
    private String url;
    @Value("${smallSize}")
    private int smallSize;
    @Value("${largeSize}")
    private int largeSize;
    private final S3Service s3Service;
    private final RestTemplate restTemplate;

    private InputStream compressPic(InputStream inputStream, int size) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BufferedImage scaledImage = Thumbnails.of(inputStream).size(size, size).asBufferedImage();
            ImageIO.write(scaledImage, "jpg", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = outputStream.toByteArray();

        return new ByteArrayInputStream(bytes);
    }

    @Override
    @Transactional
    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 3))
    public void generateAndSetPic(User user) {
        byte[] image = restTemplate.getForObject(url + user.getUsername(), byte[].class);
        if (image == null || image.length == 0) {
            throw new DataValidationException("Failed to get the generated image");
        }
        String nameForSmallPic = "small" + user.getUsername() + LocalDateTime.now() + ".jpg";

        InputStream inputStream = new ByteArrayInputStream(image);
        s3Service.uploadFile(compressPic(inputStream, smallSize), nameForSmallPic);
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setSmallFileId(nameForSmallPic);
        user.setUserProfilePic(userProfilePic);
    }
}
