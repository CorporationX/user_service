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
    private final int sizeSmallPicture = 20;
    @Value("${dicebear.url}")
    private final String url;
    @Value("${dicebear.size}")
    private final String size;

    public UserProfilePic createAvatar(String username, long userId) {
        UserProfilePic userProfilePic = new UserProfilePic();

        String nameUserProfilePic = username + userId;
        userProfilePic.setName(nameUserProfilePic);
        userProfilePic.setFileId(url + nameUserProfilePic + size);
        userProfilePic.setSmallFileId(url + nameUserProfilePic + size + sizeSmallPicture);

        avatarService.saveToAmazonS3(userProfilePic);
        return userProfilePic;
    }

    public BufferedImage getFileAmazonS3(String fileName) {
        return avatarService.getFileAmazonS3(fileName);
    }

    public void deleteFileAmazonS3(String fileName) {
        avatarService.deleteFile(fileName);
    }


}
