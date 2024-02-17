package school.faang.user_service.service.profilePic;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class ProfilePicService {
    private final UserRepository userRepository;
    private final AmazonS3 s3Client;
    @Value("${services.s3.bucketName}")
    private String bucketName;
    @Value("${services.s3.smallSize}")
    private int smallSize;
    @Value("${services.s3.largeSize}")
    private int largeSize;

    @SneakyThrows
    public UserProfilePic userAddProfilePic(long userId, MultipartFile file) {
        String uniquePicName = file.getOriginalFilename() + "_" + System.currentTimeMillis();

        String urlProfilePic = putProfilePic(file, uniquePicName, smallSize);
        String urlSmallProfilePic = putProfilePic(file, uniquePicName, largeSize);

        UserProfilePic userProfilePic = new UserProfilePic(urlProfilePic, urlSmallProfilePic);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User " + userId + "не обнаружен"));
        user.setUserProfilePic(userProfilePic);
        return userProfilePic;
    }

    @SneakyThrows
    private String putProfilePic(MultipartFile file, String uniquePicName, int size) {
        s3Client.putObject(bucketName,
                size + "/" + uniquePicName,
                compressPic(file, size),
                null);
        return s3Client.getUrl(bucketName, uniquePicName).toString();
    }

    @SneakyThrows
    private ByteArrayInputStream compressPic(MultipartFile file, int size) {
        BufferedImage bufferedImage = Thumbnails.of(file.getInputStream())
                .size(size, size)
                .asBufferedImage();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return new ByteArrayInputStream(imageBytes);
    }
}
