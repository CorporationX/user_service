package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionServiceValidator {

    private final SubscriptionRepository subscriptionRepository;

    public void checkIfAlreadySubscribed(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You are already subscribed/unsubscribed to this user");
        }
    }

    public void validateFollowIds(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot subscribe/unsubscribe to yourself");
        }
    }
}
