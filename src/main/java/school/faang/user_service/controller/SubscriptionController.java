package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@Controller
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void followUser(long followerId, long followeId) {
        if (followerId == followeId) {
            throw new DataValidationException("User can't subscribe to himself.");
        }
        subscriptionService.followUser(followerId, followeId);
    }
}
