package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.config.AvatarConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@Setter
@RequiredArgsConstructor
public class AvatarService {

    private final AvatarConfig avatarConfig;
    private final S3Service s3Service;
    private final UtilsService utilsService;
    private final RestTemplate restTemplate;

    public void setRandomAvatar(User user) {
        int styleIndex = ThreadLocalRandom.current().nextInt(0, avatarConfig.getSTYLES().length);
        int seed = ThreadLocalRandom.current().nextInt(0, avatarConfig.getSEED_RANGE());

        String avatarUrl = String.format(avatarConfig.getGENERATION_URL_PATTERN(),
                avatarConfig.getSTYLES()[styleIndex], seed);
        String fileName = String.format(avatarConfig.getAVATAR_ID_PATTERN(), user.getId(),
                avatarConfig.getEXTENSION());
        String smallFileName = String.format(avatarConfig.getSMALL_AVATAR_ID_PATTERN(), user.getId(),
                avatarConfig.getEXTENSION());

        byte[] avatar = getImageByUrl(avatarUrl);
        byte[] smallAvatar = utilsService.resizeImage(avatar, avatarConfig.getSMALL_FILE_WIDTH(),
                avatarConfig.getSMALL_FILE_HEIGHT(), avatarConfig.getEXTENSION());

        s3Service.uploadToS3(fileName, avatar, avatarConfig.getCONTENT_TYPE(), avatarConfig.getBUCKET_NAME());
        s3Service.uploadToS3(smallFileName, smallAvatar, avatarConfig.getCONTENT_TYPE(), avatarConfig.getBUCKET_NAME());

        user.setUserProfilePic(new UserProfilePic(fileName, smallFileName));
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
