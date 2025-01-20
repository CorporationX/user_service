package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionValidator subscriptionValidator;
    private final List<UserFilter> userFilters;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateNotSelfSubscription(
                "The user cannot subscribe to himself", followerId, followeeId);

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("The user has already subscribed");
        }

        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validateNotSelfSubscription(
                "The user cannot unsubscribe to himself", followerId, followeeId);

        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("The user has already been unsubscribed");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<User> getFollowers(long followeeId, UserFilterDto filters) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(users, filters).toList();
    }

    @Transactional(readOnly = true)
    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional
    public List<User> getFollowing(long followerId, UserFilterDto filters) {
        Stream<User> users = subscriptionRepository.findByFollowerId(followerId);
        return filterUsers(users, filters).toList();
    }

    @Transactional(readOnly = true)
    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private Stream<User> filterUsers(Stream<User> users, UserFilterDto filters) {
        if (filters == null) {
            return users;
        }
        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(filters)) {
                users = filter.apply(users, filters);
            }
        }
        return users;
    }
}
