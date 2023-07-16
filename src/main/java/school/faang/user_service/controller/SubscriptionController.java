package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@Component
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void unfollowUser(long followerId, long followeeId) {

        subscriptionService.unfollowUser(followerId, followeeId);
    }


}
