package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public void validationSameUser(long followerId, long followeeId, String message) {
        if (followerId == followeeId) {
            throw new DataValidationException(message);
        }
    }

    public void validationSubscriptionExists(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You are already subscribed to this user.");
        }
    }

    public void validationSubscriptionNotExists(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You are not subscribed to this user to unsubscribe from this user");
        }
    }

    public void validationUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("The user does not exist");
        }
    }

    public void validationUsersExists(long followerId, long followeeId) {
        if (!userRepository.existsById(followeeId)) {
            throw new DataValidationException("The user they are trying to subscribe to does not exist");
        }
        if (!userRepository.existsById(followerId)) {
            throw new DataValidationException("The user who is trying to subscribe does not exist");
        }
    }
}
