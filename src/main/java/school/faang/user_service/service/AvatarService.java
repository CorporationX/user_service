package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@Setter
@RequiredArgsConstructor
public class AvatarService {

    @Value("${services.s3.bucket-name}")
    private String BUCKET_NAME;

    @Value("${services.avatar.pattern}")
    private String AVATAR_ID_PATTERN;

    @Value("${services.avatar.small-pattern}")
    private String SMALL_AVATAR_ID_PATTERN;

    @Value("${dice-bear.url}/${dice-bear.version}/%s/${dice-bear.file-type}?${dice-bear.params}")
    private String GENERATION_URL_PATTERN;

    @Value("${dice-bear.file-type}")
    private String EXTENSION;

    @Value("${services.avatar.small-file-width}")
    private int SMALL_FILE_WIDTH;

    @Value("${services.avatar.small-file-height}")
    private int SMALL_FILE_HEIGHT;

    @Value("${services.avatar.seed-range}")
    private int SEED_RANGE;

    @Value("${dice-bear.styles}")
    private String[] STYLES;

    @Value("${services.avatar.content-type}")
    private String CONTENT_TYPE;

    private final S3Service s3Service;
    private final UtilsService utilsService;
    private final RestTemplate restTemplate;

    public User setRandomAvatar(User user) {
        int styleIndex = ThreadLocalRandom.current().nextInt(0, STYLES.length);
        int seed = ThreadLocalRandom.current().nextInt(0, SEED_RANGE);
        String avatarUrl = String.format(GENERATION_URL_PATTERN, STYLES[styleIndex], seed);
        String fileName = String.format(AVATAR_ID_PATTERN, user.getId(), EXTENSION);
        String smallFileName = String.format(SMALL_AVATAR_ID_PATTERN, user.getId(), EXTENSION);

        byte[] avatar = getImageByUrl(avatarUrl);
        byte[] smallAvatar = utilsService.resizeImage(avatar, SMALL_FILE_WIDTH, SMALL_FILE_HEIGHT, EXTENSION);

        s3Service.uploadToS3(fileName, avatar, CONTENT_TYPE, BUCKET_NAME);
        s3Service.uploadToS3(smallFileName, smallAvatar, CONTENT_TYPE, BUCKET_NAME);

        user.setUserProfilePic(new UserProfilePic(fileName, smallFileName));
        return user;
    }

    private byte[] getImageByUrl(String url) {
        try {
            return restTemplate.getForObject(url, byte[].class);
        } catch (RestClientException e) {
            String errMessage = String.format("Could not get image from URL: %s", url);
            log.error(errMessage, e);
            throw new RuntimeException();
        }
    }
}
