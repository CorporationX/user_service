package school.faang.user_service.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.StyleAvatarConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

import java.util.Collections;
import java.util.List;

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

    private final StyleAvatarConfig styleAvatarConfig;
    private final AmazonS3Service amazonS3Service;
    private final RestTemplateService restTemplateService;


    @Setter
    private String smallAvatarUrl;

    @Setter
    private String fullAvatarUrl;

    @PostConstruct
    void init() {
        List<String> styles = styleAvatarConfig.getStyles();
        Collections.shuffle(styles);

        String newStyle = styles.iterator().next();
        smallAvatarUrl = url + newStyle + smallPostfix;
        fullAvatarUrl = url + newStyle + fullPostfix;
    }


    public void setDefaultUserAvatar(User user) {

        String originalAvatarUrl = fullAvatarUrl + user.hashCode();
        String miniAvatarUrl = smallAvatarUrl + user.hashCode();

        String keyForOriginalAvatar = amazonS3Service.uploadFile(originalAvatarUrl, restTemplateService.getImageBytes(originalAvatarUrl));
        String keyForSmallAvatar = amazonS3Service.uploadFile(miniAvatarUrl, restTemplateService.getImageBytes(miniAvatarUrl));

        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(keyForOriginalAvatar)
                .smallFileId(keyForSmallAvatar)
                .build());
    }
}
