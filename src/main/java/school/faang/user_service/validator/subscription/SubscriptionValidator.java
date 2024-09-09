package school.faang.user_service.validator.subscription;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepository;

    public void checkIfSubscriptionExistsAndIfEqualsShouldExistThenThrowException(long followerId,
                                                                                  long followeeId,
                                                                                  boolean shouldExist,
                                                                                  String exceptionMsg)
            throws ValidationException {

        boolean isExist = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (isExist == shouldExist) {
            throw new ValidationException(exceptionMsg);
        }
    }
}
