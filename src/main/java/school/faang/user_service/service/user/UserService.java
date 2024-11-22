package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.s3.S3Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final AvatarService avatarService;
    private final S3Service s3Service;

    public Optional<User> findById(long userId) {
        return userRepository.findById(userId);
    }

    public String generateRandomAvatar() {
        Long userId = userContext.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        String avatar = avatarService.generateRandomAvatar();
        String avatarKey = String.format("%d%s%d", userId, "avatar", System.currentTimeMillis());
        String updatedKey = s3Service.saveSvg(avatar, avatarKey);
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(updatedKey);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
        return updatedKey;
    }
}
