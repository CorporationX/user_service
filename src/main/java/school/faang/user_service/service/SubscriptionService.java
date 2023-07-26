package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        validateUserId(followeeId);
        Stream<User> allUsers = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(allUsers, filter);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        validateUserId(followerId);
        Stream<User> allUsers = subscriptionRepository.findByFollowerId(followerId);
        return filterUsers(allUsers, filter);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filter) {
        Stream<User> filteredUsers = users;
        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(filter)) {
                filteredUsers = userFilter.apply(filteredUsers, filter);
            }
        }
        return userMapper.toDtoList(filteredUsers.toList());
    }

    private void validateUserId(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User id cannot be negative");
        }
    }

    public void followUser(long followerId, long followeeId) {
        validate(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    private void validate(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidException("Subscription already exists");
        }
        if (followerId == followeeId) {
            throw new DataValidException("User can't subscribe or unsubscribe on itself");
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        validate(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}