package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.event.profile.ProfilePicEvent;
import school.faang.user_service.publisher.profile.ProfilePicEventPublisher;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.picture.PictureValidator;
import school.faang.user_service.validator.picture.ScaleChanger;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfilePicService {

    private final UserService userService;
    private final PictureValidator pictureValidator;
    private final ScaleChanger scaleChanger;
    private final S3Service s3Service;
    private final ProfilePicEventPublisher profilePicEventPublisher;

    public void uploadUserAvatar(Long userId, MultipartFile file) {
        User user = userService.getUserById(userId);
        pictureValidator.checkPictureSizeExceeded(file);

        String folder = user.getId() + user.getUsername();

        List<ResponseEntity<byte[]>> images = scaleChanger.changeFileScale(file);
        List<String> keys = images.stream()
                .map(image -> s3Service.uploadHttpData(image, folder))
                .toList();

        deleteUserAvatar(userId);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(keys.get(0));
        userProfilePic.setSmallFileId(keys.get(1));

        user.setUserProfilePic(userProfilePic);
        userService.saveUser(user);
        publishProfilePicEvent(user);
    }

    public InputStream downloadUserAvatar(Long userId) {
        User user = userService.getUserById(userId);

        return s3Service.downloadFile(user.getUserProfilePic().getFileId());
    }

    public void deleteUserAvatar(Long userId) {
        User user = userService.getUserById(userId);

        s3Service.deleteFile(user.getUserProfilePic().getFileId());
        s3Service.deleteFile(user.getUserProfilePic().getSmallFileId());
    }

    private void publishProfilePicEvent(User user) {
        String linkToFile = s3Service.getFullAvatarLinkByFileName(user.getUserProfilePic().getFileId());

        ProfilePicEvent profilePicEvent = new ProfilePicEvent(user.getId(), linkToFile);
        profilePicEventPublisher.publish(profilePicEvent);
    }
}
