package school.faang.user_service.service.ProfilePic;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfilePicService {
    private final UserRepository userRepository;
    private final AmazonS3 s3Client;
    @Value("${services.s3.bucket-name}")
    private String bucketName;
    @Value("${services.s3.smallSize}")
    private int smallSize;
    @Value("${services.s3.largeSize}")
    private int largeSize;

    @SneakyThrows
    private InputStream compressPic(MultipartFile file, int size) {
        BufferedImage scaledImage = Thumbnails.of(file.getInputStream()).size(size, size).asBufferedImage();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(scaledImage, "jpg", outputStream);
        byte[] bytes = outputStream.toByteArray();

        return new ByteArrayInputStream(bytes);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " was not found"));
    }

    @SneakyThrows
    public UserProfilePic saveProfilePic(long userId, MultipartFile file) {
        User user = getUser(userId);

        String nameForSmallPic = "small" + file.getName() + LocalDateTime.now();
        String nameForLargePic = "large" + file.getName() + LocalDateTime.now();

        s3Client.putObject(bucketName, nameForSmallPic, compressPic(file, smallSize), null);
        s3Client.putObject(bucketName, nameForLargePic, compressPic(file, largeSize), null);

        UserProfilePic userProfilePic = new UserProfilePic(nameForLargePic, nameForSmallPic);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return userProfilePic;
    }

    public InputStreamResource getProfilePic(long userId) {
        User user = getUser(userId);
        S3Object s3Object = s3Client.getObject(bucketName, user.getUserProfilePic().getFileId());

        return new InputStreamResource(s3Object.getObjectContent());
    }

    public String deleteProfilePic(long userId) {
        User user = getUser(userId);
        s3Client.deleteObject(bucketName, user.getUserProfilePic().getFileId());
        s3Client.deleteObject(bucketName, user.getUserProfilePic().getSmallFileId());
        user.getUserProfilePic().setSmallFileId(null);
        user.getUserProfilePic().setFileId(null);
        userRepository.save(user);

        return "The user's avatar with the ID: " + userId + " has been successfully deleted";
    }
}
