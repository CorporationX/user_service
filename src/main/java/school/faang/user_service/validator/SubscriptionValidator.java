package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {
    private final SubscriptionRepository subscriptionRepository;

    public void validateUserIds(long followerId, long followeeId) throws DataValidationException {
        if (followerId == followeeId) {
            throw new DataValidationException("User %s is trying to unsubscribe to himself".formatted(followerId));
        }
    }

    public void validateFollowSubscription(long followerId, long followeeId) throws DataValidationException {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User %d is already subscribed to user %d.".formatted(followerId, followeeId));
        }
    }

    public void validateUnfollowSubscription(long followerId, long followeeId) throws DataValidationException {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User %d is already unsubscribe to user %d.".formatted(followerId, followeeId));
        }
    }
}