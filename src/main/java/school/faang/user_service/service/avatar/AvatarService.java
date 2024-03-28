package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarService {

    private final UserRepository userRepository;

    private final AmazonS3 s3Client;

    @Value("${services.s3.large_photo}")
    private int large_photo;

    @Value("${services.s3.small_photo}")
    private int small_photo;

    @Value("${services.s3.bucketName}")
    private String s3BucketName;

    public UserProfilePic uploadAvatar(Long userId, MultipartFile file) {
        User user = getUser(userId);
        String uniqueSmallPicName = file.getOriginalFilename() + "_small" + System.currentTimeMillis();
        String uniqueLargePicName = file.getOriginalFilename() + "_large" + System.currentTimeMillis();

        putObjectInCloud(compressorPhoto(file, small_photo), uniqueSmallPicName);
        putObjectInCloud(compressorPhoto(file, large_photo), uniqueLargePicName);

        UserProfilePic userProfilePic = new UserProfilePic(uniqueLargePicName, uniqueSmallPicName);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
        log.info(String.format("User with id %s upload avatar", userId));
        return userProfilePic;
    }

    public ResponseEntity<byte[]> getAvatar(Long userId) {
        User user = getUser(userId);
        S3Object object = s3Client.getObject(s3BucketName, user.getUserProfilePic().getFileId());
        try(S3ObjectInputStream objectInputStream = object.getObjectContent()) {
            byte[] arrayBytePhoto = objectInputStream.readAllBytes();
            log.info(String.format("User with id %s getting avatar", userId));
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(arrayBytePhoto);
        } catch (IOException e) {
            log.error("Exception on method getAvatar");
            throw new RuntimeException(e);
        }
    }

    public void deleteAvatar(Long userId) {
        User user = getUser(userId);
        s3Client.deleteObject(s3BucketName, user.getUserProfilePic().getFileId());
        s3Client.deleteObject(s3BucketName, user.getUserProfilePic().getSmallFileId());
        UserProfilePic userProfilePic = new UserProfilePic(null, null);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
    }


    private void putObjectInCloud(ByteArrayInputStream inputStream, String fileName) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, fileName, inputStream, null);
        s3Client.putObject(putObjectRequest);
        log.info("Upload photo: " + fileName);
    }

    private ByteArrayInputStream compressorPhoto(MultipartFile file, int size) {
        BufferedImage resizeImage = null;
        try {
            resizeImage = Thumbnails.of(file.getInputStream())
                    .size(size, size)
                    .outputQuality(1.0)
                    .asBufferedImage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(resizeImage, "jpg", outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id: %s not found", userId)));
    }
}
