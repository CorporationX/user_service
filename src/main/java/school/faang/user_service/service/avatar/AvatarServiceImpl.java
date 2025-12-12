package school.faang.user_service.service.avatar;

import com.amazonaws.SdkClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.s3.S3service;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableRetry
public class AvatarServiceImpl implements AvatarService {
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024;

    private final RandomAvatarService randomAvatarService;
    private final UserRepository userRepository;
    private final S3service s3service;

    @Override
    @Transactional
    public UserProfilePic uploadAvatar(long userId, MultipartFile file) {
        log.info("Received request to upload avatar for user ID: {}", userId);
        User user = userRepository.getByIdOrThrow(userId);
        deleteOldAvatarFiles(user);

        if (file.isEmpty() || file.getSize() > MAX_AVATAR_SIZE) {
            throw new DataValidationException("File size exceeds the maximum limit of "
                    + MAX_AVATAR_SIZE / 1024L / 1024L + " MB.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new DataValidationException("Invalid file type. Only images are allowed.");
        }

        UserProfilePic newProfilePic = s3service.uploadAvatar(userId, file);

        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic == null) {
            userProfilePic = new UserProfilePic();
            log.debug("Creating new UserProfilePic entity for user ID: {}", userId);
        }
        userProfilePic.setFileId(newProfilePic.getFileId());
        userProfilePic.setSmallFileId(newProfilePic.getSmallFileId());
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
        log.info("Successfully saved avatar details for user ID: {}", userId);

        return userProfilePic;
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
    public UserProfilePic deleteAvatar(long userId) {
        log.info("Received request to delete avatar for user ID: {}", userId);
        User user = userRepository.getByIdOrThrow(userId);
        deleteOldAvatarFiles(user);

        UserProfilePic randomAvatar = randomAvatarService.generateRandomAvatarForUser(user.getUsername());
        user.setUserProfilePic(randomAvatar);
        log.info("Generated new random avatar URL for user ID: {}", userId);

        userRepository.save(user);
        log.info("Successfully set random avatar for user ID: {}", userId);
        return randomAvatar;
    }

    @Retryable(retryFor = {SdkClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    private void deleteOldAvatarFiles(User user) {
        UserProfilePic oldPic = user.getUserProfilePic();
        if (oldPic != null && oldPic.getFileId() != null && !oldPic.getFileId().startsWith("http")) {
            log.info("Deleting old avatar for user ID: {}. File keys: {}, {}",
                    user.getId(), oldPic.getFileId(), oldPic.getSmallFileId());
            s3service.deleteFileFromS3(oldPic.getFileId());
            s3service.deleteFileFromS3(oldPic.getSmallFileId());
        }
    }
}