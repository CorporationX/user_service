package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AvatarService avatarService;
    private final S3Service s3Service;

    @Transactional
    public User createUserWithAvatar(User user) {
        user = userRepository.save(user);

        log.info("User created with ID: {}, generating avatar.", user.getId());
        avatarService.generateAndSaveAvatar(user.getUsername());
        String avatarUrl = s3Service.generateS3Url("avatars/" + user.getUsername() + ".svg");

        UserProfilePic profilePic = Optional.ofNullable(user.getUserProfilePic()).orElse(new UserProfilePic());
        profilePic.setAvatarUrl(avatarUrl);
        user.setUserProfilePic(profilePic);

        User savedUser = userRepository.save(user);
        log.info("User saved with avatar URL: {}", avatarUrl);
        return savedUser;
    }

    @Transactional
    public String updateAvatar(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        avatarService.generateAndSaveAvatar(username);
        String avatarUrl = s3Service.generateS3Url("avatars/" + username + ".svg");

        UserProfilePic profilePic = Optional.ofNullable(user.getUserProfilePic()).orElse(new UserProfilePic());
        profilePic.setAvatarUrl(avatarUrl);
        user.setUserProfilePic(profilePic);

        userRepository.save(user);
        return avatarUrl;
    }
}