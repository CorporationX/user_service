package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidException;
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
            throw new DataValidException("Subscription already exists");
        }
        if (followerId == followeeId) {
            throw new DataValidException("User can't subscribe on itself");
        }
    }
}