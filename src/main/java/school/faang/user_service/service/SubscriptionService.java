package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class SubscriptionService {
    public static final String USER_CANNOT_UNSUBSCRIBE_FROM_HIMSELF = "User cannot unsubscribe from himself";
    public static final String USER_NOT_SUBSCRIBED_MESSAGE = "User with ID %d is not subscribed to user with ID %d.";

    private final SubscriptionRepository subscriptionRepository;

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.error(USER_CANNOT_UNSUBSCRIBE_FROM_HIMSELF);
            throw new DataValidationException(USER_CANNOT_UNSUBSCRIBE_FROM_HIMSELF);
        }
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            String errorMessage = String.format(USER_NOT_SUBSCRIBED_MESSAGE, followerId, followeeId);
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }
}
