package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    void followUser(long followerId, long followeeId) {
        validate(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    private void validate(long followerId, long followeeId) {
        if(followerId == followeeId){
            throw new DataValidationException("You can't subscribe to yourself");
        }
    }
}
