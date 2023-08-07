package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userProfilePic.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.util.FileStorageService;
import school.faang.user_service.util.ImageService;

@Service
@RequiredArgsConstructor
public class UserProfilePicService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final ImageService imageService;

    public UserProfilePicDto upload(MultipartFile file, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataValidException("User with id " + userId + " is not found"));

        byte[] big = imageService.resizeImage(file, true);
        String bigKey = fileStorageService.uploadFile(big, file, userId, "big");

        byte[] small = imageService.resizeImage(file, false);
        String smallKey = fileStorageService.uploadFile(small, file, userId, "small");

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(bigKey);
        userProfilePic.setSmallFileId(smallKey);

        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return new UserProfilePicDto(userId, bigKey, smallKey);
    }
}
