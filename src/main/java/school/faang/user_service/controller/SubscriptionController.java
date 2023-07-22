package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.SubscriptionService;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

}