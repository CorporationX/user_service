package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<UserDto> userDtoStream = userRepository.findPremiumUsers().map(user -> userMapper.toDto(user));
        return userFilter(userDtoStream, userFilterDto).toList();
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User by id: " + userId + " not found"));
        return userMapper.toDto(user);
    }

    private Stream<UserDto> userFilter(Stream<UserDto> userDtoStream, UserFilterDto userFilterDto) {
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .flatMap(userFilter -> userFilter.apply(userDtoStream, userFilterDto));
    }
}
