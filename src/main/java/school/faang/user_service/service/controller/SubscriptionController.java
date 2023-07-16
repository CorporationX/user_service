package school.faang.user_service.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.exception.DataValidationException;
import school.faang.user_service.service.service.SubscriptionService;

@Component
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    void followUser(long followerId, long followeeId) {
        validate(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    private void validate(long followerId, long followeeId) {
        if(followerId == followeeId){
            throw new DataValidationException("User can't subscribe on itself");
        }
    }
}
