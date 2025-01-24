package school.faang.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.DiceBearClient;

@Service
@Slf4j
public class AvatarService {
    private final DiceBearClient diceBearClient;
    private final S3Service s3Service;

    public AvatarService(DiceBearClient diceBearClient, S3Service s3Service) {
        this.diceBearClient = diceBearClient;
        this.s3Service = s3Service;
    }

    public String generateAndSaveAvatar(String username) {
        String style = "adventurer";

        try {
            log.info("Generating avatar for user: {}", username);
            byte[] avatar = diceBearClient.generateAvatar(style, username);

            String fileName = "avatars/" + username + ".svg";
            String avatarUrl = s3Service.uploadFile(avatar, fileName);

            log.info("Avatar successfully saved for user: {}", username);
            return avatarUrl;
        } catch (Exception e) {
            log.error("Failed to generate and save avatar for user: {}", username, e);
            throw new RuntimeException("Failed to generate and save avatar for user: " + username, e);
        }
    }
}