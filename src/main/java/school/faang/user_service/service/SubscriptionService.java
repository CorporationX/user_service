package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class SubscriptionService {

    public static final String USER_ALREADY_FOLLOWING_ERROR = "User with ID %d is already following user with ID %d.";
    public static final String USER_CANNOT_UNSUBSCRIBE_FROM_HIMSELF = "User cannot unsubscribe from himself";
    public static final String USER_NOT_SUBSCRIBED_MESSAGE = "User with ID %d is not subscribed to user with ID %d.";
    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("User {} attempted to follow themselves.", followerId);
            throw new DataValidationException("User can't subscribe to himself.");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("User {} is already following user {}", followerId, followeeId);
            throw new DataValidationException(String.format(
                    USER_ALREADY_FOLLOWING_ERROR, followerId, followeeId));
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

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
