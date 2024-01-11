package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final SubscriptionValidator subscriptionValidator;

    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateSubscription(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validateUnsubscription(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        List<User> initialUsers = subscriptionRepository.findByFollowerId(followeeId).toList();

        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(filters)) {
                initialUsers = filter.apply(initialUsers.stream(), filters).toList();
            }
        }

        return userMapper.toDto(initialUsers);
    }

    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filters) {
        List<User> initialUsers = subscriptionRepository.findByFolloweeId(followeeId).toList();

        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(filters)) {
                initialUsers = filter.apply(initialUsers.stream(), filters).toList();
            }
        }

        return userMapper.toDto(initialUsers);
    }

    public long getFollowingCount(long followerId){
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
