package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public Stream<UserDto> getUser(UserFilterDto filterDto) {
        Stream<User> usersStream = userRepository.findAll().stream();
        for (UserFilter filter : userFilters) {
            if (filter != null && filter.isApplicable(filterDto)) {
                usersStream = filter.apply(usersStream, filterDto);
            }
        }

        return usersStream.map(userMapper::toDto);
    }
}
