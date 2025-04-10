package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.properties.ProfilePicProperties;
import school.faang.user_service.config.properties.S3Properties;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.*;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarService {

    private final UserRepository userRepository;
    private final AmazonS3 s3Client;
    private final S3Properties s3Properties;
    private final ProfilePicProperties profilePicProperties;

    @Transactional
    public void uploadAvatar(Long userId, MultipartFile file) {
        validateFile(file);
        User user = getUser(userId);
        deleteExistingAvatars(user);

        try {
            String largeAvatarKey =
                    processAndUploadImage(file, profilePicProperties.getLargePhotoSize());
            String smallAvatarKey =
                    processAndUploadImage(file, profilePicProperties.getSmallPhotoSize());

            user.setUserProfilePic(new UserProfilePic(largeAvatarKey, smallAvatarKey));
            userRepository.save(user);
        } catch (IOException e) {
            log.error("Error processing avatar for user {}", userId, e);
            throw new AvatarProcessingException("Error processing image", e);
        }
    }

    public InputStreamResource downloadLargeAvatar(Long userId) {
        return downloadAvatar(userId, false);
    }

    public InputStreamResource downloadSmallAvatar(Long userId) {
        return downloadAvatar(userId, true);
    }

    @Transactional
    public void deleteAvatar(Long userId) {
        User user = getUser(userId);
        deleteExistingAvatars(user);
    }

    private InputStreamResource downloadAvatar(Long userId, boolean isSmall) {
        User user = getUser(userId);

        if (user.getUserProfilePic() == null) {
            throw new AvatarNotFoundException("Avatar not found for user " + userId);
        }

        String avatarKey =
                isSmall
                        ? user.getUserProfilePic().getSmallFileId()
                        : user.getUserProfilePic().getFileId();

        S3Object s3Object = s3Client.getObject(s3Properties.getBucketName(), avatarKey);
        return new InputStreamResource(s3Object.getObjectContent());
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileSizeException("File is empty");
        }
        if (file.getSize() > profilePicProperties.getMaxSize()) {
            throw new FileSizeException("File size exceeds the limit");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new InvalidFileTypeException("Only images are allowed");
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

    private void deleteExistingAvatars(User user) {
        if (user.getUserProfilePic() != null) {
            deleteFromS3(user.getUserProfilePic().getFileId());
            deleteFromS3(user.getUserProfilePic().getSmallFileId());
            user.setUserProfilePic(null);
            userRepository.save(user);
        }
    }

    private void deleteFromS3(String fileKey) {
        if (fileKey != null && s3Client.doesObjectExist(s3Properties.getBucketName(), fileKey)) {
            s3Client.deleteObject(s3Properties.getBucketName(), fileKey);
        }
    }

    private User getUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));
    }
}
