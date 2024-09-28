package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.DefaultAvatarClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final static String STYLE_NAME = "adventurer";
    private final static String FORMAT = "jpg";
    private final DefaultAvatarClient defaultAvatarClient;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public void createDefaultAvatarForUser(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new DataValidationException("Пользователя с id = " + userId + " нет в системе");
        }
        if (user.getUserProfilePic() != null) {
            throw new DataValidationException("Пользователь с id = " + userId + " уже имеет аватар");
        }

        // получение аватара
        byte[] file = defaultAvatarClient.getAvatar(STYLE_NAME, FORMAT);

        // загрузка файла в хранилще
        String key = "default_avatar_for_user_" + userId + "_" + System.currentTimeMillis();
        s3Service.uploadFile(key, file, bucketName);
        UserProfilePic userPic = new UserProfilePic();
        userPic.setFileId(key);
        userPic.setSmallFileId(key);
        user.setUserProfilePic(userPic);
        userRepository.save(user);
    }
}
