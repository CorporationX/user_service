package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
    private static final String USER_FILTER_DTO_CANNOT_BE_NULL = "UserFilterDto can't be null";
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> filters;
    private final UserMapper userMapper;

    public List<UserDto> getFollowing(long followerId, UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            log.error(USER_FILTER_DTO_CANNOT_BE_NULL);
            throw new DataValidationException(USER_FILTER_DTO_CANNOT_BE_NULL);
        }
        Stream<User> followees = subscriptionRepository.findByFollowerId(followerId);
        log.debug("Initial stream of following users fetched for followerId: {}", followerId);

        for (UserFilter filter : filters) {
            if (filter.isApplicable(userFilterDto)) {
                log.debug("Applying filter: {} for followerId: {}", filter.getClass().getSimpleName(), followerId);
                followees = filter.apply(followees, userFilterDto);
            }
        }
        return followees.map(userMapper::toDto).toList();
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
