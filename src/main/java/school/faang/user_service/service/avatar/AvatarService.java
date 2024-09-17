package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import school.faang.user_service.entity.AvatarStyle;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarFetchException;
import school.faang.user_service.service.minio.MinioService;

import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
@Service
public class AvatarService {
    private static final int LARGE_AVATAR_SIZE = 200;
    private static final int SMALL_AVATAR_SIZE = 50;
    private static final String AVATAR_CONTENT_TYPE = "image/png";

    private final RestTemplate restTemplate;
    private final MinioService minioService;

    public UserProfilePic generateAndSaveAvatar(AvatarStyle style) {
        log.info("Generating avatar for style: {}", style.getStyleName());

        String largeAvatarFileName = saveAvatar(style, LARGE_AVATAR_SIZE, AVATAR_CONTENT_TYPE);
        String smallAvatarFileName = saveAvatar(style, SMALL_AVATAR_SIZE, AVATAR_CONTENT_TYPE);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(largeAvatarFileName);
        userProfilePic.setSmallFileId(smallAvatarFileName);

        return userProfilePic;
    }

    private String saveAvatar(AvatarStyle style, int size, String contentType) {
        byte[] avatarData = getRandomAvatar(style, "png", size);
        String avatarFileName = UUID.randomUUID() + ".png";

        log.info("Saving avatar with name: {}", avatarFileName);
        minioService.uploadFile(avatarFileName, avatarData, contentType);
        return avatarFileName;
    }

    public byte[] getRandomAvatar(AvatarStyle style, String format, Integer size) {
        String url = String.format("https://api.dicebear.com/9.x/%s/%s", style.getStyleName(), format);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);

        if (size != null) {
            uriBuilder.queryParam("size", size);
        }

        ResponseEntity<byte[]> response = restTemplate.getForEntity(uriBuilder.toUriString(), byte[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Avatar successfully fetched from DiceBear API for style {}", style.getStyleName());
            return response.getBody();
        } else {
            log.error("Failed to fetch avatar from DiceBear API for style {}", style.getStyleName());
            throw new AvatarFetchException("Failed to fetch avatar from DiceBear API for style " + style.getStyleName());
        }
    }
}