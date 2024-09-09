package school.faang.user_service.service.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.exception.subscription.SubscriptionAlreadyExistsException;
import school.faang.user_service.exception.subscription.SubscriptionNotFoundException;
import school.faang.user_service.repository.SubscriptionRepository;

import static school.faang.user_service.exception.ExceptionMessages.SUBSCRIBE_ITSELF_VALIDATION;
import static school.faang.user_service.exception.ExceptionMessages.SUBSCRIPTION_ALREADY_EXISTS;
import static school.faang.user_service.exception.ExceptionMessages.SUBSCRIPTION_NOT_FOUND;
import static school.faang.user_service.exception.ExceptionMessages.USER_NOT_FOUND;

@Component
public class SubscriptionValidator {
    private SubscriptionRepository subscriptionRepository;

    public void validateUsers(Long followerId, Long followeeId) {
        validateUser(followerId);
        validateUser(followeeId);
    }

    public void validateUser(Long userId) {
        if (subscriptionRepository.existsById(userId) == false) {
            throw new EntityNotFoundException(USER_NOT_FOUND.getMessage().formatted(userId));
        }
    }

    public void checkSubscriptionOnHimself(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new ValidationException(SUBSCRIBE_ITSELF_VALIDATION.getMessage());
        }
    }

    public void checkIfSubscriptionExists(Long followerId, Long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new SubscriptionAlreadyExistsException(SUBSCRIPTION_ALREADY_EXISTS.getMessage()
                    .formatted(followerId, followeeId));
        }
    }

    public void checkIfSubscriptionNotExists(Long followerId, Long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId) == false) {
            throw new SubscriptionNotFoundException(SUBSCRIPTION_NOT_FOUND.getMessage()
                    .formatted(followerId, followeeId));
        }
    }
}
