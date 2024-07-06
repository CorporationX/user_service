package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.SubscriptionService;

import java.util.zip.DataFormatException;

@Component
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) throws DataFormatException {
        if (followerId == followeeId) {
            throw new DataFormatException("Follow: Invalid id users");
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) throws DataFormatException {
        if (followerId == followeeId) {
            throw new DataFormatException("Unfollow: Invalid id users");
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }
}