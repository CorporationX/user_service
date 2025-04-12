package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class SubscriptionService {
    private static final String USER_FILTER_DTO_CANNOT_BE_NULL = "UserFilterDto can't be null";
    private static final String USER_ALREADY_FOLLOWING_ERROR = "User with ID %d is already following user with ID %d.";
    private static final String USER_CANNOT_UNSUBSCRIBE_FROM_HIMSELF = "User cannot unsubscribe from himself";
    private static final String USER_NOT_SUBSCRIBED_MESSAGE = "User with ID %d is not subscribed to user with ID %d.";

    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> filters;
    private final UserMapper userMapper;

    public List<UserDto> getFollowers(long followeeId, UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            log.error(USER_FILTER_DTO_CANNOT_BE_NULL);
            throw new DataValidationException(USER_FILTER_DTO_CANNOT_BE_NULL);
        }
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        log.debug("Initial stream of users fetched for followeeId: {}", followeeId);
        for (UserFilter filter : filters) {
            if (filter.isApplicable(userFilterDto)) {
                log.debug("Applying filter: {}", filter.getClass().getSimpleName());
                followers = filter.apply(followers, userFilterDto);
            }
        }
        return userMapper.toDtoList(followers.toList());
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("User {} attempted to follow themselves.", followerId);
            throw new DataValidationException("User can't subscribe to himself.");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("User {} is already following user {}", followerId, followeeId);
            throw new DataValidationException(String.format(
                    USER_ALREADY_FOLLOWING_ERROR, followerId, followeeId));
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.error(USER_CANNOT_UNSUBSCRIBE_FROM_HIMSELF);
            throw new DataValidationException(USER_CANNOT_UNSUBSCRIBE_FROM_HIMSELF);
        }
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            String errorMessage = String.format(USER_NOT_SUBSCRIBED_MESSAGE, followerId, followeeId);
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

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
        return userMapper.toDtoList(followees.toList());
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
