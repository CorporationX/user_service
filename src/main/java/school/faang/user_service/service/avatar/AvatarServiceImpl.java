package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Импорт для логирования
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.S3.S3service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AvatarServiceImpl implements AvatarService {
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024;
    private static final int BIG_AVATAR_SIZE = 1080;
    private static final int SMALL_AVATAR_SIZE = 170;

    private final UserRepository userRepository;
    private final S3service s3service;

    @Value("${avatar.dicebear.base-url}")
    private String dicebearBaseUrl;

    @Value("${avatar.dicebear.default-size}")
    private int dicebearDefaultSize;

    @Override
    @Transactional
    public UserProfilePic uploadAvatar(long userId, MultipartFile file) {
        log.info("Received request to upload avatar for user ID: {}", userId);
        User user = userRepository.getByIdOrThrow(userId);
        deleteOldAvatarFiles(user);

        if (file.isEmpty() || file.getSize() > MAX_AVATAR_SIZE) {
            throw new DataValidationException("File size exceeds the maximum limit of " + MAX_AVATAR_SIZE / 1024L / 1024L + " MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new DataValidationException("Invalid file type. Only images are allowed.");
        }

        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new DataValidationException("The provided file is corrupted or not a valid image.");
            }

            log.debug("Resizing images for user ID: {}", userId);
            BufferedImage resizedBig = resizeImage(originalImage, BIG_AVATAR_SIZE);
            BufferedImage resizedSmall = resizeImage(originalImage, SMALL_AVATAR_SIZE);

            byte[] bigImageBytes = imageToPngBytes(resizedBig);
            byte[] smallImageBytes = imageToPngBytes(resizedSmall);

            String bigFileKey = "avatars/" + userId + "/" + UUID.randomUUID() + ".png";
            String smallFileKey = "avatars/" + userId + "/" + UUID.randomUUID() + ".png";

            log.info("Uploading resized images to S3 for user ID: {}. Keys: {}, {}", userId, bigFileKey, smallFileKey);
            String fileId = s3service.uploadFileToS3(bigImageBytes, bigFileKey);
            String smallFileId = s3service.uploadFileToS3(smallImageBytes, smallFileKey);

            UserProfilePic userProfilePic = user.getUserProfilePic();
            if (userProfilePic == null) {
                userProfilePic = new UserProfilePic();
                log.debug("Creating new UserProfilePic entity for user ID: {}", userId);
            }
            userProfilePic.setFileId(fileId);
            userProfilePic.setSmallFileId(smallFileId);
            user.setUserProfilePic(userProfilePic);
            userRepository.save(user);
            log.info("Successfully saved avatar details for user ID: {}", userId);

            return userProfilePic;

        } catch (IOException e) {
            log.error("Error processing avatar file for user ID: {}", userId, e);
            throw new RuntimeException("Error processing avatar file.", e);
        } catch (Exception e) {
            log.error("Error uploading avatar to S3 for user ID: {}", userId, e);
            throw new RuntimeException("Error uploading avatar to S3.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadAvatar(long userId) {
        log.info("Received request to download avatar for user ID: {}", userId);
        User user = userRepository.getByIdOrThrow(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();

        if (userProfilePic == null || userProfilePic.getFileId() == null) {
            throw new EntityNotFoundException("Avatar not found for user ID: " + userId);
        }

        String fileKey = userProfilePic.getFileId();

        if (fileKey.startsWith("http")) {
            throw new DataValidationException("Cannot download the default avatar. Please use the provided URL.");
        }

        log.info("Downloading avatar from S3 with key: {} for user ID: {}", fileKey, userId);
        return s3service.downloadFileFromS3(fileKey);
    }

    @Override
    @Transactional
    public String deleteAvatar(long userId) {
        log.info("Received request to delete avatar for user ID: {}", userId);
        User user = userRepository.getByIdOrThrow(userId);
        deleteOldAvatarFiles(user);

        String defaultAvatarUrl = String.format("%s?seed=%s&size=%d",
                dicebearBaseUrl,
                user.getUsername(),
                dicebearDefaultSize
        );
        log.info("Generated new default avatar URL for user ID: {}", userId);

        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic == null) {
            userProfilePic = new UserProfilePic();
            log.debug("Creating new UserProfilePic entity for user ID: {} to set default avatar", userId);
        }
        userProfilePic.setFileId(defaultAvatarUrl);
        userProfilePic.setSmallFileId(defaultAvatarUrl); // Важно: для маленькой тоже
        user.setUserProfilePic(userProfilePic);

        userRepository.save(user);
        log.info("Successfully set default avatar for user ID: {}", userId);

        return defaultAvatarUrl;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetSize) {
        Scalr.Mode mode = originalImage.getWidth() > originalImage.getHeight() ?
                Scalr.Mode.FIT_TO_WIDTH : Scalr.Mode.FIT_TO_HEIGHT;
        return Scalr.resize(originalImage, Scalr.Method.QUALITY, mode, targetSize);
    }

    private byte[] imageToPngBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    /**
     * Приватный метод для удаления старых файлов аватара из S3, если они существуют и не являются дефолтными.
     * @param user пользователь, чей аватар нужно проверить и удалить.
     */
    private void deleteOldAvatarFiles(User user) {
        UserProfilePic oldPic = user.getUserProfilePic();
        if (oldPic != null && oldPic.getFileId() != null && !oldPic.getFileId().startsWith("http")) {
            log.info("Deleting old avatar for user ID: {}. File keys: {}, {}",
                    user.getId(), oldPic.getFileId(), oldPic.getSmallFileId());
            try {
                s3service.deleteFileFromS3(oldPic.getFileId());
                s3service.deleteFileFromS3(oldPic.getSmallFileId());
            } catch (Exception e) {
                log.error("Could not delete old avatar from S3 for user {}. Error: {}", user.getId(), e.getMessage());
            }
        }
    }
}