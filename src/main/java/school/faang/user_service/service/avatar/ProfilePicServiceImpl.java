package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfilePicServiceImpl implements ProfilePicService {
    @Value("${randomAvatar.url}")
    private String url;
    @Value("${services.s3.bucket-name}")
    private String bucketName;
    @Value("${services.s3.smallSize}")
    private int smallSize;
    @Value("${services.s3.largeSize}")
    private int largeSize;
    private final AmazonS3 s3Client;
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
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");

        InputStream inputStream = new ByteArrayInputStream(image);
        s3Client.putObject(bucketName, nameForSmallPic, compressPic(inputStream, smallSize), metadata);
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setSmallFileId(nameForSmallPic);
        user.setUserProfilePic(userProfilePic);
    }
}
