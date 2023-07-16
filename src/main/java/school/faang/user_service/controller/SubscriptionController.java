package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.SubscriptionService;

@Component
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }
}
