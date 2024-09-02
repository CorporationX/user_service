package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@AllArgsConstructor
public class SubscriptionService {
    private SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) throws DataValidationException {
        validateSubscription(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    private void validateSubscription(long followerId, long followeeId) throws DataValidationException {
        if (followerId == followeeId) {
            throw new DataValidationException("User %s is trying to subscribe to himself".formatted(followerId));
        }

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User %d is already subscribed to user %d.".formatted(followerId, followeeId));
        }
    }
}
