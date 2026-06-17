package school.faang.user_service.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.properties.ProfilePicProperties;
import school.faang.user_service.config.properties.S3Properties;
import school.faang.user_service.dto.response.UploadAvatarResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarNotFoundException;
import school.faang.user_service.exception.AvatarProcessingException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserAvatarService;
import school.faang.user_service.validator.userAvatar.UserAvatarValidator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarServiceImpl implements UserAvatarService {

    private final UserRepository userRepository;
    private final AmazonS3 s3Client;
    private final S3Properties s3Properties;
    private final ProfilePicProperties profilePicProperties;
    private final UserAvatarValidator userAvatarValidator;

    @Override
    @Transactional
    public UploadAvatarResponseDto uploadAvatar(Long userId, MultipartFile file) {
        userAvatarValidator.validateFile(file);
        User user = getUser(userId);
        deleteExistingAvatars(user);

        try {
            String largeAvatarKey =
                    processAndUploadImage(file, profilePicProperties.getLargePhotoSize());
            String smallAvatarKey =
                    processAndUploadImage(file, profilePicProperties.getSmallPhotoSize());

            user.setUserProfilePic(new UserProfilePic(largeAvatarKey, smallAvatarKey));
            userRepository.save(user);
            log.info("Avatar upload completed successfully for user id: {}", userId);

            return new UploadAvatarResponseDto(largeAvatarKey, smallAvatarKey);

        } catch (IOException e) {
            log.error("Error processing avatar for user {}", userId, e);
            throw new AvatarProcessingException("Error processing image", e);
        }
    }

    @Override
    public InputStreamResource downloadLargeAvatar(Long userId) {
        return downloadAvatar(userId, false);
    }

    @Override
    public InputStreamResource downloadSmallAvatar(Long userId) {
        return downloadAvatar(userId, true);
    }

    @Override
    @Transactional
    public void deleteAvatar(Long userId) {
        User user = getUser(userId);
        log.info("Deleting avatar for user {}", userId);
        deleteExistingAvatars(user);
    }

    private User getUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found with ID %d", userId)));
    }

    private void deleteExistingAvatars(User user) {
        if (user.getUserProfilePic() != null) {
            deleteFromS3(user.getUserProfilePic().getFileId());
            deleteFromS3(user.getUserProfilePic().getSmallFileId());
            user.setUserProfilePic(null);
            userRepository.save(user);
        } else {
            log.info("User {} has no avatar to delete", user.getId());
        }
    }

    private void deleteFromS3(String fileKey) {
        if (fileKey == null) {
            return;
        }

        try {
            if (s3Client.doesObjectExist(s3Properties.getBucketName(), fileKey)) {
                s3Client.deleteObject(s3Properties.getBucketName(), fileKey);
                log.info("Deleted old avatar file from S3: {}", fileKey);
            } else {
                log.warn("File {} does not exist in S3", fileKey);
            }
        } catch (RuntimeException e) {
            log.error("Failed to delete file from S3: {}, reason: {}", fileKey, e.getMessage(), e);
        }
    }

    private String processAndUploadImage(MultipartFile file, int size) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            BufferedImage image =
                    Thumbnails.of(file.getInputStream())
                            .size(size, size)
                            .outputFormat("jpg")
                            .asBufferedImage();

            ImageIO.write(image, "jpg", os);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");
            metadata.setContentLength(os.size());

            String key = UUID.randomUUID().toString();

            s3Client.putObject(
                    new PutObjectRequest(
                            s3Properties.getBucketName(),
                            key,
                            new ByteArrayInputStream(os.toByteArray()),
                            metadata));

            return key;
        }
    }

    private InputStreamResource downloadAvatar(Long userId, boolean isSmall) {
        User user = getUser(userId);

        if (user.getUserProfilePic() == null) {
            throw new AvatarNotFoundException("Avatar not found for user with ID " + userId);
        }

        String avatarKey =
                isSmall
                        ? user.getUserProfilePic().getSmallFileId()
                        : user.getUserProfilePic().getFileId();

        log.info("Downloading {} avatar from S3, key: {}, user id: {}",
                isSmall ? "small" : "large", avatarKey, userId);

        S3Object s3Object = s3Client.getObject(s3Properties.getBucketName(), avatarKey);
        return new InputStreamResource(s3Object.getObjectContent());
    }
}
