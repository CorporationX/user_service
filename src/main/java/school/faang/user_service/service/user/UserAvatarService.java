package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.FileSizeException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarService {

    private final UserRepository userRepository;
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.maxSize}")
    private long maxFileSize;

    @Value("${services.s3.largePhotoSize}")
    private int largePhotoSize;

    @Value("${services.s3.smallPhotoSize}")
    private int smallPhotoSize;

    public void uploadAvatar(Long userId, MultipartFile file) {
        validateFile(file);

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getUserProfilePic() != null) {
            deleteExistingAvatars(user);
        }

        try {
            String largeKey = processAndUploadImage(file, largePhotoSize);
            String smallKey = processAndUploadImage(file, smallPhotoSize);
            user.setUserProfilePic(new UserProfilePic(largeKey, smallKey));
            userRepository.save(user);
        } catch (IOException e) {
            log.error("Error processing avatar for user {}", userId, e);
            throw new FileSizeException("Error processing image");
        }
    }

    public byte[] downloadLargeAvatar(Long userId) {
        return downloadAvatar(userId, false);
    }

    public byte[] downloadSmallAvatar(Long userId) {
        return downloadAvatar(userId, true);
    }

    public byte[] downloadAvatar(Long userId, boolean isSmall) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getUserProfilePic() == null) {
            throw new UserNotFoundException("Avatar not found for user " + userId);
        }

        String fileKey =
                isSmall
                        ? user.getUserProfilePic().getSmallFileId()
                        : user.getUserProfilePic().getFileId();

        try (S3Object s3Object = s3Client.getObject(bucketName, fileKey);
                S3ObjectInputStream stream = s3Object.getObjectContent()) {
            return stream.readAllBytes();
        } catch (IOException e) {
            log.error("Error downloading avatar for user {}", userId, e);
            throw new FileSizeException("Error downloading file");
        }
    }

    public void deleteAvatar(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getUserProfilePic() == null) {
            return;
        }

        deleteFromS3(user.getUserProfilePic().getFileId());
        deleteFromS3(user.getUserProfilePic().getSmallFileId());
        user.setUserProfilePic(null);
        userRepository.save(user);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileSizeException("File is empty");
        }
        if (file.getSize() > maxFileSize) {
            throw new FileSizeException("File size exceeds limit");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new FileSizeException("Only images are allowed");
        }
    }

    private String processAndUploadImage(MultipartFile file, int size) throws IOException {
        BufferedImage image =
                Thumbnails.of(file.getInputStream()).size(size, size).asBufferedImage();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        String key = UUID.randomUUID().toString();
        s3Client.putObject(bucketName, key, new ByteArrayInputStream(os.toByteArray()), null);
        return key;
    }

    private void deleteExistingAvatars(User user) {
        if (user.getUserProfilePic() != null) {
            deleteFromS3(user.getUserProfilePic().getFileId());
            deleteFromS3(user.getUserProfilePic().getSmallFileId());
        }
    }

    private void deleteFromS3(String fileKey) {
        if (fileKey != null) {
            s3Client.deleteObject(bucketName, fileKey);
        }
    }
}
