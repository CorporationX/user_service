package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public void followUser(long followerId, long followeeId) {
        validateUserIds(followerId, followeeId);
        validateSubscriptionExist(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Override
    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        validateUserIds(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    private void validateSubscriptionExist(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("This subscription already exists");
        }
    }

    private void validateUserIds(long followerId, long followeeId) {
        if (followerId <= 0 || followeeId <= 0) {
            throw new DataValidationException("User identifiers must be positive numbers");
        }
        if (followerId == followeeId) {
            throw new DataValidationException("Follower and followee the same user");
        }
    }

}
