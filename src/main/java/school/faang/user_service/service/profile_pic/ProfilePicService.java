package school.faang.user_service.service.profile_pic;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.dto.event.EventProfilePic;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.handler.exception.FileOperationException;
import school.faang.user_service.mapper.user_profile_pic.UserProfilePicMapper;
import school.faang.user_service.publisher.AbstractEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePicService {

    private final UserService userService;

    private final UserRepository userRepository;

    private final AmazonS3 s3Client;

    private final UserProfilePicMapper userProfilePicMapper;

    private final AbstractEventPublisher<EventProfilePic> profilePicEventPublisher;

    @Value("${services.s3.large_photo}")
    private int large_photo;

    @Value("${services.s3.small_photo}")
    private int small_photo;

    @Value("${services.s3.bucketName}")
    private String s3BucketName;

    public UserProfilePicDto uploadAvatar(Long userId, MultipartFile file) {
        User user = userService.getUser(userId);
        String uniqueSmallPicName = file.getOriginalFilename() + "_small" + System.currentTimeMillis();
        String uniqueLargePicName = file.getOriginalFilename() + "_large" + System.currentTimeMillis();

        putObjectInCloud(compressorPhoto(file, small_photo), uniqueSmallPicName);
        putObjectInCloud(compressorPhoto(file, large_photo), uniqueLargePicName);

        UserProfilePic userProfilePic = new UserProfilePic(uniqueLargePicName, uniqueSmallPicName);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
        EventProfilePic eventProfilePic = EventProfilePic.builder().userId(userId).fileId(userProfilePic.getFileId()).build();
        profilePicEventPublisher.publish(eventProfilePic);
        log.info(String.format("User with id %s upload avatar", userId));
        return userProfilePicMapper.toDto(userProfilePic);
    }

    public ResponseEntity<byte[]> downloadAvatarLarge(Long userId) {
        User user = userService.getUser(userId);
        S3Object object = s3Client.getObject(s3BucketName, user.getUserProfilePic().getFileId());
        log.info(String.format("User with id %s download large avatar", userId));
        return getAvatar(object);
    }

    public ResponseEntity<byte[]> downloadAvatarSmall(Long userId) {
        User user = userService.getUser(userId);
        S3Object object = s3Client.getObject(s3BucketName, user.getUserProfilePic().getSmallFileId());
        log.info(String.format("User with id %s download small avatar", userId));
        return getAvatar(object);
    }

    public void deleteAvatar(Long userId) {
        User user = userService.getUser(userId);
        s3Client.deleteObject(s3BucketName, user.getUserProfilePic().getFileId());
        s3Client.deleteObject(s3BucketName, user.getUserProfilePic().getSmallFileId());
        user.getUserProfilePic().setFileId(null);
        user.getUserProfilePic().setSmallFileId(null);
        log.info(String.format("User with id %s delete avatar", userId));
        userRepository.save(user);
    }

    private ResponseEntity<byte[]> getAvatar(S3Object object) {
        try (S3ObjectInputStream objectInputStream = object.getObjectContent()) {
            byte[] arrayBytePhoto = objectInputStream.readAllBytes();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(arrayBytePhoto);
        } catch (IOException e) {
            log.error("Exception on method getAvatar");
            throw new RuntimeException(e);
        }
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
            e.printStackTrace();
            log.error("File read exception on method compressorPhoto");
            throw new FileOperationException("File read exception");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(resizeImage, "jpg", outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("File write exception on method compressorPhoto");
            throw new FileOperationException("File write exception");
        }
    }
}
