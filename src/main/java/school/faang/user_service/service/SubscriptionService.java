package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        followUserValidation(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            subscriptionRepository.unfollowUser(followerId, followeeId);
            return;
        }
        throw new DataValidationException("You are not subscribed to this user to unsubscribe from this user");
    }

    private void followUserValidation(long followerId, long followeeId) {
        if (!userRepository.existsById(followeeId)) {
            throw new DataValidationException("The user they are trying to subscribe to does not exist");
        }
        if (!userRepository.existsById(followerId)) {
            throw new DataValidationException("The user who is trying to subscribe does not exist");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You are already subscribed to this user.");
        }
    }
}
