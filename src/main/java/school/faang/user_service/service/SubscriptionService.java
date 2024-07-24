package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public boolean followUser(long followerId, long followeeId) throws DataValidationException {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You are already following this account");
        }
        subscriptionRepository.followUser(followerId, followeeId);
        return true;
    }

    public boolean unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
        return true;
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();
        return filterUsers(followers, filter).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<User> filterUsers(List<User> users, UserFilterDto filter) {
        return users.stream()
                .filter(user -> userFilters.stream()
                        .reduce(true,
                                (acc, userFilter) -> acc && userFilter.apply(user, filter),
                                Boolean::logicalAnd))
                .skip((long) filter.getPage() * filter.getPageSize())
                .limit(filter.getPageSize())
                .toList();
    }

    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        List<User> followings = subscriptionRepository.findByFollowerId(followerId).toList();
        return filterUsers(followings, filter).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public long getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
