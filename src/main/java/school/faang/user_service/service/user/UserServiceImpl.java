package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.publisher.EventPublisher;
import school.faang.user_service.dto.analyticsevent.ProfileViewEvent;
import school.faang.user_service.publisher.RedisTopics;
import school.faang.user_service.dto.analyticsevent.SearchAppearanceEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final List<UserFilter> filters;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final EventPublisher eventPublisher;

    @Override
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return StreamSupport.stream(premiumRepository.findAll().spliterator(), false)
                .map(premium -> userMapper.toDto(premium.getUser()))
                .filter(userDto -> filters.stream()
                        .allMatch(filter -> filter.apply(userDto, userFilterDto)))
                .toList();
    }

    @Override
    public UserDto getUser(long userId) {
        UserDto userDto = userRepository.findById(userId).map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        ProfileViewEvent profileViewEvent = new ProfileViewEvent(
                userDto.getId(),
                userContext.getUserId(),
                LocalDateTime.now()
        );
        eventPublisher.publishToTopic(RedisTopics.PROFILE_VIEW.getTopicName(), profileViewEvent);
        return userDto;
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public List<UserDto> getFilteredUsers(UserFilterDto userFilterDto) {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .filter(userDto -> filters.stream()
                        .allMatch(filter -> filter.apply(userDto, userFilterDto)))
                .peek(userDto -> {
                    SearchAppearanceEvent searchAppearanceEvent = new SearchAppearanceEvent(
                            userDto.getId(),
                            userContext.getUserId(),
                            LocalDateTime.now()
                            );
                    eventPublisher.publishToTopic(RedisTopics.SEARCH_APPEARANCE.getTopicName(), searchAppearanceEvent);
                })
                .toList();
    }
}
