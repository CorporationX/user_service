package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) {
        validate(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    private void validate(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("This subscription already exists");
        }
    }

    public boolean validateIfSubscribed(long followerId, long followeeId) {
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }


    public void unfollowUser(long followerId, long followeeId) {
        if (validateIfSubscribed(followerId, followeeId)) {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        }
    }
}
