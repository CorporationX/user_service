package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
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
    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {

        Stream<UserDto> userDtoStream = userRepository.findPremiumUsers().map(user -> userMapper.toDto(user));
        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(userFilterDto)) {
                userDtoStream = userFilter.apply(userDtoStream, userFilterDto);
            }
        }
        return userDtoStream.toList();

    }


    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User by id: " + userId + " not found"));
        return userMapper.toDto(user);
    }
}
