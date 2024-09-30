package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.DefaultAvatarClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    @Value("${default-avatar_client.styleName}")
    private String styleName;
    @Value("${default-avatar_client.format}")
    private String format;

    public void createDefaultAvatarForUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
                    String message = "Пользователя с id = " + userId + " нет в системе";
                    log.error(message);
                    return new DataValidationException(message);
                }
        );
        if (user.getUserProfilePic() != null) {
            String message = "Пользователь с id = " + userId + " уже имеет аватар";
            log.error(message);
            throw new DataValidationException(message);
        }

        // получение аватара
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder backgroundColor = new StringBuilder();
        for (int j = 0; j < 6; j ++) {
            backgroundColor.append(Integer.toHexString(random.nextInt(0, 15)).toLowerCase());
        }
        List<String> backgroundColors = new ArrayList<>(List.of(backgroundColor.toString()));
        byte[] file = defaultAvatarClient.getAvatar(styleName, format, backgroundColors);

        // загрузка файла в хранилще
        String key = "default_avatar_for_user_" + userId + "_" + System.currentTimeMillis();
        s3Service.uploadFile(key, file, bucketName);
        UserProfilePic userPic = new UserProfilePic();
        userPic.setFileId(key);
        userPic.setSmallFileId(key);
        user.setUserProfilePic(userPic);
        userRepository.save(user);

        log.info("Для пользователя с id = " + userId + "добавлен аватар по-умолчанию" + key);
    }
}
