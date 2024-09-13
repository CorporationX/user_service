package school.faang.user_service.validator.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exceptions.DataValidationException;

@Component
@Slf4j
@RequiredArgsConstructor
public class SubscriptionValidator {

    public void validateUserIds(long followerId, long followeeId) throws DataValidationException {
        if (followerId == followeeId) {
            String message = "User %s is trying to unsubscribe to himself".formatted(followerId);
            log.warn(message);
            throw new DataValidationException(message);
        }
    }

    public void validateFollowSubscription(boolean exists, long followerId, long followeeId) throws DataValidationException {
        if (exists) {
            String message = "User %d is already subscribed to user %d.".formatted(followerId, followeeId);
            log.warn(message);
            throw new DataValidationException(message);
        }
    }

    public void validateUnfollowSubscription(boolean exists, long followerId, long followeeId) throws DataValidationException {
        if (!exists) {
            String message = "User %d is already unsubscribe to user %d.".formatted(followerId, followeeId);
            log.warn(message);
            throw new DataValidationException(message);
        }
    }
}