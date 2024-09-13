package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final List<UserFilter> userFilters;

    @Override
    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        Stream<User> premiumUsersStream = Optional.ofNullable(userRepository.findPremiumUsers())
                .orElse(Stream.empty());

        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(premiumUsersStream, (stream, filter)
                        -> stream.filter(user -> filter.apply(user, filterDto)), (s1, s2) -> s1)
                .map(mapper::toDto)
                .toList();
    }
}
