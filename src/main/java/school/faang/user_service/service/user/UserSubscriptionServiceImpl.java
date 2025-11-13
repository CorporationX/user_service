package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    private final List<UserFilter> userFilters;

    @Override
    @Transactional
    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("The user {} tried to subscribe to himself", followerId);
            throw new DataValidationException("The user cannot subscribe to himself");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("The user {} is already subscribed to the user {}", followerId, followeeId);
            throw new DataValidationException("You have already subscribed to this user");
        }

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("The user {} successfully subscribed to the user {}",
                followerId, followeeId);
    }

    @Override
    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("The user {} tried to unsubscribe from himself", followerId);
            throw new DataValidationException("The user can't unsubscribe from himself");
        }
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("The user {} is not subscribed to the user {}", followerId, followeeId);
            throw new DataValidationException("The user is not subscribed to this user");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("The user {} has successfully unsubscribed from the user {}", followerId, followeeId);
    }

    @Override
    public CountResponse getFollowersCount(long followeeId) {
        long count = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        log.debug("Number of subscribers of the user {}: {}", followeeId, count);
        return new CountResponse(count);
    }

    @Override
    public CountResponse getFolloweesCount(long followerId) {
        long count = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        log.debug("Number of user subscriptions {}: {}", followerId, count);
        return new CountResponse(count);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFiltersDto filters) {
        Stream<User> followersStream = subscriptionRepository.findByFolloweeId(followeeId);

        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(filters)) {
                followersStream = userFilter.apply(followersStream, filters);
            }
        }

        return followersStream.map(userMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFollowees(long followerId, UserFiltersDto filters) {
        Stream<User> followeesStream = subscriptionRepository.findByFollowerId(followerId);

        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(filters)) {
                followeesStream = userFilter.apply(followeesStream, filters);
            }
        }

        return followeesStream.map(userMapper::toUserDto)
                .toList();
    }
}