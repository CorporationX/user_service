package school.faang.user_service.validator.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {
    private final SubscriptionRepository repository;

    public void validateExistingSubscription(long followerId, long followeeId) {
        if (repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписка уже существует");
        }
    }
}
