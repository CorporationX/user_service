package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataGettingException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.amazonS3.S3Service;
import school.faang.user_service.service.user.image.BufferedPicHolder;
import school.faang.user_service.service.user.image.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static school.faang.user_service.exception.ExceptionMessage.FILE_PROCESSING_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NO_SUCH_USER_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.USER_AVATAR_ABSENCE_EXCEPTION;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    public static final String BIG_PIC_NAME = "bigPic";
    public static final String SMALL_PIC_NAME = "smallPic";
    public static final String FOLDER_PREFIX = "user";
    private S3Service s3Service;
    private ImageProcessor imageProcessor;
    private UserRepository userRepository;
    private UserMapper userMapper;

    @Transactional
    public UserDto uploadUserPic(Long userId, BufferedImage uploadedImage) {
        User user = getUser(userId);

        BufferedPicHolder scaledImages = imageProcessor.scaleImage(uploadedImage);
        log.info("Received avatar image was successfully scaled.");

        String fileId = uploadFile(userId, imageProcessor.getImageOS(scaledImages.getBigPic()), BIG_PIC_NAME);
        String smallFileId = uploadFile(userId, imageProcessor.getImageOS(scaledImages.getSmallPic()), SMALL_PIC_NAME);

        log.info("Scaled images of user avatar were uploaded on cloud.");

        if (user.getUserProfilePic() != null) {
            deleteUserPic(userId);
        }

        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(fileId)
                .smallFileId(smallFileId)
                .build());

        User updatedUser = userRepository.save(user);

        log.info("Keys of uploaded images were saved in database.");
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public byte[] downloadUserPic(Long userId) {
        User user = getUser(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();

        if (userProfilePic == null) {
            log.warn("Can't download user's avatar, cause user doesn't have it.");
            throw new DataGettingException(USER_AVATAR_ABSENCE_EXCEPTION.getMessage());
        }

        try (InputStream userPicIS = s3Service.downloadFile(userProfilePic.getSmallFileId())) {
            byte[] imageInBytesArray = userPicIS.readAllBytes();

            log.info("User's avatar image was downloaded successfully.");
            return imageInBytesArray;

        } catch (IOException e) {
            log.error(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
            throw new RuntimeException(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
        }
    }

    @Transactional
    public void deleteUserPic(Long userId) {
        User user = getUser(userId);
        if (user.getUserProfilePic() == null) {
            log.warn("Can't delete user's avatar, cause user doesn't have it.");

            throw new DataGettingException(USER_AVATAR_ABSENCE_EXCEPTION.getMessage());
        }

        s3Service.deleteFile(user.getUserProfilePic().getFileId());
        s3Service.deleteFile(user.getUserProfilePic().getSmallFileId());

        user.setUserProfilePic(null);

        log.info("User's avatar images were deleted successfully.");
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(NO_SUCH_USER_EXCEPTION.getMessage() + "UserId = " + userId);
                    return new DataGettingException(NO_SUCH_USER_EXCEPTION.getMessage());
                });
    }

    private String uploadFile(Long userId, ByteArrayOutputStream outputStream, String fileName) {
        String key = String.format("%s/%d%s", FOLDER_PREFIX + userId, System.currentTimeMillis(), fileName);
        s3Service.uploadFile(outputStream, key);
        return key;
    }
}
