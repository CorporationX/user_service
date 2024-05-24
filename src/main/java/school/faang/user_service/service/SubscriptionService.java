package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final SubscriptionValidator subscriptionValidator;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.checkSubscriptionExists(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.checkSubscriptionNotExists(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        List<User> followers = subscriptionRepository.findByFollowerId(followeeId).toList();
        return filterUser(followers, filters);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        List<User> followees = subscriptionRepository.findByFolloweeId(followerId).toList();
        return filterUser(followees, filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<UserDto> filterUser(List<User> users, UserFilterDto filters) {
        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(filters)) {
                users = filter.apply(users, filters).toList();
            }
        }
        return users.stream().map(userMapper::toDto).toList();
    }
}