package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validation.SubscriptionValidator;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    public final SubscriptionValidator subscriptionValidator;

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }


    @Transactional
    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateExistsSubscription(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }
}
