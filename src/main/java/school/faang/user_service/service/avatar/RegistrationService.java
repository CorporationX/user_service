package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final AvatarService avatarService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDto registerUser(UserDto userDto) {
        String userId = UUID.randomUUID().toString();
        log.info("User registration with ID {}", userId);

        String avatarUrl = avatarService.generateAndUploadAvatar(userId);

        User user = userMapper.toEntity(userDto);
        user.setId(Long.valueOf(userId));
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(userId);
        userProfilePic.setSmallFileId(avatarUrl);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        log.info("User {} success register with URL {}", user.getUsername(), avatarUrl);

        return userMapper.toDto(user);
    }
}
