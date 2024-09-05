package school.faang.user_service.validators;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MessageError;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@Slf4j
public class SubscriptionValidator {
    public void validateId(long fistId, long secondId) {
        if (fistId < 0 || secondId < 0) {
            throw new DataValidationException(MessageError.USER_NOT_FOUND_EXCEPTION);
        }
    }

    public void validateId (long id) {
        if (id < 0) {
            throw new DataValidationException(MessageError.USER_NOT_FOUND_EXCEPTION);
        }
    }

    // Проверка. Если пользователь хочет подписаться на себя, то кинет ошибку
    public void subscribeAndUnsubscribeToYourself(long firstId, long secondId) {
        if (firstId == secondId) {
            throw new DataValidationException(MessageError.SUBSCRIBE_TO_YOURSELF_EXCEPTION);
        }
    }

    // Проверка. Является ли первый пользователь подписчиком второго, если да, то кидает ошибку
    public void isSubscriber(long firstId, long secondId, SubscriptionRepository repository) {
        validateId(firstId, secondId);
        if (repository.existsByFollowerIdAndFolloweeId(firstId, secondId)) {
            throw new DataValidationException(MessageError.SUBSCRIPTION_NOT_AND_ALREADY_EXISTS_EXCEPTION);
        }
    }

    // Проверка. Является ли первый пользователь подписчиком, если нет, то кидает ошибку
    public void isNotSubscriber(long firstId, long secondId, SubscriptionRepository repository) {
        validateId(firstId, secondId);
        if (!repository.existsByFollowerIdAndFolloweeId(firstId, secondId)) {
            throw new DataValidationException(MessageError.SUBSCRIPTION_NOT_AND_ALREADY_EXISTS_EXCEPTION);
        }
    }


}
