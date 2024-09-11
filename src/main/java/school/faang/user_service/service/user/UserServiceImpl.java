package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        var premiumUsers = userRepository.findPremiumUsers();
        return filter(premiumUsers, filters);

    }

    private List<UserDto> filter(Stream<User> users, UserFilterDto filters) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(users, filters))
                .map(userMapper::toDto)
                .toList();
    }
}
