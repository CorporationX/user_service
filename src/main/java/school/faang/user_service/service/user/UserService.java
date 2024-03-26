package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${services.dicebear.avatar}")
    private String avatar;

    @Value("${services.dicebear.small_avatar}")
    private String smallAvatar;

    public UserDto create(UserDto userDto) {
        userValidator.validatePassword(userDto.getPassword());
        User user = userMapper.toEntity(userDto);
        user.setActive(true);
        setUpAvatar(user);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto getUser(long userId) {
        User user = getUserFromRepository(userId);
        return userMapper.toDto(user);
    }

    public User getUserById(Long userId) {
        return getUserFromRepository(userId);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toDto(users);
    }

    public List<UserDto> getFollowers(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by ID: " + userId));
        return userMapper.toDto(user.getFollowers());
    }

    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        List<User> premiumUsers = userRepository.findPremiumUsers().toList();

        if (!userFilters.isEmpty()) {
            userFilters.stream()
                    .filter(filter -> filter.isApplicable(filters))
                    .forEach(filter -> filter.apply(premiumUsers, filters));
        }
        return userMapper.toDto(premiumUsers);
    }

    private User getUserFromRepository(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by ID: " + userId));
    }

    private void setUpAvatar(User user) {
        UUID uuid = UUID.randomUUID();
        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(avatar + uuid)
                .smallFileId(smallAvatar + uuid)
                .build());
    }
}