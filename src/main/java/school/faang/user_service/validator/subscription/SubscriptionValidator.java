package school.faang.user_service.validator.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
public class SubscriptionValidator {
    public void validateId(long fistId, long secondId) {
        if (fistId < 0 || secondId < 0) {
            throw new DataValidationException("User by ID is not found");
        }
    }

    public void validateId(long id) {
        if (id < 0) {
            throw new DataValidationException("User by ID is not found");
        }
    }

    public void checkingSubscription(long firstId, long secondId) {
        if (firstId == secondId) {
            throw new DataValidationException("You can't subscribe/unsubscribe to yourself");
        }
    }

    public void isSubscriber(long firstId, long secondId, SubscriptionRepository repository) {
        validateId(firstId, secondId);
        if (repository.existsByFollowerIdAndFolloweeId(firstId, secondId)) {
            throw new DataValidationException("You are already subscribed/unsubscribed to this user");
        }
    }

    public void isNotSubscriber(long firstId, long secondId, SubscriptionRepository repository) {
        validateId(firstId, secondId);
        if (!repository.existsByFollowerIdAndFolloweeId(firstId, secondId)) {
            throw new DataValidationException("You are already subscribed/unsubscribed to this user");
        }
    }
}
