package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataGettingException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.S3Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static school.faang.user_service.exception.ExceptionMessage.FILE_PROCESSING_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NO_SUCH_USER_EXCEPTION;

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

        List<BufferedImage> scaledImages = imageProcessor.scaleImage(uploadedImage);

        String fileId = getFileId(userId, imageProcessor.getImageOS(scaledImages.get(0)), BIG_PIC_NAME);
        String smallFileId = getFileId(userId, imageProcessor.getImageOS(scaledImages.get(1)), SMALL_PIC_NAME);

        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(fileId)
                .smallFileId(smallFileId)
                .build());

        return userMapper.toDto(userRepository.save(user));
    }

    public byte[] downloadUserPic(Long userId) {
        User user = getUser(userId);

        try (InputStream userPicIS = s3Service.downloadFile(user.getUserProfilePic().getFileId())) {
            return userPicIS.readAllBytes();

        } catch (IOException e) {
            throw new RuntimeException(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
        }
    }

    public void deleteUserPic(Long userId) {
        User user = getUser(userId);
        s3Service.deleteFile(user.getUserProfilePic().getFileId());
        s3Service.deleteFile(user.getUserProfilePic().getSmallFileId());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataGettingException(NO_SUCH_USER_EXCEPTION.getMessage()));
    }

    private String getFileId(Long userId, ByteArrayOutputStream outputStream, String fileName) {
        String key = String.format("%s/%d%s", FOLDER_PREFIX + userId, System.currentTimeMillis(), fileName);
        s3Service.uploadFile(outputStream, key);
        return key;
    }
}
