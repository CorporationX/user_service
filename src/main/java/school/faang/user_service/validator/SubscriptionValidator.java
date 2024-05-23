package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;


@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepository;

    public void validateUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsById(followeeId) ||
                !subscriptionRepository.existsById(followerId)) {
            throw new DataValidationException("This user is not registered");
        }
        if (followerId == followeeId) {
            throw new DataValidationException("IDs cannot be equal!");
        }
    }

    public void validateExistsSubscription(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Subscription already exists!");
        }
    }
}
