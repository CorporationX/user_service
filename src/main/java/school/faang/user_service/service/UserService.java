package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.messaging.MessagePublisher;
import school.faang.user_service.messaging.events.ProfileViewEvent;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final MessagePublisher<ProfileViewEvent> profileViewEventMessagePublisher;
    private final UserContext userContext;

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return applyFilter(userRepository.findPremiumUsers(), userFilterDto);
    }

    private List<UserDto> applyFilter(Stream<User> userList, UserFilterDto userFilterDto) {
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .flatMap(userFilter -> userFilter.apply(userList, userFilterDto))
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUser(long userId) {
        String message = String.format("Entity with ID %d not found", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(message));
        profileViewEventMessagePublisher.publish(new ProfileViewEvent(userContext.getUserId(), userId));

        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> allById = userRepository.findAllById(ids);

        return allById.stream()
                .map(userMapper::toDto)
                .toList();
    }
}
