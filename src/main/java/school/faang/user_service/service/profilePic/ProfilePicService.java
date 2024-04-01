package school.faang.user_service.service.profilePic;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
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
import java.io.IOException;

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

    public UserProfilePic addProfilePic(long userId, MultipartFile file) {
        User user = getUser(userId);
        String uniqueSmallPicName = file.getOriginalFilename() + "_small" + System.currentTimeMillis();
        String uniqueLargePicName = file.getOriginalFilename() + "_large" + System.currentTimeMillis();

        putProfilePic(file, uniqueSmallPicName, smallSize);
        putProfilePic(file, uniqueLargePicName, largeSize);

        UserProfilePic userProfilePic = new UserProfilePic(uniqueSmallPicName, uniqueLargePicName);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
        return userProfilePic;
    }

    private void putProfilePic(MultipartFile file, String uniquePicName, int size) {
        s3Client.putObject(bucketName,
                uniquePicName,
                compressPic(file, size),
                null);
    }

    private ByteArrayInputStream compressPic(MultipartFile file, int size) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = Thumbnails.of(file.getInputStream())
                    .size(size, size)
                    .asBufferedImage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return new ByteArrayInputStream(imageBytes);
    }

    public ResponseEntity<InputStreamResource> getProfilePic(long userId) {
        S3Object s3Object = s3Client.getObject(bucketName, getUser(userId).getUserProfilePic().getFileId());
        try {
            return ResponseEntity.ok()
                    .body(new InputStreamResource(
                            new ByteArrayInputStream(s3Object.getObjectContent().readAllBytes())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<String> deleteProfilePic(long userId) {
        User user = getUser(userId);
        s3Client.deleteObject(bucketName, user.getUserProfilePic().getFileId());
        s3Client.deleteObject(bucketName, user.getUserProfilePic().getSmallFileId());
        user.getUserProfilePic().setFileId(null);
        user.getUserProfilePic().setSmallFileId(null);
        userRepository.save(user);
        return ResponseEntity.ok()
                .body(String.format("Аватар пользователя %d удалён", userId));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User %d не обнаружен", userId)));
    }
}