package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;


@Component
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void unfollowUser(long followerId, long followeeId) {
        validate(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    private void validate(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("User can`t unsubscribe on itself");
        }
    }
}
