package school.faang.user_service.service.diceBear;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.amazon.AvatarService;

import java.awt.image.BufferedImage;

@Service
@RequiredArgsConstructor
public class DiceBearService {
    private final AvatarService avatarService;
    @Value("${service.dice-bear.url}")
    private String URL;
    @Value("${service.dice-bear.size}")
    private String SIZE;

    public void createAvatar(UserProfilePic userProfilePic) {
        userProfilePic.setFileId(URL + userProfilePic.getName());
        userProfilePic.setSmallFileId(URL + userProfilePic.getName() + SIZE);

        avatarService.saveToAmazonS3(userProfilePic);
    }
}
