package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.SubscriptionService;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void unfollowUser(long followerId, long followeeId) {

        subscriptionService.unfollowUser(followerId, followeeId);
    }
}