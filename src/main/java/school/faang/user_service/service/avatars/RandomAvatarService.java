package school.faang.user_service.service.avatars;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.dicebear.DicebearClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.util.multipart.MultipartFileFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class RandomAvatarService {

    private static final String DEFAULT_AVATARS_FOLDER = "default-avatars";

    @Value("${dicebear.avatar.styles}")
    private List<String> avatarStyles;

    private final S3Service s3Service;
    private final DicebearClient dicebearClient;
    private final UserRepository userRepository;

    @Async("taskExecutor")
    public void generateAndStoreAvatar(User user) {
        try {
            var randomStyle = getRandomStyle();
            byte[] svgBytes = dicebearClient.getAvatar(randomStyle);
            var filename = String.format("%d-%s.svg", System.currentTimeMillis(), randomStyle);
            var multipartFile = MultipartFileFactory.create(svgBytes, filename, filename, "image/svg+xml");
            var fileKey = s3Service.uploadFile(multipartFile, DEFAULT_AVATARS_FOLDER);
            updateUserAvatarLink(user, fileKey);
        } catch (Exception e) {
            log.error("Error while generating avatar for user with id: {}", user.getId(), e);
        }
    }

    private String getRandomStyle() {
        var randomIndex = ThreadLocalRandom.current().nextInt(avatarStyles.size());
        return avatarStyles.get(randomIndex);
    }

    private void updateUserAvatarLink(User user, String imageUrl) {
        var profilePicData = UserProfilePic.builder()
                .fileId(imageUrl)
                .smallFileId(imageUrl)
                .build();
        user.setUserProfilePic(profilePicData);
        userRepository.save(user);
    }
}
