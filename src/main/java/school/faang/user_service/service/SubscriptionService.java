package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static school.faang.user_service.exceptions.ExceptionMessage.REPEATED_SUBSCRIPTION_EXCEPTION;

@Service
@AllArgsConstructor
@Slf4j
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepo;

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(REPEATED_SUBSCRIPTION_EXCEPTION.getMessage());
        }

        subscriptionRepo.followUser(followerId, followeeId);
        log.info("User + (id=" + followerId + ") subscribed to user (id=" + followeeId + ").");
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepo.unfollowUser(followerId, followeeId);
        log.info("User + (id=" + followerId + ") canceled subscription to user (id=" + followeeId + ").");
    }
}

