package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarUploadException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private final AvatarGenerator avatarGenerator;
    private final StorageManager storageManager;
    @Value("${storage.bucket}")
    private String bucketName;

    public void uploadAvatar(User user) {
        Resource avatar = avatarGenerator.generateByCode(user.getId());
        String filePath = createFilePath(avatar.getFilename());

        try (InputStream inputStream = avatar.getInputStream()) {
            storageManager.putObject(bucketName, filePath, inputStream);
            updateUserProfilePic(user, filePath);
        } catch (IOException e) {
            log.error("IOException occurred while uploading avatar for User ID: {}", user.getId(), e);
            throw new AvatarUploadException("Failed to upload avatar due to IO error", e);
        } catch (Exception e) {
            log.error("Failed to upload avatar for User ID: {}", user.getId(), e);
            throw new AvatarUploadException("Failed to upload avatar", e);
        }
    }

    private String createFilePath(String originalFilename) {
        return String.format("%s-%s", UUID.randomUUID().toString(), originalFilename);
    }

    private void updateUserProfilePic(User user, String filePath) {
        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(filePath)
                .build());
    }
}
