package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.zip.DataFormatException;

@Component
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) throws DataFormatException {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataFormatException("there are no users with this id");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) throws DataFormatException {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataFormatException("there are no users with this id");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }
}