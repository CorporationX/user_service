package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        if (!isValid(followerId, followeeId)) {
            throw new DataValidationException("user can't follow itself");
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    public boolean isValid(long followerId, long followeeId) {
        return followerId != followeeId;
    }
}
