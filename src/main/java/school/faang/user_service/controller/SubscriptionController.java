package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.SubscriptionService;

import java.util.zip.DataFormatException;

@Component
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService, SubscriptionService subscriptionService1) {
        this.subscriptionService = subscriptionService1;
    }

    public void followUser(long followerId, long followeeId) throws DataFormatException {
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) throws DataFormatException {
        subscriptionService.unfollowUser(followerId, followeeId);
    }
}