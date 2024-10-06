package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filters.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    @Transactional(readOnly = true)
    public List<User> findPremiumUser(UserFilterDto filterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .reduce(premiumUsers, (stream, filter) -> filter.apply(stream, filterDto), (stream, filter) -> stream)
                .toList();
    }

    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User does not exist"));
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toDto(users);
    }

    @Transactional
    public void banUser(Long userId) {
        User user = getUserById(userId);
        user.setBanned(true);
        log.info("User with ID: " + userId + " banned.");
        userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new DataValidationException("User with ID: " + userId + " not found.")
        );
    }
}
