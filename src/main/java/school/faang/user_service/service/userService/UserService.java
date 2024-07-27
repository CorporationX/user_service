package school.faang.user_service.service.userService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.s3.FileDownloadException;
import school.faang.user_service.exception.s3.FileUploadException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.randomAvatar.RandomAvatarService;
import school.faang.user_service.service.s3Service.S3Service;
import school.faang.user_service.validation.user.UserValidator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final S3Service s3Service;
    private final RandomAvatarService randomAvatarService;
    private final UserValidator userValidator;
    private final UserRepository userRepository;

    @Transactional
    public String generateRandomAvatar(Long userId) {
        if (!userValidator.isCurrentUser(userId)) {
            throw new DataValidationException("Request contains another user id than in header");
        }

        Optional<User> user = userRepository.findById(userId);
        user.orElseThrow(() -> new DataValidationException(String.format("User with %s id doesn't exist", userId)));
        User userToRandomAvatar = user.get();

        File randomPhoto = randomAvatarService.getRandomPhoto();

        String folder = String.format("users/avatar/%s_%s", userToRandomAvatar.getUsername(), userToRandomAvatar.getId());
        String avatarId = s3Service.uploadFile(randomPhoto, folder);

        if (avatarId == null) {
            throw new FileUploadException("Avatar id can't be nullable");
        }

        userToRandomAvatar.setUserProfilePic(UserProfilePic.builder().fileId(avatarId).build());
        userRepository.save(userToRandomAvatar);

        return avatarId;
    }

    @Transactional
    public byte[] getUserRandomAvatar(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        user.orElseThrow(() -> new DataValidationException(String.format("User with %s id doesn't exist", userId)));
        User userWithRandomAvatar = user.get();

        String avatarId = userWithRandomAvatar.getUserProfilePic().getFileId();

        if (avatarId == null) {
            throw new FileDownloadException("Avatar id can't be nullable");
        }

        InputStream file = s3Service.getFile(avatarId);

        try {
            return file.readAllBytes();
        } catch (IOException e) {
            throw new FileDownloadException(e.getMessage());
        }
    }
}
