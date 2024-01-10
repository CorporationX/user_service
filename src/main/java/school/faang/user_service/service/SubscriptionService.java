package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionValidator subscriptionValidator;

    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateSubscription(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validateUnsubscription(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }
}
