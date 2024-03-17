package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;

    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        List<User> premiumUsers = userRepository.findPremiumUsers().toList();
        if (!userFilters.isEmpty()) {
            userFilters.stream()
                    .filter(userFilter -> userFilter.isApplicable(filters))
                    .forEach(userFilter -> userFilter.apply(premiumUsers, filters));
        }
        return userMapper.toDto(premiumUsers);
    }

    public UserDto getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by ID: " + userId));
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        if (users.isEmpty()) {
            throw new EntityNotFoundException("Users with given IDs don't exist");
        }
        return userMapper.toDto(users);
    }
}
