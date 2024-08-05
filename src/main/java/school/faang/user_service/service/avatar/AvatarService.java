package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.properties.AvatarProperties;
import school.faang.user_service.properties.DiceBearProperties;
import school.faang.user_service.service.aws.AmazonS3Service;
import school.faang.user_service.service.image.ImageService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final AmazonS3Service amazonS3Service;
    private final ImageService imageService;
    private final DiceBearProperties diceBearProperties;
    private final AvatarProperties avatarProperties;
    private final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.avatar-content-type}")
    private String contentType;

    public void setRandomAvatar(User user) {
        String style = getRandomStyle();
        String avatarUrl = generateAvatarUrl(style, user.hashCode());
        byte[] avatar = imageService.fetchImage(avatarUrl);
        byte[] smallAvatar = imageService.resizeImage(avatar,
            avatarProperties.getSmallSize(),
            avatarProperties.getSmallSize(),
            diceBearProperties.getFileType());
        String fileName = generateFileName(user, avatarProperties.getNormalPattern());
        String smallFileName = generateFileName(user, avatarProperties.getSmallPattern());

        amazonS3Service.uploadFile(avatar, bucketName, fileName, contentType);
        amazonS3Service.uploadFile(smallAvatar, bucketName, smallFileName, contentType);
        user.setUserProfilePic(createUserProfilePic(fileName, smallFileName));
    }

    private String getRandomStyle() {
        List<String> styles = diceBearProperties.getStyles();
        return styles.get(threadLocalRandom.nextInt(styles.size()));
    }

   private String generateAvatarUrl(String style, int seed) {
        return String.format(diceBearProperties.getUrlPattern(), style, seed);
   }

   private String generateFileName(User user, String pattern) {
        return String.format(pattern, user.getId());
   }

    private UserProfilePic createUserProfilePic(String fileName, String smallFileName) {
        return UserProfilePic.builder()
            .fileId(fileName)
            .smallFileId(smallFileName)
            .build();
    }
}
