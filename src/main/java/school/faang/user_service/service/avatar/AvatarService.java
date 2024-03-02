package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.s3.S3Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {
    private final RestTemplate restTemplate;
    private final S3Service s3Service;

    @Value("${services.s3.random-avatar.url}")
    private String avatarServiceUrl;

    public Optional<UserProfilePic> generateAndSaveAvatar(User user) {
        try {
            generateAvatarUrl(user.getUsername());
            byte[] avatarImage = downloadAvatarImage(avatarServiceUrl);
            String s3FileUrl = uploadAvatarImage(avatarImage, user.getUsername());
            UserProfilePic userProfilePic = new UserProfilePic();
            userProfilePic.setFileId(s3FileUrl);
            return Optional.of(userProfilePic);
        } catch (Exception e) {
            log.error("File generation failed", e);
            return Optional.empty();
        }
    }

    private String generateAvatarUrl(String userName) {
        return String.format("%s%s", avatarServiceUrl, userName);
    }

    private byte[] downloadAvatarImage(String avatarUrl) {
        return restTemplate.getForObject(avatarUrl, byte[].class);
    }

    private String uploadAvatarImage(byte[] avatarImage, String userName) {
        return s3Service.uploadFile(avatarImage, userName);
    }
}