package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    @Transactional
    public void followUser(long followerId, long followeeId) throws DataValidationException {
        validateUserIds(followerId, followeeId);
        validateFollowSubscription(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    private void validateFollowSubscription(long followerId, long followeeId) throws DataValidationException {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User %d is already subscribed to user %d.".formatted(followerId, followeeId));
        }
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) throws DataValidationException {
        validateUserIds(followerId, followeeId);
        validateUnfollowSubscription(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    private void validateUnfollowSubscription(long followerId, long followeeId) throws DataValidationException {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User %d is already unsubscribe to user %d.".formatted(followerId, followeeId));
        }
    }

    private void validateUserIds(long followerId, long followeeId) throws DataValidationException {
        if (followerId == followeeId) {
            throw new DataValidationException("User %s is trying to unsubscribe to himself".formatted(followerId));
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        Stream<User> followers = subscriptionRepository.findByFollowerId(followeeId);
        return filterUsers(followers, filters)
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filters) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(followers, filters)
                .map(userMapper::toDto)
                .toList();
    }

    private Stream<User> filterUsers(Stream<User> followers, UserFilterDto filters) {
        List<UserFilter> applicableFilters = userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();
        return followers
                .filter(user -> applicableFilters.stream().allMatch(filter -> filter.apply(user, filters)));
    }

    public int getFollowersCount(long followerId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followerId);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
