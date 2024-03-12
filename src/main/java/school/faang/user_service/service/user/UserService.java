package school.faang.user_service.service.user;

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
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filters))
                .forEach(userFilter -> userFilter.apply(premiumUsers, filters));
        return userMapper.toDto(premiumUsers);
    }
}
