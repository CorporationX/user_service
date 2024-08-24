package school.faang.user_service.service.avatars;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.dicebear.DicebearClient;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private static final String DEFAULT_AVATARS_FOLDER = "default-avatars";

    @Value("${dicebear.avatar.styles}")
    private List<String> avatarStyles;

    private final S3Service s3Service;
    private final DicebearClient dicebearClient;
    private final UserRepository userRepository;

    @Async("taskExecutor")
    public void generateAndStoreAvatar(long userId) {
        try {
            var randomStyle = getRandomStyle();
            byte[] svgBytes = dicebearClient.getAvatar(randomStyle);
            var fileKey = s3Service.uploadFileAsByteArray(svgBytes, DEFAULT_AVATARS_FOLDER, String.format("%d-%s.svg", System.currentTimeMillis(), randomStyle));
            updateUserAvatarLink(userId, fileKey);
        } catch (Exception e) {
            log.error("Error while generating avatar for user with id: {}", userId, e);
        }
    }

    private String getRandomStyle() {
        var randomIndex = ThreadLocalRandom.current().nextInt(avatarStyles.size());
        return avatarStyles.get(randomIndex);
    }

    private void updateUserAvatarLink(long userId, String imageUrl) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND, userId)));
        var profilePicData = UserProfilePic.builder()
                .fileId(imageUrl)
                .smallFileId(imageUrl)
                .build();
        user.setUserProfilePic(profilePicData);
        userRepository.save(user);
    }
}
