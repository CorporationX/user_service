package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.StyleAvatarConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AvatarService {

    @Value("${services.diceBear.avatarUrl.url}")
    @Setter
    private String url;

    @Value("${services.diceBear.avatarUrl.smallPostfix}")
    @Setter
    private String smallPostfix;

    @Value("${services.diceBear.avatarUrl.fullPostfix}")
    @Setter
    private String fullPostfix;

    @Value("${services.diceBear.avatarUrl.avatarPattern}")
    @Setter
    private String avatarPattern;

    @Value("${services.diceBear.avatarUrl.smallAvatarPattern}")
    @Setter
    private String smallAvatarPattern;

    private final StyleAvatarConfig styleAvatarConfig;
    private final AmazonS3Service amazonS3Service;
    private final RestTemplateService restTemplateService;


    public void setDefaultUserAvatar(User user) {

        String randomStyleUrl = getStyle();

        String originalAvatarUrl = randomStyleUrl + fullPostfix + user.hashCode();
        String miniAvatarUrl = randomStyleUrl + smallPostfix + user.hashCode();

        String fullAvatarKey = String.format(avatarPattern, user.getId());
        String smallAvatarKey = String.format(smallAvatarPattern, user.getId());

        String keyForOriginalAvatar = amazonS3Service.uploadFile(fullAvatarKey, restTemplateService.getImageBytes(originalAvatarUrl));
        String keyForSmallAvatar = amazonS3Service.uploadFile(smallAvatarKey, restTemplateService.getImageBytes(miniAvatarUrl));

        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(keyForOriginalAvatar)
                .smallFileId(keyForSmallAvatar)
                .build());
    }

    private String getStyle() {
        List<String> styles = styleAvatarConfig.getStyles();
        String newStyle = styles.get(ThreadLocalRandom.current().nextInt(styles.size()));
        return url + newStyle;
    }
}
