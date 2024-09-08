package school.faang.user_service.validators;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MessageError;
import school.faang.user_service.repository.SubscriptionRepository;

@Component

public class SubscriptionValidator {
    public void validateId(long fistId, long secondId) {
        if (fistId < 0 || secondId < 0) {
            throw new DataValidationException(MessageError.USER_NOT_FOUND_EXCEPTION);
        }
    }

    public void validateId(long id) {
        if (id < 0) {
            throw new DataValidationException(MessageError.USER_NOT_FOUND_EXCEPTION);
        }
    }

    public void checkingSubscription(long firstId, long secondId) {
        if (firstId == secondId) {
            throw new DataValidationException(MessageError.SUBSCRIBE_TO_YOURSELF_EXCEPTION);
        }
    }

    public void isSubscriber(long firstId, long secondId, SubscriptionRepository repository) {
        validateId(firstId, secondId);
        if (repository.existsByFollowerIdAndFolloweeId(firstId, secondId)) {
            throw new DataValidationException(MessageError.SUBSCRIPTION_NOT_AND_ALREADY_EXISTS_EXCEPTION);
        }
    }

    public void isNotSubscriber(long firstId, long secondId, SubscriptionRepository repository) {
        validateId(firstId, secondId);
        if (!repository.existsByFollowerIdAndFolloweeId(firstId, secondId)) {
            throw new DataValidationException(MessageError.SUBSCRIPTION_NOT_AND_ALREADY_EXISTS_EXCEPTION);
        }
    }


}
