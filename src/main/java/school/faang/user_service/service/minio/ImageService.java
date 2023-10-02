package school.faang.user_service.service.minio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.ByteMultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.NotEnoughMemoryInDB;
import school.faang.user_service.service.UserService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    private final UserService userService;
    private final MinioServiceImpl minioService;
    @Value("${file-size.user.image.common}")
    private int maxSideBigField;
    @Value("${file-size.user.image.small}")
    private int maxSideSmallField;

    @Retryable(maxAttempts = 3)
    @Transactional
    public UserProfilePic addImage(Long userId, MultipartFile file) {
        User user = userService.findUserByIdOptimisticLock(userId);
        MultipartFile bigImage = resizePicture(file, maxSideBigField, maxSideBigField);
        MultipartFile smallImage = resizePicture(file, maxSideSmallField, maxSideSmallField);
        BigInteger storageForImages = BigInteger.valueOf(bigImage.getSize() + smallImage.getSize());
        BigInteger requiredStorage = user.getStorageSize().add(storageForImages);

        checkAvailableStorage(user.getMaxStorageSize(), storageForImages);

        String folder = user.getId() + user.getUsername();

        String bigImageKey = minioService.uploadFile(bigImage, folder);
        String smallImageKey = minioService.uploadFile(smallImage, folder) + "small";
        UserProfilePic userProfilePic = new UserProfilePic(bigImageKey, smallImageKey);

        user.setUserProfilePic(userProfilePic);
        user.setStorageSize(requiredStorage);
        userService.saveUser(user);

        return userProfilePic;
    }

    @Transactional(readOnly = true)
    public InputStream downloadImage(Long userId) {
        User user = userService.findUserById(userId);
        return minioService.downloadFile(user.getUserProfilePic().getFileId());
    }

    @Transactional
    public void deleteImage(Long userId) {
        User user = userService.findUserById(userId);
        minioService.deleteFile(user.getUserProfilePic().getFileId());
        minioService.deleteFile(user.getUserProfilePic().getSmallFileId());
        user.setUserProfilePic(new UserProfilePic());
        user.setStorageSize(BigInteger.ZERO);
        userService.saveUser(user);
    }

    private void checkAvailableStorage(BigInteger maxStorage, BigInteger requiredStorage) {
        if (maxStorage.compareTo(requiredStorage) < 0) {
            throw new NotEnoughMemoryInDB("There is no available storage");
        }
    }

    private MultipartFile resizePicture(MultipartFile inputFile, int height, int width) {
        BufferedImage originalImage = null;
        BufferedImage resizedImage = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            originalImage = ImageIO.read(new ByteArrayInputStream(inputFile.getBytes()));
            height = Math.min(height, originalImage.getHeight());
            width = Math.min(width, originalImage.getWidth());
            resizedImage = Thumbnails.of(originalImage)
                    .size(width, height)
                    .asBufferedImage();
            ImageIO.write(resizedImage, "png", baos);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error in resize picture");
        }

        return new ByteMultipartFile(
                baos.toByteArray(),
                inputFile.getName(),
                inputFile.getOriginalFilename(),
                inputFile.getContentType());
    }
}
