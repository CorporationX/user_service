package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvatarService {

    @Setter
    @Value("${services.s3.bucket-name}")
    private String BUCKET_NAME;

    @Setter
    @Value("${services.avatar.avatar-prefix}")
    private String AVATAR_PREFIX;

    @Setter
    @Value("${services.avatar.small-avatar-prefix}")
    private String SMALL_AVATAR_PREFIX;

    @Setter
    @Value("${dice-bear.url}/${dice-bear.version}/%s/${dice-bear.file-type}?${dice-bear.generation-param}=%d")
    private String AVATAR_GENERATOR_URL_PATTERN;

    @Setter
    @Value("${dice-bear.file-type}")
    private String FILE_EXTENSION;

    @Setter
    @Value("${services.avatar.small-file-height}")
    private int SMALL_FILE_WIDTH;

    @Setter
    @Value("${services.avatar.small-file-width}")
    private int SMALL_FILE_HEIGHT;

    @Setter
    @Value("${services.avatar.seed-range}")
    private int SEED_RANGE;

    @Setter
    @Value("${dice-bear.styles}")
    private String[] STYLES;

    private final AmazonS3 s3Client;
    private final FileService fileService;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Transactional
    public void setRandomAvatar(User user) {
        String fileName = AVATAR_PREFIX + "_" + user.getId() + "." + FILE_EXTENSION;
        String smallFileName = SMALL_AVATAR_PREFIX + "_" + user.getId() + "." + FILE_EXTENSION;
        File file, smallFile;

        int styleIndex = ThreadLocalRandom.current().nextInt(0, STYLES.length);
        int seed = ThreadLocalRandom.current().nextInt(0, SEED_RANGE);
        ResponseEntity<Resource> response = restTemplate
                .getForEntity(String.format(AVATAR_GENERATOR_URL_PATTERN, STYLES[styleIndex], seed), Resource.class);
        byte[] avatar = fileService.convertResponseToByteArray(response);
        file = fileService.convertByteArrayToFile(avatar, fileName);
        smallFile = fileService.resizeImageFile(file, SMALL_FILE_WIDTH, SMALL_FILE_HEIGHT, smallFileName);

        s3Client.putObject(new PutObjectRequest(BUCKET_NAME, fileName, file));
        s3Client.putObject(new PutObjectRequest(BUCKET_NAME, smallFileName, smallFile));
        file.delete();
        smallFile.delete();
        user.setUserProfilePic(new UserProfilePic(fileName, smallFileName));
        userRepository.save(user);
    }
}
