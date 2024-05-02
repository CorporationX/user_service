package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;


@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepository;

    public void validateUserTriedHimself(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("The user " + followeeId +
                    " tried to follow himself!");
        }
    }

    public void validateIsExists(long followerId, long followeeId) {
        boolean isExists = subscriptionRepository
                .existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isExists) {
            throw new DataValidationException("User " + followerId +
                    " subscription to user " + followeeId + " exist");
        }
    }
}
