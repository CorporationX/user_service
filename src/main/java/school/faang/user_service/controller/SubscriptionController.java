package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@RequiredArgsConstructor
@Controller
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("User cannot unsubscribe from himself");
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }
}
