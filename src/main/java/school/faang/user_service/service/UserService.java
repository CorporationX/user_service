package school.faang.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.AvatarDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AvatarService avatarService;

    public UserService(UserRepository userRepository, AvatarService avatarService) {
        this.userRepository = userRepository;
        this.avatarService = avatarService;
    }

    @Transactional
    public User createUserWithAvatar(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        user = userRepository.save(user);

        log.info("User created with ID: {}, generating avatar.", user.getId());
        String avatarUrl = avatarService.generateAndSaveAvatar(user.getUsername());

        UserProfilePic profilePic = user.getUserProfilePic();
        if (profilePic == null) {
            profilePic = new UserProfilePic();
        }
        profilePic.setAvatarUrl(avatarUrl);
        user.setUserProfilePic(profilePic);

        User savedUser = userRepository.save(user);
        log.info("User saved with avatar URL: {}", avatarUrl);
        return savedUser;
    }

    @Transactional
    public AvatarDto updateAvatar(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        String newAvatarUrl = avatarService.generateAndSaveAvatar(username);

        UserProfilePic profilePic = user.getUserProfilePic();
        if (profilePic == null) {
            profilePic = new UserProfilePic();
        }
        profilePic.setAvatarUrl(newAvatarUrl);
        user.setUserProfilePic(profilePic);

        userRepository.save(user);

        return new AvatarDto(user.getUsername(), newAvatarUrl);
    }
}