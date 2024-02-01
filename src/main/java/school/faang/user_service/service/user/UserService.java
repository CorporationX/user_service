package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Value("${dicebear.pic-base-url}")
    private String avatarBaseUrl;
    @Value("${dicebear.pic-base-url-small}")
    private String smallAvatarBaseUrl;

    public UserDto createUser(UserDto userDto) {
        //some actions
        User savedUser = new User();
        savedUser.setUserProfilePic(generateUserProfilePic());
        //some actions
        return userDto;
    }

    public UserDto getUser(long id) {
        return userMapper.toDto(getExistingUserById(id));
    }

    public User getExistingUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " is not found in database"));
    }

    private UserProfilePic generateUserProfilePic() {
        UUID randomUUID = UUID.randomUUID();
        return new UserProfilePic(
                avatarBaseUrl + randomUUID,
                smallAvatarBaseUrl + randomUUID
        );
    }
}