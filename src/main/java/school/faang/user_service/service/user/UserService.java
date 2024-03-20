package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final UserValidator userValidator;

    private static final String AVATAR_URL = "https://api.dicebear.com/8.x/lorelei-neutral/svg?seed=";
    private static final String SMALL_AVATAR_URL = "https://api.dicebear.com/8.x/lorelei-neutral/svg?size=100&seed=";

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("A user with this id: " + userId + "was not found in userRepository"));
    }

    public UserDto create(UserDto userDto) {
        userValidator.validatePassword(userDto);
        User user = userMapper.toEntity(userDto);
        user.setActive(true);
        setUpAvatar(user);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by ID: " + userId));
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toDto(users);
    }

    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        List<User> premiumUsers = userRepository.findPremiumUsers().toList();
        if (!userFilters.isEmpty()) {
            userFilters.stream()
                    .filter(userFilter -> userFilter.isApplicable(filters))
                    .forEach(userFilter -> userFilter.apply(premiumUsers, filters));
        }
        return userMapper.toDto(premiumUsers);
    }

    private void setUpAvatar(User user) {
        UUID uuid = UUID.randomUUID();
        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(AVATAR_URL + uuid)
                .smallFileId(SMALL_AVATAR_URL + uuid)
                .build());
    }
}