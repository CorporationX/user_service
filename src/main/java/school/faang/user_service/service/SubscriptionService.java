package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.zip.DataFormatException;

@Component
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void followUser(long followerId, long followeeId) throws DataFormatException {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataFormatException();
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) throws DataFormatException {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataFormatException();
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }
}