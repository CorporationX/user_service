package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.AmazonS3Service;
import school.faang.user_service.util.ImageService;

@Service
@RequiredArgsConstructor
public class UserProfilePicService {

    private final AmazonS3Service s3Service;
    private final UserService userService;
    private final ImageService imageService;

    @Transactional
    public void uploadProfilePic(MultipartFile file, long userId) {
        User user = userService.getUserById(userId);

        byte[] big = imageService.resize(file, true);
        String bigImageURL = s3Service.uploadFile(big, file, userId, "big");

        byte[] small = imageService.resize(file, false);
        String smallImageURL = s3Service.uploadFile(small, file, userId, "small");

        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(bigImageURL);
        profilePic.setSmallFileId(smallImageURL);
        user.setUserProfilePic(profilePic);
    }
}