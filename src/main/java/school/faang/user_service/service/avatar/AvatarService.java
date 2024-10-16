package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.DefaultAvatarClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarService {
    private final DefaultAvatarClient defaultAvatarClient;
    private final S3Service s3Service;
    private final UserRepository userRepository;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${default-avatar-client.styleName}")
    private String styleName;

    @Value("${default-avatar-client.format}")
    private String format;

    @Value("${default-avatar-client.prefixFileName}")
    private String prefixFileName;

    public void createDefaultAvatarForUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            String message = "User with id = " + userId + " has not in system";
            return new DataValidationException(message);
        });
        if (user.getUserProfilePic() != null) {
            String message = "User with id = " + userId + " already has an avatar";
            throw new DataValidationException(message);
        }
        List<String> backgroundColors = getRandomBackGroundColorAvatar();
        ResponseEntity<byte[]> response = defaultAvatarClient.getAvatar(styleName, format, backgroundColors);
        HttpHeaders headers = response.getHeaders();
        loadFileInStorage(response.getBody(), user, headers.getContentLength(), headers.getContentType());
    }

    private List<String> getRandomBackGroundColorAvatar() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder backgroundColor = new StringBuilder();
        for (int j = 0; j < 6; j++) {
            backgroundColor.append(Integer.toHexString(random.nextInt(0, 15)).toLowerCase());
        }
        return new ArrayList<>(List.of(backgroundColor.toString()));
    }


    private void loadFileInStorage(byte[] file, User user, long contentLength, MediaType contentType) {
        long userId = user.getId();
        String key = prefixFileName + userId + "_" + System.currentTimeMillis();
        s3Service.uploadFile(key, file, bucketName, contentLength, contentType);
        UserProfilePic userPic = new UserProfilePic();
        userPic.setFileId(key);
        userPic.setSmallFileId(key);
        user.setUserProfilePic(userPic);
        userRepository.save(user);

        log.info("For a user with an id = " + userId + " added a default avatar " + key);
    }
}
