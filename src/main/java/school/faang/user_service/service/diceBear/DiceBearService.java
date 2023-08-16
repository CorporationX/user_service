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
    @Value("${services.dice-bear.url}")
    private String URL;
    @Value("${services.dice-bear.size}")
    private String SIZE;

    public UserProfilePic createAvatar(String username, long userId) {
        UserProfilePic userProfilePic = new UserProfilePic();

        String nameUserProfilePic = username + userId;
        userProfilePic.setName(nameUserProfilePic);
        userProfilePic.setFileId(URL + nameUserProfilePic);
        userProfilePic.setSmallFileId(URL + nameUserProfilePic + SIZE);

        //avatarService.saveToAmazonS3(userProfilePic);
        return userProfilePic;
    }

    public BufferedImage getFileAmazonS3(String fileName) {
        return avatarService.getFileAmazonS3(fileName);
    }

    public void deleteFileAmazonS3(String fileName) {
        avatarService.deleteFile(fileName);
    }
}
