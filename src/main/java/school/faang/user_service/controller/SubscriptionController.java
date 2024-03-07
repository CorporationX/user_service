package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;


@Component
@AllArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can not follow yourself!");
        }
        subscriptionService.followUser(followerId, followeeId);
    }
}
