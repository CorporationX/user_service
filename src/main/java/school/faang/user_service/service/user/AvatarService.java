package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserAvatarDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.image.ImageResizingService;
import school.faang.user_service.service.s3.S3Service;

@Service
@RequiredArgsConstructor
public class AvatarService {

    public static final int LARGE_IMAGE_SIZE = 1080;
    public static final int SMALL_IMAGE_SIZE = 170;
    private final UserRepository userRepository;
    private final ImageResizingService imageResizingService;
    private final S3Service s3Service;

    public UserAvatarDto getAvatar(Long userId) {
        UserProfilePic avatar = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", userId)))
                .getUserProfilePic();

        if (avatar == null || avatar.getFileId() == null || avatar.getSmallFileId() == null) {
            throw new NotFoundException("Avatar not found");
        }

        String largeUrl = s3Service.getFileUrl(avatar.getFileId());
        String smallUrl = s3Service.getFileUrl(avatar.getSmallFileId());
        return new UserAvatarDto(largeUrl, smallUrl);
    }

    @Transactional
    public void addAvatar(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", userId)));

        byte[] largeImageBytes = imageResizingService.resizeImage(file, LARGE_IMAGE_SIZE);
        byte[] smallImageBytes = imageResizingService.resizeImage(file, SMALL_IMAGE_SIZE);

        String largeImageFolder = "user_avatars_large";
        String smallImageFolder = "user_avatars_small";
        String largeFileId = s3Service.uploadFile(largeImageBytes, file.getContentType(), largeImageFolder);
        String smallFileId = s3Service.uploadFile(smallImageBytes, file.getContentType(), smallImageFolder);

        UserProfilePic avatar = user.getUserProfilePic();
        if (avatar == null) {
            avatar = new UserProfilePic();
        }
        avatar.setFileId(largeFileId);
        avatar.setSmallFileId(smallFileId);

        user.setUserProfilePic(avatar);
        userRepository.save(user);
    }

    @Transactional
    public void deleteAvatar(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", userId)));
        UserProfilePic avatar = user.getUserProfilePic();

        if (avatar == null || avatar.getFileId() == null || avatar.getSmallFileId() == null) {
            throw new NotFoundException("Avatar not found");
        }

        s3Service.deleteFile(avatar.getFileId());
        s3Service.deleteFile(avatar.getSmallFileId());
        avatar.setFileId(null);
        avatar.setSmallFileId(null);
    }
}
