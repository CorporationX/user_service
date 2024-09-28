package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.picture.PictureValidator;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfilePicService {

    private final UserService userService;
    private final PictureValidator pictureValidator;
    private final S3Service s3Service;

    public void uploadUserAvatar(Long userId, MultipartFile file) {
        User user = userService.getUserById(userId);
        pictureValidator.checkPictureSizeExceeded(file);

        String folder = user.getId() + user.getUsername();

        List<byte[]> images = pictureValidator.changeFileScale(file);
        List<String> keys = images.stream()
                .map(image -> s3Service.uploadAvatar(image, folder, file.getContentType()))
                .toList();

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(keys.get(0));
        userProfilePic.setSmallFileId(keys.get(1));

        user.setUserProfilePic(userProfilePic);
        userService.saveUser(user);
    }

    public InputStream downloadUserAvatar(Long userId) {
        User user = userService.getUserById(userId);

        return s3Service.downloadAvatar(user.getUserProfilePic().getFileId());
    }

    public void deleteUserAvatar(Long userId) {
        User user = userService.getUserById(userId);

        s3Service.deleteAvatar(user.getUserProfilePic().getFileId());
        s3Service.deleteAvatar(user.getUserProfilePic().getSmallFileId());
    }
}
