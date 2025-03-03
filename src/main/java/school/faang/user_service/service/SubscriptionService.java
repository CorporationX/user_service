package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(String.format(
                    "User with ID %d is not subscribed to user with ID %d.", followerId, followeeId));
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }
}
