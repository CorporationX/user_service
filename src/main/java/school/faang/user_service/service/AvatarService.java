package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.DiceBearClient;

@RequiredArgsConstructor
@Slf4j
@Service
public class AvatarService {

    private final DiceBearClient diceBearClient;
    private final S3Service s3Service;

    public void generateAndSaveAvatar(String username) {
        String style = "adventurer";
        String fileName = "avatars/" + username + ".svg";

        try {
            log.info("Generating avatar for user: {}", username);
            byte[] avatar = diceBearClient.generateAvatar(style, username);

            s3Service.uploadFile(avatar, fileName);

            log.info("Avatar successfully saved for user: {}", username);
        } catch (Exception e) {
            log.error("Failed to generate and save avatar for user: {}", username, e);
            throw new RuntimeException("Failed to generate and save avatar for user: " + username, e);
        }
    }
}