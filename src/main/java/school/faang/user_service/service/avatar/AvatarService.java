package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DiceBearException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.MinioException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.minio.MinioService;
import school.faang.user_service.service.user.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static school.faang.user_service.exception.ErrorMessage.AVATAR_ALREADY_EXIST_ERROR;
import static school.faang.user_service.exception.ErrorMessage.AVATAR_NOT_FOUND;
import static school.faang.user_service.exception.ErrorMessage.DICE_BEAR_GENERATING_ERROR;
import static school.faang.user_service.exception.ErrorMessage.AVATAR_PROCESS_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {
    private static final String AVATAR_FILE_FORMAT = "%d.jpg";
    private static final String SMALL_AVATAR_FILE_FORMAT = "%d_small.jpg";
    private static final int AVATAR_CONSTRAINT = 1080;
    private static final int SMALL_AVATAR_CONSTRAINT = 170;
    private static final String DEFAULT_AVATAR_CONTENT_TYPE = "image/jpeg";

    private final MinioService minioService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final DiceBearService diceBearService;

    @Transactional(readOnly = true)
    public byte[] getOriginalUserAvatar(Long userId) {
        log.info("Start receiving original avatar for user ID: {}", userId);
        UserProfilePic userProfilePic = userRepository.findUserProfilePicByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(AVATAR_NOT_FOUND, userId)));
        return minioService.downloadFile(userProfilePic.getFileId());
    }

    @Transactional(readOnly = true)
    public byte[] getSmallUserAvatar(Long userId) {
        log.info("Start receiving small avatar for user ID: {}", userId);
        UserProfilePic userProfilePic = userRepository.findUserProfilePicByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(AVATAR_NOT_FOUND, userId)));
        return minioService.downloadFile(userProfilePic.getSmallFileId());
    }

    @Transactional
    public void deleteUserAvatar(Long userId) {
        log.info("Deleting avatar for user ID: {}", userId);
        UserProfilePic userProfilePic = userRepository.findUserProfilePicByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(AVATAR_NOT_FOUND, userId)));

        if (userProfilePic.getFileId() != null) {
            minioService.deleteFile(userProfilePic.getFileId());
        }
        if (userProfilePic.getSmallFileId() != null) {
            minioService.deleteFile(userProfilePic.getSmallFileId());
        }

        userRepository.deleteProfilePic(userId);
        log.info("Avatar successfully deleted for user ID: {}", userId);
    }

    @Transactional
    public UserProfilePic uploadUserAvatar(Long userId, MultipartFile file) {
        log.info("Start uploading avatar for user ID: {}", userId);
        checkAvatarExists(userId);

        byte[] avatarData;
        String contentType;

        if (file == null) {
            log.info("No file provided for user ID: {}. Generating random avatar.", userId);
            avatarData = diceBearService.getRandomAvatar(userId)
                    .orElseThrow(() -> new DiceBearException(DICE_BEAR_GENERATING_ERROR));
            contentType = DEFAULT_AVATAR_CONTENT_TYPE;
        } else {
            try {
                avatarData = file.getBytes();
                contentType = file.getContentType();
                log.info("Uploaded avatar received for user ID: {}. File size: {} bytes, Content type: {}", userId, avatarData.length, contentType);
            } catch (IOException e) {
                log.error("Failed to read the uploaded file for user ID: {}", userId, e);
                throw new RuntimeException("Failed to read the uploaded file", e);
            }
        }
        return processAndSaveAvatar(userId, avatarData, contentType);
    }

    private UserProfilePic processAndSaveAvatar(Long userId, byte[] avatarData, String contentType) {
        try {
            byte[] fileBytes = resizeImage(avatarData, AVATAR_CONSTRAINT);
            byte[] smallFileBytes = resizeImage(avatarData, SMALL_AVATAR_CONSTRAINT);

            String fileName = String.format(AVATAR_FILE_FORMAT, userId);
            String smallFileName = String.format(SMALL_AVATAR_FILE_FORMAT, userId);

            minioService.uploadFile(userId, fileName, fileBytes, contentType);
            minioService.uploadFile(userId, smallFileName, smallFileBytes, contentType);
            userRepository.updateProfilePic(userId, fileName, smallFileName);

            log.info("Avatar successfully uploaded for user ID: {}", userId);
            return new UserProfilePic(fileName, smallFileName);
        } catch (IOException e) {
            log.error("Error resizing and saving avatar for user ID: {}", userId, e);
            throw new RuntimeException(AVATAR_PROCESS_ERROR, e);
        }
    }

    private void checkAvatarExists(Long userId) {
        UserProfilePic userProfilePic = userService.getUserEntity(userId).getUserProfilePic();
        if (userProfilePic != null && (userProfilePic.getFileId() != null || userProfilePic.getSmallFileId() != null)) {
            throw new MinioException(String.format(AVATAR_ALREADY_EXIST_ERROR, userId));
        }
    }

    byte[] resizeImage(byte[] imageData, int size) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Thumbnails.of(inputStream)
                    .size(size, size)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
    }
}
