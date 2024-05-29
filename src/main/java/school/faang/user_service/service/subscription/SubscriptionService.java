package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final SubscriptionValidator subscriptionValidator;
    private final UserValidator userValidator;
    private final List<UserFilter> userFilters;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        userValidator.checkUserInDB(followerId);
        userValidator.checkUserInDB(followeeId);
        subscriptionValidator.checkSubscriptionExists(followerId, followeeId);

        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.checkSubscriptionNotExists(followerId, followeeId);

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        userValidator.checkUserInDB(followeeId);

        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();
        return filterUser(followers, filters);
    }

    @Transactional(readOnly = true)
    public int getFollowersCount(long followeeId) {
        userValidator.checkUserInDB(followeeId);

        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        userValidator.checkUserInDB(followerId);

        List<User> followees = subscriptionRepository.findByFollowerId(followerId).toList();
        return filterUser(followees, filter);
    }

    @Transactional(readOnly = true)
    public int getFollowingCount(long followerId) {
        userValidator.checkUserInDB(followerId);

        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<UserDto> filterUser(List<User> users, UserFilterDto filters) {
        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(filters)) {
                users = filter.apply(users.stream(), filters).toList();
            }
        }
        return users.stream().map(userMapper::toDto).toList();
    }
}