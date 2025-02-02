package school.faang.user_service.service.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final List<UserFilter> userFilters;
  private final UserMapper userMapper;

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  public User getUser(Long userId) {
    if (userId == null) {
      logger.error("User ID is null");
      throw new IllegalArgumentException("User ID must not be null");
    }

    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> {
              logger.warn("User with ID {} not found", userId);
              return new EntityNotFoundException("User with ID: " + userId + " not found");
            });
  }

  public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
    List<User> users = userRepository.findPremiumUsers().toList();
    for (UserFilter filter : userFilters) {
      if (filter.isApplicable(userFilterDto)) {
        users = filter.apply(users, userFilterDto);
      }
    }

    return users.stream().map(userMapper::toDto).toList();
  }

  public List<UserDto> getPremiumUsers() {
    return userRepository.findPremiumUsers().map(userMapper::toDto).toList();
  }
}
